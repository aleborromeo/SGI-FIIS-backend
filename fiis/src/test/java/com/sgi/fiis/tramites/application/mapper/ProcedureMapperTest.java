package com.sgi.fiis.tramites.application.mapper;

import com.sgi.fiis.tramites.application.dto.ProcedureMovementResponseDto;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ProcedureMapperTest {

    @Test
    void toResponse_mapeaTodosLosCamposCorrectamente() {
        LocalDateTime ahora = LocalDateTime.of(2026, Month.JUNE, 17, 10, 0);

        Procedure tramite = Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-000001")
                .tipoTramite(ProcedureType.PROYECTO)
                .estadoActual(ProcedureStatus.PENDIENTE_COORDINADOR)
                .idSolicitante(42L)
                .idGrupo(5L)
                .rolRevisorActual(RoleEnum.COORDINADOR_GRUPO)
                .observacionActual(null)
                .idReferenciaProyecto(100L)
                .idReferenciaTesis(null)
                .idReferenciaInforme(null)
                .fechaEnvio(ahora)
                .fechaActualizacion(ahora)
                .build();

        ProcedureResponseDto dto = ProcedureMapper.toResponse(tramite);

        assertEquals(1L, dto.getId());
        assertEquals("TRM-2026-000001", dto.getCodigoTramite());
        assertEquals(ProcedureType.PROYECTO, dto.getTipoTramite());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, dto.getEstadoActual());
        assertEquals(42L, dto.getIdSolicitante());
        assertEquals(5L, dto.getIdGrupo());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, dto.getRolRevisorActual());
        assertNull(dto.getObservacionActual());
        assertEquals(100L, dto.getIdReferenciaProyecto());
        assertNull(dto.getIdReferenciaTesis());
        assertNull(dto.getIdReferenciaInforme());
        assertEquals(ahora, dto.getFechaEnvio());
        assertEquals(ahora, dto.getFechaActualizacion());
    }

    @Test
    void toMovimientoResponse_mapeaTodosLosCamposCorrectamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, Month.JUNE, 17, 11, 0);

        ProcedureMovement movimiento = ProcedureMovement.builder()
                .idUsuarioAccion(10L)
                .accion("APROBADO_POR_COORDINADOR")
                .estadoAnterior(ProcedureStatus.PENDIENTE_COORDINADOR)
                .estadoNuevo(ProcedureStatus.PENDIENTE_DIRECCION)
                .observacion(null)
                .fechaMovimiento(fecha)
                .build();

        ProcedureMovementResponseDto dto = ProcedureMapper.toMovimientoResponse(movimiento);

        assertEquals(10L, dto.getIdUsuarioAccion());
        assertEquals("APROBADO_POR_COORDINADOR", dto.getAccion());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, dto.getEstadoAnterior());
        assertEquals(ProcedureStatus.PENDIENTE_DIRECCION, dto.getEstadoNuevo());
        assertNull(dto.getObservacion());
        assertEquals(fecha, dto.getFechaMovimiento());
    }

    @Test
    void testConstructorIsPrivate() throws NoSuchMethodException {
        java.lang.reflect.Constructor<ProcedureMapper> constructor = ProcedureMapper.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
    }
}

