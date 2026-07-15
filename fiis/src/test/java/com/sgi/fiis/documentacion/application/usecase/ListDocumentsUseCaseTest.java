package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListDocumentsUseCase Unit Tests")
@SuppressWarnings("all")
class ListDocumentsUseCaseTest {

    @Mock
    private DocumentRepositoryPort documentRepositoryPort;

    @InjectMocks
    private ListDocumentsUseCase listDocumentsUseCase;

    @Test
    @DisplayName("Should return list of document DTOs")
    void execute_ReturnsListOfDtos() {
        Document doc1 = Document.builder()
                .id(1L)
                .originalName("informe.pdf")
                .extension("PDF")
                .sizeBytes(1024L)
                .uploadedById(10L)
                .uploadDate(LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0))
                .active(true)
                .build();

        Document doc2 = Document.builder()
                .id(2L)
                .originalName("tesis.docx")
                .extension("DOCX")
                .sizeBytes(2048L)
                .uploadedById(20L)
                .uploadDate(LocalDateTime.of(2026, java.time.Month.JUNE, 18, 10, 0))
                .active(true)
                .build();

        when(documentRepositoryPort.findAll()).thenReturn(List.of(doc1, doc2));

        List<DocumentResponseDto> result = listDocumentsUseCase.execute();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("informe.pdf", result.get(0).originalName());
        assertEquals("PDF", result.get(0).extension());
        assertEquals(1024L, result.get(0).sizeBytes());
        assertEquals(10L, result.get(0).uploadedById());

        assertEquals(2L, result.get(1).id());
        assertEquals("tesis.docx", result.get(1).originalName());
        assertEquals("DOCX", result.get(1).extension());

        verify(documentRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no documents exist")
    void execute_ReturnsEmptyList() {
        when(documentRepositoryPort.findAll()).thenReturn(List.of());

        List<DocumentResponseDto> result = listDocumentsUseCase.execute();

        assertTrue(result.isEmpty());
        verify(documentRepositoryPort).findAll();
    }
}
