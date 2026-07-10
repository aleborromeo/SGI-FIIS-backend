package com.sgi.fiis.tramites.domain.service;

import com.sgi.fiis.tramites.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static com.sgi.fiis.tramites.domain.model.ProcedureStatus.*;
import static com.sgi.fiis.users.domain.model.RoleEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcedureStateMachine - Servicio de dominio del flujo de aprobación institucional")
class ProcedureStateMachineTest {

    private static final Long ID_SOLICITANTE = 10L;
    private static final Long ID_COORDINADOR = 20L;
    private static final Long ID_DIRECTOR    = 30L;
    private static final Long ID_DECANO      = 40L;
    private static final LocalDateTime FECHA = LocalDateTime.of(2026, Month.JANUARY, 1, 9, 0);

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
                .code("TRM-001").procedureType(ProcedureType.PROJECT)
                .applicantId(ID_SOLICITANTE).groupId(1L)
                .currentStatus(PENDIENTE_COORDINADOR).currentReviewerRole(COORDINADOR_GRUPO)
                .projectReferenceId(100L).sentAt(FECHA).updatedAt(FECHA)
                .build();
    }

    private Procedure tramiteEnObservado() {
        return Procedure.builder()
                .code("TRM-001").procedureType(ProcedureType.PROJECT)
                .applicantId(ID_SOLICITANTE).groupId(1L)
                .currentStatus(OBSERVADO).currentReviewerRole(null)
                .projectReferenceId(100L).sentAt(FECHA).updatedAt(FECHA)
                .build();
    }

    private Procedure tramiteEnPendienteDireccion() {
        return Procedure.builder()
                .code("TRM-001").procedureType(ProcedureType.PROJECT)
                .applicantId(ID_SOLICITANTE).groupId(1L)
                .currentStatus(PENDIENTE_DIRECCION).currentReviewerRole(DIRECTOR_INVESTIGACION)
                .projectReferenceId(100L).sentAt(FECHA).updatedAt(FECHA)
                .build();
    }

    private Procedure tramiteEnPendienteDecanato() {
        return Procedure.builder()
                .code("TRM-001").procedureType(ProcedureType.PROJECT)
                .applicantId(ID_SOLICITANTE).groupId(1L)
                .currentStatus(PENDIENTE_DECANATO).currentReviewerRole(DECANO)
                .projectReferenceId(100L).sentAt(FECHA).updatedAt(FECHA)
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

        assertEquals(PENDIENTE_DIRECCION, tramite.getCurrentStatus());
        assertEquals(DIRECTOR_INVESTIGACION, tramite.getCurrentReviewerRole());
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

        assertEquals(OBSERVADO, tramite.getCurrentStatus());
        assertNull(tramite.getCurrentReviewerRole());
        assertEquals("Falta la firma del asesor", tramite.getCurrentObservation());
    }

    // -------------------------------------------------------------------------
    // rechazarPorCoordinador
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("rechazarPorCoordinador pasa al estado terminal RECHAZADO")
    void rechazarPorCoordinador_pasaARechazado() {
        Procedure tramite = tramiteEnPendienteCoordinador();

        stateMachine.rechazarPorCoordinador(tramite, ID_COORDINADOR);

        assertEquals(RECHAZADO, tramite.getCurrentStatus());
        assertTrue(tramite.getCurrentStatus().isTerminalStatus());
    }

    // -------------------------------------------------------------------------
    // remediateByApplicant
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("remediateByApplicant con solicitante correcto termina en PENDIENTE_COORDINADOR")
    void remediateByApplicant_conSolicitanteCorrecto_finalizaEnPendienteCoordinador() {
        Procedure tramite = tramiteEnObservado();

        stateMachine.remediateByApplicant(tramite, ID_SOLICITANTE, "Se adjuntó la firma");

        assertEquals(PENDIENTE_COORDINADOR, tramite.getCurrentStatus());
        assertEquals(COORDINADOR_GRUPO, tramite.getCurrentReviewerRole());
    }

    @Test
    @DisplayName("remediateByApplicant registra dos movimientos: la subsanación y el reenvío automático")
    void remediateByApplicant_registraDosMovimientosEnElHistorial() {
        Procedure tramite = tramiteEnObservado();

        stateMachine.remediateByApplicant(tramite, ID_SOLICITANTE, "Se adjuntó la firma");

        assertEquals(2, tramite.getMovements().size());
        assertEquals(OBSERVADO,              tramite.getMovements().get(0).getPreviousStatus());
        assertEquals(SUBSANADO,              tramite.getMovements().get(0).getNewStatus());
        assertEquals(SUBSANADO,              tramite.getMovements().get(1).getPreviousStatus());
        assertEquals(PENDIENTE_COORDINADOR,  tramite.getMovements().get(1).getNewStatus());
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

        assertEquals(PENDIENTE_DECANATO, tramite.getCurrentStatus());
        assertEquals(DECANO, tramite.getCurrentReviewerRole());
    }

    // -------------------------------------------------------------------------
    // observarPorDirector — RN-07: devuelve al Coordinador, NO al solicitante
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("observarPorDirector pasa a OBSERVADO y asigna COORDINADOR_GRUPO como revisor (RN-07)")
    void observarPorDirector_asignaCoordinadorComoRolRevisor() {
        Procedure tramite = tramiteEnPendienteDireccion();

        stateMachine.observarPorDirector(tramite, ID_DIRECTOR, "El presupuesto no está justificado");

        assertEquals(OBSERVADO, tramite.getCurrentStatus());
        assertEquals(COORDINADOR_GRUPO, tramite.getCurrentReviewerRole());
    }

    // -------------------------------------------------------------------------
    // rechazarPorDirector
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("rechazarPorDirector pasa al estado terminal RECHAZADO")
    void rechazarPorDirector_pasaARechazado() {
        Procedure tramite = tramiteEnPendienteDireccion();

        stateMachine.rechazarPorDirector(tramite, ID_DIRECTOR);

        assertEquals(RECHAZADO, tramite.getCurrentStatus());
        assertTrue(tramite.getCurrentStatus().isTerminalStatus());
    }

    // -------------------------------------------------------------------------
    // registrarResolucionPorDecano
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("registrarResolucionPorDecano ejecuta dos transiciones y deja el trámite en FINALIZADO")
    void registrarResolucionPorDecano_finalizaElTramiteCompletamente() {
        Procedure tramite = tramiteEnPendienteDecanato();

        stateMachine.registrarResolucionPorDecano(tramite, ID_DECANO);

        assertEquals(FINALIZADO, tramite.getCurrentStatus());
        assertTrue(tramite.getCurrentStatus().isTerminalStatus());
        assertEquals(2, tramite.getMovements().size());
        assertEquals(APROBADO_CON_RESOLUCION, tramite.getMovements().get(0).getNewStatus());
        assertEquals(FINALIZADO,              tramite.getMovements().get(1).getNewStatus());
    }

    // -------------------------------------------------------------------------
    // observarPorDecano — RN-07: devuelve al Director
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("observarPorDecano pasa a OBSERVADO y asigna DIRECTOR_INVESTIGACION como revisor (RN-07)")
    void observarPorDecano_asignaDirectorComoRolRevisor() {
        Procedure tramite = tramiteEnPendienteDecanato();

        stateMachine.observarPorDecano(tramite, ID_DECANO, "Revisar el plan financiero");

        assertEquals(OBSERVADO, tramite.getCurrentStatus());
        assertEquals(DIRECTOR_INVESTIGACION, tramite.getCurrentReviewerRole());
    }

    // -------------------------------------------------------------------------
    // Validación de rol — transversal a todos los métodos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Llamar a aprobarPorCoordinador con rol incorrecto lanza InvalidTransitionException")
    void aprobarPorCoordinador_conRolIncorrecto_lanzaExcepcion() {
        Procedure tramite = Procedure.builder()
                .code("TRM-001").procedureType(ProcedureType.PROJECT)
                .applicantId(ID_SOLICITANTE).groupId(1L)
                .currentStatus(PENDIENTE_COORDINADOR).currentReviewerRole(DIRECTOR_INVESTIGACION)
                .projectReferenceId(100L).sentAt(FECHA).updatedAt(FECHA)
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
