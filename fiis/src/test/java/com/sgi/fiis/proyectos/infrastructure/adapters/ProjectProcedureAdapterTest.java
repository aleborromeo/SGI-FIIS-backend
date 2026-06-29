package com.sgi.fiis.proyectos.infrastructure.adapters;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.tramites.infrastructure.persistence.ProcedureEntity;
import com.sgi.fiis.tramites.infrastructure.persistence.ProcedureMovementEntity;
import com.sgi.fiis.tramites.infrastructure.persistence.ProcedureMovementJpaRepository;
import com.sgi.fiis.tramites.infrastructure.persistence.SpringDataProcedureRepository;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
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

    private ProjectEntity createProjectEntity(Integer id, UserEntity responsible, ResearchGroupEntity group) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(id);
        entity.setTitle("Test Project");
        entity.setResponsible(responsible);
        entity.setGroup(group);
        entity.setStatus("POSTULADO");
        return entity;
    }

    private ProcedureEntity buildSavedProcedure(Long id) {
        ProcedureEntity p = new ProcedureEntity();
        p.setId(id);
        p.setCodigoTramite("TRM-2026-ABCD1234");
        return p;
    }

    @Test
    void createPostulationProcedure_ShouldSucceed_WhenProjectExists() {
        Project project = Project.builder().id(1).build();
        UserEntity responsible = new UserEntity();
        responsible.setId(10L);
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(20);
        ProjectEntity projectEntity = createProjectEntity(1, responsible, group);

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(buildSavedProcedure(100L));
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
        UserEntity responsible = new UserEntity();
        responsible.setId(10L);
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(20);
        ProjectEntity projectEntity = createProjectEntity(1, responsible, group);

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(buildSavedProcedure(100L));
        when(movementRepository.save(any(ProcedureMovementEntity.class))).thenReturn(null);

        adapter.createPostulationProcedure(project);

        ArgumentCaptor<ProcedureEntity> procedureCaptor = ArgumentCaptor.forClass(ProcedureEntity.class);
        verify(procedureRepository).save(procedureCaptor.capture());
        ProcedureEntity capturedProcedure = procedureCaptor.getValue();

        assertNotNull(capturedProcedure.getCodigoTramite());
        assertTrue(capturedProcedure.getCodigoTramite().startsWith("TRM-"));
        assertTrue(capturedProcedure.getCodigoTramite().length() <= 30);
        assertEquals("PROYECTO", capturedProcedure.getTipoTramite());
        assertEquals("PENDING_COORDINATOR", capturedProcedure.getEstadoActual());
        assertEquals("COORDINADOR_GRUPO", capturedProcedure.getRolRevisorActual());
        assertEquals(10L, capturedProcedure.getIdSolicitante());
        assertEquals(20L, capturedProcedure.getIdGrupo());
        assertEquals(1L, capturedProcedure.getIdReferenciaProyecto());
        assertNotNull(capturedProcedure.getFechaEnvio());
        assertNotNull(capturedProcedure.getFechaActualizacion());

        ArgumentCaptor<ProcedureMovementEntity> movementCaptor = ArgumentCaptor.forClass(ProcedureMovementEntity.class);
        verify(movementRepository).save(movementCaptor.capture());
        ProcedureMovementEntity capturedMovement = movementCaptor.getValue();

        assertEquals(100L, capturedMovement.getIdTramite());
        assertEquals(10L, capturedMovement.getIdUsuarioAccion());
        assertEquals("CREAR", capturedMovement.getAccion());
        assertEquals("REGISTERED", capturedMovement.getEstadoAnterior());
        assertEquals("PENDING_COORDINATOR", capturedMovement.getEstadoNuevo());
        assertNotNull(capturedMovement.getObservacion());
        assertNotNull(capturedMovement.getFechaMovimiento());
    }

    @Test
    void createPostulationProcedure_ShouldSaveWithNullResponsible() {
        Project project = Project.builder().id(1).build();
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(20);
        ProjectEntity projectEntity = createProjectEntity(1, null, group);

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(buildSavedProcedure(100L));
        when(movementRepository.save(any(ProcedureMovementEntity.class))).thenReturn(null);

        adapter.createPostulationProcedure(project);

        ArgumentCaptor<ProcedureEntity> procedureCaptor = ArgumentCaptor.forClass(ProcedureEntity.class);
        verify(procedureRepository).save(procedureCaptor.capture());
        assertNull(procedureCaptor.getValue().getIdSolicitante());

        ArgumentCaptor<ProcedureMovementEntity> movementCaptor = ArgumentCaptor.forClass(ProcedureMovementEntity.class);
        verify(movementRepository).save(movementCaptor.capture());
        assertNull(movementCaptor.getValue().getIdUsuarioAccion());
    }

    @Test
    void createPostulationProcedure_ShouldSaveWithNullGroup() {
        Project project = Project.builder().id(1).build();
        UserEntity responsible = new UserEntity();
        responsible.setId(10L);
        ProjectEntity projectEntity = createProjectEntity(1, responsible, null);

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectEntity));
        when(procedureRepository.save(any(ProcedureEntity.class))).thenReturn(buildSavedProcedure(100L));
        when(movementRepository.save(any(ProcedureMovementEntity.class))).thenReturn(null);

        adapter.createPostulationProcedure(project);

        ArgumentCaptor<ProcedureEntity> procedureCaptor = ArgumentCaptor.forClass(ProcedureEntity.class);
        verify(procedureRepository).save(procedureCaptor.capture());
        assertNull(procedureCaptor.getValue().getIdGrupo());
    }
}
