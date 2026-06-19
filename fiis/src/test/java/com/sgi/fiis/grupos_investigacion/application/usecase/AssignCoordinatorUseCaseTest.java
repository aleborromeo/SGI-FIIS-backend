package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
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
class AssignCoordinatorUseCaseTest {

    @Mock
    private ResearchGroupRepositoryPort repository;

    @InjectMocks
    private AssignCoordinatorUseCase useCase;

    @Test
    void execute_shouldThrowException_whenGroupDoesNotExist() {
        given(repository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void execute_shouldThrowException_whenUserNotActive() {
        ResearchGroup group = ResearchGroup.builder().id(1).groupCode("GI-001").build();
        given(repository.findById(1)).willReturn(Optional.of(group));
        given(repository.existsActiveUser(5)).willReturn(false);

        assertThatThrownBy(() -> useCase.execute(1, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("5");
    }

    @Test
    void execute_shouldAssignCoordinator_whenDataIsValid() {
        ResearchGroup group = ResearchGroup.builder()
                .id(1).groupCode("GI-001").groupName("Grupo A").active(true).build();
        ResearchGroup updated = ResearchGroup.builder()
                .id(1).groupCode("GI-001").groupName("Grupo A")
                .currentCoordinatorId(3).coordinatorFirstNames("Juan").coordinatorLastNames("Perez")
                .active(true).build();

        given(repository.findById(1)).willReturn(Optional.of(group));
        given(repository.existsActiveUser(3)).willReturn(true);
        given(repository.save(any())).willReturn(updated);

        ResearchGroup result = useCase.execute(1, 3);

        assertThat(result.getCurrentCoordinatorId()).isEqualTo(3);
        assertThat(result.getCoordinatorFirstNames()).isEqualTo("Juan");
    }
}
