package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchGroupLineRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveGroupFromResearchLineUseCaseTest {

    @Mock
    private ResearchGroupLineRepositoryPort repository;

    @Mock
    private GetResearchLineUseCase getResearchLineUseCase;

    @InjectMocks
    private RemoveGroupFromResearchLineUseCase useCase;

    @Test
    void execute_shouldRemoveGroup_whenAssigned() {
        ResearchLine line = ResearchLine.builder().id(1).build();

        when(getResearchLineUseCase.execute(1)).thenReturn(line);
        when(repository.isGroupAssignedToLine(2, 1)).thenReturn(true);

        useCase.execute(1, 2);

        verify(getResearchLineUseCase).execute(1);
        verify(repository).isGroupAssignedToLine(2, 1);
        verify(repository).removeGroupFromLine(2, 1);
    }

    @Test
    void execute_shouldNotRemoveGroup_whenNotAssigned() {
        ResearchLine line = ResearchLine.builder().id(1).build();

        when(getResearchLineUseCase.execute(1)).thenReturn(line);
        when(repository.isGroupAssignedToLine(2, 1)).thenReturn(false);

        useCase.execute(1, 2);

        verify(repository).isGroupAssignedToLine(2, 1);
        verify(repository, never()).removeGroupFromLine(anyInt(), anyInt());
    }
}
