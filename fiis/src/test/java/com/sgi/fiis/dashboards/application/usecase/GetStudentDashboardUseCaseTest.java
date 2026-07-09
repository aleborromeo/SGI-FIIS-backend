package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardStudentResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardStudent;
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
@DisplayName("Get Student Dashboard UseCase Unit Tests")
class GetStudentDashboardUseCaseTest {

    @Mock
    private DashboardRepositoryPort repository;

    @Mock
    private DashboardMapper mapper;

    @Test
    @DisplayName("Should execute student dashboard use case")
    void execute_shouldReturnStudentDashboardResponse() {
        Integer userId = 7;

        DashboardStudent model = DashboardStudent.builder()
                .submittedThesisPlans(1)
                .currentPlanStatus("OBSERVADO")
                .pendingProcedures(2)
                .build();

        DashboardStudentResponse response = DashboardStudentResponse.builder()
                .submittedThesisPlans(1)
                .currentPlanStatus("OBSERVADO")
                .pendingProcedures(2)
                .build();

        when(repository.getStudentDashboard(userId)).thenReturn(model);
        when(mapper.toStudentResponse(model)).thenReturn(response);

        GetStudentDashboardUseCase useCase = new GetStudentDashboardUseCase(repository, mapper);

        DashboardStudentResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getStudentDashboard(userId);
        verify(mapper).toStudentResponse(model);
    }
}