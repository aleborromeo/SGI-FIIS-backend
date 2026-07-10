package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcedureRepositoryAdapter Unit Tests")
class ProcedureRepositoryAdapterTest {

    @Mock private SpringDataProcedureRepository tramiteRepository;
    @Mock private SpringDataProcedureMovementRepository movimientoRepository;
    @InjectMocks private ProcedureRepositoryAdapter adapter;

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0);

    private ProcedureEntity buildEntity(Integer id) {
        ProcedureEntity e = new ProcedureEntity();
        e.setId(id);
        e.setCode("TRM-2026-001");
        e.setProcedureType("PROYECTO");
        
        com.sgi.fiis.users.infrastructure.persistence.UserEntity applicant = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        applicant.setId(10L);
        e.setApplicant(applicant);
        
        com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity group = new com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity();
        group.setId(1);
        e.setGroup(group);
        
        e.setStatus("PENDIENTE_COORDINADOR");
        e.setReviewerRole("COORDINADOR_GRUPO");
        e.setSentAt(FECHA);
        e.setUpdatedAt(FECHA);
        return e;
    }

    private Procedure buildDomain() {
        return Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-001")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(10L)
                .idGrupo(1L)
                .estadoActual(ProcedureStatus.PENDIENTE_COORDINADOR)
                .rolRevisorActual(RoleEnum.COORDINADOR_GRUPO)
                .fechaEnvio(FECHA)
                .fechaActualizacion(FECHA)
                .movimientos(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("save: persists entity and returns mapped domain")
    void save_persistsEntityAndReturnsDomain() {
        when(tramiteRepository.save(any())).thenReturn(buildEntity(1));
        when(movimientoRepository.countByProcedureId(1)).thenReturn(0L);

        Procedure result = adapter.save(buildDomain());

        assertEquals(1L, result.getId());
        assertEquals("TRM-2026-001", result.getCodigoTramite());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, result.getEstadoActual());
        verify(tramiteRepository).save(any());
    }

    @Test
    @DisplayName("findById: found → returns mapped domain")
    void findById_found_returnsDomain() {
        when(tramiteRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movimientoRepository.findByProcedureIdOrderByDateAsc(1)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("TRM-2026-001", result.get().getCodigoTramite());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, result.get().getRolRevisorActual());
    }

    @Test
    @DisplayName("findById: not found → returns empty Optional")
    void findById_notFound_returnsEmpty() {
        when(tramiteRepository.findById(99)).thenReturn(Optional.empty());

        assertTrue(adapter.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findByCode: found → returns mapped domain")
    void findByCode_found_returnsDomain() {
        when(tramiteRepository.findByCode("TRM-2026-001"))
                .thenReturn(Optional.of(buildEntity(1)));
        when(movimientoRepository.findByProcedureIdOrderByDateAsc(1)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findByCode("TRM-2026-001");

        assertTrue(result.isPresent());
        assertEquals(ProcedureType.PROYECTO, result.get().getTipoTramite());
    }

    @Test
    @DisplayName("findByApplicantId: returns list of mapped procedures")
    void findByApplicantId_returnsMappedList() {
        when(tramiteRepository.findByApplicantId(10L)).thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByApplicantId(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getIdSolicitante());
    }

    @Test
    @DisplayName("findByStatus: returns list filtered by status")
    void findByStatus_returnsMappedList() {
        when(tramiteRepository.findByStatus("PENDIENTE_COORDINADOR"))
                .thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);

        assertEquals(1, result.size());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, result.get(0).getEstadoActual());
    }

    @Test
    @DisplayName("existsByCode: delegates to repository")
    void existsByCode_delegatesToRepository() {
        when(tramiteRepository.existsByCode("TRM-2026-001")).thenReturn(true);

        assertTrue(adapter.existsByCode("TRM-2026-001"));
        verify(tramiteRepository).existsByCode("TRM-2026-001");
    }

    @Test
    @DisplayName("save: handles null values correctly")
    void save_handlesNullValuesCorrectly() {
        Procedure minimalDomain = Procedure.builder()
                .codigoTramite("TRM-MINIMAL")
                .tipoTramite(ProcedureType.INFORME_AVANCE)
                .estadoActual(ProcedureStatus.REGISTRADO)
                .fechaEnvio(FECHA)
                .fechaActualizacion(FECHA)
                .movimientos(new ArrayList<>())
                .build();
                
        ProcedureEntity minimalEntity = new ProcedureEntity();
        minimalEntity.setId(1);
        minimalEntity.setCode("TRM-MINIMAL");
        minimalEntity.setProcedureType("INFORME_AVANCE");
        minimalEntity.setStatus("REGISTRADO");
        minimalEntity.setSentAt(FECHA);
        minimalEntity.setUpdatedAt(FECHA);
        
        when(tramiteRepository.save(any())).thenReturn(minimalEntity);
        
        Procedure result = adapter.save(minimalDomain);
        
        assertEquals(1L, result.getId());
        assertNull(result.getIdSolicitante());
        assertNull(result.getIdGrupo());
        assertNull(result.getRolRevisorActual());
        assertNull(result.getIdReferenciaProyecto());
    }
    
    @Test
    @DisplayName("save: handles movements with null action user")
    void save_handlesMovementsWithNullActionUser() {
        List<com.sgi.fiis.tramites.domain.model.ProcedureMovement> movs = new ArrayList<>();
        movs.add(com.sgi.fiis.tramites.domain.model.ProcedureMovement.builder()
                .accion("SISTEMA_AUTO")
                .estadoAnterior(ProcedureStatus.REGISTRADO)
                .estadoNuevo(ProcedureStatus.PENDIENTE_COORDINADOR)
                .fechaMovimiento(FECHA)
                .build());
                
        Procedure domain = Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-001")
                .tipoTramite(ProcedureType.PROYECTO)
                .estadoActual(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movimientos(movs)
                .build();
                
        when(tramiteRepository.save(any())).thenReturn(buildEntity(1));
        when(movimientoRepository.countByProcedureId(1)).thenReturn(0L);
        when(movimientoRepository.save(any())).thenReturn(null);
        
        Procedure result = adapter.save(domain);
        
        assertEquals(1, result.getMovements().size());
        assertNull(result.getMovements().get(0).getIdUsuarioAccion());
        verify(movimientoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("findByCode: not found → returns empty Optional")
    void findByCode_notFound_returnsEmpty() {
        when(tramiteRepository.findByCode("TRM-NONEXISTENT")).thenReturn(Optional.empty());

        assertTrue(adapter.findByCode("TRM-NONEXISTENT").isEmpty());
    }

    @Test
    @DisplayName("save: handles entity with projectReference and thesisReferenceId")
    void save_handlesProjectReferenceAndThesis() {
        Procedure domain = Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-001")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(10L)
                .idGrupo(1L)
                .estadoActual(ProcedureStatus.PENDIENTE_COORDINADOR)
                .rolRevisorActual(RoleEnum.COORDINADOR_GRUPO)
                .idReferenciaProyecto(99L)
                .idReferenciaTesis(42L)
                .idReferenciaInforme(7L)
                .fechaEnvio(FECHA)
                .fechaActualizacion(FECHA)
                .movimientos(new ArrayList<>())
                .build();

        ProcedureEntity entityWithRefs = buildEntity(1);
        entityWithRefs.setProjectReference(new com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity());
        entityWithRefs.getProjectReference().setId(99);
        entityWithRefs.setThesisReferenceId(42L);
        entityWithRefs.setReportReferenceId(7L);

        when(tramiteRepository.save(any())).thenReturn(entityWithRefs);
        when(movimientoRepository.countByProcedureId(1)).thenReturn(0L);

        Procedure result = adapter.save(domain);

        verify(tramiteRepository).save(any());
        assertNotNull(result);
    }

    @Test
    @DisplayName("findById: maps entity with projectReference and thesisReferenceId")
    void findById_mapsProjectReferenceAndThesis() {
        ProcedureEntity entity = buildEntity(1);
        com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity pe =
                new com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity();
        pe.setId(99);
        entity.setProjectReference(pe);
        entity.setThesisReferenceId(42L);
        entity.setReportReferenceId(7L);

        when(tramiteRepository.findById(1)).thenReturn(Optional.of(entity));
        when(movimientoRepository.findByProcedureIdOrderByDateAsc(1)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(99L, result.get().getIdReferenciaProyecto());
        assertEquals(42L, result.get().getIdReferenciaTesis());
        assertEquals(7L, result.get().getIdReferenciaInforme());
    }

    @Test
    @DisplayName("toMovimientoDomain: handles null previousState and newState")
    void findById_handlesMovementsWithNullStates() {
        ProcedureMovementEntity movEntity = new ProcedureMovementEntity();
        movEntity.setAction("SISTEMA_AUTO");
        movEntity.setPreviousState(null);
        movEntity.setNewState(null);
        movEntity.setComment("test");

        when(tramiteRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movimientoRepository.findByProcedureIdOrderByDateAsc(1)).thenReturn(List.of(movEntity));

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getMovements().size());
        assertNull(result.get().getMovements().get(0).getEstadoAnterior());
        assertNull(result.get().getMovements().get(0).getEstadoNuevo());
    }

    @Test
    @DisplayName("save: handles movement with null estadoAnterior and estadoNuevo")
    void save_handlesMovementWithNullEnumStates() {
        List<com.sgi.fiis.tramites.domain.model.ProcedureMovement> movs = new ArrayList<>();
        movs.add(com.sgi.fiis.tramites.domain.model.ProcedureMovement.builder()
                .accion("SISTEMA_AUTO")
                .estadoAnterior(null)
                .estadoNuevo(null)
                .fechaMovimiento(FECHA)
                .build());

        Procedure domain = Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-001")
                .tipoTramite(ProcedureType.PROYECTO)
                .estadoActual(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movimientos(movs)
                .build();

        when(tramiteRepository.save(any())).thenReturn(buildEntity(1));
        when(movimientoRepository.countByProcedureId(1)).thenReturn(0L);
        when(movimientoRepository.save(any())).thenReturn(null);

        Procedure result = adapter.save(domain);

        assertEquals(1, result.getMovements().size());
        verify(movimientoRepository).save(any());
    }

    @Test
    @DisplayName("findById: entity con procedureType, status, applicant, group, reviewerRole null")
    void findById_entityWithAllNullOptionalFields() {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(1);
        entity.setCode("TRM-NULL");
        entity.setProcedureType(null);
        entity.setApplicant(null);
        entity.setGroup(null);
        entity.setStatus(null);
        entity.setReviewerRole(null);
        entity.setProjectReference(null);
        entity.setThesisReferenceId(null);
        entity.setReportReferenceId(null);
        entity.setSentAt(null);
        entity.setUpdatedAt(null);

        when(tramiteRepository.findById(99)).thenReturn(Optional.of(entity));
        when(movimientoRepository.findByProcedureIdOrderByDateAsc(nullable(Integer.class))).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(99L);

        assertTrue(result.isPresent());
        assertNull(result.get().getTipoTramite());
        assertNull(result.get().getIdSolicitante());
        assertNull(result.get().getIdGrupo());
        assertNull(result.get().getEstadoActual());
        assertNull(result.get().getRolRevisorActual());
        assertNull(result.get().getIdReferenciaProyecto());
        assertNull(result.get().getIdReferenciaTesis());
        assertNull(result.get().getIdReferenciaInforme());
        assertNull(result.get().getFechaEnvio());
        assertNull(result.get().getFechaActualizacion());
    }

    @Test
    @DisplayName("save: domain sin estadoActual y sin rolRevisorActual")
    void save_domainWithNullEstadoAndRol() {
        Procedure domain = Procedure.builder()
                .codigoTramite("TRM-X")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(10L)
                .idGrupo(1L)
                .estadoActual(null)
                .rolRevisorActual(null)
                .idReferenciaProyecto(null)
                .fechaEnvio(null)
                .fechaActualizacion(null)
                .movimientos(new ArrayList<>())
                .build();

        ProcedureEntity saved = new ProcedureEntity();
        saved.setId(1);
        saved.setCode("TRM-X");
        saved.setProcedureType("PROYECTO");
        saved.setStatus(null);
        saved.setReviewerRole(null);
        saved.setSentAt(null);
        saved.setUpdatedAt(null);
        com.sgi.fiis.users.infrastructure.persistence.UserEntity applicant = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        applicant.setId(10L);
        saved.setApplicant(applicant);
        com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity group =
                new com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity();
        group.setId(1);
        saved.setGroup(group);

        when(tramiteRepository.save(any())).thenReturn(saved);
        when(movimientoRepository.countByProcedureId(1)).thenReturn(0L);

        Procedure result = adapter.save(domain);

        assertNull(result.getEstadoActual());
        assertNull(result.getRolRevisorActual());
        assertNull(result.getFechaEnvio());
        assertNull(result.getFechaActualizacion());
    }

    @Test
    @DisplayName("findByApplicantId: entity sin optional fields")
    void findByApplicantId_entityWithNullOptionalFields() {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(5);
        entity.setCode("TRM-5");
        entity.setProcedureType(null);
        entity.setApplicant(null);
        entity.setGroup(null);
        entity.setStatus(null);
        entity.setReviewerRole(null);

        when(tramiteRepository.findByApplicantId(10L)).thenReturn(List.of(entity));

        List<Procedure> result = adapter.findByApplicantId(10L);

        assertEquals(1, result.size());
        assertNull(result.get(0).getTipoTramite());
        assertNull(result.get(0).getIdSolicitante());
        assertNull(result.get(0).getIdGrupo());
    }

    @Test
    @DisplayName("findByStatus: entity sin optional fields")
    void findByStatus_entityWithNullOptionalFields() {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(5);
        entity.setCode("TRM-5");
        entity.setProcedureType(null);
        entity.setApplicant(null);
        entity.setGroup(null);
        entity.setStatus(null);
        entity.setReviewerRole(null);

        when(tramiteRepository.findByStatus("PENDIENTE_COORDINADOR")).thenReturn(List.of(entity));

        List<Procedure> result = adapter.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);

        assertEquals(1, result.size());
        assertNull(result.get(0).getEstadoActual());
    }

    @Test
    @DisplayName("toMovimientoDomain: actionUser con id válido, movement completo")
    void toMovimientoDomain_completeMovement() {
        ProcedureMovementEntity movEntity = new ProcedureMovementEntity();
        movEntity.setAction("TEST");
        com.sgi.fiis.users.infrastructure.persistence.UserEntity actionUser =
                new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        actionUser.setId(77L);
        movEntity.setActionUser(actionUser);
        movEntity.setPreviousState("REGISTRADO");
        movEntity.setNewState("PENDIENTE_COORDINADOR");
        movEntity.setComment("comment");
        movEntity.setMovementAt(FECHA);
        movEntity.setDocumentAttachmentId(99L);

        when(tramiteRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movimientoRepository.findByProcedureIdOrderByDateAsc(1)).thenReturn(List.of(movEntity));

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        com.sgi.fiis.tramites.domain.model.ProcedureMovement mov = result.get().getMovements().get(0);
        assertEquals(77L, mov.getIdUsuarioAccion());
        assertEquals("TEST", mov.getAccion());
        assertEquals(ProcedureStatus.REGISTRADO, mov.getEstadoAnterior());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, mov.getEstadoNuevo());
        assertEquals("comment", mov.getObservacion());
        assertEquals(FECHA, mov.getFechaMovimiento());
        assertEquals(99L, mov.getIdDocumentoAdjunto());
    }
}
