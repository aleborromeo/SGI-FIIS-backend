package com.sgi.fiis.thesis.infrastructure.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentValidationJdbcAdapter Unit Tests")
class DocumentValidationJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private DocumentValidationJdbcAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DocumentValidationJdbcAdapter(jdbcTemplate);
    }

    @Test
    @DisplayName("existeDocumentoActivo returns true when count > 0")
    void existeDocumentoActivoReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
                .thenReturn(1);

        assertTrue(adapter.existeDocumentoActivo(1));
    }

    @Test
    @DisplayName("existeDocumentoActivo returns false when count is 0")
    void existeDocumentoActivoReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(99)))
                .thenReturn(0);

        assertFalse(adapter.existeDocumentoActivo(99));
    }

    @Test
    @DisplayName("existeDocumentoActivo returns false when id is null")
    void existeDocumentoActivoReturnsFalseForNull() {
        assertFalse(adapter.existeDocumentoActivo(null));
    }

    @Test
    @DisplayName("documentoPerteneceAUsuario returns true when count > 0")
    void documentoPerteneceAUsuarioReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1), eq(101L)))
                .thenReturn(1);

        assertTrue(adapter.documentoPerteneceAUsuario(1, 101L));
    }

    @Test
    @DisplayName("documentoPerteneceAUsuario returns false when count is 0")
    void documentoPerteneceAUsuarioReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1), eq(999L)))
                .thenReturn(0);

        assertFalse(adapter.documentoPerteneceAUsuario(1, 999L));
    }

    @Test
    @DisplayName("documentoPerteneceAUsuario returns false when id is null")
    void documentoPerteneceAUsuarioReturnsFalseForNullId() {
        assertFalse(adapter.documentoPerteneceAUsuario(null, 101L));
    }

    @Test
    @DisplayName("documentoPerteneceAUsuario returns false when usuario is null")
    void documentoPerteneceAUsuarioReturnsFalseForNullUsuario() {
        assertFalse(adapter.documentoPerteneceAUsuario(1, null));
    }
}
