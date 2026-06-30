package com.sgi.fiis.resolutions.infrastructure.adapter;

import com.sgi.fiis.resolutions.domain.port.out.DocumentStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalDocumentStorageAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private LocalDocumentStorageAdapter adapter;

    @Test
    void saveDocument_shouldReturnGeneratedId() {
        // Arrange
        doAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            Map<String, Object> keys = new HashMap<>();
            keys.put("id_documento", 5L);
            keyHolder.getKeyList().add(keys);
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        // Act
        Long result = adapter.saveDocument(new byte[]{1, 2}, "test.pdf", "application/pdf");

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void saveDocument_whenFails_shouldThrowException() {
        // Arrange
        doAnswer(invocation -> {
            // No generated keys added
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        when(messageSource.getMessage(eq("resolution.error.document-save-failed"), any(), any(Locale.class)))
                .thenReturn("Failed to save the attached document.");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            adapter.saveDocument(new byte[]{1, 2}, "test.pdf", "application/pdf");
        });
    }
}
