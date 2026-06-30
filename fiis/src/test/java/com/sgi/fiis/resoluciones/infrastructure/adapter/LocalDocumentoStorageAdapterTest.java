package com.sgi.fiis.resoluciones.infrastructure.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class LocalDocumentoStorageAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LocalDocumentoStorageAdapter adapter;

    @Test
    void guardarDocumento_DebeRetornarIdGenerado() {
        // Arrange
        doAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            Map<String, Object> keys = new HashMap<>();
            keys.put("id_documento", 5L);
            keyHolder.getKeyList().add(keys);
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        // Act
        Long result = adapter.guardarDocumento(new byte[]{1, 2}, "test.pdf", "application/pdf");

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void guardarDocumento_CuandoFalla_DebeLanzarExcepcion() {
        // Arrange
        doAnswer(invocation -> {
            // No agregamos llaves generadas
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            adapter.guardarDocumento(new byte[]{1, 2}, "test.pdf", "application/pdf");
        });
    }
}
