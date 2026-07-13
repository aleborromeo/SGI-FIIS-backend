package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("all")
class ListResearchLinesUseCaseTest {

    @Mock
    private ResearchLineRepositoryPort repository;

    @InjectMocks
    private ListResearchLinesUseCase useCase;

    @Test
    void execute_shouldReturnOnlyActive_whenFlagIsTrue() {
        List<ResearchLine> activeLines = List.of(
                ResearchLine.builder().id(1).lineName("IA").active(true).build()
        );
        given(repository.findAllActive()).willReturn(activeLines);

        List<ResearchLine> result = useCase.execute(true);

        assertThat(result).hasSize(1).allMatch(ResearchLine::isActive);
        then(repository).should().findAllActive();
        then(repository).should(never()).findAll();
    }

    @Test
    void execute_shouldReturnAll_whenFlagIsFalse() {
        List<ResearchLine> allLines = List.of(
                ResearchLine.builder().id(1).lineName("IA").active(true).build(),
                ResearchLine.builder().id(2).lineName("Antigua").active(false).build()
        );
        given(repository.findAll()).willReturn(allLines);

        List<ResearchLine> result = useCase.execute(false);

        assertThat(result).hasSize(2);
        then(repository).should().findAll();
        then(repository).should(never()).findAllActive();
    }
}
