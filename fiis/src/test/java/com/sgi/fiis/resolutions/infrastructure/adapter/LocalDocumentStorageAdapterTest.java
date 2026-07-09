package com.sgi.fiis.resolutions.infrastructure.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalDocumentStorageAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private LocalDocumentStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LocalDocumentStorageAdapter(jdbcTemplate, messageSource, "target/test-uploads");
    }

    @Test
    void saveDocument_shouldReturnGeneratedId_andExecuteLambda() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);

        doAnswer(invocation -> {
            PreparedStatementCreator psc = invocation.getArgument(0);
            KeyHolder keyHolder = invocation.getArgument(1);
            
            // Execute lambda to cover it
            psc.createPreparedStatement(connection);
            
            Map<String, Object> keys = new HashMap<>();
            keys.put("id_documento", 5L);
            keyHolder.getKeyList().add(keys);
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        // Act
        Long result = adapter.saveDocument(new byte[]{1, 2}, "test.pdf", "application/pdf");

        // Assert
        assertEquals(5L, result);
        verify(preparedStatement).setString(1, "test.pdf");
        verify(preparedStatement).setString(3, "pdf");
        verify(preparedStatement).setLong(4, 2L);
        verify(preparedStatement).setLong(5, 1L);
    }
    
    @Test
    void saveDocument_nullFileBytes_shouldHandleGracefully() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);

        doAnswer(invocation -> {
            PreparedStatementCreator psc = invocation.getArgument(0);
            KeyHolder keyHolder = invocation.getArgument(1);
            psc.createPreparedStatement(connection);
            
            Map<String, Object> keys = new HashMap<>();
            keys.put("id_documento", 10L);
            keyHolder.getKeyList().add(keys);
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        // Act
        Long result = adapter.saveDocument(null, "test.pdf", "application/pdf");

        // Assert
        assertEquals(10L, result);
        verify(preparedStatement).setLong(4, 0L); // Because size should be 0
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
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            adapter.saveDocument(new byte[]{1, 2}, "test.pdf", "application/pdf");
        });
        
        assertEquals("Failed to save the attached document.", ex.getMessage());
    }

    @Test
    void saveDocument_withNullFileName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            adapter.saveDocument(new byte[]{1, 2}, null, "application/pdf");
        });
    }

    @Test
    void saveDocument_withEmptyFileName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            adapter.saveDocument(new byte[]{1, 2}, "", "application/pdf");
        });
    }

    @Test
    void saveDocument_withPathTraversalFileName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            adapter.saveDocument(new byte[]{1, 2}, "../escaped.pdf", "application/pdf");
        });
    }

    @Test
    void saveDocument_withPathTraversalFileNameContainsDoubleDot_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            adapter.saveDocument(new byte[]{1, 2}, "some..path.pdf", "application/pdf");
        });
    }

    @Test
    void constructor_whenCannotCreateDirectories_shouldThrowIllegalStateException() throws java.io.IOException {
        java.io.File tempFile = java.io.File.createTempFile("fiis-test-file", ".tmp");
        try {
            assertThrows(IllegalStateException.class, () -> {
                new LocalDocumentStorageAdapter(jdbcTemplate, messageSource, tempFile.getAbsolutePath());
            });
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void saveDocument_withActiveSecurityContext_shouldUseUserIdFromContext() throws SQLException {
        // Arrange
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        com.sgi.fiis.auth.infrastructure.security.CustomUserDetails userDetails = mock(com.sgi.fiis.auth.infrastructure.security.CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(88L);
        when(auth.getPrincipal()).thenReturn(userDetails);
        
        org.springframework.security.core.context.SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);

        try {
            when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenReturn(preparedStatement);

            doAnswer(invocation -> {
                PreparedStatementCreator psc = invocation.getArgument(0);
                KeyHolder keyHolder = invocation.getArgument(1);
                psc.createPreparedStatement(connection);
                
                Map<String, Object> keys = new HashMap<>();
                keys.put("id_documento", 45L);
                keyHolder.getKeyList().add(keys);
                return 1;
            }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

            // Act
            Long result = adapter.saveDocument(new byte[]{1, 2}, "test.pdf", "application/pdf");

            // Assert
            assertEquals(45L, result);
            verify(preparedStatement).setLong(5, 88L); // userId should be 88L
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }
}
