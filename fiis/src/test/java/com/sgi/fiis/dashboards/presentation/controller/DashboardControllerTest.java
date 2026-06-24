package com.sgi.fiis.dashboards.presentation.controller;

import com.sgi.fiis.dashboards.application.dto.*;
import com.sgi.fiis.dashboards.application.usecase.*;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardController Unit Tests")
class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GetAdminDashboardUseCase getAdminDashboardUseCase;

    @Mock
    private GetDirectorDashboardUseCase getDirectorDashboardUseCase;

    @Mock
    private GetCoordinatorDashboardUseCase getCoordinatorDashboardUseCase;

    @Mock
    private GetTeacherDashboardUseCase getTeacherDashboardUseCase;

    @Mock
    private GetEvaluatorDashboardUseCase getEvaluatorDashboardUseCase;

    @Mock
    private GetDeanDashboardUseCase getDeanDashboardUseCase;

    @Mock
    private GetStudentDashboardUseCase getStudentDashboardUseCase;

    @BeforeEach
    void setUp() {
        DashboardController controller = new DashboardController(
                getAdminDashboardUseCase,
                getDirectorDashboardUseCase,
                getCoordinatorDashboardUseCase,
                getTeacherDashboardUseCase,
                getEvaluatorDashboardUseCase,
                getDeanDashboardUseCase,
                getStudentDashboardUseCase
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    private UsernamePasswordAuthenticationToken getMockAuth(Long id, String role) {
        CustomUserDetails userDetails = new CustomUserDetails(
                id,
                "test@unas.edu.pe",
                "password",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/me should return correct dashboard for logged-in role")
    void getDashboardMe_shouldReturnCorrectRoleDashboard() throws Exception {
        DashboardStudentResponse studentResponse = DashboardStudentResponse.builder()
                .submittedThesisPlans(1)
                .currentPlanStatus("POSTULADO")
                .groupName("Grupo Tesis")
                .groupCode("GT-01")
                .alerts(List.of())
                .build();

        when(getStudentDashboardUseCase.execute(7)).thenReturn(studentResponse);

        mockMvc.perform(get("/api/v1/dashboard/me")
                        .principal(getMockAuth(7L, "ESTUDIANTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submittedThesisPlans").value(1))
                .andExpect(jsonPath("$.currentPlanStatus").value("POSTULADO"))
                .andExpect(jsonPath("$.groupName").value("Grupo Tesis"));
    }

    @Test
    @DisplayName("GET /api/dashboard/admin/{userId} should return admin dashboard")
    void getDashboardAdmin_shouldReturnOk() throws Exception {
        DashboardAdminResponse response = DashboardAdminResponse.builder()
                .totalUsers(10)
                .totalActiveUsers(8)
                .totalGroups(3)
                .totalActiveGroups(2)
                .totalProjects(5)
                .activeProjects(4)
                .pendingProcedures(6)
                .issuedResolutions(7)
                .proceduresUnderReview(1)
                .approvedProcedures(2)
                .rejectedProcedures(3)
                .alerts(List.of(
                        AlertItemResponse.builder()
                                .type("REVIEW")
                                .title("Pending procedures")
                                .description("There are pending procedures")
                                .build()
                ))
                .build();

        when(getAdminDashboardUseCase.execute(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/admin/{userId}", 1)
                        .principal(getMockAuth(1L, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.totalActiveUsers").value(8))
                .andExpect(jsonPath("$.totalGroups").value(3))
                .andExpect(jsonPath("$.totalActiveGroups").value(2))
                .andExpect(jsonPath("$.totalProjects").value(5))
                .andExpect(jsonPath("$.activeProjects").value(4))
                .andExpect(jsonPath("$.pendingProcedures").value(6))
                .andExpect(jsonPath("$.issuedResolutions").value(7))
                .andExpect(jsonPath("$.proceduresUnderReview").value(1))
                .andExpect(jsonPath("$.approvedProcedures").value(2))
                .andExpect(jsonPath("$.rejectedProcedures").value(3))
                .andExpect(jsonPath("$.alerts", hasSize(1)))
                .andExpect(jsonPath("$.alerts[0].type").value("REVIEW"))
                .andExpect(jsonPath("$.alerts[0].title").value("Pending procedures"))
                .andExpect(jsonPath("$.alerts[0].description").value("There are pending procedures"));
    }

    @Test
    @DisplayName("GET /api/dashboard/director/{userId} should return director dashboard")
    void getDashboardDirector_shouldReturnOk() throws Exception {
        DashboardDirectorResponse response = DashboardDirectorResponse.builder()
                .totalProjects(20)
                .activeProjects(12)
                .submittedProjects(5)
                .observedProjects(2)
                .pendingReviewProcedures(4)
                .reportsNearingDeadline(3)
                .issuedResolutions(9)
                .openCallsForApplication(1)
                .proceduresWithCoordinator(2)
                .proceduresWithDirector(3)
                .proceduresWithDean(4)
                .completedProcedures(5)
                .alerts(List.of())
                .build();

        when(getDirectorDashboardUseCase.execute(2)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/director/{userId}", 2)
                        .principal(getMockAuth(2L, "DIRECTOR_INVESTIGACION")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjects").value(20))
                .andExpect(jsonPath("$.activeProjects").value(12))
                .andExpect(jsonPath("$.submittedProjects").value(5))
                .andExpect(jsonPath("$.observedProjects").value(2))
                .andExpect(jsonPath("$.pendingReviewProcedures").value(4))
                .andExpect(jsonPath("$.reportsNearingDeadline").value(3))
                .andExpect(jsonPath("$.issuedResolutions").value(9))
                .andExpect(jsonPath("$.openCallsForApplication").value(1))
                .andExpect(jsonPath("$.proceduresWithCoordinator").value(2))
                .andExpect(jsonPath("$.proceduresWithDirector").value(3))
                .andExpect(jsonPath("$.proceduresWithDean").value(4))
                .andExpect(jsonPath("$.completedProcedures").value(5))
                .andExpect(jsonPath("$.alerts", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/coordinador/{userId} should return coordinator dashboard")
    void getDashboardCoordinator_shouldReturnOk() throws Exception {
        DashboardCoordinatorResponse response = DashboardCoordinatorResponse.builder()
                .groupId(3)
                .groupName("Grupo FIIS")
                .groupCode("GI-FIIS")
                .totalMembers(8)
                .activeMembers(7)
                .totalGroupProjects(4)
                .activeGroupProjects(3)
                .pendingGroupProcedures(2)
                .groupProgressReports(5)
                .groupThesisPlans(6)
                .submittedProcedures(1)
                .proceduresUnderReview(2)
                .approvedProcedures(3)
                .observedProcedures(4)
                .alerts(List.of())
                .build();

        when(getCoordinatorDashboardUseCase.execute(3)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/coordinator/{userId}", 3)
                        .principal(getMockAuth(3L, "COORDINADOR_GRUPO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupId").value(3))
                .andExpect(jsonPath("$.groupName").value("Grupo FIIS"))
                .andExpect(jsonPath("$.groupCode").value("GI-FIIS"))
                .andExpect(jsonPath("$.totalMembers").value(8))
                .andExpect(jsonPath("$.activeMembers").value(7))
                .andExpect(jsonPath("$.totalGroupProjects").value(4))
                .andExpect(jsonPath("$.activeGroupProjects").value(3))
                .andExpect(jsonPath("$.pendingGroupProcedures").value(2))
                .andExpect(jsonPath("$.groupProgressReports").value(5))
                .andExpect(jsonPath("$.groupThesisPlans").value(6))
                .andExpect(jsonPath("$.submittedProcedures").value(1))
                .andExpect(jsonPath("$.proceduresUnderReview").value(2))
                .andExpect(jsonPath("$.approvedProcedures").value(3))
                .andExpect(jsonPath("$.observedProcedures").value(4))
                .andExpect(jsonPath("$.alerts", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/docente/{userId} should return teacher dashboard")
    void getDashboardTeacher_shouldReturnOk() throws Exception {
        DashboardTeacherResponse response = DashboardTeacherResponse.builder()
                .projectsAsLead(2)
                .projectsAsMember(4)
                .pendingProcedures(1)
                .pendingProgressReports(3)
                .uploadedDocuments(6)
                .receivedResolutions(5)
                .submittedProjects(1)
                .approvedProjects(2)
                .projectsInExecution(3)
                .completedProjects(4)
                .alerts(List.of())
                .build();

        when(getTeacherDashboardUseCase.execute(4)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/teacher/{userId}", 4)
                        .principal(getMockAuth(4L, "DOCENTE_INVESTIGADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectsAsLead").value(2))
                .andExpect(jsonPath("$.projectsAsMember").value(4))
                .andExpect(jsonPath("$.pendingProcedures").value(1))
                .andExpect(jsonPath("$.pendingProgressReports").value(3))
                .andExpect(jsonPath("$.uploadedDocuments").value(6))
                .andExpect(jsonPath("$.receivedResolutions").value(5))
                .andExpect(jsonPath("$.submittedProjects").value(1))
                .andExpect(jsonPath("$.approvedProjects").value(2))
                .andExpect(jsonPath("$.projectsInExecution").value(3))
                .andExpect(jsonPath("$.completedProjects").value(4))
                .andExpect(jsonPath("$.alerts", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/evaluador/{userId} should return evaluator dashboard")
    void getDashboardEvaluator_shouldReturnOk() throws Exception {
        DashboardEvaluatorResponse response = DashboardEvaluatorResponse.builder()
                .assignedEvaluations(10)
                .pendingEvaluations(3)
                .completedEvaluations(7)
                .assignedProjects(4)
                .assignedThesisPlans(5)
                .approvedEvaluations(2)
                .rejectedEvaluations(1)
                .evaluationsWithObservations(6)
                .alerts(List.of())
                .build();

        when(getEvaluatorDashboardUseCase.execute(5)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/evaluator/{userId}", 5)
                        .principal(getMockAuth(5L, "EVALUADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedEvaluations").value(10))
                .andExpect(jsonPath("$.pendingEvaluations").value(3))
                .andExpect(jsonPath("$.completedEvaluations").value(7))
                .andExpect(jsonPath("$.assignedProjects").value(4))
                .andExpect(jsonPath("$.assignedThesisPlans").value(5))
                .andExpect(jsonPath("$.approvedEvaluations").value(2))
                .andExpect(jsonPath("$.rejectedEvaluations").value(1))
                .andExpect(jsonPath("$.evaluationsWithObservations").value(6))
                .andExpect(jsonPath("$.alerts", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/decano/{userId} should return dean dashboard")
    void getDashboardDean_shouldReturnOk() throws Exception {
        DashboardDeanResponse response = DashboardDeanResponse.builder()
                .totalFacultyProjects(30)
                .activeProjects(15)
                .pendingSignatureProcedures(4)
                .issuedResolutions(8)
                .activeCallsForApplication(2)
                .totalActiveGroups(6)
                .waitingProcedures(3)
                .approvedProceduresThisMonth(5)
                .rejectedProceduresThisMonth(1)
                .alerts(List.of())
                .build();

        when(getDeanDashboardUseCase.execute(6)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/dean/{userId}", 6)
                        .principal(getMockAuth(6L, "DECANO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFacultyProjects").value(30))
                .andExpect(jsonPath("$.activeProjects").value(15))
                .andExpect(jsonPath("$.pendingSignatureProcedures").value(4))
                .andExpect(jsonPath("$.issuedResolutions").value(8))
                .andExpect(jsonPath("$.activeCallsForApplication").value(2))
                .andExpect(jsonPath("$.totalActiveGroups").value(6))
                .andExpect(jsonPath("$.waitingProcedures").value(3))
                .andExpect(jsonPath("$.approvedProceduresThisMonth").value(5))
                .andExpect(jsonPath("$.rejectedProceduresThisMonth").value(1))
                .andExpect(jsonPath("$.alerts", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/estudiante/{userId} should return student dashboard")
    void getDashboardStudent_shouldReturnOk() throws Exception {
        DashboardStudentResponse response = DashboardStudentResponse.builder()
                .submittedThesisPlans(1)
                .currentPlanStatus("OBSERVADO")
                .pendingProcedures(2)
                .uploadedDocuments(3)
                .openCallsForApplication(4)
                .groupName("Grupo Tesis")
                .groupCode("GT-01")
                .alerts(List.of())
                .build();

        when(getStudentDashboardUseCase.execute(7)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/student/{userId}", 7)
                        .principal(getMockAuth(7L, "ESTUDIANTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submittedThesisPlans").value(1))
                .andExpect(jsonPath("$.currentPlanStatus").value("OBSERVADO"))
                .andExpect(jsonPath("$.pendingProcedures").value(2))
                .andExpect(jsonPath("$.uploadedDocuments").value(3))
                .andExpect(jsonPath("$.openCallsForApplication").value(4))
                .andExpect(jsonPath("$.groupName").value("Grupo Tesis"))
                .andExpect(jsonPath("$.groupCode").value("GT-01"))
                .andExpect(jsonPath("$.alerts", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard/dean/{userId} with incorrect role should throw AccessDeniedException")
    void getDashboardDean_withIncorrectRole_shouldThrowAccessDeniedException() {
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/v1/dashboard/dean/{userId}", 6)
                            .principal(getMockAuth(6L, "ESTUDIANTE")));
        });
    }

    @Test
    @DisplayName("GET /api/dashboard/dean/{userId} with different user ID should throw AccessDeniedException")
    void getDashboardDean_withDifferentUser_shouldThrowAccessDeniedException() {
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/v1/dashboard/dean/{userId}", 99)
                            .principal(getMockAuth(6L, "DECANO")));
        });
    }

    @Test
    @DisplayName("GET /api/dashboard/dean/{userId} with null authentication should throw AccessDeniedException")
    void getDashboardDean_withNullAuth_shouldThrowAccessDeniedException() {
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/v1/dashboard/dean/{userId}", 6));
        });
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/me with null authentication should return 401 Unauthorized")
    void getDashboardMe_withNullAuth_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/me with unsupported role should return 403 Forbidden")
    void getDashboardMe_withUnsupportedRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/me")
                        .principal(getMockAuth(10L, "INVITADO")))
                .andExpect(status().isForbidden());
    }
}