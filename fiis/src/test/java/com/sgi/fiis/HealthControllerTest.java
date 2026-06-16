package com.sgi.fiis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthController Unit Tests")
class HealthControllerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private HealthController healthController;

    @Test
    @DisplayName("Should return OK and CONNECTED when database query succeeds")
    void testHealthCheckSuccess() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        Map<String, Object> response = healthController.healthCheck();

        assertNotNull(response);
        assertEquals("OK", response.get("spring"));
        assertEquals("CONNECTED", response.get("database"));
        assertEquals(1, response.get("query_result"));
        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
    }

    @Test
    @DisplayName("Should return ERROR when database query throws exception")
    void testHealthCheckDatabaseError() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenThrow(new RuntimeException("Connection failed"));

        Map<String, Object> response = healthController.healthCheck();

        assertNotNull(response);
        assertEquals("OK", response.get("spring"));
        assertEquals("ERROR", response.get("database"));
        assertEquals("Connection failed", response.get("message"));
        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
    }
}
