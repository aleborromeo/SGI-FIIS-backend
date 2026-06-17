package com.sgi.fiis.proyectos.infrastructure.adapters;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.tramites.infrastructure.persistence.*;
import com.sgi.fiis.users.infrastructure.persistence.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectProcedureAdapterTest {

    private ProjectJpaRepository projectRepository;
    private SpringDataProcedureRepository procedureRepository;
    private ProcedureMovementJpaRepository movementRepository;
    private ProjectProcedureAdapter adapter;

    @BeforeEach
    void setUp() {
        projectRepository = Mockito.mock(ProjectJpaRepository.class);
        procedureRepository = Mockito.mock(SpringDataProcedureRepository.class);
        movementRepository = Mockito.mock(ProcedureMovementJpaRepository.class);
        adapter = new ProjectProcedureAdapter(projectRepository, procedureRepository, movementRepository);
    }

    private ProjectEntity createProjectEntity(Integer id, UsuarioEntity responsible, ResearchGroupEntity group) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(id);
        entity.setTitle("Test Project");
        entity.setResponsible(responsible);
        entity.setGroup(group);
        entity.setStatus("POSTULADO");
        return entity;
    }

    @Test
    void createPostulationProcedure_ShouldSucceed_WhenProjectExists() {
        Project project = Project.builder().id(1).build();
        UsuarioEntity responsible = new UsuarioEntity();
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
    void createPostulationProcedure_ShouldThrow_WhenProjectNotFound() {
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
    void createPostulationProcedure_ShouldSetCorrectProcedureFields() {
        Project project = Project.builder().id(1).build();
        UsuarioEntity responsible = new UsuarioEntity();
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
        assertEquals("PENDING_COORDINATOR", capturedProcedure.getStatus());
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
        assertEquals("REGISTERED", capturedMovement.getPreviousState());
        assertEquals("PENDING_COORDINATOR", capturedMovement.getNewState());
        assertNotNull(capturedMovement.getComment());
        assertNotNull(capturedMovement.getMovementAt());
    }

    @Test
    void createPostulationProcedure_ShouldSaveWithNullResponsible() {
        Project project = Project.builder().id(1).build();
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(20);
        ProjectEntity projectEntity = createProjectEntity(1, null, group);

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
        assertNull(procedureCaptor.getValue().getApplicant());

        ArgumentCaptor<ProcedureMovementEntity> movementCaptor = ArgumentCaptor.forClass(ProcedureMovementEntity.class);
        verify(movementRepository).save(movementCaptor.capture());
        assertNull(movementCaptor.getValue().getActionUser());
    }

    @Test
    void createPostulationProcedure_ShouldSaveWithNullGroup() {
        Project project = Project.builder().id(1).build();
        UsuarioEntity responsible = new UsuarioEntity();
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
}
