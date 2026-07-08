package com.sgi.fiis.tramites.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sgi.fiis.tramites.domain.model.ProcedureStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcedureStatus - Máquina de estados del módulo tramites")
class ProcedureStatusTest {

    // -------------------------------------------------------------------------
    // Transiciones válidas — deben retornar true
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("REGISTRADO puede transicionar a PENDIENTE_COORDINADOR (creación automática)")
    void registrado_canTransitionTo_pendienteCoordinador() {
        assertTrue(REGISTRADO.canTransitionTo(PENDIENTE_COORDINADOR));
    }

    @Test
    @DisplayName("PENDIENTE_COORDINADOR permite aprobar, observar y rechazar")
    void pendienteCoordinador_permiteTransicionesValidas() {
        assertTrue(PENDIENTE_COORDINADOR.canTransitionTo(PENDIENTE_DIRECCION));
        assertTrue(PENDIENTE_COORDINADOR.canTransitionTo(OBSERVADO));
        assertTrue(PENDIENTE_COORDINADOR.canTransitionTo(RECHAZADO));
    }

    @Test
    @DisplayName("OBSERVADO puede transicionar a SUBSANADO (solo el solicitante)")
    void observado_canTransitionTo_subsanado() {
        assertTrue(OBSERVADO.canTransitionTo(SUBSANADO));
    }

    @Test
    @DisplayName("SUBSANADO puede transicionar a PENDIENTE_COORDINADOR (reenvío automático)")
    void subsanado_canTransitionTo_pendienteCoordinador() {
        assertTrue(SUBSANADO.canTransitionTo(PENDIENTE_COORDINADOR));
    }

    @Test
    @DisplayName("PENDIENTE_DIRECCION permite aprobar, observar y rechazar")
    void pendienteDireccion_permiteTransicionesValidas() {
        assertTrue(PENDIENTE_DIRECCION.canTransitionTo(PENDIENTE_DECANATO));
        assertTrue(PENDIENTE_DIRECCION.canTransitionTo(OBSERVADO));
        assertTrue(PENDIENTE_DIRECCION.canTransitionTo(RECHAZADO));
    }

    @Test
    @DisplayName("PENDIENTE_DECANATO permite registrar resolución u observar")
    void pendienteDecanato_permiteTransicionesValidas() {
        assertTrue(PENDIENTE_DECANATO.canTransitionTo(APROBADO_CON_RESOLUCION));
        assertTrue(PENDIENTE_DECANATO.canTransitionTo(OBSERVADO));
    }

    @Test
    @DisplayName("APROBADO_CON_RESOLUCION puede transicionar a FINALIZADO (cierre automático)")
    void aprobadoConResolucion_canTransitionTo_finalizado() {
        assertTrue(APROBADO_CON_RESOLUCION.canTransitionTo(FINALIZADO));
    }

    // -------------------------------------------------------------------------
    // Transiciones inválidas — deben retornar false
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("FINALIZADO no puede transicionar a ningún estado (estado terminal)")
    void finalizado_noPermiteNingunaTransicion() {
        assertFalse(FINALIZADO.canTransitionTo(RECHAZADO));
        assertFalse(FINALIZADO.canTransitionTo(PENDIENTE_COORDINADOR));
        assertFalse(FINALIZADO.canTransitionTo(FINALIZADO));
    }

    @Test
    @DisplayName("RECHAZADO no puede transicionar a ningún estado (estado terminal)")
    void rechazado_noPermiteNingunaTransicion() {
        assertFalse(RECHAZADO.canTransitionTo(PENDIENTE_COORDINADOR));
        assertFalse(RECHAZADO.canTransitionTo(FINALIZADO));
        assertFalse(RECHAZADO.canTransitionTo(RECHAZADO));
    }

    @Test
    @DisplayName("REGISTRADO no puede saltar directo a APROBADO_CON_RESOLUCION")
    void registrado_noPuedeSaltarseElFlujoCompleto() {
        assertFalse(REGISTRADO.canTransitionTo(APROBADO_CON_RESOLUCION));
    }

    @Test
    @DisplayName("PENDIENTE_COORDINADOR no puede saltar PENDIENTE_DIRECCION e ir a PENDIENTE_DECANATO")
    void pendienteCoordinador_noPuedeSaltarAlDirector() {
        assertFalse(PENDIENTE_COORDINADOR.canTransitionTo(PENDIENTE_DECANATO));
    }

    @Test
    @DisplayName("OBSERVADO no puede volver directamente a PENDIENTE_COORDINADOR sin pasar por SUBSANADO")
    void observado_noPuedeVolver_sinSubsanar() {
        assertFalse(OBSERVADO.canTransitionTo(PENDIENTE_COORDINADOR));
    }

    @Test
    @DisplayName("PENDIENTE_DECANATO no puede rechazar (el Decano solo aprueba con resolución u observa)")
    void pendienteDecanato_noPuedeRechazar() {
        assertFalse(PENDIENTE_DECANATO.canTransitionTo(RECHAZADO));
    }

    @Test
    @DisplayName("PENDIENTE_DIRECCION no puede saltar PENDIENTE_DECANATO e ir directo a APROBADO")
    void pendienteDireccion_noPuedeSaltarAlDecanato() {
        assertFalse(PENDIENTE_DIRECCION.canTransitionTo(APROBADO_CON_RESOLUCION));
    }

    // -------------------------------------------------------------------------
    // Estados terminales
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("FINALIZADO es un estado terminal")
    void finalizado_isTerminalStatus() {
        assertTrue(FINALIZADO.isTerminalStatus());
    }

    @Test
    @DisplayName("RECHAZADO es un estado terminal")
    void rechazado_isTerminalStatus() {
        assertTrue(RECHAZADO.isTerminalStatus());
    }

    @Test
    @DisplayName("Los estados intermedios no son terminales")
    void estadosIntermedios_noSonTerminales() {
        assertFalse(REGISTRADO.isTerminalStatus());
        assertFalse(PENDIENTE_COORDINADOR.isTerminalStatus());
        assertFalse(OBSERVADO.isTerminalStatus());
        assertFalse(SUBSANADO.isTerminalStatus());
        assertFalse(PENDIENTE_DIRECCION.isTerminalStatus());
        assertFalse(PENDIENTE_DECANATO.isTerminalStatus());
        assertFalse(APROBADO_CON_RESOLUCION.isTerminalStatus());
    }
}
