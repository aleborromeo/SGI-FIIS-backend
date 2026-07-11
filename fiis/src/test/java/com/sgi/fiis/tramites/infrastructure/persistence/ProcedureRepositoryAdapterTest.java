package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
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
@SuppressWarnings("all")
class ProcedureRepositoryAdapterTest {

    @Mock private SpringDataProcedureRepository procedureRepository;
    @Mock private SpringDataProcedureMovementRepository movementRepository;
    @Mock private SpringDataUserRepository userRepository;
    @Mock private ResearchGroupJpaRepository groupRepository;
    @Mock private ProjectJpaRepository projectRepository;
    @InjectMocks private ProcedureRepositoryAdapter adapter;

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0);

    private UserEntity buildApplicant() {
        UserEntity u = new UserEntity();
        u.setId(10L);
        return u;
    }

    private ResearchGroupEntity buildGroup() {
        ResearchGroupEntity g = new ResearchGroupEntity();
        g.setId(1);
        return g;
    }

    private ProcedureEntity buildEntity(Integer id) {
        ProcedureEntity e = new ProcedureEntity();
        e.setId(id);
        e.setCode("TRM-2026-001");
        e.setProcedureType("PROYECTO");
        e.setApplicant(buildApplicant());
        e.setGroup(buildGroup());
        e.setStatus("PENDIENTE_COORDINADOR");
        e.setReviewerRole("COORDINADOR_GRUPO");
        e.setSentAt(FECHA);
        e.setUpdatedAt(FECHA);
        return e;
    }

    private Procedure buildDomain() {
        return Procedure.builder()
                .id(1L)
                .code("TRM-2026-001")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(10L)
                .groupId(1L)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .currentReviewerRole(RoleEnum.COORDINADOR_GRUPO)
                .sentAt(FECHA)
                .updatedAt(FECHA)
                .movements(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("save: persists entity and returns mapped domain")
    void save_persistsEntityAndReturnsDomain() {
        when(userRepository.getReferenceById(10L)).thenReturn(buildApplicant());
        when(groupRepository.getReferenceById(1)).thenReturn(buildGroup());
        when(procedureRepository.save(any())).thenReturn(buildEntity(1));
        when(movementRepository.countByProcedure_Id(1L)).thenReturn(0L);

        Procedure result = adapter.save(buildDomain());

        assertEquals(1L, result.getId());
        assertEquals("TRM-2026-001", result.getCode());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, result.getCurrentStatus());
        verify(procedureRepository).save(any());
    }

    @Test
    @DisplayName("findById: found -> returns mapped domain")
    void findById_found_returnsDomain() {
        when(procedureRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("TRM-2026-001", result.get().getCode());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, result.get().getCurrentReviewerRole());
    }

    @Test
    @DisplayName("findById: not found -> returns empty Optional")
    void findById_notFound_returnsEmpty() {
        when(procedureRepository.findById(99)).thenReturn(Optional.empty());

        assertTrue(adapter.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findByCode: found -> returns mapped domain")
    void findByCode_found_returnsDomain() {
        when(procedureRepository.findByCode("TRM-2026-001"))
                .thenReturn(Optional.of(buildEntity(1)));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findByCode("TRM-2026-001");

        assertTrue(result.isPresent());
        assertEquals(ProcedureType.PROJECT, result.get().getProcedureType());
    }

    @Test
    @DisplayName("findByApplicantId: returns list of mapped procedures")
    void findByApplicantId_returnsMappedList() {
        when(procedureRepository.findByApplicant_Id(10L)).thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByApplicantId(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getApplicantId());
    }

    @Test
    @DisplayName("findByStatus: returns list filtered by status")
    void findByStatus_returnsMappedList() {
        when(procedureRepository.findByStatus("PENDIENTE_COORDINADOR"))
                .thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);

        assertEquals(1, result.size());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, result.get(0).getCurrentStatus());
    }

    @Test
    @DisplayName("existsByCode: delegates to repository")
    void existsByCode_delegatesToRepository() {
        when(procedureRepository.existsByCode("TRM-2026-001")).thenReturn(true);

        assertTrue(adapter.existsByCode("TRM-2026-001"));
        verify(procedureRepository).existsByCode("TRM-2026-001");
    }

    @Test
    @DisplayName("save: handles null values correctly")
    void save_handlesNullValuesCorrectly() {
        Procedure minimalDomain = Procedure.builder()
                .code("TRM-MINIMAL")
                .procedureType(ProcedureType.PROJECT)
                .currentStatus(ProcedureStatus.REGISTRADO)
                .sentAt(FECHA)
                .updatedAt(FECHA)
                .movements(new ArrayList<>())
                .build();

        ProcedureEntity minimalEntity = new ProcedureEntity();
        minimalEntity.setId(1);
        minimalEntity.setCode("TRM-MINIMAL");
        minimalEntity.setProcedureType("INFORME_AVANCE");
        minimalEntity.setStatus("REGISTRADO");
        minimalEntity.setSentAt(FECHA);
        minimalEntity.setUpdatedAt(FECHA);

        when(procedureRepository.save(any())).thenReturn(minimalEntity);

        Procedure result = adapter.save(minimalDomain);

        assertEquals(1L, result.getId());
        assertNull(result.getApplicantId());
        assertNull(result.getGroupId());
        assertNull(result.getCurrentReviewerRole());
        assertNull(result.getProjectReferenceId());
    }

    @Test
    @DisplayName("save: handles movements with null action user")
    void save_handlesMovementsWithNullActionUser() {
        List<com.sgi.fiis.tramites.domain.model.ProcedureMovement> movs = new ArrayList<>();
        movs.add(com.sgi.fiis.tramites.domain.model.ProcedureMovement.builder()
                .action("SISTEMA_AUTO")
                .previousStatus(ProcedureStatus.REGISTRADO)
                .newStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movementAt(FECHA)
                .build());

        Procedure domain = Procedure.builder()
                .id(1L)
                .code("TRM-2026-001")
                .procedureType(ProcedureType.PROJECT)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movements(movs)
                .build();

        when(procedureRepository.save(any())).thenReturn(buildEntity(1));
        when(movementRepository.countByProcedure_Id(1L)).thenReturn(0L);
        when(movementRepository.save(any())).thenReturn(null);

        Procedure result = adapter.save(domain);

        assertEquals(1, result.getMovements().size());
        assertNull(result.getMovements().get(0).getActionUserId());
        verify(movementRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("findByCode: not found -> returns empty Optional")
    void findByCode_notFound_returnsEmpty() {
        when(procedureRepository.findByCode("TRM-NONEXISTENT")).thenReturn(Optional.empty());

        assertTrue(adapter.findByCode("TRM-NONEXISTENT").isEmpty());
    }

    @Test
    @DisplayName("save: handles entity with projectReference and thesisReferenceId")
    void save_handlesProjectReferenceAndThesis() {
        Procedure domain = Procedure.builder()
                .id(1L)
                .code("TRM-2026-001")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(10L)
                .groupId(1L)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .currentReviewerRole(RoleEnum.COORDINADOR_GRUPO)
                .projectReferenceId(99L)
                .thesisReferenceId(42L)
                .reportReferenceId(7L)
                .sentAt(FECHA)
                .updatedAt(FECHA)
                .movements(new ArrayList<>())
                .build();

        ProcedureEntity entityWithRefs = buildEntity(1);
        entityWithRefs.setProjectReference(new com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity());
        entityWithRefs.getProjectReference().setId(99);
        entityWithRefs.setThesisReferenceId(42L);
        entityWithRefs.setReportReferenceId(7L);

        when(procedureRepository.save(any())).thenReturn(entityWithRefs);
        when(movementRepository.countByProcedure_Id(1L)).thenReturn(0L);

        Procedure result = adapter.save(domain);

        verify(procedureRepository).save(any());
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

        when(procedureRepository.findById(1)).thenReturn(Optional.of(entity));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(99L, result.get().getProjectReferenceId());
        assertEquals(42L, result.get().getThesisReferenceId());
        assertEquals(7L, result.get().getReportReferenceId());
    }

    @Test
    @DisplayName("toMovimientoDomain: handles null previousState and newState")
    void findById_handlesMovementsWithNullStates() {
        ProcedureMovementEntity movEntity = new ProcedureMovementEntity();
        movEntity.setActionUser(new UserEntity());
        movEntity.getActionUser().setId(1L);
        movEntity.setAction("SISTEMA_AUTO");
        movEntity.setPreviousState(null);
        movEntity.setNewState(null);
        movEntity.setComment("test");

        when(procedureRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of(movEntity));

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getMovements().size());
        assertNull(result.get().getMovements().get(0).getPreviousStatus());
        assertNull(result.get().getMovements().get(0).getNewStatus());
    }

    @Test
    @DisplayName("save: handles movement with null previousStatus and newStatus")
    void save_handlesMovementWithNullEnumStates() {
        List<com.sgi.fiis.tramites.domain.model.ProcedureMovement> movs = new ArrayList<>();
        movs.add(com.sgi.fiis.tramites.domain.model.ProcedureMovement.builder()
                .action("SISTEMA_AUTO")
                .previousStatus(null)
                .newStatus(null)
                .movementAt(FECHA)
                .build());

        Procedure domain = Procedure.builder()
                .id(1L)
                .code("TRM-2026-001")
                .procedureType(ProcedureType.PROJECT)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movements(movs)
                .build();

        when(procedureRepository.save(any())).thenReturn(buildEntity(1));
        when(movementRepository.countByProcedure_Id(1L)).thenReturn(0L);
        when(movementRepository.save(any())).thenReturn(null);

        Procedure result = adapter.save(domain);

        assertEquals(1, result.getMovements().size());
        verify(movementRepository).save(any());
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

        when(procedureRepository.findById(99)).thenReturn(Optional.of(entity));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(nullable(Long.class))).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(99L);

        assertTrue(result.isPresent());
        assertNull(result.get().getProcedureType());
        assertNull(result.get().getApplicantId());
        assertNull(result.get().getGroupId());
        assertNull(result.get().getCurrentStatus());
        assertNull(result.get().getCurrentReviewerRole());
        assertNull(result.get().getProjectReferenceId());
        assertNull(result.get().getThesisReferenceId());
        assertNull(result.get().getReportReferenceId());
        assertNull(result.get().getSentAt());
        assertNull(result.get().getUpdatedAt());
    }

    @Test
    @DisplayName("save: domain sin currentStatus y sin currentReviewerRole")
    void save_domainWithNullEstadoAndRol() {
        Procedure domain = Procedure.builder()
                .code("TRM-X")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(10L)
                .groupId(1L)
                .currentStatus(null)
                .currentReviewerRole(null)
                .projectReferenceId(null)
                .sentAt(null)
                .updatedAt(null)
                .movements(new ArrayList<>())
                .build();

        ProcedureEntity saved = new ProcedureEntity();
        saved.setId(1);
        saved.setCode("TRM-X");
        saved.setProcedureType("PROYECTO");
        saved.setStatus(null);
        saved.setReviewerRole(null);
        saved.setSentAt(null);
        saved.setUpdatedAt(null);
        UserEntity applicant = new UserEntity();
        applicant.setId(10L);
        saved.setApplicant(applicant);
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(1);
        saved.setGroup(group);

        when(procedureRepository.save(any())).thenReturn(saved);
        when(movementRepository.countByProcedure_Id(1L)).thenReturn(0L);

        Procedure result = adapter.save(domain);

        assertNull(result.getCurrentStatus());
        assertNull(result.getCurrentReviewerRole());
        assertNull(result.getSentAt());
        assertNull(result.getUpdatedAt());
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

        when(procedureRepository.findByApplicant_Id(10L)).thenReturn(List.of(entity));

        List<Procedure> result = adapter.findByApplicantId(10L);

        assertEquals(1, result.size());
        assertNull(result.get(0).getProcedureType());
        assertNull(result.get(0).getApplicantId());
        assertNull(result.get(0).getGroupId());
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

        when(procedureRepository.findByStatus("PENDIENTE_COORDINADOR")).thenReturn(List.of(entity));

        List<Procedure> result = adapter.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);

        assertEquals(1, result.size());
        assertNull(result.get(0).getCurrentStatus());
    }

    @Test
    @DisplayName("toMovimientoDomain: actionUser con id valido, movement completo")
    void toMovimientoDomain_completeMovement() {
        ProcedureMovementEntity movEntity = new ProcedureMovementEntity();
        movEntity.setAction("TEST");
        UserEntity actionUser = new UserEntity();
        actionUser.setId(77L);
        movEntity.setActionUser(actionUser);
        movEntity.setPreviousState("REGISTRADO");
        movEntity.setNewState("PENDIENTE_COORDINADOR");
        movEntity.setComment("comment");
        movEntity.setMovementAt(FECHA);
        movEntity.setDocumentAttachmentId(99L);

        when(procedureRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of(movEntity));

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        com.sgi.fiis.tramites.domain.model.ProcedureMovement mov = result.get().getMovements().get(0);
        assertEquals(77L, mov.getActionUserId());
        assertEquals("TEST", mov.getAction());
        assertEquals(ProcedureStatus.REGISTRADO, mov.getPreviousStatus());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, mov.getNewStatus());
        assertEquals("comment", mov.getComment());
        assertEquals(FECHA, mov.getMovementAt());
    }
}
