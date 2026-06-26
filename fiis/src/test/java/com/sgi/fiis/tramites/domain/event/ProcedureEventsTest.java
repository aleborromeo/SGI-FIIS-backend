package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProcedureEventsTest {

    @Test
    void tramiteAprobadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 17, 10, 0);

        ProcedureApprovedEvent evento = ProcedureApprovedEvent.builder()
                .idTramite(1L)
                .codigoTramite("TRM-2026-001")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(42L)
                .estadoResultante(ProcedureStatus.PENDIENTE_COORDINADOR)
                .idAprobador(10L)
                .rolAprobador(RoleEnum.COORDINADOR_GRUPO)
                .fechaAprobacion(fecha)
                .build();

        assertEquals(1L, evento.getIdTramite());
        assertEquals("TRM-2026-001", evento.getCodigoTramite());
        assertEquals(ProcedureType.PROYECTO, evento.getTipoTramite());
        assertEquals(42L, evento.getIdSolicitante());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, evento.getEstadoResultante());
        assertEquals(10L, evento.getIdAprobador());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, evento.getRolAprobador());
        assertEquals(fecha, evento.getFechaAprobacion());
    }

    @Test
    void tramiteObservadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 17, 11, 30);

        ProcedureFlaggedEvent evento = ProcedureFlaggedEvent.builder()
                .idTramite(2L)
                .codigoTramite("TRM-2026-002")
                .tipoTramite(ProcedureType.PLAN_TESIS)
                .idSolicitante(55L)
                .idObservador(20L)
                .rolObservador(RoleEnum.DIRECTOR_INVESTIGACION)
                .textoObservacion("Falta bibliografía actualizada")
                .fechaObservacion(fecha)
                .build();

        assertEquals(2L, evento.getIdTramite());
        assertEquals("TRM-2026-002", evento.getCodigoTramite());
        assertEquals(ProcedureType.PLAN_TESIS, evento.getTipoTramite());
        assertEquals(55L, evento.getIdSolicitante());
        assertEquals(20L, evento.getIdObservador());
        assertEquals(RoleEnum.DIRECTOR_INVESTIGACION, evento.getRolObservador());
        assertEquals("Falta bibliografía actualizada", evento.getTextoObservacion());
        assertEquals(fecha, evento.getFechaObservacion());
    }

    @Test
    void tramiteFinalizadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 17, 15, 45);

        ProcedureFinalizedEvent evento = ProcedureFinalizedEvent.builder()
                .idTramite(3L)
                .codigoTramite("TRM-2026-003")
                .tipoTramite(ProcedureType.INFORME_AVANCE)
                .idSolicitante(77L)
                .fechaFinalizacion(fecha)
                .build();

        assertEquals(3L, evento.getIdTramite());
        assertEquals("TRM-2026-003", evento.getCodigoTramite());
        assertEquals(ProcedureType.INFORME_AVANCE, evento.getTipoTramite());
        assertEquals(77L, evento.getIdSolicitante());
        assertEquals(fecha, evento.getFechaFinalizacion());
    }
}
