package com.sgi.fiis.proyectos;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.out.CreateProcedurePort;
import com.sgi.fiis.proyectos.application.ports.out.SaveProjectPort;
import com.sgi.fiis.proyectos.application.usecases.CreateProjectInteractor;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectModuleTest {

    // Fixed dates to avoid system clock usage in tests (SonarCloud S5977)
    private static final LocalDate FIXED_TODAY     = LocalDate.of(2026, Month.JUNE, 1);
    private static final LocalDate FIXED_PAST_1D   = LocalDate.of(2026, Month.MAY, 31);
    private static final LocalDate FIXED_FUTURE_6M = LocalDate.of(2026, Month.DECEMBER, 1);
    private static final LocalDate FIXED_FUTURE_1M = LocalDate.of(2026, Month.JULY, 1);
    private static final LocalDate FIXED_FUTURE_10 = LocalDate.of(2026, Month.JUNE, 11);

    private SaveProjectPort saveProjectPort;
    private SaveCallPort saveCallPort;
    private CreateProcedurePort createProcedurePort;
    private CreateProjectInteractor createProjectInteractor;

    @BeforeEach
    void setup() {
        saveProjectPort = Mockito.mock(SaveProjectPort.class);
        saveCallPort = Mockito.mock(SaveCallPort.class);
        createProcedurePort = Mockito.mock(CreateProcedurePort.class);
        createProjectInteractor = new CreateProjectInteractor(saveProjectPort, saveCallPort, createProcedurePort);
    }

    @Test
    void shouldCreateProjectSuccessfully() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("New Tech Project");
        request.setSummary("Summary of tech project");
        request.setGeneralObjective("Objective of tech project");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("1500.00"));
        request.setStartDate(FIXED_TODAY);
        request.setEndDate(FIXED_FUTURE_6M);
        request.setExecutionPlace("FIIS Lab");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);
        request.setCallId(4);

        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(true);
        when(saveProjectPort.isLineActive(1)).thenReturn(true);
        when(saveProjectPort.getGroupCode(2)).thenReturn(Optional.of("GINSOFT"));
        when(saveProjectPort.getLineName(1)).thenReturn(Optional.of("Computacion"));

        ResearchCall call = new ResearchCall(4, "Call 2026", "Description", FIXED_PAST_1D, FIXED_FUTURE_1M, CallStatus.OPEN, null, null);
        when(saveCallPort.findById(4)).thenReturn(Optional.of(call));

        Project savedProject = new Project(
                1, "PRJ-2026-XYZ", "New Tech Project", "Summary of tech project", "Objective of tech project",
                1, "Computacion", new BigDecimal("1500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "FIIS Lab", 3L, 2, "GINSOFT", 4, null, ProjectStatus.POSTULATED
        );
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("PRJ-2026-XYZ", response.getCode());
        assertEquals("POSTULADO", response.getStatus());
        verify(createProcedurePort, times(1)).createPostulationProcedure(any(Project.class));
    }

    @Test
    void shouldFailWhenResearchGroupNotActive() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Title");
        request.setSummary("Summary");
        request.setGeneralObjective("Objective");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("100.00"));
        request.setStartDate(FIXED_TODAY);
        request.setEndDate(FIXED_FUTURE_10);
        request.setExecutionPlace("Place");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);

        when(saveProjectPort.isGroupActive(2)).thenReturn(false);

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.execute(request));
    }

    @Test
    void shouldFailWhenResponsibleNotMemberOfGroup() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Title");
        request.setSummary("Summary");
        request.setGeneralObjective("Objective");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("100.00"));
        request.setStartDate(FIXED_TODAY);
        request.setEndDate(FIXED_FUTURE_10);
        request.setExecutionPlace("Place");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);

        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(false);

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.execute(request));
    }

    @Test
    void shouldFailWhenBudgetIsZeroOrNegative() {
        Project project = new Project(
                1, "PRJ-X", "Title", "Summary", "Objective",
                1, "Computacion", new BigDecimal("0.00"), FIXED_TODAY, FIXED_FUTURE_10,
                "Place", 3L, 1, "GINSOFT", 1, 1, ProjectStatus.POSTULATED
        );

        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void shouldApplyGinsoftLineRestrictionCorrectly() {
        // GINSOFT group restricts to 'Computacion' or 'Ingenieria de software'
        Project validProject = new Project(
                1, "PRJ-X", "Title", "Summary", "Objective",
                1, "Computacion", new BigDecimal("500.00"), FIXED_TODAY, FIXED_FUTURE_10,
                "Place", 3L, 1, "GINSOFT", 1, 1, ProjectStatus.POSTULATED
        );
        assertDoesNotThrow(validProject::validateInvariants);

        Project invalidProject = new Project(
                1, "PRJ-Y", "Title", "Summary", "Objective",
                1, "Ciberseguridad", new BigDecimal("500.00"), FIXED_TODAY, FIXED_FUTURE_10,
                "Place", 3L, 1, "GINSOFT", 1, 1, ProjectStatus.POSTULATED
        );
        assertThrows(BusinessRuleValidationException.class, invalidProject::validateInvariants);
    }

    @Test
    void testGetProjectsByResponsible() {
        Project p = Project.builder().id(1).status(ProjectStatus.POSTULATED).build();
        when(saveProjectPort.findByResponsibleId(3L)).thenReturn(Collections.singletonList(p));
        List<ProjectResponse> res = createProjectInteractor.getProjectsByResponsible(3L);
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).getId());
        assertEquals("POSTULADO", res.get(0).getStatus());
    }

    @Test
    void getProjectsByGroup_Success() {
        Project project = Project.builder()
                .id(2)
                .status(ProjectStatus.APPROVED)
                .build();
        when(saveProjectPort.findByGroupId(5)).thenReturn(Collections.singletonList(project));
        
        List<ProjectResponse> responses = createProjectInteractor.getProjectsByGroup(5);
        assertEquals(1, responses.size());
        assertEquals("APROBADO", responses.get(0).getStatus());
    }

    @Test
    void getAllProjects_Success() {
        Project project1 = Project.builder()
                .id(3)
                .status(ProjectStatus.REJECTED)
                .build();
        Project project2 = Project.builder()
                .id(4)
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project project3 = Project.builder()
                .id(5)
                .status(ProjectStatus.COMPLETED)
                .build();
        when(saveProjectPort.findAll()).thenReturn(List.of(project1, project2, project3));
        
        List<ProjectResponse> responses = createProjectInteractor.getAllProjects();
        assertEquals(3, responses.size());
        assertEquals("RECHAZADO", responses.get(0).getStatus());
        assertEquals("EN_EJECUCION", responses.get(1).getStatus());
        assertEquals("FINALIZADO", responses.get(2).getStatus());
    }

    @Test
    void testGetProjectByIdFound() {
        Project p = Project.builder().id(1).status(ProjectStatus.POSTULATED).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        ProjectResponse res = createProjectInteractor.getProjectById(1);
        assertEquals(1, res.getId());
    }

    @Test
    void testGetProjectByIdNotFound() {
        when(saveProjectPort.findById(99)).thenReturn(Optional.empty());
        assertThrows(BusinessRuleValidationException.class, () -> createProjectInteractor.getProjectById(99));
    }
    
    @Test
    void validateInvariants_WhenBudgetIsZero_ThrowsException() {
        Project project = Project.builder()
                .title("Test")
                .budget(BigDecimal.ZERO)
                .startDate(LocalDate.of(2026, Month.JANUARY, 1))
                .endDate(LocalDate.of(2026, Month.JANUARY, 10))
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void validateInvariants_WhenStartDateAfterEndDate_ThrowsException() {
        Project project = Project.builder()
                .title("Test")
                .budget(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, Month.JANUARY, 10))
                .endDate(LocalDate.of(2026, Month.JANUARY, 1))
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void validateInvariants_WhenTitleIsEmpty_ThrowsException() {
        Project project = Project.builder()
                .title("  ")
                .budget(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, Month.JANUARY, 1))
                .endDate(LocalDate.of(2026, Month.JANUARY, 10))
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void validateInvariants_WhenGinsoftInvalidLine_ThrowsException() {
        Project project = Project.builder()
                .title("Test")
                .budget(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, Month.JANUARY, 1))
                .endDate(LocalDate.of(2026, Month.JANUARY, 10))
                .researchGroupCode("GINSOFT")
                .researchLineName("Telecomunicaciones")
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void validateInvariants_WhenGinsoftValidLine_Success() {
        Project project = Project.builder()
                .title("Test")
                .budget(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, Month.JANUARY, 1))
                .endDate(LocalDate.of(2026, Month.JANUARY, 10))
                .researchGroupCode("GINSOFT")
                .researchLineName("Computacion")
                .build();
        assertDoesNotThrow(project::validateInvariants);
    }
}