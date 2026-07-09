package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AssignMemberUseCaseTest {

    @Mock
    private ResearchGroupRepositoryPort groupRepository;

    @Mock
    private MembershipRepositoryPort membershipRepository;

    @InjectMocks
    private AssignMemberUseCase useCase;

    @Test
    void execute_shouldThrowException_whenGroupDoesNotExist() {
        given(groupRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void execute_shouldThrowException_whenUserNotActive() {
        given(groupRepository.findById(1)).willReturn(Optional.of(
                ResearchGroup.builder().id(1).build()));
        given(groupRepository.existsActiveUserWithRole(5, "DOCENTE_INVESTIGADOR")).willReturn(false);

        assertThatThrownBy(() -> useCase.execute(1, 5))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException business = (BusinessException) ex;
                    assertThat(business.getErrorKey()).isEqualTo("grupos.error.member-invalid-role");
                    assertThat(business.getArgs()).containsExactly(5);
                });
    }

    @Test
    void execute_shouldThrowException_whenUserAlreadyHasMembership_RF21() {
        given(groupRepository.findById(1)).willReturn(Optional.of(
                ResearchGroup.builder().id(1).build()));
        given(groupRepository.existsActiveUserWithRole(2, "DOCENTE_INVESTIGADOR")).willReturn(true);
        given(membershipRepository.existsActiveByUser(2)).willReturn(true);

        assertThatThrownBy(() -> useCase.execute(1, 2))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException business = (BusinessException) ex;
                    assertThat(business.getErrorKey()).isEqualTo("grupos.error.member-already-active");
                    assertThat(business.getArgs()).containsExactly(2);
                });
    }

    @Test
    void execute_shouldCreateMembership_whenDataIsValid() {
        Membership saved = Membership.builder()
                .id(10).groupId(1).userId(2).active(true)
                .startDate(LocalDateTime.now()).build();

        given(groupRepository.findById(1)).willReturn(Optional.of(
                ResearchGroup.builder().id(1).build()));
        given(groupRepository.existsActiveUserWithRole(2, "DOCENTE_INVESTIGADOR")).willReturn(true);
        given(membershipRepository.existsActiveByUser(2)).willReturn(false);
        given(membershipRepository.save(any())).willReturn(saved);

        Membership result = useCase.execute(1, 2);

        assertThat(result.isActive()).isTrue();
        assertThat(result.getGroupId()).isEqualTo(1);
        assertThat(result.getUserId()).isEqualTo(2);
        assertThat(result.getStartDate()).isNotNull();
    }
}
