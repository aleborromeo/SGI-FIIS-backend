package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetGroupUseCaseTest {

    @Mock
    private ResearchGroupRepositoryPort groupRepository;

    @InjectMocks
    private GetGroupUseCase useCase;

    @Test
    void execute_shouldReturnGroup_whenExists() {
        ResearchGroup group = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-001")
                .groupName("Grupo Investigacion")
                .active(true)
                .build();

        given(groupRepository.findById(1)).willReturn(Optional.of(group));

        ResearchGroup result = useCase.execute(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getGroupCode()).isEqualTo("GI-001");
    }

    @Test
    void execute_shouldThrowResourceNotFoundException_whenDoesNotExist() {
        given(groupRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .satisfies(ex -> {
                    ResourceNotFoundException notFound = (ResourceNotFoundException) ex;
                    assertThat(notFound.getErrorKey()).isEqualTo("grupos.error.not-found");
                    assertThat(notFound.getArgs()).containsExactly(99);
                });
    }
}
