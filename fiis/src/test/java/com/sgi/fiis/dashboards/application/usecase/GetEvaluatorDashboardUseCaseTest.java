package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardEvaluatorResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardEvaluator;
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
@DisplayName("Get Evaluator Dashboard UseCase Unit Tests")
class GetEvaluatorDashboardUseCaseTest {

    @Mock
    private DashboardRepositoryPort repository;

    @Mock
    private DashboardMapper mapper;

    @Test
    @DisplayName("Should execute evaluator dashboard use case")
    void execute_shouldReturnEvaluatorDashboardResponse() {
        Integer userId = 5;

        DashboardEvaluator model = DashboardEvaluator.builder()
                .assignedEvaluations(10)
                .pendingEvaluations(3)
                .completedEvaluations(7)
                .build();

        DashboardEvaluatorResponse response = DashboardEvaluatorResponse.builder()
                .assignedEvaluations(10)
                .pendingEvaluations(3)
                .completedEvaluations(7)
                .build();

        when(repository.getEvaluatorDashboard(userId)).thenReturn(model);
        when(mapper.toEvaluatorResponse(model)).thenReturn(response);

        GetEvaluatorDashboardUseCase useCase = new GetEvaluatorDashboardUseCase(repository, mapper);

        DashboardEvaluatorResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getEvaluatorDashboard(userId);
        verify(mapper).toEvaluatorResponse(model);
    }
}