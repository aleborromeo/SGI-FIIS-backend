package com.sgi.fiis.tramites.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sgi.fiis.tramites.domain.model.EstadoTramite.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EstadoTramite - Máquina de estados del módulo tramites")
class EstadoTramiteTest {

    // -------------------------------------------------------------------------
    // Transiciones válidas — deben retornar true
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("REGISTRADO puede transicionar a PENDIENTE_COORDINADOR (creación automática)")
    void registrado_puedeTransicionarA_pendienteCoordinador() {
        assertTrue(REGISTRADO.puedeTransicionarA(PENDIENTE_COORDINADOR));
    }

    @Test
    @DisplayName("PENDIENTE_COORDINADOR permite aprobar, observar y rechazar")
    void pendienteCoordinador_permiteTransicionesValidas() {
        assertTrue(PENDIENTE_COORDINADOR.puedeTransicionarA(PENDIENTE_DIRECCION));
        assertTrue(PENDIENTE_COORDINADOR.puedeTransicionarA(OBSERVADO));
        assertTrue(PENDIENTE_COORDINADOR.puedeTransicionarA(RECHAZADO));
    }

    @Test
    @DisplayName("OBSERVADO puede transicionar a SUBSANADO (solo el solicitante)")
    void observado_puedeTransicionarA_subsanado() {
        assertTrue(OBSERVADO.puedeTransicionarA(SUBSANADO));
    }

    @Test
    @DisplayName("SUBSANADO puede transicionar a PENDIENTE_COORDINADOR (reenvío automático)")
    void subsanado_puedeTransicionarA_pendienteCoordinador() {
        assertTrue(SUBSANADO.puedeTransicionarA(PENDIENTE_COORDINADOR));
    }

    @Test
    @DisplayName("PENDIENTE_DIRECCION permite aprobar, observar y rechazar")
    void pendienteDireccion_permiteTransicionesValidas() {
        assertTrue(PENDIENTE_DIRECCION.puedeTransicionarA(PENDIENTE_DECANATO));
        assertTrue(PENDIENTE_DIRECCION.puedeTransicionarA(OBSERVADO));
        assertTrue(PENDIENTE_DIRECCION.puedeTransicionarA(RECHAZADO));
    }

    @Test
    @DisplayName("PENDIENTE_DECANATO permite registrar resolución u observar")
    void pendienteDecanato_permiteTransicionesValidas() {
        assertTrue(PENDIENTE_DECANATO.puedeTransicionarA(APROBADO_CON_RESOLUCION));
        assertTrue(PENDIENTE_DECANATO.puedeTransicionarA(OBSERVADO));
    }

    @Test
    @DisplayName("APROBADO_CON_RESOLUCION puede transicionar a FINALIZADO (cierre automático)")
    void aprobadoConResolucion_puedeTransicionarA_finalizado() {
        assertTrue(APROBADO_CON_RESOLUCION.puedeTransicionarA(FINALIZADO));
    }

    // -------------------------------------------------------------------------
    // Transiciones inválidas — deben retornar false
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("FINALIZADO no puede transicionar a ningún estado (estado terminal)")
    void finalizado_noPermiteNingunaTransicion() {
        assertFalse(FINALIZADO.puedeTransicionarA(RECHAZADO));
        assertFalse(FINALIZADO.puedeTransicionarA(PENDIENTE_COORDINADOR));
        assertFalse(FINALIZADO.puedeTransicionarA(FINALIZADO));
    }

    @Test
    @DisplayName("RECHAZADO no puede transicionar a ningún estado (estado terminal)")
    void rechazado_noPermiteNingunaTransicion() {
        assertFalse(RECHAZADO.puedeTransicionarA(PENDIENTE_COORDINADOR));
        assertFalse(RECHAZADO.puedeTransicionarA(FINALIZADO));
        assertFalse(RECHAZADO.puedeTransicionarA(RECHAZADO));
    }

    @Test
    @DisplayName("REGISTRADO no puede saltar directo a APROBADO_CON_RESOLUCION")
    void registrado_noPuedeSaltarseElFlujoCompleto() {
        assertFalse(REGISTRADO.puedeTransicionarA(APROBADO_CON_RESOLUCION));
    }

    @Test
    @DisplayName("PENDIENTE_COORDINADOR no puede saltar PENDIENTE_DIRECCION e ir a PENDIENTE_DECANATO")
    void pendienteCoordinador_noPuedeSaltarAlDirector() {
        assertFalse(PENDIENTE_COORDINADOR.puedeTransicionarA(PENDIENTE_DECANATO));
    }

    @Test
    @DisplayName("OBSERVADO no puede volver directamente a PENDIENTE_COORDINADOR sin pasar por SUBSANADO")
    void observado_noPuedeVolver_sinSubsanar() {
        assertFalse(OBSERVADO.puedeTransicionarA(PENDIENTE_COORDINADOR));
    }

    @Test
    @DisplayName("PENDIENTE_DECANATO no puede rechazar (el Decano solo aprueba con resolución u observa)")
    void pendienteDecanato_noPuedeRechazar() {
        assertFalse(PENDIENTE_DECANATO.puedeTransicionarA(RECHAZADO));
    }

    @Test
    @DisplayName("PENDIENTE_DIRECCION no puede saltar PENDIENTE_DECANATO e ir directo a APROBADO")
    void pendienteDireccion_noPuedeSaltarAlDecanato() {
        assertFalse(PENDIENTE_DIRECCION.puedeTransicionarA(APROBADO_CON_RESOLUCION));
    }

    // -------------------------------------------------------------------------
    // Estados terminales
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("FINALIZADO es un estado terminal")
    void finalizado_esEstadoTerminal() {
        assertTrue(FINALIZADO.esEstadoTerminal());
    }

    @Test
    @DisplayName("RECHAZADO es un estado terminal")
    void rechazado_esEstadoTerminal() {
        assertTrue(RECHAZADO.esEstadoTerminal());
    }

    @Test
    @DisplayName("Los estados intermedios no son terminales")
    void estadosIntermedios_noSonTerminales() {
        assertFalse(REGISTRADO.esEstadoTerminal());
        assertFalse(PENDIENTE_COORDINADOR.esEstadoTerminal());
        assertFalse(OBSERVADO.esEstadoTerminal());
        assertFalse(SUBSANADO.esEstadoTerminal());
        assertFalse(PENDIENTE_DIRECCION.esEstadoTerminal());
        assertFalse(PENDIENTE_DECANATO.esEstadoTerminal());
        assertFalse(APROBADO_CON_RESOLUCION.esEstadoTerminal());
    }
}
