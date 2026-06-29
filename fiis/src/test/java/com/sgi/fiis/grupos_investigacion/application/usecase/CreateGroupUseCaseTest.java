package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGroupUseCaseTest {

    @Mock
    private ResearchGroupRepositoryPort repository;

    @InjectMocks
    private CreateGroupUseCase useCase;

    @Test
    void execute_shouldThrowException_whenCodeAlreadyExists() {
        ResearchGroup input = ResearchGroup.builder()
                .groupCode("GI-001").groupName("Grupo Test").build();
        given(repository.existsByCode("GI-001")).willReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("GI-001");

        then(repository).should(never()).save(any());
    }

    @Test
    void execute_shouldSaveGroup_withActiveStatus() {
        ResearchGroup input = ResearchGroup.builder()
                .groupCode("GI-002").groupName("Nuevo Grupo").build();
        ResearchGroup saved = ResearchGroup.builder()
                .id(1).groupCode("GI-002").groupName("Nuevo Grupo").active(true).build();

        given(repository.existsByCode("GI-002")).willReturn(false);
        given(repository.save(any())).willReturn(saved);

        ResearchGroup result = useCase.execute(input);

        assertThat(result.isActive()).isTrue();
        assertThat(result.getId()).isEqualTo(1);
        then(repository).should().save(any(ResearchGroup.class));
    }
}
