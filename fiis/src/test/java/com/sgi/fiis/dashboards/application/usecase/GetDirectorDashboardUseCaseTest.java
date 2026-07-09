package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardDirectorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardDirector;
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
@DisplayName("Get Director Dashboard UseCase Unit Tests")
class GetDirectorDashboardUseCaseTest {

    @Mock
    private DashboardRepositoryPort repository;

    @Mock
    private DashboardMapper mapper;

    @Test
    @DisplayName("Should execute director dashboard use case")
    void execute_shouldReturnDirectorDashboardResponse() {
        Integer userId = 2;

        DashboardDirector model = DashboardDirector.builder()
                .totalProjects(20)
                .activeProjects(12)
                .pendingReviewProcedures(4)
                .build();

        DashboardDirectorResponse response = DashboardDirectorResponse.builder()
                .totalProjects(20)
                .activeProjects(12)
                .pendingReviewProcedures(4)
                .build();

        when(repository.getDirectorDashboard(userId)).thenReturn(model);
        when(mapper.toDirectorResponse(model)).thenReturn(response);

        GetDirectorDashboardUseCase useCase = new GetDirectorDashboardUseCase(repository, mapper);

        DashboardDirectorResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getDirectorDashboard(userId);
        verify(mapper).toDirectorResponse(model);
    }
}