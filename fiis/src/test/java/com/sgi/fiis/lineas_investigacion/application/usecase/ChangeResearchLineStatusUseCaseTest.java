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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeResearchLineStatusUseCaseTest {

    @Mock
    private ResearchLineRepositoryPort repository;

    @InjectMocks
    private ChangeResearchLineStatusUseCase useCase;

    @Test
    void execute_shouldActivateLine() {
        ResearchLine line = ResearchLine.builder().id(1).lineName("IA").active(false).build();
        given(repository.findById(1)).willReturn(Optional.of(line));
        given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));

        ResearchLine result = useCase.execute(1, true);

        assertThat(result.isActive()).isTrue();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void execute_shouldDeactivateLine() {
        ResearchLine line = ResearchLine.builder().id(1).lineName("IA").active(true).build();
        given(repository.findById(1)).willReturn(Optional.of(line));
        given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));

        ResearchLine result = useCase.execute(1, false);

        assertThat(result.isActive()).isFalse();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void execute_shouldThrowException_whenLineDoesNotExist() {
        given(repository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99, true))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
