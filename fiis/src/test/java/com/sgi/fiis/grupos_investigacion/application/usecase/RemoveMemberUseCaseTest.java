package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
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
class RemoveMemberUseCaseTest {

    @Mock
    private MembershipRepositoryPort repository;

    @InjectMocks
    private RemoveMemberUseCase useCase;

    @Test
    void execute_shouldThrowException_whenActiveMembershipDoesNotExist() {
        given(repository.findActiveByUserInGroup(5, 1)).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(1, 5))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void execute_shouldRemoveMember_withEndDateAndActiveFalse() {
        Membership membership = Membership.builder()
                .id(10).groupId(1).userId(5)
                .active(true).startDate(LocalDateTime.now()).build();

        given(repository.findActiveByUserInGroup(5, 1)).willReturn(Optional.of(membership));
        given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Membership result = useCase.execute(1, 5);

        assertThat(result.isActive()).isFalse();
        assertThat(result.getEndDate()).isNotNull();
    }
}
