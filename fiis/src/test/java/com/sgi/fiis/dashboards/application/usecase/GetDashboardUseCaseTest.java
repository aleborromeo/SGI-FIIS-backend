package com.sgi.fiis.dashboards.application.usecase;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.domain.model.*;
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
@DisplayName("Get Dashboard UseCase Unit Tests")
class GetDashboardUseCaseTest {

    @Mock
    private DashboardRepositoryPort repository;

    @Mock
    private DashboardMapper mapper;

    @Test
    @DisplayName("Should execute admin dashboard usecase")
    void executeAdmin_shouldReturnResponse() {
        Integer userId = 1;
        DashboardAdmin model = DashboardAdmin.builder().totalUsers(10).build();
        DashboardAdminResponse response = DashboardAdminResponse.builder().totalUsers(10).build();

        when(repository.getAdminDashboard(userId)).thenReturn(model);
        when(mapper.toAdminResponse(model)).thenReturn(response);

        GetAdminDashboardUseCase useCase = new GetAdminDashboardUseCase(repository, mapper);
        DashboardAdminResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getAdminDashboard(userId);
        verify(mapper).toAdminResponse(model);
    }

    @Test
    @DisplayName("Should execute director dashboard usecase")
    void executeDirector_shouldReturnResponse() {
        Integer userId = 2;
        DashboardDirector model = DashboardDirector.builder().totalProjects(20).build();
        DashboardDirectorResponse response = DashboardDirectorResponse.builder().totalProjects(20).build();

        when(repository.getDirectorDashboard(userId)).thenReturn(model);
        when(mapper.toDirectorResponse(model)).thenReturn(response);

        GetDirectorDashboardUseCase useCase = new GetDirectorDashboardUseCase(repository, mapper);
        DashboardDirectorResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getDirectorDashboard(userId);
        verify(mapper).toDirectorResponse(model);
    }

    @Test
    @DisplayName("Should execute coordinator dashboard usecase")
    void executeCoordinator_shouldReturnResponse() {
        Integer userId = 3;
        DashboardCoordinator model = DashboardCoordinator.builder().groupId(5).build();
        DashboardCoordinatorResponse response = DashboardCoordinatorResponse.builder().groupId(5).build();

        when(repository.getCoordinatorDashboard(userId)).thenReturn(model);
        when(mapper.toCoordinatorResponse(model)).thenReturn(response);

        GetCoordinatorDashboardUseCase useCase = new GetCoordinatorDashboardUseCase(repository, mapper);
        DashboardCoordinatorResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getCoordinatorDashboard(userId);
        verify(mapper).toCoordinatorResponse(model);
    }

    @Test
    @DisplayName("Should execute teacher dashboard usecase")
    void executeTeacher_shouldReturnResponse() {
        Integer userId = 4;
        DashboardTeacher model = DashboardTeacher.builder().projectsAsLead(2).build();
        DashboardTeacherResponse response = DashboardTeacherResponse.builder().projectsAsLead(2).build();

        when(repository.getTeacherDashboard(userId)).thenReturn(model);
        when(mapper.toTeacherResponse(model)).thenReturn(response);

        GetTeacherDashboardUseCase useCase = new GetTeacherDashboardUseCase(repository, mapper);
        DashboardTeacherResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getTeacherDashboard(userId);
        verify(mapper).toTeacherResponse(model);
    }

    @Test
    @DisplayName("Should execute evaluator dashboard usecase")
    void executeEvaluator_shouldReturnResponse() {
        Integer userId = 5;
        DashboardEvaluator model = DashboardEvaluator.builder().assignedEvaluations(8).build();
        DashboardEvaluatorResponse response = DashboardEvaluatorResponse.builder().assignedEvaluations(8).build();

        when(repository.getEvaluatorDashboard(userId)).thenReturn(model);
        when(mapper.toEvaluatorResponse(model)).thenReturn(response);

        GetEvaluatorDashboardUseCase useCase = new GetEvaluatorDashboardUseCase(repository, mapper);
        DashboardEvaluatorResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getEvaluatorDashboard(userId);
        verify(mapper).toEvaluatorResponse(model);
    }

    @Test
    @DisplayName("Should execute dean dashboard usecase")
    void executeDean_shouldReturnResponse() {
        Integer userId = 6;
        DashboardDean model = DashboardDean.builder().totalFacultyProjects(15).build();
        DashboardDeanResponse response = DashboardDeanResponse.builder().totalFacultyProjects(15).build();

        when(repository.getDeanDashboard(userId)).thenReturn(model);
        when(mapper.toDeanResponse(model)).thenReturn(response);

        GetDeanDashboardUseCase useCase = new GetDeanDashboardUseCase(repository, mapper);
        DashboardDeanResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getDeanDashboard(userId);
        verify(mapper).toDeanResponse(model);
    }

    @Test
    @DisplayName("Should execute student dashboard usecase")
    void executeStudent_shouldReturnResponse() {
        Integer userId = 7;
        DashboardStudent model = DashboardStudent.builder().submittedThesisPlans(1).build();
        DashboardStudentResponse response = DashboardStudentResponse.builder().submittedThesisPlans(1).build();

        when(repository.getStudentDashboard(userId)).thenReturn(model);
        when(mapper.toStudentResponse(model)).thenReturn(response);

        GetStudentDashboardUseCase useCase = new GetStudentDashboardUseCase(repository, mapper);
        DashboardStudentResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(repository).getStudentDashboard(userId);
        verify(mapper).toStudentResponse(model);
    }
}
