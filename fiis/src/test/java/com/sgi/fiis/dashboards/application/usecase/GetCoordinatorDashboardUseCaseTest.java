package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardCoordinatorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardCoordinator;
import com.sgi.fiis.dashboards.domain.port.DashboardRepositoryPort;
import com.sgi.fiis.dashboards.presentation.mapper.DashboardMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Coordinator Dashboard UseCase Unit Tests")
class GetCoordinatorDashboardUseCaseTest {

    @Mock
    private DashboardRepositoryPort repository;

    @Mock
    private DashboardMapper mapper;

    @Test
    @DisplayName("Should execute coordinator dashboard use case")
    void execute_shouldReturnCoordinatorDashboardResponse() {
        Integer userId = 3;

        DashboardCoordinator model = DashboardCoordinator.builder()
                .groupId(10)
                .groupName("Grupo FIIS")
                .groupCode("GI-FIIS")
                .build();

        DashboardCoordinatorResponse response = DashboardCoordinatorResponse.builder()
                .groupId(10)
                .groupName("Grupo FIIS")
                .groupCode("GI-FIIS")
                .build();

        when(repository.getCoordinatorDashboard(userId)).thenReturn(model);
        when(mapper.toCoordinatorResponse(model)).thenReturn(response);

        GetCoordinatorDashboardUseCase useCase = new GetCoordinatorDashboardUseCase(repository, mapper);

        DashboardCoordinatorResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getCoordinatorDashboard(userId);
        verify(mapper).toCoordinatorResponse(model);
    }
}