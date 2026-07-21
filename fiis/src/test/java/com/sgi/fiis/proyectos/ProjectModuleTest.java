package com.sgi.fiis.proyectos;

import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.MemberRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.out.CreateProcedurePort;
import com.sgi.fiis.proyectos.application.ports.out.SaveProjectPort;
import com.sgi.fiis.proyectos.application.usecases.CreateProjectInteractor;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored", "ThrowableResultOfMethodCallIgnored"})
class ProjectModuleTest {

    // Fixed dates to avoid system clock usage in tests (SonarCloud S5977)
    private static final LocalDate FIXED_TODAY     = LocalDate.of(2026, Month.JUNE, 1);
    private static final LocalDate FIXED_FUTURE_6M = LocalDate.of(2026, Month.DECEMBER, 1);
    private static final LocalDate FIXED_FUTURE_10 = LocalDate.of(2026, Month.JUNE, 11);
    private static final LocalDate CALL_OPEN_START = LocalDate.of(2000, Month.JANUARY, 1);
    private static final LocalDate CALL_OPEN_END   = LocalDate.of(2100, Month.DECEMBER, 31);

    private SaveProjectPort saveProjectPort;
    private SaveCallPort saveCallPort;
    private CreateProcedurePort createProcedurePort;
    private UserRepositoryPort userRepositoryPort;
    private CreateProjectInteractor createProjectInteractor;
    private Clock fixedClock;

    @BeforeEach
    void setup() {
        saveProjectPort = mock(SaveProjectPort.class);
        saveCallPort = mock(SaveCallPort.class);
        createProcedurePort = mock(CreateProcedurePort.class);
        userRepositoryPort = mock(UserRepositoryPort.class);
        fixedClock = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneId.of("UTC"));
        createProjectInteractor = new CreateProjectInteractor(saveProjectPort, saveCallPort, createProcedurePort, userRepositoryPort, fixedClock);
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

        ResearchCall call = new ResearchCall(4, "Call 2026", "Description", CALL_OPEN_START, CALL_OPEN_END, CallStatus.OPEN,
                null, null);
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

    @Test
    void testUpdateStatus_Success() {
        Project existing = Project.builder().id(1).status(ProjectStatus.POSTULATED).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(existing));
        when(saveProjectPort.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectResponse response = createProjectInteractor.updateStatus(1, "APROBADO");

        assertEquals("APROBADO", response.getStatus());
        verify(saveProjectPort, times(1)).save(argThat(p -> p.getStatus() == ProjectStatus.APPROVED));
    }

