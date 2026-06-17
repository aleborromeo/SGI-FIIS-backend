package com.sgi.fiis.documentacion.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Cobertura - DocumentResponseDto")
class DocumentResponseDtoTest {

    @Test
    @DisplayName("Debe cubrir la estructura de datos del DTO de respuesta")
    void testDtoDataAndStructure() {

        LocalDateTime now = LocalDateTime.now();

        // 1. Instanciar DTO
        DocumentResponseDto dto1 =
                new DocumentResponseDto(
                        1L,
                        "tesis_sistema.pdf",
                        "PDF",
                        2048L,
                        42L,
                        now
                );

        // 2. Validación de atributos del record
        assertEquals(1L, dto1.id());
        assertEquals("tesis_sistema.pdf", dto1.originalName());
        assertEquals("PDF", dto1.extension());
        assertEquals(2048L, dto1.sizeBytes());
        assertEquals(42L, dto1.uploadedById());
        assertEquals(now, dto1.uploadDate());

        // 3. Cobertura estructural JaCoCo
        DocumentResponseDto dto2 =
                new DocumentResponseDto(
                        1L,
                        "tesis_sistema.pdf",
                        "PDF",
                        2048L,
                        42L,
                        now
                );

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}