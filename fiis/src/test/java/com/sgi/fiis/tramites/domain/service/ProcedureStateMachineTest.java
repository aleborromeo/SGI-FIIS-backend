package com.sgi.fiis.tramites.domain.service;

import com.sgi.fiis.tramites.domain.model.*;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.sgi.fiis.tramites.domain.model.ProcedureStatus.*;
import static com.sgi.fiis.users.domain.model.RoleEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcedureStateMachine - Servicio de dominio del flujo de aprobación institucional")
class ProcedureStateMachineTest {

    private static final Long ID_SOLICITANTE = 10L;
    private static final Long ID_COORDINADOR = 20L;
    private static final Long ID_DIRECTOR    = 30L;
    private static final Long ID_DECANO      = 40L;
    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 1, 1, 9, 0);

    private ProcedureStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new ProcedureStateMachine();
    }

    // -------------------------------------------------------------------------
    // Helpers de construcción por estado de inicio
    // -------------------------------------------------------------------------

    private Procedure tramiteEnPendienteCoordinador() {
        return Procedure.builder()
                .codigoTramite("TRM-001").tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(PENDIENTE_COORDINADOR).rolRevisorActual(COORDINADOR_GRUPO)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();
    }

    private Procedure tramiteEnObservado() {
        return Procedure.builder()
                .codigoTramite("TRM-001").tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(OBSERVADO).rolRevisorActual(null)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();
    }

    private Procedure tramiteEnPendienteDireccion() {
        return Procedure.builder()
                .codigoTramite("TRM-001").tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(PENDIENTE_DIRECCION).rolRevisorActual(DIRECTOR_INVESTIGACION)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();
    }

    private Procedure tramiteEnPendienteDecanato() {
        return Procedure.builder()
                .codigoTramite("TRM-001").tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(PENDIENTE_DECANATO).rolRevisorActual(DECANO)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();
    }

    // -------------------------------------------------------------------------
    // aprobarPorCoordinador
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("aprobarPorCoordinador avanza a PENDIENTE_DIRECCION y asigna DIRECTOR como próximo revisor")
    void aprobarPorCoordinador_avanzaADireccionYAsignaDirectorComoRevisor() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        stateMachine.aprobarPorCoordinador(tramite, ID_COORDINADOR);

        assertEquals(PENDIENTE_DIRECCION, tramite.getEstadoActual());
        assertEquals(DIRECTOR_INVESTIGACION, tramite.getRolRevisorActual());
        assertEquals(1, tramite.getMovements().size());
    }

    // -------------------------------------------------------------------------
    // observarPorCoordinador
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("observarPorCoordinador pasa a OBSERVADO y deja rolRevisorActual en null")
    void observarPorCoordinador_pasaAObservadoConRolRevisorNull() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        stateMachine.observarPorCoordinador(tramite, ID_COORDINADOR, "Falta la firma del asesor");

        assertEquals(OBSERVADO, tramite.getEstadoActual());
        assertNull(tramite.getRolRevisorActual());
        assertEquals("Falta la firma del asesor", tramite.getObservacionActual());
    }

    // -------------------------------------------------------------------------
    // rechazarPorCoordinador
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("rechazarPorCoordinador pasa al estado terminal RECHAZADO")
    void rechazarPorCoordinador_pasaARechazado() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        stateMachine.rechazarPorCoordinador(tramite, ID_COORDINADOR);

        assertEquals(RECHAZADO, tramite.getEstadoActual());
        assertTrue(tramite.getEstadoActual().isTerminalStatus());
    }

    // -------------------------------------------------------------------------
    // remediateByApplicant
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("remediateByApplicant con solicitante correcto termina en PENDIENTE_COORDINADOR")
    void remediateByApplicant_conSolicitanteCorrecto_finalizaEnPendienteCoordinador() {
        Procedure tramite = tramiteEnObservado();

        stateMachine.remediateByApplicant(tramite, ID_SOLICITANTE, "Se adjuntó la firma");

        assertEquals(PENDIENTE_COORDINADOR, tramite.getEstadoActual());
        assertEquals(COORDINADOR_GRUPO, tramite.getRolRevisorActual());
    }

    @Test
    @DisplayName("remediateByApplicant registra dos movimientos: la subsanación y el reenvío automático")
    void remediateByApplicant_registraDosMovimientosEnElHistorial() {
        Procedure tramite = tramiteEnObservado();

        stateMachine.remediateByApplicant(tramite, ID_SOLICITANTE, "Se adjuntó la firma");

        assertEquals(2, tramite.getMovements().size());
        assertEquals(OBSERVADO,              tramite.getMovements().get(0).getEstadoAnterior());
        assertEquals(SUBSANADO,              tramite.getMovements().get(0).getEstadoNuevo());
        assertEquals(SUBSANADO,              tramite.getMovements().get(1).getEstadoAnterior());
        assertEquals(PENDIENTE_COORDINADOR,  tramite.getMovements().get(1).getEstadoNuevo());
    }

    @Test
    @DisplayName("remediateByApplicant con id de usuario ajeno lanza InvalidTransitionException")
    void remediateByApplicant_conIdIncorrecto_lanzaExcepcion() {
        Procedure tramite = tramiteEnObservado();

        assertThrows(InvalidTransitionException.class, () ->
                stateMachine.remediateByApplicant(tramite, 99L, "Intento no autorizado")
        );
    }

    // -------------------------------------------------------------------------
    // aprobarPorDirector
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("aprobarPorDirector avanza a PENDIENTE_DECANATO y asigna DECANO como próximo revisor")
    void aprobarPorDirector_avanzaADecanatoYAsignaDecanoComoRevisor() {
        Procedure tramite = tramiteEnPendienteDireccion();

        stateMachine.aprobarPorDirector(tramite, ID_DIRECTOR);

        assertEquals(PENDIENTE_DECANATO, tramite.getEstadoActual());
        assertEquals(DECANO, tramite.getRolRevisorActual());
    }

    // -------------------------------------------------------------------------
    // observarPorDirector — RN-07: devuelve al Coordinador, NO al solicitante
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("observarPorDirector pasa a OBSERVADO y asigna COORDINADOR_GRUPO como revisor (RN-07)")
    void observarPorDirector_asignaCoordinadorComoRolRevisor() {
        Procedure tramite = tramiteEnPendienteDireccion();

        stateMachine.observarPorDirector(tramite, ID_DIRECTOR, "El presupuesto no está justificado");

        assertEquals(OBSERVADO, tramite.getEstadoActual());
        assertEquals(COORDINADOR_GRUPO, tramite.getRolRevisorActual());
    }

    // -------------------------------------------------------------------------
    // rechazarPorDirector
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("rechazarPorDirector pasa al estado terminal RECHAZADO")
    void rechazarPorDirector_pasaARechazado() {
        Procedure tramite = tramiteEnPendienteDireccion();

        stateMachine.rechazarPorDirector(tramite, ID_DIRECTOR);

        assertEquals(RECHAZADO, tramite.getEstadoActual());
        assertTrue(tramite.getEstadoActual().isTerminalStatus());
    }

    // -------------------------------------------------------------------------
    // registrarResolucionPorDecano
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("registrarResolucionPorDecano ejecuta dos transiciones y deja el trámite en FINALIZADO")
    void registrarResolucionPorDecano_finalizaElTramiteCompletamente() {
        Procedure tramite = tramiteEnPendienteDecanato();

        stateMachine.registrarResolucionPorDecano(tramite, ID_DECANO);

        assertEquals(FINALIZADO, tramite.getEstadoActual());
        assertTrue(tramite.getEstadoActual().isTerminalStatus());
        assertEquals(2, tramite.getMovements().size());
        assertEquals(APROBADO_CON_RESOLUCION, tramite.getMovements().get(0).getEstadoNuevo());
        assertEquals(FINALIZADO,              tramite.getMovements().get(1).getEstadoNuevo());
    }

    // -------------------------------------------------------------------------
    // observarPorDecano — RN-07: devuelve al Director
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("observarPorDecano pasa a OBSERVADO y asigna DIRECTOR_INVESTIGACION como revisor (RN-07)")
    void observarPorDecano_asignaDirectorComoRolRevisor() {
        Procedure tramite = tramiteEnPendienteDecanato();

        stateMachine.observarPorDecano(tramite, ID_DECANO, "Revisar el plan financiero");

        assertEquals(OBSERVADO, tramite.getEstadoActual());
        assertEquals(DIRECTOR_INVESTIGACION, tramite.getRolRevisorActual());
    }

    // -------------------------------------------------------------------------
    // Validación de rol — transversal a todos los métodos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Llamar a aprobarPorCoordinador con rol incorrecto lanza InvalidTransitionException")
    void aprobarPorCoordinador_conRolIncorrecto_lanzaExcepcion() {
        Procedure tramite = Procedure.builder()
                .codigoTramite("TRM-001").tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(PENDIENTE_COORDINADOR).rolRevisorActual(DIRECTOR_INVESTIGACION)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();

        assertThrows(InvalidTransitionException.class, () ->
                stateMachine.aprobarPorCoordinador(tramite, ID_DIRECTOR)
        );
    }

    @Test
    @DisplayName("Llamar a aprobarPorDirector cuando el revisor actual es el Coordinador lanza excepción")
    void aprobarPorDirector_conRolIncorrecto_lanzaExcepcion() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        assertThrows(InvalidTransitionException.class, () ->
                stateMachine.aprobarPorDirector(tramite, ID_DIRECTOR)
        );
    }
}
