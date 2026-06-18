package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterResearchLineUseCaseTest {

    @Mock
    private ResearchLineRepositoryPort repository;

    @InjectMocks
    private RegisterResearchLineUseCase useCase;

    @Test
    void execute_shouldThrowException_whenNameAlreadyExists() {
        ResearchLine input = ResearchLine.builder().lineName("IA Aplicada").build();
        given(repository.existsByName("IA Aplicada")).willReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("IA Aplicada");

        then(repository).should(never()).save(any());
    }

    @Test
    void execute_shouldSaveLine_withActiveStatusAndDates() {
        ResearchLine input = ResearchLine.builder().lineName("Robótica").build();
        ResearchLine saved = ResearchLine.builder()
                .id(1)
                .lineName("Robótica")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(repository.existsByName("Robótica")).willReturn(false);
        given(repository.save(any())).willReturn(saved);

        ResearchLine result = useCase.execute(input);

        assertThat(result.isActive()).isTrue();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        then(repository).should().save(any(ResearchLine.class));
    }
}
