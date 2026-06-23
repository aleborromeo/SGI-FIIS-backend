package com.sgi.fiis.tramites.domain.model;

import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.sgi.fiis.tramites.domain.model.EstadoTramite.*;
import static com.sgi.fiis.users.domain.model.RoleEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tramite - Entidad raíz del módulo tramites")
class TramiteTest {

    private static final Long ID_SOLICITANTE = 10L;
    private static final Long ID_COORDINADOR = 20L;
    private static final LocalDateTime FECHA_INICIAL = LocalDateTime.of(2026, 1, 1, 9, 0);

    private Tramite tramiteEnPendienteCoordinador() {
        return Tramite.builder()
                .codigoTramite("TRM-001")
                .tipoTramite(TipoTramite.PROYECTO)
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
    // transicionarA — casos válidos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("transicionarA con transición válida actualiza el estado actual")
    void transicionarA_conTransicionValida_actualizaEstadoActual() {
        Tramite tramite = tramiteEnPendienteCoordinador();

        tramite.transicionarA(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertEquals(PENDIENTE_DIRECCION, tramite.getEstadoActual());
    }

    @Test
    @DisplayName("transicionarA con transición válida agrega un movimiento al historial")
    void transicionarA_conTransicionValida_agregaMovimientoAlHistorial() {
        Tramite tramite = tramiteEnPendienteCoordinador();
        assertEquals(0, tramite.getMovimientos().size());

        tramite.transicionarA(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertEquals(1, tramite.getMovimientos().size());
    }

    @Test
    @DisplayName("transicionarA registra correctamente el estado anterior y el nuevo en el movimiento")
    void transicionarA_movimientoContieneEstadosCorrectos() {
        Tramite tramite = tramiteEnPendienteCoordinador();

        tramite.transicionarA(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        MovimientoTramite movimiento = tramite.getMovimientos().get(0);
        assertEquals(PENDIENTE_COORDINADOR, movimiento.getEstadoAnterior());
        assertEquals(PENDIENTE_DIRECCION, movimiento.getEstadoNuevo());
        assertEquals(ID_COORDINADOR, movimiento.getIdUsuarioAccion());
        assertEquals("APROBADO_POR_COORDINADOR", movimiento.getAccion());
    }

    @Test
    @DisplayName("transicionarA actualiza fechaActualizacion en cada transición")
    void transicionarA_actualizaFechaActualizacion() {
        Tramite tramite = tramiteEnPendienteCoordinador();
        LocalDateTime tiempoAntes = tramite.getFechaActualizacion();

        tramite.transicionarA(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertNotEquals(tiempoAntes, tramite.getFechaActualizacion());
    }

    @Test
    @DisplayName("transicionarA múltiples veces crece el historial de movimientos correctamente")
    void transicionarA_multiplesVeces_historialCrecePorCadaTransicion() {
        Tramite tramite = tramiteEnPendienteCoordinador();

        tramite.transicionarA(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);
        tramite.transicionarA(PENDIENTE_DECANATO, DIRECTOR_INVESTIGACION, 30L,
                "APROBADO_POR_DIRECTOR", null, DECANO);

        assertEquals(2, tramite.getMovimientos().size());
        assertEquals(PENDIENTE_DECANATO, tramite.getEstadoActual());
    }

    @Test
    @DisplayName("transicionarA guarda la observación en el movimiento cuando está presente")
    void transicionarA_conObservacion_laGuardaEnElMovimiento() {
        Tramite tramite = tramiteEnPendienteCoordinador();
        String textoObservacion = "Falta la firma del asesor";

        tramite.transicionarA(OBSERVADO, COORDINADOR_GRUPO, ID_COORDINADOR,
                "OBSERVADO_POR_COORDINADOR", textoObservacion, null);

        assertEquals(textoObservacion, tramite.getMovimientos().get(0).getObservacion());
        assertEquals(textoObservacion, tramite.getObservacionActual());
    }

    // -------------------------------------------------------------------------
    // transicionarA — casos inválidos
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("transicionarA con transición inválida lanza TransicionInvalidaException")
    void transicionarA_conTransicionInvalida_lanzaExcepcion() {
        Tramite tramite = tramiteEnPendienteCoordinador();

        assertThrows(TransicionInvalidaException.class, () ->
                tramite.transicionarA(FINALIZADO, COORDINADOR_GRUPO, ID_COORDINADOR,
                        "ACCION_INVALIDA", null, null)
        );
    }

    @Test
    @DisplayName("transicionarA inválida no modifica el estado del trámite")
    void transicionarA_conTransicionInvalida_noModificaElEstado() {
        Tramite tramite = tramiteEnPendienteCoordinador();

        assertThrows(TransicionInvalidaException.class, () ->
                tramite.transicionarA(APROBADO_CON_RESOLUCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                        "ACCION_INVALIDA", null, null)
        );

        assertEquals(PENDIENTE_COORDINADOR, tramite.getEstadoActual());
        assertEquals(0, tramite.getMovimientos().size());
    }

    // -------------------------------------------------------------------------
    // getMovimientos — inmutabilidad
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getMovimientos retorna una lista inmutable que no permite modificaciones externas")
    void getMovimientos_retornaVistaInmutable() {
        Tramite tramite = tramiteEnPendienteCoordinador();
        tramite.transicionarA(PENDIENTE_DIRECCION, COORDINADOR_GRUPO, ID_COORDINADOR,
                "APROBADO_POR_COORDINADOR", null, DIRECTOR_INVESTIGACION);

        assertThrows(UnsupportedOperationException.class, () ->
                tramite.getMovimientos().clear()
        );
    }

    // -------------------------------------------------------------------------
    // validarArcoExcluyente
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("validarArcoExcluyente con exactamente una referencia no lanza excepción")
    void validarArcoExcluyente_conUnaReferencia_esValido() {
        Tramite tramite = Tramite.builder()
                .codigoTramite("TRM-002")
                .tipoTramite(TipoTramite.PLAN_TESIS)
                .idSolicitante(ID_SOLICITANTE)
                .idGrupo(1L)
                .estadoActual(REGISTRADO)
                .rolRevisorActual(COORDINADOR_GRUPO)
                .idReferenciaTesis(50L)
                .fechaEnvio(FECHA_INICIAL)
                .fechaActualizacion(FECHA_INICIAL)
                .build();

        assertDoesNotThrow(tramite::validarArcoExcluyente);
    }

    @Test
    @DisplayName("validarArcoExcluyente sin ninguna referencia lanza IllegalArgumentException")
    void validarArcoExcluyente_sinReferencias_lanzaExcepcion() {
        Tramite tramite = Tramite.builder()
                .codigoTramite("TRM-003")
                .tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(ID_SOLICITANTE)
                .idGrupo(1L)
                .estadoActual(REGISTRADO)
                .rolRevisorActual(COORDINADOR_GRUPO)
                .fechaEnvio(FECHA_INICIAL)
                .fechaActualizacion(FECHA_INICIAL)
                .build();

        assertThrows(IllegalArgumentException.class, tramite::validarArcoExcluyente);
    }

    @Test
    @DisplayName("validarArcoExcluyente con múltiples referencias lanza IllegalArgumentException")
    void validarArcoExcluyente_conMultiplesReferencias_lanzaExcepcion() {
        Tramite tramite = Tramite.builder()
                .codigoTramite("TRM-004")
                .tipoTramite(TipoTramite.PROYECTO)
                .idSolicitante(ID_SOLICITANTE)
                .idGrupo(1L)
                .estadoActual(REGISTRADO)
                .rolRevisorActual(COORDINADOR_GRUPO)
                .idReferenciaProyecto(100L)
                .idReferenciaTesis(50L)
                .fechaEnvio(FECHA_INICIAL)
                .fechaActualizacion(FECHA_INICIAL)
                .build();

        assertThrows(IllegalArgumentException.class, tramite::validarArcoExcluyente);
    }
}
