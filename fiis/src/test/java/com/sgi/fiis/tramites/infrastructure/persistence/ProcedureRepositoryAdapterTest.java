package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupJpaRepository;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.shared.infrastructure.aspect.CorrelationContext;
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
@SuppressWarnings({"java:S100", "java:S1192", "MethodName", "MultipleStringLiterals"})
class ProcedureRepositoryAdapterTest {

    @Mock private SpringDataProcedureRepository procedureRepository;
    @Mock private SpringDataProcedureMovementRepository movementRepository;
    @Mock private SpringDataUserRepository userRepository;
    @Mock private ResearchGroupJpaRepository groupRepository;
    @Mock private ProjectJpaRepository projectRepository;
    @Mock private CorrelationContext correlationContext;
    @InjectMocks private ProcedureRepositoryAdapter adapter;

    private static final LocalDateTime DATE = LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0);

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
        e.setSentAt(DATE);
        e.setUpdatedAt(DATE);
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
                .sentAt(DATE)
                .updatedAt(DATE)
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
    @DisplayName("findById: found returns mapped domain")
    void findById_found_returnsDomain() {
        when(procedureRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("TRM-2026-001", result.get().getCode());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, result.get().getCurrentReviewerRole());
    }

    @Test
    @DisplayName("findById: not found returns empty Optional")
    void findById_notFound_returnsEmpty() {
        when(procedureRepository.findById(99)).thenReturn(Optional.empty());

        assertTrue(adapter.findById(99L).isEmpty());
    }

    @Test
    @DisplayName("findByCode: found returns mapped domain")
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
                .procedureType(ProcedureType.REPORT_AVANCE)
                .currentStatus(ProcedureStatus.REGISTRADO)
                .sentAt(DATE)
                .updatedAt(DATE)
                .movements(new ArrayList<>())
                .build();

        ProcedureEntity minimalEntity = new ProcedureEntity();
        minimalEntity.setId(1);
        minimalEntity.setCode("TRM-MINIMAL");
        minimalEntity.setProcedureType("INFORME_AVANCE");
        minimalEntity.setStatus("REGISTRADO");
        minimalEntity.setSentAt(DATE);
        minimalEntity.setUpdatedAt(DATE);

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
        List<ProcedureMovement> movs = new ArrayList<>();
        movs.add(ProcedureMovement.builder()
                .action("SISTEMA_AUTO")
                .previousStatus(ProcedureStatus.REGISTRADO)
                .newStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movementAt(DATE)
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
    @DisplayName("findByCode: not found returns empty Optional")
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
                .sentAt(DATE)
                .updatedAt(DATE)
                .movements(new ArrayList<>())
                .build();

        when(userRepository.getReferenceById(10L)).thenReturn(buildApplicant());
        when(groupRepository.getReferenceById(1)).thenReturn(buildGroup());
        when(projectRepository.getReferenceById(99)).thenReturn(new ProjectEntity());

        ProcedureEntity entityWithRefs = buildEntity(1);
        entityWithRefs.setProjectReference(new ProjectEntity());
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
        ProjectEntity pe = new ProjectEntity();
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
        List<ProcedureMovement> movs = new ArrayList<>();
        movs.add(ProcedureMovement.builder()
                .action("SISTEMA_AUTO")
                .previousStatus(null)
                .newStatus(null)
                .movementAt(DATE)
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
    @DisplayName("findById: entity with all null optional fields")
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
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of());

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
    @DisplayName("save: domain with null currentStatus and reviewerRole")
    void save_domainWithNullStatusAndRole() {
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

        when(userRepository.getReferenceById(10L)).thenReturn(buildApplicant());
        when(groupRepository.getReferenceById(1)).thenReturn(buildGroup());

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
    @DisplayName("findByApplicantId: entity with null optional fields")
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
    @DisplayName("findByStatus: entity with null optional fields")
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
    @DisplayName("toMovimientoDomain: complete movement with valid actionUser")
    void toMovimientoDomain_completeMovement() {
        ProcedureMovementEntity movEntity = new ProcedureMovementEntity();
        movEntity.setAction("TEST");
        UserEntity actionUser = new UserEntity();
        actionUser.setId(77L);
        movEntity.setActionUser(actionUser);
        movEntity.setPreviousState("REGISTRADO");
        movEntity.setNewState("PENDIENTE_COORDINADOR");
        movEntity.setComment("comment");
        movEntity.setMovementAt(DATE);

        when(procedureRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movementRepository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of(movEntity));

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        ProcedureMovement mov = result.get().getMovements().get(0);
        assertEquals(77L, mov.getActionUserId());
        assertEquals("TEST", mov.getAction());
        assertEquals(ProcedureStatus.REGISTRADO, mov.getPreviousStatus());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, mov.getNewStatus());
        assertEquals("comment", mov.getComment());
        assertEquals(DATE, mov.getMovementAt());
    }

    @Test
    @DisplayName("findAll: returns all procedures mapped to domain")
    void findAll_returnsAllProcedures() {
        when(procedureRepository.findAll()).thenReturn(List.of(buildEntity(1), buildEntity(2)));

        List<Procedure> result = adapter.findAll();

        assertEquals(2, result.size());
        verify(procedureRepository).findAll();
    }

    @Test
    @DisplayName("save: handles projectReferenceId null and applicant null")
    void save_handlesProjectReferenceAndApplicantNull() {
        Procedure domain = Procedure.builder()
                .code("TRM-NO-REF")
                .currentStatus(ProcedureStatus.REGISTRADO)
                .movements(new ArrayList<>())
                .build();

        ProcedureEntity saved = new ProcedureEntity();
        saved.setId(1);
        saved.setCode("TRM-NO-REF");
        when(procedureRepository.save(any())).thenReturn(saved);

        Procedure result = adapter.save(domain);

        assertNotNull(result);
        verify(procedureRepository).save(any());
    }

    @Test
    @DisplayName("findByApplicantId: toDomain handles null entity id (new code coverage)")
    void findByApplicantId_toDomainHandlesNullEntityId() {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setId(null);
        entity.setCode("TRM-NULL-ID");
        entity.setStatus("REGISTRADO");

        when(procedureRepository.findByApplicant_Id(10L)).thenReturn(List.of(entity));

        List<Procedure> result = adapter.findByApplicantId(10L);

        assertEquals(1, result.size());
        assertNull(result.get(0).getId());
    }

    @Test
    @DisplayName("save: toMovimientoEntity sets actionUser when actionUserId is not null")
    void save_toMovimientoEntitySetsActionUser() {
        List<ProcedureMovement> movs = new ArrayList<>();
        movs.add(ProcedureMovement.builder()
                .actionUserId(42L)
                .action("APROBADO")
                .previousStatus(ProcedureStatus.REGISTRADO)
                .newStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movementAt(DATE)
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
    @DisplayName("findByStatus: entity with non-null projectReference maps correctly")
    void findByStatus_entityWithProjectReference() {
        ProcedureEntity entity = buildEntity(1);
        ProjectEntity pe = new ProjectEntity();
        pe.setId(42);
        entity.setProjectReference(pe);

        when(procedureRepository.findByStatus("PENDIENTE_COORDINADOR"))
                .thenReturn(List.of(entity));

        List<Procedure> result = adapter.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);

        assertEquals(1, result.size());
        assertEquals(42L, result.get(0).getProjectReferenceId());
    }

    @Test
    @DisplayName("save: skips projectReference when projectReferenceId is null")
    void save_skipsProjectReferenceWhenNull() {
        Procedure domain = Procedure.builder()
                .id(1L)
                .code("TRM-2026-001")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(10L)
                .groupId(1L)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .projectReferenceId(null)
                .movements(new ArrayList<>())
                .build();

        when(userRepository.getReferenceById(10L)).thenReturn(buildApplicant());
        when(groupRepository.getReferenceById(1)).thenReturn(buildGroup());
        when(procedureRepository.save(any())).thenReturn(buildEntity(1));
        when(movementRepository.countByProcedure_Id(1L)).thenReturn(0L);

        Procedure result = adapter.save(domain);

        verify(projectRepository, never()).getReferenceById(anyInt());
        assertNotNull(result);
    }

    @Test
    @DisplayName("findByStatusAndGroupId should delegate and map entities")
    void findByStatusAndGroupId_returnsMappedProcedures() {
        when(procedureRepository.findByStatusAndGroupId("PENDIENTE_COORDINADOR", 5L))
                .thenReturn(List.of(buildEntity(1), buildEntity(2)));

        List<Procedure> result = adapter.findByStatusAndGroupId(ProcedureStatus.PENDIENTE_COORDINADOR, 5L);

        assertEquals(2, result.size());
        verify(procedureRepository).findByStatusAndGroupId("PENDIENTE_COORDINADOR", 5L);
    }

    @Test
    @DisplayName("findByStatusAndGroupId should return empty when no entities")
    void findByStatusAndGroupId_returnsEmpty() {
        when(procedureRepository.findByStatusAndGroupId("PENDIENTE_DIRECCION", 99L))
                .thenReturn(List.of());

        List<Procedure> result = adapter.findByStatusAndGroupId(ProcedureStatus.PENDIENTE_DIRECCION, 99L);

        assertTrue(result.isEmpty());
        verify(procedureRepository).findByStatusAndGroupId("PENDIENTE_DIRECCION", 99L);
    }

    @Test
    @DisplayName("findByReviewerRole should return mapped list")
    void findByReviewerRole_returnsList() {
        when(procedureRepository.findByReviewerRole("COORDINADOR_GRUPO")).thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByReviewerRole(RoleEnum.COORDINADOR_GRUPO);

        assertEquals(1, result.size());
        verify(procedureRepository).findByReviewerRole("COORDINADOR_GRUPO");
    }

    @Test
    @DisplayName("findByStatusAndReviewerRole should return mapped list")
    void findByStatusAndReviewerRole_returnsList() {
        when(procedureRepository.findByStatusAndReviewerRole("PENDIENTE_COORDINADOR", "COORDINADOR_GRUPO"))
                .thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByStatusAndReviewerRole(
                ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO
        );

        assertEquals(1, result.size());
        verify(procedureRepository).findByStatusAndReviewerRole("PENDIENTE_COORDINADOR", "COORDINADOR_GRUPO");
    }

    @Test
    @DisplayName("save: movement sets correlationId from context when present")
    void save_setsCorrelationIdFromContext() {
        List<ProcedureMovement> movs = List.of(ProcedureMovement.builder()
                .action("APROBADO")
                .movementAt(DATE)
                .build());

        Procedure domain = Procedure.builder()
                .id(1L)
                .code("TRM-2026-001")
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movements(movs)
                .build();

        when(procedureRepository.save(any())).thenReturn(buildEntity(1));
        when(movementRepository.countByProcedure_Id(1L)).thenReturn(0L);
        when(correlationContext.getCorrelationId()).thenReturn("corr-1234");

        adapter.save(domain);

        verify(correlationContext).getCorrelationId();
        verify(movementRepository).save(argThat(entity -> "corr-1234".equals(entity.getCorrelationId())));
    }
}
