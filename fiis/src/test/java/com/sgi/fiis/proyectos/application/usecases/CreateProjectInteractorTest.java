package com.sgi.fiis.proyectos.application.usecases;

import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
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
        when(saveCallPort.findById(5)).thenReturn(Optional.of(call));

        Project savedProject = new Project(100, "PRJ-2026-XXXX", "Title", null, null, 3, "Line", new BigDecimal("100"), LocalDate.now(), LocalDate.now(), null, 2L, 1, "GRP", null, null, ProjectStatus.POSTULATED);
        when(saveProjectPort.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse response = interactor.execute(request);

        assertNotNull(response);
        verify(saveProjectPort, never()).saveMembers(anyInt(), anyList());
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
}
