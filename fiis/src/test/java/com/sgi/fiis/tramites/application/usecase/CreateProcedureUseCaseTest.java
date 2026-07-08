package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.ProcedureRequestDto;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
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
class CreateProcedureUseCaseTest {

    @Mock
    private ProcedureRepositoryPort tramiteRepositoryPort;

    @InjectMocks
    private CreateProcedureUseCase crearTramiteUseCase;

    @Test
    void execute_creaYPresentaTramite_estadoResultanteEsPendienteCoordinador() {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(1L)
                .idGrupo(10L)
                .idReferenciaProyecto(100L)
                .build();

        Procedure tramiteGuardado = Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-000001")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(1L)
                .idGrupo(10L)
                .estadoActual(ProcedureStatus.PENDIENTE_COORDINADOR)
                .rolRevisorActual(RoleEnum.COORDINADOR_GRUPO)
                .idReferenciaProyecto(100L)
                .fechaEnvio(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        when(tramiteRepositoryPort.save(any(Procedure.class))).thenReturn(tramiteGuardado);

        ProcedureResponseDto resultado = crearTramiteUseCase.execute(dto);

        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, resultado.getEstadoActual());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, resultado.getRolRevisorActual());
        assertEquals(ProcedureType.PROYECTO, resultado.getTipoTramite());
        verify(tramiteRepositoryPort).save(any(Procedure.class));
    }

    @Test
    void execute_tramiteGuardado_tieneTransicionRegistradoAPendienteCoordinador() {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .tipoTramite(ProcedureType.PLAN_TESIS)
                .idSolicitante(2L)
                .idReferenciaTesis(200L)
                .build();

        when(tramiteRepositoryPort.save(any(Procedure.class))).thenAnswer(inv -> inv.getArgument(0));

        crearTramiteUseCase.execute(dto);

        // El tramite fue guardado ya en PENDIENTE_COORDINADOR con un movimiento registrado
        ArgumentCaptor<Procedure> captor = ArgumentCaptor.forClass(Procedure.class);
        verify(tramiteRepositoryPort).save(captor.capture());

        Procedure tramiteCapturado = captor.getValue();
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, tramiteCapturado.getEstadoActual());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, tramiteCapturado.getRolRevisorActual());
        assertEquals(1, tramiteCapturado.getMovements().size());
        assertEquals("PRESENTADO_POR_SOLICITANTE", tramiteCapturado.getMovements().get(0).getAccion());
    }

    @Test
    void execute_arcoExcluyenteInvalido_lanzaExcepcionSinGuardar() {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(1L)
                .idReferenciaProyecto(100L)
                .idReferenciaTesis(200L) // dos referencias — inválido
                .build();

        assertThrows(IllegalArgumentException.class, () -> crearTramiteUseCase.execute(dto));
        verify(tramiteRepositoryPort, never()).save(any());
    }
}
