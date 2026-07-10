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
class ProcedureRepositoryAdapterTest {

    @Mock private SpringDataProcedureRepository tramiteRepository;
    @Mock private SpringDataProcedureMovementRepository movimientoRepository;
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
        when(tramiteRepository.save(any())).thenReturn(buildEntity(1));
        when(movimientoRepository.countByProcedure_Id(1)).thenReturn(0L);

        Procedure result = adapter.save(buildDomain());

        assertEquals(1L, result.getId());
        assertEquals("TRM-2026-001", result.getCode());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, result.getCurrentStatus());
        verify(tramiteRepository).save(any());
    }

    @Test
    @DisplayName("findById: found → returns mapped domain")
    void findById_found_returnsDomain() {
        when(tramiteRepository.findById(1)).thenReturn(Optional.of(buildEntity(1)));
        when(movimientoRepository.findByProcedure_IdOrderByMovementAtAsc(1)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("TRM-2026-001", result.get().getCode());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, result.get().getCurrentReviewerRole());
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
        when(movimientoRepository.findByProcedure_IdOrderByMovementAtAsc(1)).thenReturn(List.of());

        Optional<Procedure> result = adapter.findByCode("TRM-2026-001");

        assertTrue(result.isPresent());
        assertEquals(ProcedureType.PROJECT, result.get().getProcedureType());
    }

    @Test
    @DisplayName("findByApplicantId: returns list of mapped procedures")
    void findByApplicantId_returnsMappedList() {
        when(tramiteRepository.findByApplicant_Id(10L)).thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByApplicantId(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getApplicantId());
    }

    @Test
    @DisplayName("findByStatus: returns list filtered by status")
    void findByStatus_returnsMappedList() {
        when(tramiteRepository.findByStatus("PENDIENTE_COORDINADOR"))
                .thenReturn(List.of(buildEntity(1)));

        List<Procedure> result = adapter.findByStatus(ProcedureStatus.PENDIENTE_COORDINADOR);

        assertEquals(1, result.size());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, result.get(0).getCurrentStatus());
    }

    @Test
    @DisplayName("existsByCode: delegates to repository")
    void existsByCode_delegatesToRepository() {
        when(tramiteRepository.existsByCode("TRM-2026-001")).thenReturn(true);

        assertTrue(adapter.existsByCode("TRM-2026-001"));
        verify(tramiteRepository).existsByCode("TRM-2026-001");
    }
}
