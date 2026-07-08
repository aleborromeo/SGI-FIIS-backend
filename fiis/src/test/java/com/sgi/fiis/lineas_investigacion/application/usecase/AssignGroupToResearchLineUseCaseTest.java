package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.application.usecase.GetGroupUseCase;
import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
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
class AssignGroupToResearchLineUseCaseTest {

    @Mock
    private ResearchGroupLineRepositoryPort repository;

    @Mock
    private GetResearchLineUseCase getResearchLineUseCase;

    @Mock
    private GetGroupUseCase getGroupUseCase;

    @InjectMocks
    private AssignGroupToResearchLineUseCase useCase;

    @Test
    void execute_shouldAssignGroup_whenNotAssigned() {
        ResearchLine line = ResearchLine.builder().id(1).build();
        ResearchGroup group = ResearchGroup.builder().id(2).build();

        when(getResearchLineUseCase.execute(1)).thenReturn(line);
        when(getGroupUseCase.execute(2)).thenReturn(group);
        when(repository.isGroupAssignedToLine(2, 1)).thenReturn(false);

        useCase.execute(1, 2);

        verify(getResearchLineUseCase).execute(1);
        verify(getGroupUseCase).execute(2);
        verify(repository).isGroupAssignedToLine(2, 1);
        verify(repository).assignGroupToLine(2, 1);
    }

    @Test
    void execute_shouldNotAssignGroup_whenAlreadyAssigned() {
        ResearchLine line = ResearchLine.builder().id(1).build();
        ResearchGroup group = ResearchGroup.builder().id(2).build();

        when(getResearchLineUseCase.execute(1)).thenReturn(line);
        when(getGroupUseCase.execute(2)).thenReturn(group);
        when(repository.isGroupAssignedToLine(2, 1)).thenReturn(true);

        useCase.execute(1, 2);

        verify(repository).isGroupAssignedToLine(2, 1);
        verify(repository, never()).assignGroupToLine(anyInt(), anyInt());
    }
}
