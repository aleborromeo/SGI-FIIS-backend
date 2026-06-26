package com.sgi.fiis.tramites.domain.model;

import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.sgi.fiis.tramites.domain.model.ProcedureStatus.*;
import static com.sgi.fiis.users.domain.model.RoleEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Procedure - Entidad raíz del módulo tramites")
class ProcedureTest {

    private static final Long ID_SOLICITANTE = 10L;
    private static final Long ID_COORDINADOR = 20L;
    private static final LocalDateTime FECHA_INICIAL = LocalDateTime.of(2026, 1, 1, 9, 0);

    private Procedure tramiteEnPendienteCoordinador() {
        return Procedure.builder()
                .codigoTramite("TRM-001")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE)
                .idGrupo(1L)
                .estadoActual(PENDIENTE_COORDINADOR)
                .rolRevisorActual(COORDINADOR_GRUPO)
                .idReferenciaProyecto(100L)
                .fechaEnvio(FECHA_INICIAL)
                .fechaActualizacion(FECHA_INICIAL)
                .build();
    }

    // -------------------------------------------------------------------------
    // transitionTo — casos válidos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("transitionTo con transición válida actualiza el estado actual")
    void transitionTo_conTransicionValida_actualizaEstadoActual() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        tramite.transitionTo(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertEquals(PENDIENTE_DIRECCION, tramite.getEstadoActual());
    }

    @Test
    @DisplayName("transitionTo con transición válida agrega un movimiento al historial")
    void transitionTo_conTransicionValida_agregaMovimientoAlHistorial() {
        Procedure tramite = tramiteEnPendienteCoordinador();
        assertEquals(0, tramite.getMovements().size());

        tramite.transitionTo(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertEquals(1, tramite.getMovements().size());
    }

    @Test
    @DisplayName("transitionTo registra correctamente el estado anterior y el nuevo en el movimiento")
    void transitionTo_movimientoContieneEstadosCorrectos() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        tramite.transitionTo(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        ProcedureMovement movimiento = tramite.getMovements().get(0);
        assertEquals(PENDIENTE_COORDINADOR, movimiento.getEstadoAnterior());
        assertEquals(PENDIENTE_DIRECCION, movimiento.getEstadoNuevo());
        assertEquals(ID_COORDINADOR, movimiento.getIdUsuarioAccion());
        assertEquals("APROBADO_POR_COORDINADOR", movimiento.getAccion());
    }

    @Test
    @DisplayName("transitionTo actualiza fechaActualizacion en cada transición")
    void transitionTo_actualizaFechaActualizacion() {
        Procedure tramite = tramiteEnPendienteCoordinador();
        LocalDateTime tiempoAntes = tramite.getFechaActualizacion();

        tramite.transitionTo(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertNotEquals(tiempoAntes, tramite.getFechaActualizacion());
    }

    @Test
    @DisplayName("transitionTo múltiples veces crece el historial de movimientos correctamente")
    void transitionTo_multiplesVeces_historialCrecePorCadaTransicion() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        tramite.transitionTo(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);
        tramite.transitionTo(PENDIENTE_DECANATO, DIRECTOR_INVESTIGACION, 30L,
                "APROBADO_POR_DIRECTOR", null, DECANO);

        assertEquals(2, tramite.getMovements().size());
        assertEquals(PENDIENTE_DECANATO, tramite.getEstadoActual());
    }

    @Test
    @DisplayName("transitionTo guarda la observación en el movimiento cuando está presente")
    void transitionTo_conObservacion_laGuardaEnElMovimiento() {
        Procedure tramite = tramiteEnPendienteCoordinador();
        String textoObservacion = "Falta la firma del asesor";

        tramite.transitionTo(OBSERVADO, COORDINADOR_GRUPO, ID_COORDINADOR,
                "OBSERVADO_POR_COORDINADOR", textoObservacion, null);

        assertEquals(textoObservacion, tramite.getMovements().get(0).getObservacion());
        assertEquals(textoObservacion, tramite.getObservacionActual());
    }

    // -------------------------------------------------------------------------
    // transitionTo — casos inválidos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("transitionTo con transición inválida lanza InvalidTransitionException")
    void transitionTo_conTransicionInvalida_lanzaExcepcion() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        assertThrows(InvalidTransitionException.class, () ->
                tramite.transitionTo(FINALIZADO, COORDINADOR_GRUPO, ID_COORDINADOR,
                        "ACCION_INVALIDA", null, null)
        );
    }

    @Test
    @DisplayName("transitionTo inválida no modifica el estado del trámite")
    void transitionTo_conTransicionInvalida_noModificaElEstado() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        assertThrows(InvalidTransitionException.class, () ->
                tramite.transitionTo(APROBADO_CON_RESOLUCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                        "ACCION_INVALIDA", null, null)
        );

        assertEquals(PENDIENTE_COORDINADOR, tramite.getEstadoActual());
        assertEquals(0, tramite.getMovements().size());
    }

    // -------------------------------------------------------------------------
    // getMovements — inmutabilidad
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getMovements retorna una lista inmutable que no permite modificaciones externas")
    void getMovimientos_retornaVistaInmutable() {
        Procedure tramite = tramiteEnPendienteCoordinador();
        tramite.transitionTo(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertThrows(UnsupportedOperationException.class, () ->
                tramite.getMovements().clear()
        );
    }

    // -------------------------------------------------------------------------
    // validateExclusiveReference
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("validateExclusiveReference con exactamente una referencia no lanza excepción")
    void validateExclusiveReference_conUnaReferencia_esValido() {
        Procedure tramite = Procedure.builder()
                .codigoTramite("TRM-002")
                .tipoTramite(ProcedureType.PLAN_TESIS)
                .idSolicitante(ID_SOLICITANTE)
                .idGrupo(1L)
                .estadoActual(REGISTRADO)
                .rolRevisorActual(COORDINADOR_GRUPO)
                .idReferenciaTesis(50L)
                .fechaEnvio(FECHA_INICIAL)
                .fechaActualizacion(FECHA_INICIAL)
                .build();

        assertDoesNotThrow(tramite::validateExclusiveReference);
    }

    @Test
    @DisplayName("validateExclusiveReference sin ninguna referencia lanza IllegalArgumentException")
    void validateExclusiveReference_sinReferencias_lanzaExcepcion() {
        Procedure tramite = Procedure.builder()
                .codigoTramite("TRM-003")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE)
                .idGrupo(1L)
                .estadoActual(REGISTRADO)
                .rolRevisorActual(COORDINADOR_GRUPO)
                .fechaEnvio(FECHA_INICIAL)
                .fechaActualizacion(FECHA_INICIAL)
                .build();

        assertThrows(IllegalArgumentException.class, tramite::validateExclusiveReference);
    }

    @Test
    @DisplayName("validateExclusiveReference con múltiples referencias lanza IllegalArgumentException")
    void validateExclusiveReference_conMultiplesReferencias_lanzaExcepcion() {
        Procedure tramite = Procedure.builder()
                .codigoTramite("TRM-004")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE)
                .idGrupo(1L)
                .estadoActual(REGISTRADO)
                .rolRevisorActual(COORDINADOR_GRUPO)
                .idReferenciaProyecto(100L)
                .idReferenciaTesis(50L)
                .fechaEnvio(FECHA_INICIAL)
                .fechaActualizacion(FECHA_INICIAL)
                .build();

        assertThrows(IllegalArgumentException.class, tramite::validateExclusiveReference);
    }
}
