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
}
