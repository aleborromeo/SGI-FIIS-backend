package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeactivateLineaInvestigacionUseCase Unit Tests")
class DeactivateLineaInvestigacionUseCaseTest {

    @Mock
    private ResearchLineRepositoryPort repository;

    @InjectMocks
    private DeactivateLineaInvestigacionUseCase useCase;

    @Test
    @DisplayName("Should successfully deactivate a research line")
    void testExecuteSuccess() {
        ResearchLine line = ResearchLine.builder()
                .id(1)
                .lineName("Tecnologia")
                .active(true)
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(line));
        when(repository.save(any(ResearchLine.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResearchLine result = useCase.execute(1);

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(repository).findById(1);
        verify(repository).save(line);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when research line ID does not exist")
    void testExecuteNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(1));
        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }
}
