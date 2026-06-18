package com.sgi.fiis.tramites.application.mapper;

import com.sgi.fiis.tramites.application.dto.MovimientoResponseDto;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.MovimientoTramite;
import com.sgi.fiis.tramites.domain.model.TipoTramite;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.users.domain.model.RolEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TramiteMapperTest {

    @Test
    void toResponse_mapeaTodosLosCamposCorrectamente() {
        LocalDateTime ahora = LocalDateTime.of(2026, 6, 17, 10, 0);

        Tramite tramite = Tramite.builder()
                .id(1L)
                .codigoTramite("TRM-2026-000001")
                .tipoTramite(TipoTramite.PROYECTO)
                .estadoActual(EstadoTramite.PENDIENTE_COORDINADOR)
                .idSolicitante(42L)
                .idGrupo(5L)
                .rolRevisorActual(RolEnum.COORDINADOR_GRUPO)
                .observacionActual(null)
                .idReferenciaProyecto(100L)
                .idReferenciaTesis(null)
                .idReferenciaInforme(null)
                .fechaEnvio(ahora)
                .fechaActualizacion(ahora)
                .build();

        TramiteResponseDto dto = TramiteMapper.toResponse(tramite);

        assertEquals(1L, dto.getId());
        assertEquals("TRM-2026-000001", dto.getCodigoTramite());
        assertEquals(TipoTramite.PROYECTO, dto.getTipoTramite());
        assertEquals(EstadoTramite.PENDIENTE_COORDINADOR, dto.getEstadoActual());
        assertEquals(42L, dto.getIdSolicitante());
        assertEquals(5L, dto.getIdGrupo());
        assertEquals(RolEnum.COORDINADOR_GRUPO, dto.getRolRevisorActual());
        assertNull(dto.getObservacionActual());
        assertEquals(100L, dto.getIdReferenciaProyecto());
        assertNull(dto.getIdReferenciaTesis());
        assertNull(dto.getIdReferenciaInforme());
        assertEquals(ahora, dto.getFechaEnvio());
        assertEquals(ahora, dto.getFechaActualizacion());
    }

    @Test
    void toMovimientoResponse_mapeaTodosLosCamposCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 17, 11, 0);

        MovimientoTramite movimiento = MovimientoTramite.builder()
                .idUsuarioAccion(10L)
                .accion("APROBADO_POR_COORDINADOR")
                .estadoAnterior(EstadoTramite.PENDIENTE_COORDINADOR)
                .estadoNuevo(EstadoTramite.PENDIENTE_DIRECCION)
                .observacion(null)
                .fechaMovimiento(fecha)
                .build();

        MovimientoResponseDto dto = TramiteMapper.toMovimientoResponse(movimiento);

        assertEquals(10L, dto.getIdUsuarioAccion());
        assertEquals("APROBADO_POR_COORDINADOR", dto.getAccion());
        assertEquals(EstadoTramite.PENDIENTE_COORDINADOR, dto.getEstadoAnterior());
        assertEquals(EstadoTramite.PENDIENTE_DIRECCION, dto.getEstadoNuevo());
        assertNull(dto.getObservacion());
        assertEquals(fecha, dto.getFechaMovimiento());
    }
}
