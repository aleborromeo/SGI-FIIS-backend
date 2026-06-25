package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ListGroupsUseCaseTest {

    @Mock
    private ResearchGroupRepositoryPort groupRepository;

    @InjectMocks
    private ListGroupsUseCase useCase;

    @Test
    void execute_shouldReturnListOfGroups() {
        ResearchGroup group1 = ResearchGroup.builder().id(1).groupCode("GI-001").build();
        ResearchGroup group2 = ResearchGroup.builder().id(2).groupCode("GI-002").build();
        given(groupRepository.findAll()).willReturn(Arrays.asList(group1, group2));

        List<ResearchGroup> result = useCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(2);
    }
}
