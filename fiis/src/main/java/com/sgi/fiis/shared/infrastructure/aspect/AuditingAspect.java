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
        Long idUsuario = 1L; // Fallback System User ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            idUsuario = userDetails.getId();
        }

        String ipAddress = "0.0.0.0";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ipAddress = request.getRemoteAddr();
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader != null && !xfHeader.isEmpty()) {
                ipAddress = xfHeader.split(",")[0].trim();
            }
        }

        // Determine action mapping based on check constraint
        String rawAction = auditable.action().toUpperCase();
        String mappedAction = "EDITAR"; // default fallback
        if (rawAction.contains("CREATE") || rawAction.contains("REGISTRAR") || rawAction.contains("POSTULAR")) {
            mappedAction = "CREAR";
        } else if (rawAction.contains("DELETE") || rawAction.contains("ELIMINAR") || rawAction.contains("REJECT")) {
            mappedAction = "ELIMINAR";
        } else if (rawAction.contains("DEACTIVATE") || rawAction.contains("DESACTIVAR")) {
            mappedAction = "DESACTIVAR";
        } else if (rawAction.contains("ACTIVATE") || rawAction.contains("ACTIVAR")) {
            mappedAction = "ACTIVAR";
        } else if (rawAction.contains("LOGIN")) {
            mappedAction = "LOGIN";
        } else if (rawAction.contains("LOGOUT")) {
            mappedAction = "LOGOUT";
        }

        // Determine affected table simple name
        String targetClass = joinPoint.getTarget().getClass().getSimpleName();
        String tablaAfectada = targetClass.toLowerCase()
                .replace("interactor", "")
                .replace("service", "")
                .replace("impl", "");
        if (tablaAfectada.isEmpty() || tablaAfectada.length() > 100) {
            tablaAfectada = "general";
        }

        // Determine register ID
        Long idRegistro = 0L;
        if (result != null) {
            try {
                java.lang.reflect.Method getIdMethod = null;
                for (java.lang.reflect.Method m : result.getClass().getMethods()) {
                    if (m.getName().startsWith("get") && (m.getName().endsWith("Id") || m.getName().contains("Id")) && m.getParameterCount() == 0) {
                        getIdMethod = m;
                        break;
                    }
                }
                if (getIdMethod != null) {
                    Object idVal = getIdMethod.invoke(result);
                    if (idVal instanceof Number) {
                        idRegistro = ((Number) idVal).longValue();
                    }
                }
            } catch (Exception ignored) {}
        }

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
}
