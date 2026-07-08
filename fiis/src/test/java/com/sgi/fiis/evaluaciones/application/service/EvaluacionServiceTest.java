package com.sgi.fiis.evaluaciones.application.service;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.domain.exception.EvaluacionException;
import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.domain.ports.out.EvaluacionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepositoryPort evaluacionRepositoryPort;

    private EvaluacionService evaluacionService;

    @BeforeEach
    void setUp() {
        evaluacionService = new EvaluacionService(evaluacionRepositoryPort);
    }

    @Test
    void asignarEvaluadorAProyectoDebeGuardarEvaluacion() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(1L, null, 2L);

        when(evaluacionRepositoryPort.existeEvaluacionPendienteParaProyecto(1L, 2L))
                .thenReturn(false);

        when(evaluacionRepositoryPort.guardar(any(Evaluacion.class)))
                .thenReturn(Evaluacion.reconstruir(
                        1L,
                        1L,
                        null,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                ));

        EvaluacionResponse response = evaluacionService.asignarEvaluador(command);

        assertEquals(1L, response.idEvaluacion());
        assertEquals(1L, response.idProyecto());
        assertNull(response.idPlanTesis());
        assertEquals(2L, response.idEvaluador());
        assertTrue(response.pendiente());

        verify(evaluacionRepositoryPort).guardar(any(Evaluacion.class));
    }

    @Test
    void asignarEvaluadorAPlanTesisDebeGuardarEvaluacion() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(null, 5L, 2L);

        when(evaluacionRepositoryPort.existeEvaluacionPendienteParaPlanTesis(5L, 2L))
                .thenReturn(false);

        when(evaluacionRepositoryPort.guardar(any(Evaluacion.class)))
                .thenReturn(Evaluacion.reconstruir(
                        1L,
                        null,
                        5L,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                ));

        EvaluacionResponse response = evaluacionService.asignarEvaluador(command);

        assertEquals(1L, response.idEvaluacion());
        assertNull(response.idProyecto());
        assertEquals(5L, response.idPlanTesis());
        assertEquals(2L, response.idEvaluador());
        assertTrue(response.pendiente());
    }

    @Test
    void asignarEvaluadorConProyectoDuplicadoDebeLanzarExcepcion() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(1L, null, 2L);

        when(evaluacionRepositoryPort.existeEvaluacionPendienteParaProyecto(1L, 2L))
                .thenReturn(true);

        assertThrows(EvaluacionException.class, () -> evaluacionService.asignarEvaluador(command));

        verify(evaluacionRepositoryPort, never()).guardar(any(Evaluacion.class));
    }

    @Test
    void asignarEvaluadorSinProyectoNiPlanDebeLanzarExcepcion() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(null, null, 2L);

        assertThrows(EvaluacionException.class, () -> evaluacionService.asignarEvaluador(command));
    }

    @Test
    void asignarEvaluadorConProyectoYPlanDebeLanzarExcepcion() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(1L, 5L, 2L);

        assertThrows(EvaluacionException.class, () -> evaluacionService.asignarEvaluador(command));
    }

    @Test
    void asignarEvaluadorSinEvaluadorDebeLanzarExcepcion() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(1L, null, null);

        assertThrows(EvaluacionException.class, () -> evaluacionService.asignarEvaluador(command));
    }

    @Test
    void registrarResultadoDebeActualizarEvaluacion() {
        RegistrarResultadoEvaluacionCommand command = new RegistrarResultadoEvaluacionCommand(
                1L,
                2L,
                ResultadoEvaluacion.APROBADO,
                90,
                "Cumple con los criterios establecidos."
        );

        Evaluacion evaluacion = Evaluacion.reconstruir(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null
        );

        when(evaluacionRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(evaluacion));
        when(evaluacionRepositoryPort.guardar(any(Evaluacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EvaluacionResponse response = evaluacionService.registrarResultado(command);

        assertEquals(ResultadoEvaluacion.APROBADO, response.resultado());
        assertEquals(90, response.puntaje());
        assertEquals("Cumple con los criterios establecidos.", response.observaciones());
        assertFalse(response.pendiente());

        verify(evaluacionRepositoryPort).guardar(any(Evaluacion.class));
    }

    @Test
    void registrarResultadoConEvaluadorIncorrectoDebeLanzarExcepcion() {
        RegistrarResultadoEvaluacionCommand command = new RegistrarResultadoEvaluacionCommand(
                1L,
                99L,
                ResultadoEvaluacion.APROBADO,
                90,
                "Observación"
        );

        Evaluacion evaluacion = Evaluacion.reconstruir(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null
        );

        when(evaluacionRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(evaluacion));

        assertThrows(EvaluacionException.class, () -> evaluacionService.registrarResultado(command));

        verify(evaluacionRepositoryPort, never()).guardar(any(Evaluacion.class));
    }

    @Test
    void registrarResultadoDeEvaluacionNoExistenteDebeLanzarExcepcion() {
        RegistrarResultadoEvaluacionCommand command = new RegistrarResultadoEvaluacionCommand(
                1L,
                2L,
                ResultadoEvaluacion.APROBADO,
                90,
                "Observación"
        );

        when(evaluacionRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(EvaluacionException.class, () -> evaluacionService.registrarResultado(command));
    }

    @Test
    void listarPorEvaluadorDebeRetornarLista() {
        Evaluacion evaluacion = Evaluacion.reconstruir(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null
        );

        when(evaluacionRepositoryPort.listarPorEvaluador(2L)).thenReturn(List.of(evaluacion));

        List<EvaluacionResponse> response = evaluacionService.listarPorEvaluador(2L);

        assertEquals(1, response.size());
        assertEquals(2L, response.get(0).idEvaluador());
    }

    @Test
    void listarPorEvaluadorSinIdDebeLanzarExcepcion() {
        assertThrows(EvaluacionException.class, () -> evaluacionService.listarPorEvaluador(null));
    }

    @Test
    void listarTodasDebeRetornarLista() {
        Evaluacion evaluacion = Evaluacion.reconstruir(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null
        );

        when(evaluacionRepositoryPort.listarTodas()).thenReturn(List.of(evaluacion));

        List<EvaluacionResponse> response = evaluacionService.listarTodas();

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).idEvaluacion());
    }

    @Test
    void buscarPorIdDebeRetornarEvaluacion() {
        Evaluacion evaluacion = Evaluacion.reconstruir(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null
        );

        when(evaluacionRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(evaluacion));

        EvaluacionResponse response = evaluacionService.buscarPorId(1L);

        assertEquals(1L, response.idEvaluacion());
    }

    @Test
    void buscarPorIdNoExistenteDebeLanzarExcepcion() {
        when(evaluacionRepositoryPort.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(EvaluacionException.class, () -> evaluacionService.buscarPorId(1L));
    }

    @Test
    void buscarPorIdNuloDebeLanzarExcepcion() {
        assertThrows(EvaluacionException.class, () -> evaluacionService.buscarPorId(null));
    }

    @Test
void asignarEvaluadorConCommandNuloDebeLanzarExcepcion() {
    assertThrows(EvaluacionException.class, () -> evaluacionService.asignarEvaluador(null));
}

@Test
void asignarEvaluadorConPlanTesisDuplicadoDebeLanzarExcepcion() {
    AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(null, 5L, 2L);

    when(evaluacionRepositoryPort.existeEvaluacionPendienteParaPlanTesis(5L, 2L))
            .thenReturn(true);

    assertThrows(EvaluacionException.class, () -> evaluacionService.asignarEvaluador(command));

    verify(evaluacionRepositoryPort, never()).guardar(any(Evaluacion.class));
}

@Test
void registrarResultadoConCommandNuloDebeLanzarExcepcion() {
    assertThrows(EvaluacionException.class, () -> evaluacionService.registrarResultado(null));
}

@Test
void registrarResultadoSinIdEvaluacionDebeLanzarExcepcion() {
    RegistrarResultadoEvaluacionCommand command = new RegistrarResultadoEvaluacionCommand(
            null,
            2L,
            ResultadoEvaluacion.APROBADO,
            90,
            "Observación"
    );

    assertThrows(EvaluacionException.class, () -> evaluacionService.registrarResultado(command));
}

@Test
void registrarResultadoSinIdEvaluadorDebeLanzarExcepcion() {
    RegistrarResultadoEvaluacionCommand command = new RegistrarResultadoEvaluacionCommand(
            1L,
            null,
            ResultadoEvaluacion.APROBADO,
            90,
            "Observación"
    );

    assertThrows(EvaluacionException.class, () -> evaluacionService.registrarResultado(command));
}

@Test
void registrarResultadoSinResultadoDebeLanzarExcepcion() {
    RegistrarResultadoEvaluacionCommand command = new RegistrarResultadoEvaluacionCommand(
            1L,
            2L,
            null,
            90,
            "Observación"
    );

    assertThrows(EvaluacionException.class, () -> evaluacionService.registrarResultado(command));
}

    @Test
    void asignarEvaluadorConSoloProyectoValidoDebeCumplirArcoExcluyente() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(10L, null, 20L);
        LocalDateTime fechaAsignacion = LocalDateTime.now();

        when(evaluacionRepositoryPort.existeEvaluacionPendienteParaProyecto(10L, 20L))
                .thenReturn(false);

        when(evaluacionRepositoryPort.guardar(any(Evaluacion.class)))
                .thenReturn(Evaluacion.reconstruir(
                        1L,
                        10L,
                        null,
                        20L,
                        null,
                        null,
                        null,
                        fechaAsignacion,
                        null
                ));

        EvaluacionResponse response = evaluacionService.asignarEvaluador(command);

        assertEquals(10L, response.idProyecto());
        assertNull(response.idPlanTesis());
        assertEquals(20L, response.idEvaluador());
        assertTrue(response.pendiente());

        verify(evaluacionRepositoryPort).existeEvaluacionPendienteParaProyecto(10L, 20L);
        verify(evaluacionRepositoryPort, never()).existeEvaluacionPendienteParaPlanTesis(any(), any());
        verify(evaluacionRepositoryPort).guardar(any(Evaluacion.class));
    }

    @Test
    void asignarEvaluadorConSoloPlanTesisValidoDebeCumplirArcoExcluyente() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(null, 30L, 20L);
        LocalDateTime fechaAsignacion = LocalDateTime.now();

        when(evaluacionRepositoryPort.existeEvaluacionPendienteParaPlanTesis(30L, 20L))
                .thenReturn(false);

        when(evaluacionRepositoryPort.guardar(any(Evaluacion.class)))
                .thenReturn(Evaluacion.reconstruir(
                        1L,
                        null,
                        30L,
                        20L,
                        null,
                        null,
                        null,
                        fechaAsignacion,
                        null
                ));

        EvaluacionResponse response = evaluacionService.asignarEvaluador(command);

        assertNull(response.idProyecto());
        assertEquals(30L, response.idPlanTesis());
        assertEquals(20L, response.idEvaluador());
        assertTrue(response.pendiente());

        verify(evaluacionRepositoryPort).existeEvaluacionPendienteParaPlanTesis(30L, 20L);
        verify(evaluacionRepositoryPort, never()).existeEvaluacionPendienteParaProyecto(any(), any());
        verify(evaluacionRepositoryPort).guardar(any(Evaluacion.class));
    }

    @Test
    void asignarEvaluadorConProyectoYPlanTesisDebeRechazarArcoExcluyente() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(10L, 30L, 20L);

        EvaluacionException exception = assertThrows(
                EvaluacionException.class,
                () -> evaluacionService.asignarEvaluador(command)
        );

        assertEquals(
                "Debe asignar la evaluación a un proyecto o a un plan de tesis, no a ambos.",
                exception.getMessage()
        );

        verify(evaluacionRepositoryPort, never()).guardar(any(Evaluacion.class));
    }

    @Test
    void asignarEvaluadorSinProyectoNiPlanTesisDebeRechazarArcoExcluyente() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(null, null, 20L);

        EvaluacionException exception = assertThrows(
                EvaluacionException.class,
                () -> evaluacionService.asignarEvaluador(command)
        );

        assertEquals(
                "Debe indicar un proyecto o un plan de tesis para la evaluación.",
                exception.getMessage()
        );

        verify(evaluacionRepositoryPort, never()).guardar(any(Evaluacion.class));
    }

    @Test
    void asignarEvaluadorDuplicadoDebeLanzarExcepcion() {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(10L, null, 20L);

        when(evaluacionRepositoryPort.existeEvaluacionPendienteParaProyecto(10L, 20L))
                .thenReturn(true);

        EvaluacionException exception = assertThrows(
                EvaluacionException.class,
                () -> evaluacionService.asignarEvaluador(command)
        );

        assertEquals(
                "El evaluador ya tiene una evaluación pendiente para este proyecto.",
                exception.getMessage()
        );

        verify(evaluacionRepositoryPort).existeEvaluacionPendienteParaProyecto(10L, 20L);
        verify(evaluacionRepositoryPort, never()).guardar(any(Evaluacion.class));
    }

}