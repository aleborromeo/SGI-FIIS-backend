package com.sgi.fiis.proyectos.application.usecases;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.MemberRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.out.CreateProcedurePort;
import com.sgi.fiis.proyectos.application.ports.out.SaveProjectPort;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unused", "ThrowableResultOfMethodCallIgnored", "ResultOfMethodCallIgnored", "java:S1192"})
class CreateProjectInteractorTest {

    private SaveProjectPort saveProjectPort;
    private SaveCallPort saveCallPort;
    private CreateProcedurePort createProcedurePort;
    private CreateProjectInteractor interactor;

    @BeforeEach
    void setUp() {
        saveProjectPort = mock(SaveProjectPort.class);
        saveCallPort = mock(SaveCallPort.class);
        createProcedurePort = mock(CreateProcedurePort.class);
        interactor = new CreateProjectInteractor(saveProjectPort, saveCallPort, createProcedurePort, Clock.systemDefaultZone());
    }

    private CreateProjectRequest buildValidRequest() {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setResearchGroupId(1);
        req.setResponsibleId(2);
        req.setResearchLineId(3);
        req.setTitle("Project Title");
        req.setSummary("Project summary");
        req.setGeneralObjective("General objective");
        req.setExecutionPlace("Lima");
        req.setCallId(5);
        req.setBudget(new BigDecimal("1000"));
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusDays(10));
        return req;
    }

    @Test
    void execute_ValidRequest_Success() {
        CreateProjectRequest request = buildValidRequest();
        MemberRequest mr = new MemberRequest();
        mr.setUserId(4);
        mr.setRole("CO-INVESTIGADOR");
        request.setMembers(List.of(mr));
        request.setCallId(5);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        ResearchCall call = mock(ResearchCall.class);
        when(call.getStatus()).thenReturn(CallStatus.OPEN);
        when(saveCallPort.findById(5)).thenReturn(Optional.of(call));
        
        Project savedProject = new Project(100, "PRJ-2026-XXXX", "Title", null, null, 3, "Line", new BigDecimal("100"), LocalDate.now(), LocalDate.now(), null, 2L, 1, "GRP", null, null, ProjectStatus.POSTULATED);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = interactor.execute(request);
        
        assertNotNull(response);
        assertEquals(100, response.getId());
        verify(createProcedurePort).createPostulationProcedure(savedProject);
        verify(saveProjectPort).saveMembers(eq(100), anyList());
    }

    @Test
    void execute_GroupNotActive_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        when(saveProjectPort.isGroupActive(1)).thenReturn(false);

        BusinessRuleValidationException ex = assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
        assertEquals("proyectos.error.group-not-active", ex.getMessage());
    }

    @Test
    void execute_ResponsibleNotMember_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(false);

        BusinessRuleValidationException ex = assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
        assertEquals("proyectos.error.responsible-not-member", ex.getMessage());
    }

    @Test
    void execute_LineNotActive_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(false);

        BusinessRuleValidationException ex = assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
        assertEquals("proyectos.error.line-not-active", ex.getMessage());
    }

    @Test
    void execute_GroupCodeNotFound_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.empty());

        BusinessRuleValidationException ex = assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
        assertEquals("proyectos.error.group-code-not-found", ex.getMessage());
    }

    @Test
    void execute_LineNameNotFound_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.empty());

        BusinessRuleValidationException ex = assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
        assertEquals("proyectos.error.line-name-not-found", ex.getMessage());
    }

    @Test
    void execute_CallNotFound_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        request.setCallId(99);
        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));
        when(saveCallPort.findById(99)).thenReturn(Optional.empty());

        BusinessRuleValidationException ex = assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
        assertEquals("proyectos.error.call-not-found", ex.getMessage());
    }

    @Test
    void testQueries() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.APPROVED);
        when(saveProjectPort.findByResponsibleId(2L)).thenReturn(List.of(p));
        when(saveProjectPort.findByGroupId(3)).thenReturn(List.of(p));
        when(saveProjectPort.findAll()).thenReturn(List.of(p));
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));

        assertEquals(1, interactor.getProjectsByResponsible(2L).size());
        assertEquals(1, interactor.getProjectsByGroup(3).size());
        assertEquals(1, interactor.getAllProjects().size());
        assertNotNull(interactor.getProjectById(1));
    }

    @Test
    void updateStatus_Success() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.POSTULATED);
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        when(saveProjectPort.save(any(Project.class))).thenReturn(p);

        ProjectResponse res = interactor.updateStatus(1, "APROBADO");
        assertEquals("APROBADO", res.getStatus());
    }

    @Test
    void updateStatus_InvalidId_ThrowsException() {
        when(saveProjectPort.findById(1)).thenReturn(Optional.empty());
        assertThrows(BusinessRuleValidationException.class, () -> interactor.updateStatus(1, "APROBADO"));
    }

    @Test
    void updateStatus_InvalidStatus_ThrowsException() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.POSTULATED);
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        
        assertThrows(BusinessRuleValidationException.class, () -> interactor.updateStatus(1, "INVALID"));
    }
    
    @Test
    void execute_CallNotOpen_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        request.setCallId(5);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        ResearchCall call = mock(ResearchCall.class);
        when(call.getStatus()).thenReturn(CallStatus.CLOSED);
        when(saveCallPort.findById(5)).thenReturn(Optional.of(call));

        BusinessRuleValidationException ex = assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
        assertEquals("La convocatoria especificada no está abierta", ex.getMessage());
    }

    @Test
    void execute_EmptyMembers_DoesNotCallSaveMembers() {
        CreateProjectRequest request = buildValidRequest();
        request.setMembers(null);
        request.setCallId(5);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        ResearchCall call = mock(ResearchCall.class);
        when(call.getStatus()).thenReturn(CallStatus.OPEN);
        when(saveCallPort.findById(5)).thenReturn(Optional.of(call));

        Project savedProject = new Project(100, "PRJ-2026-XXXX", "Title", null, null, 3, "Line", new BigDecimal("100"), LocalDate.now(), LocalDate.now(), null, 2L, 1, "GRP", null, null, ProjectStatus.POSTULATED);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = interactor.execute(request);

        assertNotNull(response);
        verify(saveProjectPort, never()).saveMembers(anyInt(), anyList());
    }

    @Test
    void execute_EmptyMemberList_DoesNotCallSaveMembers() {
        CreateProjectRequest request = buildValidRequest();
        request.setMembers(List.of());
        request.setCallId(5);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        ResearchCall call = mock(ResearchCall.class);
        when(call.getStatus()).thenReturn(CallStatus.OPEN);
        when(saveCallPort.findById(5)).thenReturn(Optional.of(call));

        Project savedProject = new Project(100, "PRJ-2026-XXXX", "Title", null, null, 3, "Line", new BigDecimal("100"), LocalDate.now(), LocalDate.now(), null, 2L, 1, "GRP", null, null, ProjectStatus.POSTULATED);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = interactor.execute(request);

        assertNotNull(response);
        verify(saveProjectPort, never()).saveMembers(anyInt(), anyList());
    }

    @Test
    void execute_DraftTrue_CreatesDraftProject() {
        CreateProjectRequest request = buildValidRequest();
        request.setDraft(true);
        request.setCallId(null);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        Project savedProject = new Project(100, "BOR-2026-ABC12345", "Project Title", "Project summary",
                "General objective", 3, "Line-01", new BigDecimal("1000"), LocalDate.now(), LocalDate.now().plusMonths(6),
                "Lima", 2L, 1, "GRP-01", null, null, ProjectStatus.DRAFT);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = interactor.execute(request);

        assertNotNull(response);
        assertEquals("BORRADOR", response.getStatus());
        verify(createProcedurePort, never()).createPostulationProcedure(any());
    }

    @Test
    void execute_DraftTrue_WithCallId() {
        CreateProjectRequest request = buildValidRequest();
        request.setDraft(true);
        request.setCallId(5);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        ResearchCall call = mock(ResearchCall.class);
        when(call.getStatus()).thenReturn(CallStatus.OPEN);
        when(saveCallPort.findById(5)).thenReturn(Optional.of(call));

        Project savedProject = new Project(100, "BOR-2026-XYZ99999", "Project Title", "Project summary",
                "General objective", 3, "Line-01", new BigDecimal("1000"), LocalDate.now(), LocalDate.now().plusMonths(6),
                "Lima", 2L, 1, "GRP-01", 5, null, ProjectStatus.DRAFT);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = interactor.execute(request);

        assertNotNull(response);
        assertEquals("BORRADOR", response.getStatus());
    }

    @Test
    void execute_DraftTrue_NullOptionalFields_DefaultsApplied() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setDraft(true);
        request.setResearchGroupId(1);
        request.setResponsibleId(2);
        request.setResearchLineId(3);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        Project savedProject = new Project(100, "BOR-2026-DEF67890", "Borrador sin título", "",
                "", 3, "Line-01", BigDecimal.ZERO, LocalDate.now(), LocalDate.now().plusMonths(6),
                "", 2L, 1, "GRP-01", null, null, ProjectStatus.DRAFT);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = interactor.execute(request);

        assertNotNull(response);
        assertEquals("BORRADOR", response.getStatus());
        verify(createProcedurePort, never()).createPostulationProcedure(any());
    }

    @Test
    void mapStatusFromString_TestAllValues() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.POSTULATED);
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));
        when(saveProjectPort.save(any(Project.class))).thenReturn(p);
        
        interactor.updateStatus(1, "OBSERVADO");
        assertEquals(ProjectStatus.OBSERVED, p.getStatus());
        interactor.updateStatus(1, "RECHAZADO");
        assertEquals(ProjectStatus.REJECTED, p.getStatus());
        interactor.updateStatus(1, "EN_EJECUCION");
        assertEquals(ProjectStatus.IN_PROGRESS, p.getStatus());
        interactor.updateStatus(1, "FINALIZADO");
        assertEquals(ProjectStatus.COMPLETED, p.getStatus());
        interactor.updateStatus(1, "POSTULADO");
        assertEquals(ProjectStatus.POSTULATED, p.getStatus());
    }

    @Test
    void execute_RequiredFieldsMissing_ThrowsException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setDraft(false);

        assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
    }

    @Test
    void getDraftsByResponsible_Success() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.DRAFT);
        when(saveProjectPort.findByResponsibleIdAndStatus(2L, ProjectStatus.DRAFT)).thenReturn(List.of(p));

        var result = interactor.getDraftsByResponsible(2L);
        assertEquals(1, result.size());
    }

    @Test
    void deleteDraft_Success() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.DRAFT);
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));

        interactor.deleteDraft(1, 2L);
        verify(saveProjectPort).deleteById(1);
    }

    @Test
    void deleteDraft_NotDraft_ThrowsException() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.APPROVED);
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));

        assertThrows(BusinessRuleValidationException.class, () -> interactor.deleteDraft(1, 2L));
    }

    @Test
    void deleteDraft_NotResponsible_ThrowsException() {
        Project p = new Project(1, "CODE", "T", "S", "O", 1, "LN", new BigDecimal("10"), LocalDate.now(), LocalDate.now(), "P", 2L, 3, "GC", 4, 5, ProjectStatus.DRAFT);
        when(saveProjectPort.findById(1)).thenReturn(Optional.of(p));

        assertThrows(BusinessRuleValidationException.class, () -> interactor.deleteDraft(1, 99L));
    }

    @Test
    void deleteDraft_NotFound_ThrowsException() {
        when(saveProjectPort.findById(1)).thenReturn(Optional.empty());
        assertThrows(BusinessRuleValidationException.class, () -> interactor.deleteDraft(1, 2L));
    }

    @Test
    void execute_CallIdNull_NoOpenCalls_ThrowsException() {
        CreateProjectRequest request = buildValidRequest();
        request.setCallId(null);

        when(saveProjectPort.isGroupActive(1)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(2L, 1)).thenReturn(true);
        when(saveProjectPort.isLineActive(3)).thenReturn(true);
        when(saveProjectPort.getGroupCode(1)).thenReturn(Optional.of("GRP-01"));
        when(saveProjectPort.getLineName(3)).thenReturn(Optional.of("Line-01"));

        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(List.of());

        assertThrows(BusinessRuleValidationException.class, () -> interactor.execute(request));
    }
}
