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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectModuleTest {

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
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(6));
        request.setExecutionPlace("FIIS Lab");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);
        request.setCallId(4);

        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(true);
        when(saveProjectPort.isLineActive(1)).thenReturn(true);
        when(saveProjectPort.getGroupCode(2)).thenReturn(Optional.of("GINSOFT"));
        when(saveProjectPort.getLineName(1)).thenReturn(Optional.of("Computacion"));

        ResearchCall call = new ResearchCall(4, "Call 2026", LocalDate.now().minusDays(1), LocalDate.now().plusMonths(1), CallStatus.OPEN);
        when(saveCallPort.findById(4)).thenReturn(Optional.of(call));

        Project savedProject = new Project(
                1, "PRJ-2026-XYZ", "New Tech Project", "Summary of tech project", "Objective of tech project",
                1, "Computacion", new BigDecimal("1500.00"), LocalDate.now(), LocalDate.now().plusMonths(6),
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
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(10));
        request.setExecutionPlace("Place");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);

        when(saveProjectPort.isGroupActive(2)).thenReturn(false);

        assertThrows(BusinessRuleValidationException.class, () -> {
            createProjectInteractor.execute(request);
        });
    }

    @Test
    void shouldFailWhenResponsibleNotMemberOfGroup() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Title");
        request.setSummary("Summary");
        request.setGeneralObjective("Objective");
        request.setResearchLineId(1);
        request.setBudget(new BigDecimal("100.00"));
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(10));
        request.setExecutionPlace("Place");
        request.setResponsibleId(3);
        request.setResearchGroupId(2);

        when(saveProjectPort.isGroupActive(2)).thenReturn(true);
        when(saveProjectPort.isUserMemberOfGroup(3L, 2)).thenReturn(false);

        assertThrows(BusinessRuleValidationException.class, () -> {
            createProjectInteractor.execute(request);
        });
    }

    @Test
    void shouldFailWhenBudgetIsZeroOrNegative() {
        Project project = new Project(
                1, "PRJ-X", "Title", "Summary", "Objective",
                1, "Computacion", new BigDecimal("0.00"), LocalDate.now(), LocalDate.now().plusDays(10),
                "Place", 3L, 1, "GINSOFT", 1, 1, ProjectStatus.POSTULATED
        );

        assertThrows(BusinessRuleValidationException.class, () -> {
            project.validateInvariants();
        });
    }

    @Test
    void shouldApplyGinsoftLineRestrictionCorrectly() {
        // GINSOFT group restricts to 'Computacion' or 'Ingenieria de software'
        Project validProject = new Project(
                1, "PRJ-X", "Title", "Summary", "Objective",
                1, "Computacion", new BigDecimal("500.00"), LocalDate.now(), LocalDate.now().plusDays(10),
                "Place", 3L, 1, "GINSOFT", 1, 1, ProjectStatus.POSTULATED
        );
        assertDoesNotThrow(() -> validProject.validateInvariants());

        Project invalidProject = new Project(
                1, "PRJ-Y", "Title", "Summary", "Objective",
                1, "Ciberseguridad", new BigDecimal("500.00"), LocalDate.now(), LocalDate.now().plusDays(10),
                "Place", 3L, 1, "GINSOFT", 1, 1, ProjectStatus.POSTULATED
        );
        assertThrows(BusinessRuleValidationException.class, () -> invalidProject.validateInvariants());
    }
}