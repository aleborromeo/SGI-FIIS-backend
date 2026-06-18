package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.TramiteRequestDto;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.model.TipoTramite;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.users.domain.model.RolEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearTramiteUseCaseTest {

    @Mock
    private TramiteRepositoryPort tramiteRepositoryPort;

    @InjectMocks
    private CrearTramiteUseCase crearTramiteUseCase;

    @Test
    void execute_creaYPresentaTramite_estadoResultanteEsPendienteCoordinador() {
        TramiteRequestDto dto = TramiteRequestDto.builder()
                .tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(1L)
                .idGrupo(10L)
                .idReferenciaProyecto(100L)
                .build();

        Tramite tramiteGuardado = Tramite.builder()
                .id(1L)
                .codigoTramite("TRM-2026-000001")
                .tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(1L)
                .idGrupo(10L)
                .estadoActual(EstadoTramite.PENDIENTE_COORDINADOR)
                .rolRevisorActual(RolEnum.COORDINADOR_GRUPO)
                .idReferenciaProyecto(100L)
                .fechaEnvio(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        when(tramiteRepositoryPort.guardar(any(Tramite.class))).thenReturn(tramiteGuardado);

        TramiteResponseDto resultado = crearTramiteUseCase.execute(dto);

        assertEquals(EstadoTramite.PENDIENTE_COORDINADOR, resultado.getEstadoActual());
        assertEquals(RolEnum.COORDINADOR_GRUPO, resultado.getRolRevisorActual());
        assertEquals(TipoTramite.PROYECTO, resultado.getTipoTramite());
        verify(tramiteRepositoryPort).guardar(any(Tramite.class));
    }

    @Test
    void execute_tramiteGuardado_tieneTransicionRegistradoAPendienteCoordinador() {
        TramiteRequestDto dto = TramiteRequestDto.builder()
                .tipoTramite(TipoTramite.PLAN_TESIS)
                .idSolicitante(2L)
                .idReferenciaTesis(200L)
                .build();

        when(tramiteRepositoryPort.guardar(any(Tramite.class))).thenAnswer(inv -> inv.getArgument(0));

        crearTramiteUseCase.execute(dto);

        // El tramite fue guardado ya en PENDIENTE_COORDINADOR con un movimiento registrado
        ArgumentCaptor<Tramite> captor = ArgumentCaptor.forClass(Tramite.class);
        verify(tramiteRepositoryPort).guardar(captor.capture());

        Tramite tramiteCapturado = captor.getValue();
        assertEquals(EstadoTramite.PENDIENTE_COORDINADOR, tramiteCapturado.getEstadoActual());
        assertEquals(RolEnum.COORDINADOR_GRUPO, tramiteCapturado.getRolRevisorActual());
        assertEquals(1, tramiteCapturado.getMovimientos().size());
        assertEquals("PRESENTADO_POR_SOLICITANTE", tramiteCapturado.getMovimientos().get(0).getAccion());
    }

    @Test
    void execute_arcoExcluyenteInvalido_lanzaExcepcionSinGuardar() {
        TramiteRequestDto dto = TramiteRequestDto.builder()
                .tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(1L)
                .idReferenciaProyecto(100L)
                .idReferenciaTesis(200L) // dos referencias — inválido
                .build();

        assertThrows(IllegalArgumentException.class, () -> crearTramiteUseCase.execute(dto));
        verify(tramiteRepositoryPort, never()).guardar(any());
    }
}
