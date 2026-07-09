package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GetResearchLineUseCaseTest {

    @Mock
    private ResearchLineRepositoryPort repository;

    @InjectMocks
    private GetResearchLineUseCase useCase;

    @Test
    void execute_shouldReturnLine_whenExists() {
        ResearchLine line = ResearchLine.builder().id(1).lineName("IA").build();
        given(repository.findById(1)).willReturn(Optional.of(line));

        ResearchLine result = useCase.execute(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getLineName()).isEqualTo("IA");
    }

    @Test
    void execute_shouldThrowException_whenDoesNotExist() {
        given(repository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(ex -> {
                    ResourceNotFoundException notFound = (ResourceNotFoundException) ex;
                    assertThat(notFound.getErrorKey()).isEqualTo("lineas.error.not-found");
                    assertThat(notFound.getArgs()).containsExactly(99);
                });
    }
}
