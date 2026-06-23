package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.TipoTramite;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TramiteEventosTest {

    @Test
    void tramiteAprobadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 17, 10, 0);

        TramiteAprobadoEvent evento = TramiteAprobadoEvent.builder()
                .idTramite(1L)
                .codigoTramite("TRM-2026-001")
                .tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(42L)
                .estadoResultante(EstadoTramite.PENDIENTE_COORDINADOR)
                .idAprobador(10L)
                .rolAprobador(RoleEnum.COORDINADOR_GRUPO)
                .fechaAprobacion(fecha)
                .build();

        assertEquals(1L, evento.getIdTramite());
        assertEquals("TRM-2026-001", evento.getCodigoTramite());
        assertEquals(TipoTramite.PROYECTO, evento.getTipoTramite());
        assertEquals(42L, evento.getIdSolicitante());
        assertEquals(EstadoTramite.PENDIENTE_COORDINADOR, evento.getEstadoResultante());
        assertEquals(10L, evento.getIdAprobador());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, evento.getRolAprobador());
        assertEquals(fecha, evento.getFechaAprobacion());
    }

    @Test
    void tramiteObservadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 17, 11, 30);

        TramiteObservadoEvent evento = TramiteObservadoEvent.builder()
                .idTramite(2L)
                .codigoTramite("TRM-2026-002")
                .tipoTramite(TipoTramite.PLAN_TESIS)
                .idSolicitante(55L)
                .idObservador(20L)
                .rolObservador(RoleEnum.DIRECTOR_INVESTIGACION)
                .textoObservacion("Falta bibliografía actualizada")
                .fechaObservacion(fecha)
                .build();

        assertEquals(2L, evento.getIdTramite());
        assertEquals("TRM-2026-002", evento.getCodigoTramite());
        assertEquals(TipoTramite.PLAN_TESIS, evento.getTipoTramite());
        assertEquals(55L, evento.getIdSolicitante());
        assertEquals(20L, evento.getIdObservador());
        assertEquals(RoleEnum.DIRECTOR_INVESTIGACION, evento.getRolObservador());
        assertEquals("Falta bibliografía actualizada", evento.getTextoObservacion());
        assertEquals(fecha, evento.getFechaObservacion());
    }

    @Test
    void tramiteFinalizadoEvent_construyeCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 17, 15, 45);

        TramiteFinalizadoEvent evento = TramiteFinalizadoEvent.builder()
                .idTramite(3L)
                .codigoTramite("TRM-2026-003")
                .tipoTramite(TipoTramite.INFORME_AVANCE)
                .idSolicitante(77L)
                .fechaFinalizacion(fecha)
                .build();

        assertEquals(3L, evento.getIdTramite());
        assertEquals("TRM-2026-003", evento.getCodigoTramite());
        assertEquals(TipoTramite.INFORME_AVANCE, evento.getTipoTramite());
        assertEquals(77L, evento.getIdSolicitante());
        assertEquals(fecha, evento.getFechaFinalizacion());
    }
}
