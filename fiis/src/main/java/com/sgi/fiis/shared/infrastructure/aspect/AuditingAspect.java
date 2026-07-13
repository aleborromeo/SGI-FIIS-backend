package com.sgi.fiis.shared.infrastructure.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;

@Aspect
@Component
public class AuditingAspect {

    private final JdbcTemplate jdbcTemplate;

    public AuditingAspect(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @AfterReturning(value = "@annotation(auditable)", returning = "result")
    public void audit(JoinPoint joinPoint, Auditable auditable, Object result) {
        Long idUsuario = extractUserId();
        String ipAddress = extractIpAddress();
        String mappedAction = mapAction(auditable.action());

        // Determine affected table simple name
        String targetClass = joinPoint.getTarget().getClass().getSimpleName();
        String tablaAfectada = targetClass.toLowerCase()
                .replace("interactor", "")
                .replace("service", "")
                .replace("impl", "");
        if (tablaAfectada.isEmpty() || tablaAfectada.length() > 100) {
            tablaAfectada = "general";
        }

        Long idRegistro = extractRegisterId(result);

        jdbcTemplate.update(
                "INSERT INTO auditoria_general (tabla_afectada, id_registro, accion, id_usuario, ip_origen, fecha_accion) VALUES (?, ?, ?, ?, ?, ?)",
                tablaAfectada,
                idRegistro,
                mappedAction,
                idUsuario,
                ipAddress,
                LocalDateTime.now(ZoneId.of("UTC"))
        );
    }

    private Long extractUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        return 1L; // Fallback System User ID
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
                if (m.getName().startsWith("get") && (m.getName().endsWith("Id") || m.getName().contains("Id")) && m.getParameterCount() == 0) {
                    Object idVal = m.invoke(result);
                    if (idVal instanceof Number number) {
                        return number.longValue();
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException ignored) {
            // Ignored because reflection errors default to return 0L
        }
        return 0L;
    }
}
