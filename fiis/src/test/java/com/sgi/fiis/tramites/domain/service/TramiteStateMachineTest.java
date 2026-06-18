package com.sgi.fiis.tramites.domain.service;

import com.sgi.fiis.tramites.domain.model.*;
import com.sgi.fiis.users.domain.model.RolEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.sgi.fiis.tramites.domain.model.EstadoTramite.*;
import static com.sgi.fiis.users.domain.model.RolEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TramiteStateMachine - Servicio de dominio del flujo de aprobación institucional")
class TramiteStateMachineTest {

    private static final Long ID_SOLICITANTE = 10L;
    private static final Long ID_COORDINADOR = 20L;
    private static final Long ID_DIRECTOR    = 30L;
    private static final Long ID_DECANO      = 40L;
    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 1, 1, 9, 0);

    private TramiteStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new TramiteStateMachine();
    }

    // -------------------------------------------------------------------------
    // Helpers de construcción por estado de inicio
    // -------------------------------------------------------------------------

    private Tramite tramiteEnPendienteCoordinador() {
        return Tramite.builder()
                .codigoTramite("TRM-001").tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(PENDIENTE_COORDINADOR).rolRevisorActual(COORDINADOR_GRUPO)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();
    }

    private Tramite tramiteEnObservado() {
        return Tramite.builder()
                .codigoTramite("TRM-001").tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(OBSERVADO).rolRevisorActual(null)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();
    }

    private Tramite tramiteEnPendienteDireccion() {
        return Tramite.builder()
                .codigoTramite("TRM-001").tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(PENDIENTE_DIRECCION).rolRevisorActual(DIRECTOR_INVESTIGACION)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();
    }

    private Tramite tramiteEnPendienteDecanato() {
        return Tramite.builder()
                .codigoTramite("TRM-001").tipoTramite(TipoTramite.PROYECTO)
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
        Tramite tramite = tramiteEnPendienteCoordinador();

        stateMachine.aprobarPorCoordinador(tramite, ID_COORDINADOR);

        assertEquals(PENDIENTE_DIRECCION, tramite.getEstadoActual());
        assertEquals(DIRECTOR_INVESTIGACION, tramite.getRolRevisorActual());
        assertEquals(1, tramite.getMovimientos().size());
    }

    // -------------------------------------------------------------------------
    // observarPorCoordinador
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("observarPorCoordinador pasa a OBSERVADO y deja rolRevisorActual en null")
    void observarPorCoordinador_pasaAObservadoConRolRevisorNull() {
        Tramite tramite = tramiteEnPendienteCoordinador();

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
        Tramite tramite = tramiteEnPendienteCoordinador();

        stateMachine.rechazarPorCoordinador(tramite, ID_COORDINADOR);

        assertEquals(RECHAZADO, tramite.getEstadoActual());
        assertTrue(tramite.getEstadoActual().esEstadoTerminal());
    }

    // -------------------------------------------------------------------------
    // subsanarPorSolicitante
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("subsanarPorSolicitante con solicitante correcto termina en PENDIENTE_COORDINADOR")
    void subsanarPorSolicitante_conSolicitanteCorrecto_finalizaEnPendienteCoordinador() {
        Tramite tramite = tramiteEnObservado();

        stateMachine.subsanarPorSolicitante(tramite, ID_SOLICITANTE, "Se adjuntó la firma");

        assertEquals(PENDIENTE_COORDINADOR, tramite.getEstadoActual());
        assertEquals(COORDINADOR_GRUPO, tramite.getRolRevisorActual());
    }

    @Test
    @DisplayName("subsanarPorSolicitante registra dos movimientos: la subsanación y el reenvío automático")
    void subsanarPorSolicitante_registraDosMovimientosEnElHistorial() {
        Tramite tramite = tramiteEnObservado();

        stateMachine.subsanarPorSolicitante(tramite, ID_SOLICITANTE, "Se adjuntó la firma");

        assertEquals(2, tramite.getMovimientos().size());
        assertEquals(OBSERVADO,              tramite.getMovimientos().get(0).getEstadoAnterior());
        assertEquals(SUBSANADO,              tramite.getMovimientos().get(0).getEstadoNuevo());
        assertEquals(SUBSANADO,              tramite.getMovimientos().get(1).getEstadoAnterior());
        assertEquals(PENDIENTE_COORDINADOR,  tramite.getMovimientos().get(1).getEstadoNuevo());
    }

    @Test
    @DisplayName("subsanarPorSolicitante con id de usuario ajeno lanza TransicionInvalidaException")
    void subsanarPorSolicitante_conIdIncorrecto_lanzaExcepcion() {
        Tramite tramite = tramiteEnObservado();

        assertThrows(TransicionInvalidaException.class, () ->
                stateMachine.subsanarPorSolicitante(tramite, 99L, "Intento no autorizado")
        );
    }

    // -------------------------------------------------------------------------
    // aprobarPorDirector
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("aprobarPorDirector avanza a PENDIENTE_DECANATO y asigna DECANO como próximo revisor")
    void aprobarPorDirector_avanzaADecanatoYAsignaDecanoComoRevisor() {
        Tramite tramite = tramiteEnPendienteDireccion();

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
        Tramite tramite = tramiteEnPendienteDireccion();

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
        Tramite tramite = tramiteEnPendienteDireccion();

        stateMachine.rechazarPorDirector(tramite, ID_DIRECTOR);

        assertEquals(RECHAZADO, tramite.getEstadoActual());
        assertTrue(tramite.getEstadoActual().esEstadoTerminal());
    }

    // -------------------------------------------------------------------------
    // registrarResolucionPorDecano
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("registrarResolucionPorDecano ejecuta dos transiciones y deja el trámite en FINALIZADO")
    void registrarResolucionPorDecano_finalizaElTramiteCompletamente() {
        Tramite tramite = tramiteEnPendienteDecanato();

        stateMachine.registrarResolucionPorDecano(tramite, ID_DECANO);

        assertEquals(FINALIZADO, tramite.getEstadoActual());
        assertTrue(tramite.getEstadoActual().esEstadoTerminal());
        assertEquals(2, tramite.getMovimientos().size());
        assertEquals(APROBADO_CON_RESOLUCION, tramite.getMovimientos().get(0).getEstadoNuevo());
        assertEquals(FINALIZADO,              tramite.getMovimientos().get(1).getEstadoNuevo());
    }

    // -------------------------------------------------------------------------
    // observarPorDecano — RN-07: devuelve al Director
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("observarPorDecano pasa a OBSERVADO y asigna DIRECTOR_INVESTIGACION como revisor (RN-07)")
    void observarPorDecano_asignaDirectorComoRolRevisor() {
        Tramite tramite = tramiteEnPendienteDecanato();

        stateMachine.observarPorDecano(tramite, ID_DECANO, "Revisar el plan financiero");

        assertEquals(OBSERVADO, tramite.getEstadoActual());
        assertEquals(DIRECTOR_INVESTIGACION, tramite.getRolRevisorActual());
    }

    // -------------------------------------------------------------------------
    // Validación de rol — transversal a todos los métodos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Llamar a aprobarPorCoordinador con rol incorrecto lanza TransicionInvalidaException")
    void aprobarPorCoordinador_conRolIncorrecto_lanzaExcepcion() {
        Tramite tramite = Tramite.builder()
                .codigoTramite("TRM-001").tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(ID_SOLICITANTE).idGrupo(1L)
                .estadoActual(PENDIENTE_COORDINADOR).rolRevisorActual(DIRECTOR_INVESTIGACION)
                .idReferenciaProyecto(100L).fechaEnvio(FECHA).fechaActualizacion(FECHA)
                .build();

        assertThrows(TransicionInvalidaException.class, () ->
                stateMachine.aprobarPorCoordinador(tramite, ID_DIRECTOR)
        );
    }

    @Test
    @DisplayName("Llamar a aprobarPorDirector cuando el revisor actual es el Coordinador lanza excepción")
    void aprobarPorDirector_conRolIncorrecto_lanzaExcepcion() {
        Tramite tramite = tramiteEnPendienteCoordinador();

        assertThrows(TransicionInvalidaException.class, () ->
                stateMachine.aprobarPorDirector(tramite, ID_DIRECTOR)
        );
    }
}
