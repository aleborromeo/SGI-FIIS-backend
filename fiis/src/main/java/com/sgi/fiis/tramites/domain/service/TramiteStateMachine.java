package com.sgi.fiis.tramites.domain.service;

import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.model.TransicionInvalidaException;
import com.sgi.fiis.users.domain.model.RolEnum;

public class TramiteStateMachine {

    private static final String APROBADO_POR_COORDINADOR  = "APROBADO_POR_COORDINADOR";
    private static final String OBSERVADO_POR_COORDINADOR = "OBSERVADO_POR_COORDINADOR";
    private static final String RECHAZADO_POR_COORDINADOR = "RECHAZADO_POR_COORDINADOR";
    private static final String SUBSANADO_POR_SOLICITANTE = "SUBSANADO_POR_SOLICITANTE";
    private static final String REENVIADO_A_COORDINADOR   = "REENVIADO_A_COORDINADOR";
    private static final String APROBADO_POR_DIRECTOR     = "APROBADO_POR_DIRECTOR";
    private static final String OBSERVADO_POR_DIRECTOR    = "OBSERVADO_POR_DIRECTOR";
    private static final String RECHAZADO_POR_DIRECTOR    = "RECHAZADO_POR_DIRECTOR";
    private static final String RESOLUCION_REGISTRADA     = "RESOLUCION_REGISTRADA";
    private static final String TRAMITE_FINALIZADO        = "TRAMITE_FINALIZADO";
    private static final String OBSERVADO_POR_DECANO      = "OBSERVADO_POR_DECANO";

    public void aprobarPorCoordinador(Tramite tramite, Long idCoordinador) {
        validarRolRevisor(tramite, RolEnum.COORDINADOR_GRUPO);
        tramite.transicionarA(
                EstadoTramite.PENDIENTE_DIRECCION,
                RolEnum.COORDINADOR_GRUPO,
                idCoordinador,
                APROBADO_POR_COORDINADOR,
                null,
                RolEnum.DIRECTOR_INVESTIGACION
        );
    }

    public void observarPorCoordinador(Tramite tramite, Long idCoordinador, String observacion) {
        validarRolRevisor(tramite, RolEnum.COORDINADOR_GRUPO);
        // nuevoRolRevisor=null: en OBSERVADO el solicitante actúa por identidad, no por rol
        tramite.transicionarA(
                EstadoTramite.OBSERVADO,
                RolEnum.COORDINADOR_GRUPO,
                idCoordinador,
                OBSERVADO_POR_COORDINADOR,
                observacion,
                null
        );
    }

    public void rechazarPorCoordinador(Tramite tramite, Long idCoordinador) {
        validarRolRevisor(tramite, RolEnum.COORDINADOR_GRUPO);
        tramite.transicionarA(
                EstadoTramite.RECHAZADO,
                RolEnum.COORDINADOR_GRUPO,
                idCoordinador,
                RECHAZADO_POR_COORDINADOR,
                null,
                null
        );
    }

    public void subsanarPorSolicitante(Tramite tramite, Long idSolicitante, String detalleSubsanacion) {
        if (!tramite.getIdSolicitante().equals(idSolicitante)) {
            throw new TransicionInvalidaException(String.format(
                    "Solo el solicitante original [id=%d] puede subsanar el trámite. " +
                    "Usuario que intenta subsanar: [id=%d]",
                    tramite.getIdSolicitante(), idSolicitante
            ));
        }
        // OBSERVADO → SUBSANADO (acción del solicitante)
        tramite.transicionarA(
                EstadoTramite.SUBSANADO,
                null,
                idSolicitante,
                SUBSANADO_POR_SOLICITANTE,
                detalleSubsanacion,
                null
        );
        // SUBSANADO → PENDIENTE_COORDINADOR (reenvío automático — RN-07)
        tramite.transicionarA(
                EstadoTramite.PENDIENTE_COORDINADOR,
                null,
                idSolicitante,
                REENVIADO_A_COORDINADOR,
                null,
                RolEnum.COORDINADOR_GRUPO
        );
    }

    public void aprobarPorDirector(Tramite tramite, Long idDirector) {
        validarRolRevisor(tramite, RolEnum.DIRECTOR_INVESTIGACION);
        tramite.transicionarA(
                EstadoTramite.PENDIENTE_DECANATO,
                RolEnum.DIRECTOR_INVESTIGACION,
                idDirector,
                APROBADO_POR_DIRECTOR,
                null,
                RolEnum.DECANO
        );
    }

    public void observarPorDirector(Tramite tramite, Long idDirector, String observacion) {
        validarRolRevisor(tramite, RolEnum.DIRECTOR_INVESTIGACION);
        // RN-07: la observación del Director devuelve al Coordinador, no al solicitante
        tramite.transicionarA(
                EstadoTramite.OBSERVADO,
                RolEnum.DIRECTOR_INVESTIGACION,
                idDirector,
                OBSERVADO_POR_DIRECTOR,
                observacion,
                RolEnum.COORDINADOR_GRUPO
        );
    }

    public void rechazarPorDirector(Tramite tramite, Long idDirector) {
        validarRolRevisor(tramite, RolEnum.DIRECTOR_INVESTIGACION);
        tramite.transicionarA(
                EstadoTramite.RECHAZADO,
                RolEnum.DIRECTOR_INVESTIGACION,
                idDirector,
                RECHAZADO_POR_DIRECTOR,
                null,
                null
        );
    }

    public void registrarResolucionPorDecano(Tramite tramite, Long idDecano) {
        validarRolRevisor(tramite, RolEnum.DECANO);
        // PENDIENTE_DECANATO → APROBADO_CON_RESOLUCION (acción del Decano — RN-10)
        tramite.transicionarA(
                EstadoTramite.APROBADO_CON_RESOLUCION,
                RolEnum.DECANO,
                idDecano,
                RESOLUCION_REGISTRADA,
                null,
                null
        );
        // APROBADO_CON_RESOLUCION → FINALIZADO (cierre automático del sistema)
        tramite.transicionarA(
                EstadoTramite.FINALIZADO,
                null,
                idDecano,
                TRAMITE_FINALIZADO,
                null,
                null
        );
    }

    public void observarPorDecano(Tramite tramite, Long idDecano, String observacion) {
        validarRolRevisor(tramite, RolEnum.DECANO);
        // RN-07: la observación del Decano devuelve al Director
        tramite.transicionarA(
                EstadoTramite.OBSERVADO,
                RolEnum.DECANO,
                idDecano,
                OBSERVADO_POR_DECANO,
                observacion,
                RolEnum.DIRECTOR_INVESTIGACION
        );
    }

    private void validarRolRevisor(Tramite tramite, RolEnum rolEsperado) {
        if (rolEsperado != tramite.getRolRevisorActual()) {
            throw new TransicionInvalidaException(String.format(
                    "Acción no autorizada: se requiere rol [%s] pero el revisor actual del trámite es [%s]",
                    rolEsperado, tramite.getRolRevisorActual()
            ));
        }
    }
}
