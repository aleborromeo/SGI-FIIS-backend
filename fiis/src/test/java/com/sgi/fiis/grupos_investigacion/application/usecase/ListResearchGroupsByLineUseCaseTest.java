package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class ListResearchGroupsByLineUseCaseTest {

    @Mock
    private ResearchGroupRepositoryPort repository;

    @InjectMocks
    private ListResearchGroupsByLineUseCase useCase;

    @Test
    void execute_shouldReturnListOfGroups() {
        ResearchGroup group = ResearchGroup.builder().id(1).groupName("Group 1").build();
        when(repository.findGroupsByLineId(anyInt())).thenReturn(List.of(group));

        List<ResearchGroup> result = useCase.execute(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        verify(repository).findGroupsByLineId(10);
    }
}
