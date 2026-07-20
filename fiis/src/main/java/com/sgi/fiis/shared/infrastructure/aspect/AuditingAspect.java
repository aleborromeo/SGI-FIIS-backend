package com.sgi.fiis.shared.infrastructure.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;

@Aspect
@Component
public class AuditingAspect {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final CorrelationContext correlationContext;

    public AuditingAspect(JdbcTemplate jdbcTemplate, CorrelationContext correlationContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.correlationContext = correlationContext;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String mappedAction = mapAction(auditable.action());
        String tablaAfectada = resolveTableName(auditable, joinPoint);
        Long idRegistro = extractRegisterIdFromArgs(joinPoint);

        String datosAnteriores = null;
        if (!"CREAR".equals(mappedAction) && idRegistro > 0 && !tablaAfectada.isBlank()) {
            datosAnteriores = queryPreviousState(tablaAfectada, idRegistro);
        }

        String correlationId = java.util.UUID.randomUUID().toString();
        correlationContext.setCorrelationId(correlationId);

        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            correlationContext.clear();
        }

        if (result != null && idRegistro == 0L) {
            idRegistro = extractRegisterId(result);
        }

        Long idUsuario = extractUserId();
        String ipAddress = extractIpAddress();
        String datosNuevos = serializeArgs(joinPoint, result);

        jdbcTemplate.update(
                "INSERT INTO auditoria_general (tabla_afectada, id_registro, accion, id_usuario, datos_anteriores, datos_nuevos, ip_origen, fecha_accion, correlation_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                tablaAfectada,
                idRegistro,
                mappedAction,
                idUsuario,
                datosAnteriores,
                datosNuevos,
                ipAddress,
                LocalDateTime.now(ZoneId.of("UTC")),
                correlationId
        );

        return result;
    }

    private String resolveTableName(Auditable auditable, JoinPoint joinPoint) {
        String explicit = auditable.table();
        if (explicit != null && !explicit.isBlank()) {
            return explicit;
        }
        String targetClass = joinPoint.getTarget().getClass().getSimpleName();
        String tableName = targetClass.toLowerCase()
                .replace("interactor", "")
                .replace("service", "")
                .replace("impl", "");
        if (tableName.isEmpty() || tableName.length() > 100) {
            tableName = "general";
        }
        return tableName;
    }

    private String queryPreviousState(String tableName, Long idRegistro) {
        try {
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idRegistro);
            if (rows.isEmpty()) {
                sql = "SELECT * FROM " + tableName + " WHERE id_" + tableName + " = ?";
                rows = jdbcTemplate.queryForList(sql, idRegistro);
            }
            if (!rows.isEmpty()) {
                return objectMapper.writeValueAsString(rows.get(0));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private Long extractRegisterIdFromArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null) return 0L;
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = sig.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) continue;
            if (args[i] instanceof Number number) {
                String name = (paramNames != null && i < paramNames.length) ? paramNames[i] : "";
                if (name.contains("id") || name.contains("Id") || name.contains("ID")) {
                    return number.longValue();
                }
            }
        }
        return 0L;
    }

    private String serializeArgs(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null) continue;
                    if (sb.length() > 1) sb.append(",");
                    String paramName = getParamName(joinPoint, i);
                    sb.append("\"").append(paramName).append("\":");
                    sb.append(objectMapper.writeValueAsString(args[i]));
                }
            }
            if (result != null) {
                if (sb.length() > 1) sb.append(",");
                sb.append("\"resultado\":");
                sb.append(objectMapper.writeValueAsString(result));
            }
            sb.append("}");
            return sb.toString();
        } catch (JsonProcessingException e) {
            return "{\"error\":\"No se pudo serializar\"}";
        }
    }

    private String getParamName(JoinPoint joinPoint, int index) {
        if (joinPoint.getSignature() instanceof MethodSignature sig) {
            String[] names = sig.getParameterNames();
            if (names != null && index < names.length) {
                return names[index];
            }
        }
        return "arg" + index;
    }

    private Long extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        return 1L;
    }

    private String extractIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ipAddress = request.getRemoteAddr();
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader != null && !xfHeader.isEmpty()) {
                ipAddress = xfHeader.split(",")[0].trim();
            }
            return ipAddress;
        }
        return "0.0.0.0";
    }

    private String mapAction(String rawAction) {
        String actionUpper = rawAction.toUpperCase();
        if (actionUpper.contains("CREATE") || actionUpper.contains("REGISTRAR") || actionUpper.contains("POSTULAR")) {
            return "CREAR";
        }
        if (actionUpper.contains("DELETE") || actionUpper.contains("ELIMINAR") || actionUpper.contains("REJECT")) {
            return "ELIMINAR";
        }
        if (actionUpper.contains("DEACTIVATE") || actionUpper.contains("DESACTIVAR")) {
            return "DESACTIVAR";
        }
        if (actionUpper.contains("ACTIVATE") || actionUpper.contains("ACTIVAR")) {
            return "ACTIVAR";
        }
        if (actionUpper.contains("LOGIN")) {
            return "LOGIN";
        }
        if (actionUpper.contains("LOGOUT")) {
            return "LOGOUT";
        }
        return "EDITAR";
    }

    private Long extractRegisterId(Object result) {
        if (result == null) {
            return 0L;
        }
        try {
            for (java.lang.reflect.Method m : result.getClass().getMethods()) {
                if (m.getName().startsWith("get") && m.getParameterCount() == 0) {
                    String methodName = m.getName();
                    if (methodName.equals("getId") || methodName.equals("getIdConvocatoria") || methodName.equals("getIdProyecto")) {
                        Object idVal = m.invoke(result);
                        if (idVal instanceof Number number) {
                            return number.longValue();
                        }
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException ignored) {
            // Fallback: If reflection fails, return 0L
        }
        return 0L;
    }
}
