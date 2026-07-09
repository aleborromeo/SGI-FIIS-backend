package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.DashboardTeacherResponse;
import com.sgi.fiis.dashboards.domain.model.DashboardTeacher;
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
@DisplayName("Get Teacher Dashboard UseCase Unit Tests")
class GetTeacherDashboardUseCaseTest {

    @Mock
    private DashboardRepositoryPort repository;

    @Mock
    private DashboardMapper mapper;

    @Test
    @DisplayName("Should execute teacher dashboard use case")
    void execute_shouldReturnTeacherDashboardResponse() {
        Integer userId = 4;

        DashboardTeacher model = DashboardTeacher.builder()
                .projectsAsLead(2)
                .projectsAsMember(3)
                .pendingProcedures(1)
                .build();

        DashboardTeacherResponse response = DashboardTeacherResponse.builder()
                .projectsAsLead(2)
                .projectsAsMember(3)
                .pendingProcedures(1)
                .build();

        when(repository.getTeacherDashboard(userId)).thenReturn(model);
        when(mapper.toTeacherResponse(model)).thenReturn(response);

        GetTeacherDashboardUseCase useCase = new GetTeacherDashboardUseCase(repository, mapper);

        DashboardTeacherResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getTeacherDashboard(userId);
        verify(mapper).toTeacherResponse(model);
    }
}