    @Test
    void testUpdateStatus_InvalidStatus() {
        Project existing = Project.builder().id(1).status(ProjectStatus.POSTULATED).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.updateStatus(1, "INVALID_STATUS"));
    }

    @Test
    void validateInvariants_WhenBudgetIsNull_ThrowsException() {
        Project project = Project.builder()
                .title("Test")
                .startDate(LocalDate.of(2026, Month.JANUARY, 1))
                .endDate(LocalDate.of(2026, Month.JANUARY, 10))
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void validateInvariants_WhenTitleIsNull_ThrowsException() {
        Project project = Project.builder()
                .budget(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, Month.JANUARY, 1))
                .endDate(LocalDate.of(2026, Month.JANUARY, 10))
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void validateInvariants_WhenStartDateIsNull_ThrowsException() {
        Project project = Project.builder()
                .title("Test")
                .budget(new BigDecimal("100"))
                .endDate(LocalDate.of(2026, Month.JANUARY, 10))
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    @Test
    void validateInvariants_WhenEndDateIsNull_ThrowsException() {
        Project project = Project.builder()
                .title("Test")
                .budget(new BigDecimal("100"))
                .startDate(LocalDate.of(2026, Month.JANUARY, 1))
                .build();
        assertThrows(BusinessRuleValidationException.class, project::validateInvariants);
    }

    // === NUEVOS TESTS PARA CREAR COBERTURA COMPLETA ===

    private CreateProjectRequest buildDraftRequest() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Draft Project");
        request.setSummary("Summary");
        request.setGeneralObjective("Objective");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("500.00"));
        request.setStartDate(FIXED_TODAY);
        request.setEndDate(FIXED_FUTURE_6M);
        request.setExecutionPlace("Place");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);
        request.setCallId(4);
        request.setDraft(true);
        return request;
    }

    private CreateProjectRequest buildSubmitRequest() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Submit Project");
        request.setSummary("Summary");
        request.setGeneralObjective("Objective");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("1500.00"));
        request.setStartDate(FIXED_TODAY);
        request.setEndDate(FIXED_FUTURE_6M);
        request.setExecutionPlace("Place");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);
        request.setCallId(4);
        request.setDraft(false);
        return request;
    }

    private void setupValidGroupLine() {
        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(true);
        when(saveProjectPort.isLineActive(1)).thenReturn(true);
        when(saveProjectPort.getGroupCode(2)).thenReturn(Optional.of("GINSOFT"));
        when(saveProjectPort.getLineName(1)).thenReturn(Optional.of("Computacion"));
    }

    @Test
    void shouldCreateDraftProject() {
        setupValidGroupLine();
        CreateProjectRequest request = buildDraftRequest();

        ResearchCall call = new ResearchCall(4, "Call 2026", "Desc", CALL_OPEN_START, CALL_OPEN_END, CallStatus.OPEN, null, null);
        when(saveCallPort.findById(4)).thenReturn(Optional.of(call));

        Project savedProject = new Project(1, "BOR-2026-ABC", "Draft Project", "Summary", "Objective",
                1, "Computacion", new BigDecimal("500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "Place", 3L, 2, "GINSOFT", 4, null, ProjectStatus.DRAFT);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        assertEquals("BORRADOR", response.getStatus());
        verify(createProcedurePort, never()).createPostulationProcedure(any());
    }

    @Test
    void shouldCreateDraftProjectWithoutCallId() {
        setupValidGroupLine();
        CreateProjectRequest request = buildDraftRequest();
        request.setCallId(null);

        Project savedProject = new Project(1, "BOR-2026-DEF", "Draft Project", "Summary", "Objective",
                1, "Computacion", new BigDecimal("500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "Place", 3L, 2, "GINSOFT", null, null, ProjectStatus.DRAFT);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        assertEquals("BORRADOR", response.getStatus());
    }

    @Test
    void shouldCreateDraftWithMembers() {
        setupValidGroupLine();
        CreateProjectRequest request = buildDraftRequest();
        MemberRequest member = new MemberRequest();
        member.setUserId(5);
        member.setRole("INVESTIGADOR");
        request.setMembers(List.of(member));

        Project savedProject = new Project(1, "BOR-2026-XYZ", "Draft Project", "Summary", "Objective",
                1, "Computacion", new BigDecimal("500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "Place", 3L, 2, "GINSOFT", 4, null, ProjectStatus.DRAFT);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        verify(saveProjectPort).saveMembers(eq(1), any());
    }

    @Test
    void shouldCreateDraftWithNullMembers() {
        setupValidGroupLine();
        CreateProjectRequest request = buildDraftRequest();
        request.setMembers(null);

        Project savedProject = new Project(1, "BOR-2026-NULL", "Draft Project", "Summary", "Objective",
                1, "Computacion", new BigDecimal("500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "Place", 3L, 2, "GINSOFT", 4, null, ProjectStatus.DRAFT);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        verify(saveProjectPort, never()).saveMembers(anyInt(), any());
    }

    @Test
    void shouldSubmitProjectWithoutCallId_AutoSelectOpenCall() {
        setupValidGroupLine();
        CreateProjectRequest request = buildSubmitRequest();
        request.setCallId(null);

        ResearchCall call = new ResearchCall(10, "Auto Call", "Desc", CALL_OPEN_START, CALL_OPEN_END, CallStatus.OPEN, null, null);
        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(List.of(call));

        Project savedProject = new Project(1, "PRJ-2026-AUTO", "Submit Project", "Summary", "Objective",
                1, "Computacion", new BigDecimal("1500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "Place", 3L, 2, "GINSOFT", 10, null, ProjectStatus.POSTULATED);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        assertEquals("POSTULADO", response.getStatus());
        verify(createProcedurePort).createPostulationProcedure(any());
    }

    @Test
    void shouldThrowWhenNoOpenCallsAvailable() {
        setupValidGroupLine();
        CreateProjectRequest request = buildSubmitRequest();
        request.setCallId(null);

        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(List.of());

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.execute(request));
    }

    @Test
    void shouldThrowWhenAllOpenCallsFailValidation() {
        setupValidGroupLine();
        CreateProjectRequest request = buildSubmitRequest();
        request.setCallId(null);

        ResearchCall pastCall = new ResearchCall(10, "Past", "Desc",
                LocalDate.of(2000, Month.JANUARY, 1), LocalDate.of(2000, Month.DECEMBER, 31),
                CallStatus.OPEN, null, null);
        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(List.of(pastCall));

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.execute(request));
    }

    @Test
    void shouldFailWhenLineNotActive() {
        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(true);
        when(saveProjectPort.isLineActive(1)).thenReturn(false);
        CreateProjectRequest request = buildSubmitRequest();

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.execute(request));
    }

    @Test
    void shouldFailWhenGroupCodeNotFound() {
        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(true);
        when(saveProjectPort.isLineActive(1)).thenReturn(true);
        when(saveProjectPort.getGroupCode(2)).thenReturn(Optional.empty());
        CreateProjectRequest request = buildSubmitRequest();

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.execute(request));
    }

    @Test
    void shouldFailWhenLineNameNotFound() {
        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(true);
        when(saveProjectPort.isLineActive(1)).thenReturn(true);
        when(saveProjectPort.getGroupCode(2)).thenReturn(Optional.of("GINSOFT"));
        when(saveProjectPort.getLineName(1)).thenReturn(Optional.empty());
        CreateProjectRequest request = buildSubmitRequest();

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.execute(request));
    }

    @Test
    void shouldDeleteDraftSuccessfully() {
        Project draft = Project.builder().id(1).status(ProjectStatus.DRAFT).responsibleId(3L).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(draft));

        createProjectInteractor.deleteDraft(1, 3L);

        verify(saveProjectPort).deleteById(1);
    }

    @Test
    void shouldThrowWhenDeleteNonDraftProject() {
        Project project = Project.builder().id(1).status(ProjectStatus.POSTULATED).responsibleId(3L).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(project));

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.deleteDraft(1, 3L));
    }

    @Test
    void shouldThrowWhenDeleteDraftByNonOwner() {
        Project draft = Project.builder().id(1).status(ProjectStatus.DRAFT).responsibleId(3L).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(draft));

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.deleteDraft(1, 99L));
    }

    @Test
    void shouldThrowWhenDeleteDraftNotFound() {
        when(saveProjectPort.findById(99)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.deleteDraft(99, 3L));
    }

    @Test
    void shouldGetDraftsByResponsible() {
        Project draft = Project.builder().id(1).status(ProjectStatus.DRAFT).build();
        when(saveProjectPort.findByResponsibleIdAndStatus(3L, ProjectStatus.DRAFT))
                .thenReturn(Collections.singletonList(draft));

        List<ProjectResponse> result = createProjectInteractor.getDraftsByResponsible(3L);

        assertEquals(1, result.size());
        assertEquals("BORRADOR", result.get(0).getStatus());
    }

    @Test
    void shouldReturnEmptyDraftsWhenNone() {
        when(saveProjectPort.findByResponsibleIdAndStatus(3L, ProjectStatus.DRAFT))
                .thenReturn(List.of());

        List<ProjectResponse> result = createProjectInteractor.getDraftsByResponsible(3L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldMapAllStatusesInUpdateStatus() {
        String[] statuses = {"BORRADOR", "POSTULADO", "OBSERVADO", "APROBADO", "RECHAZADO", "EN_EJECUCION", "FINALIZADO"};
        String[] expectedDb = {"BORRADOR", "POSTULADO", "OBSERVADO", "APROBADO", "RECHAZADO", "EN_EJECUCION", "FINALIZADO"};

        for (int i = 0; i < statuses.length; i++) {
            Project existing = Project.builder().id(1).status(ProjectStatus.POSTULATED).build();
            when(saveProjectPort.findById(1)).thenReturn(Optional.of(existing));
            when(saveProjectPort.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

            ProjectResponse response = createProjectInteractor.updateStatus(1, statuses[i]);
            assertEquals(expectedDb[i], response.getStatus());
        }
    }

    @Test
    void shouldUpdateStatusNotFound() {
        when(saveProjectPort.findById(99)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleValidationException.class,
                () -> createProjectInteractor.updateStatus(99, "APROBADO"));
    }

    @Test
    void shouldMapObservedStatusInResponse() {
        Project p = Project.builder().id(1).status(ProjectStatus.OBSERVED).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        when(saveProjectPort.findMembersByProjectId(1)).thenReturn(List.of());

        ProjectResponse response = createProjectInteractor.getProjectById(1);
        assertEquals("OBSERVADO", response.getStatus());
    }

    @Test
    void shouldMapRejectedStatusInResponse() {
        Project p = Project.builder().id(1).status(ProjectStatus.REJECTED).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        when(saveProjectPort.findMembersByProjectId(1)).thenReturn(List.of());

        ProjectResponse response = createProjectInteractor.getProjectById(1);
        assertEquals("RECHAZADO", response.getStatus());
    }

    @Test
    void shouldMapInProgressStatusInResponse() {
        Project p = Project.builder().id(1).status(ProjectStatus.IN_PROGRESS).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        when(saveProjectPort.findMembersByProjectId(1)).thenReturn(List.of());

        ProjectResponse response = createProjectInteractor.getProjectById(1);
        assertEquals("EN_EJECUCION", response.getStatus());
    }

    @Test
    void shouldMapCompletedStatusInResponse() {
        Project p = Project.builder().id(1).status(ProjectStatus.COMPLETED).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        when(saveProjectPort.findMembersByProjectId(1)).thenReturn(List.of());

        ProjectResponse response = createProjectInteractor.getProjectById(1);
        assertEquals("FINALIZADO", response.getStatus());
    }

    @Test
    void shouldMapDraftStatusInResponse() {
        Project p = Project.builder().id(1).status(ProjectStatus.DRAFT).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        when(saveProjectPort.findMembersByProjectId(1)).thenReturn(List.of());

        ProjectResponse response = createProjectInteractor.getProjectById(1);
        assertEquals("BORRADOR", response.getStatus());
    }

    @Test
    void shouldReturnNullMembersWhenIdIsNull() {
        Project p = Project.builder().id(null).status(ProjectStatus.POSTULATED).build();
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));

        ProjectResponse response = createProjectInteractor.getProjectById(1);
        assertNull(response.getMembers());
    }

    @Test
    void shouldSubmitProjectWithMembers() {
        setupValidGroupLine();
        CreateProjectRequest request = buildSubmitRequest();
        MemberRequest member = new MemberRequest();
        member.setUserId(5);
        member.setRole("COORDINADOR");
        request.setMembers(List.of(member));

        ResearchCall call = new ResearchCall(4, "Call", "Desc", CALL_OPEN_START, CALL_OPEN_END, CallStatus.OPEN, null, null);
        when(saveCallPort.findById(4)).thenReturn(Optional.of(call));

        Project savedProject = new Project(1, "PRJ-2026-XYZ", "Submit Project", "Summary", "Objective",
                1, "Computacion", new BigDecimal("1500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "Place", 3L, 2, "GINSOFT", 4, null, ProjectStatus.POSTULATED);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        verify(saveProjectPort).saveMembers(eq(1), any());
    }

    @Test
    void shouldSubmitProjectWithDefaultMemberRole() {
        setupValidGroupLine();
        CreateProjectRequest request = buildSubmitRequest();
        MemberRequest member = new MemberRequest();
        member.setUserId(5);
        member.setRole(null);
        request.setMembers(List.of(member));

        ResearchCall call = new ResearchCall(4, "Call", "Desc", CALL_OPEN_START, CALL_OPEN_END, CallStatus.OPEN, null, null);
        when(saveCallPort.findById(4)).thenReturn(Optional.of(call));

        Project savedProject = new Project(1, "PRJ-2026-XYZ", "Submit Project", "Summary", "Objective",
                1, "Computacion", new BigDecimal("1500.00"), FIXED_TODAY, FIXED_FUTURE_6M,
                "Place", 3L, 2, "GINSOFT", 4, null, ProjectStatus.POSTULATED);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = createProjectInteractor.execute(request);

        assertNotNull(response);
        verify(saveProjectPort).saveMembers(eq(1), argThat(members ->
                members.get(0).getRole().equals("INVESTIGADOR")));
    }
}