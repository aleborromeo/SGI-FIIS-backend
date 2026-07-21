package com.sgi.fiis.proyectos.infrastructure.adapters;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.aspect.CorrelationContext;
import com.sgi.fiis.tramites.infrastructure.persistence.*;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
class ProjectProcedureAdapterTest {

    private ProjectJpaRepository projectRepository;
    private SpringDataProcedureRepository procedureRepository;
    private ProcedureMovementJpaRepository movementRepository;
    private CorrelationContext correlationContext;
    private ProjectProcedureAdapter adapter;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectJpaRepository.class);
        procedureRepository = mock(SpringDataProcedureRepository.class);
        movementRepository = mock(ProcedureMovementJpaRepository.class);
        correlationContext = mock(CorrelationContext.class);
        adapter = new ProjectProcedureAdapter(projectRepository, procedureRepository, movementRepository, correlationContext);
    }

    private ProjectEntity createProjectEntity(Integer id, UserEntity responsible, ResearchGroupEntity group) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(id);
        entity.setTitle("Test Project");
        entity.setResponsible(responsible);
        entity.setGroup(group);
        entity.setStatus("POSTULADO");
        return entity;
    }

    @Test
    void createPostulationProcedureShouldSucceedWhenProjectExists() {
        Project project = Project.builder().id(1).build();
        UserEntity responsible = new UserEntity();
        responsible.setId(10L);
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(20);
        ProjectEntity projectEntity = createProjectEntity(1, responsible, group);

        ProcedureEntity savedProcedure = ProcedureEntity.builder()
                .id(100)
                .code("TRM-2026-ABCD1234")
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(savedProcedure);
        when(movementRepository.save(any(ProcedureMovementEntity.class))).thenReturn(null);

        adapter.createPostulationProcedure(project);

        verify(projectRepository).findById(1);
        verify(procedureRepository).save(any(ProcedureEntity.class));
        verify(movementRepository).save(any(ProcedureMovementEntity.class));
    }

    @Test
    void createPostulationProcedureShouldThrowWhenProjectNotFound() {
        Project project = Project.builder().id(99).build();

        when(projectRepository.findById(99)).thenReturn(Optional.empty());

        BusinessRuleValidationException exception = assertThrows(BusinessRuleValidationException.class,
                () -> adapter.createPostulationProcedure(project));
        assertTrue(exception.getMessage().contains("Project not found with ID: 99"));

        verify(projectRepository).findById(99);
        verify(procedureRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void createPostulationProcedureShouldSetCorrectProcedureFields() {
        Project project = Project.builder().id(1).build();
        UserEntity responsible = new UserEntity();
        responsible.setId(10L);
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(20);
        ProjectEntity projectEntity = createProjectEntity(1, responsible, group);

        ProcedureEntity savedProcedure = ProcedureEntity.builder()
                .id(100)
                .code("TRM-2026-ABCD1234")
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(savedProcedure);
        when(movementRepository.save(any(ProcedureMovementEntity.class))).thenReturn(null);

        adapter.createPostulationProcedure(project);

        ArgumentCaptor<ProcedureEntity> procedureCaptor = ArgumentCaptor.forClass(ProcedureEntity.class);
        verify(procedureRepository).save(procedureCaptor.capture());
        ProcedureEntity capturedProcedure = procedureCaptor.getValue();

        assertNotNull(capturedProcedure.getCode());
        assertTrue(capturedProcedure.getCode().startsWith("TRM-"));
        assertTrue(capturedProcedure.getCode().length() <= 30);
        assertEquals("PROYECTO", capturedProcedure.getProcedureType());
        assertEquals("PENDIENTE_COORDINADOR", capturedProcedure.getStatus());
        assertEquals("COORDINADOR_GRUPO", capturedProcedure.getReviewerRole());
        assertSame(responsible, capturedProcedure.getApplicant());
        assertSame(group, capturedProcedure.getGroup());
        assertSame(projectEntity, capturedProcedure.getProjectReference());
        assertNotNull(capturedProcedure.getSentAt());
        assertNotNull(capturedProcedure.getUpdatedAt());

        ArgumentCaptor<ProcedureMovementEntity> movementCaptor = ArgumentCaptor.forClass(ProcedureMovementEntity.class);
        verify(movementRepository).save(movementCaptor.capture());
        ProcedureMovementEntity capturedMovement = movementCaptor.getValue();

        assertSame(savedProcedure, capturedMovement.getProcedure());
        assertSame(responsible, capturedMovement.getActionUser());
        assertEquals("CREAR", capturedMovement.getAction());
        assertEquals("REGISTRADO", capturedMovement.getPreviousState());
        assertEquals("PENDIENTE_COORDINADOR", capturedMovement.getNewState());
        assertNotNull(capturedMovement.getComment());
        assertNotNull(capturedMovement.getMovementAt());
    }

    @Test
    void createPostulationProcedureShouldThrowWhenProjectResponsibleIsNull() {
        Project project = Project.builder().id(1).build();
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(20);
        ProjectEntity projectEntity = createProjectEntity(1, null, group);

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));

        BusinessRuleValidationException exception = assertThrows(BusinessRuleValidationException.class,
                () -> adapter.createPostulationProcedure(project));
        assertTrue(exception.getMessage().contains("Project responsible must not be null"));

        verify(procedureRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void createPostulationProcedureShouldSaveWithNullGroup() {
        Project project = Project.builder().id(1).build();
        UserEntity responsible = new UserEntity();
        responsible.setId(10L);
        ProjectEntity projectEntity = createProjectEntity(1, responsible, null);

        ProcedureEntity savedProcedure = ProcedureEntity.builder()
                .id(100)
                .code("TRM-2026-ABCD1234")
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(savedProcedure);
        when(movementRepository.save(any(ProcedureMovementEntity.class))).thenReturn(null);

        adapter.createPostulationProcedure(project);

        ArgumentCaptor<ProcedureEntity> procedureCaptor = ArgumentCaptor.forClass(ProcedureEntity.class);
        verify(procedureRepository).save(procedureCaptor.capture());
        assertNull(procedureCaptor.getValue().getGroup());
    }

    @Test
    void createPostulationProcedureShouldSaveWithCorrelationIdWhenPresent() {
        Project project = Project.builder().id(1).build();
        UserEntity responsible = new UserEntity();
        responsible.setId(10L);
        ProjectEntity projectEntity = createProjectEntity(1, responsible, null);

        ProcedureEntity savedProcedure = ProcedureEntity.builder()
                .id(100)
                .code("TRM-2026-ABCD1234")
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(savedProcedure);
        when(correlationContext.getCorrelationId()).thenReturn("corr-5555");

        adapter.createPostulationProcedure(project);

        ArgumentCaptor<ProcedureMovementEntity> movementCaptor = ArgumentCaptor.forClass(ProcedureMovementEntity.class);
        verify(movementRepository).save(movementCaptor.capture());
        assertEquals("corr-5555", movementCaptor.getValue().getCorrelationId());
    }
}
