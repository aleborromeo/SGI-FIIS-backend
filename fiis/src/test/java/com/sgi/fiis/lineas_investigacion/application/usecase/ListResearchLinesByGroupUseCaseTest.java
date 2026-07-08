package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ListResearchLinesByGroupUseCaseTest {

    @Mock
    private ResearchLineRepositoryPort repository;

    @InjectMocks
    private ListResearchLinesByGroupUseCase useCase;

    @Test
    void execute_shouldReturnActiveLinesForGroup() {
        ResearchLine line = ResearchLine.builder().id(1).lineName("IA").active(true).build();
        given(repository.findActiveByGroup(10)).willReturn(List.of(line));

        List<ResearchLine> result = useCase.execute(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLineName()).isEqualTo("IA");
    }

    @Test
    void execute_shouldReturnEmptyList_whenGroupHasNoLines() {
        given(repository.findActiveByGroup(99)).willReturn(Collections.emptyList());

        List<ResearchLine> result = useCase.execute(99);

        assertThat(result).isEmpty();
    }
}
