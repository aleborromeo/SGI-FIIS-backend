package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ListMembersUseCaseTest {

    @Mock
    private ResearchGroupRepositoryPort groupRepository;

    @Mock
    private MembershipRepositoryPort membershipRepository;

    @InjectMocks
    private ListMembersUseCase useCase;

    @Test
    void execute_shouldReturnActiveMembers_whenGroupExists() {
        ResearchGroup group = ResearchGroup.builder().id(1).groupCode("GI-001").build();
        Membership member1 = Membership.builder().id(10).userId(5).groupId(1).active(true).build();
        Membership member2 = Membership.builder().id(11).userId(6).groupId(1).active(true).build();

        given(groupRepository.findById(1)).willReturn(Optional.of(group));
        given(membershipRepository.findActiveByGroup(1)).willReturn(Arrays.asList(member1, member2));

        List<Membership> result = useCase.execute(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(10);
        assertThat(result.get(1).getId()).isEqualTo(11);
    }

    @Test
    void execute_shouldThrowResourceNotFoundException_whenGroupDoesNotExist() {
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
