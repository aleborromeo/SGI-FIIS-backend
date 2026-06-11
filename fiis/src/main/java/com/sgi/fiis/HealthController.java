package com.sgi.fiis;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    // Constructor Injection
    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT 1",
                    Integer.class
            );

            response.put("spring", "OK");
            response.put("database", "CONNECTED");
            response.put("query_result", result);
            response.put("message", "Spring Boot + PostgreSQL funcionando 🚀");

        } catch (Exception e) {

            response.put("spring", "OK");
            response.put("database", "ERROR");
            response.put("message", e.getMessage());
        }

        return response;
    }
}