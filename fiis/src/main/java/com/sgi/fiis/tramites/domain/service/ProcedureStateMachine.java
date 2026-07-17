package com.sgi.fiis.tramites.domain.service;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.model.InvalidTransitionException;
import com.sgi.fiis.users.domain.model.RoleEnum;

public class ProcedureStateMachine {

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

    public void aprobarPorCoordinador(Procedure tramite, Long idCoordinador) {
        validateReviewerRole(tramite, RoleEnum.COORDINADOR_GRUPO);
        tramite.transitionTo(
                ProcedureStatus.PENDIENTE_DIRECCION,
                RoleEnum.COORDINADOR_GRUPO,
                idCoordinador,
                APROBADO_POR_COORDINADOR,
                null,
                RoleEnum.DIRECTOR_INVESTIGACION
        );
    }

    public void observarPorCoordinador(Procedure tramite, Long idCoordinador, String observacion) {
        validateReviewerRole(tramite, RoleEnum.COORDINADOR_GRUPO);
        // nuevoRolRevisor=null: en OBSERVADO el solicitante actúa por identidad, no por rol
        tramite.transitionTo(
                ProcedureStatus.OBSERVADO,
                RoleEnum.COORDINADOR_GRUPO,
                idCoordinador,
                OBSERVADO_POR_COORDINADOR,
                observacion,
                null
        );
    }

    public void rechazarPorCoordinador(Procedure tramite, Long idCoordinador) {
        validateReviewerRole(tramite, RoleEnum.COORDINADOR_GRUPO);
        tramite.transitionTo(
                ProcedureStatus.RECHAZADO,
                RoleEnum.COORDINADOR_GRUPO,
                idCoordinador,
                RECHAZADO_POR_COORDINADOR,
                null,
                null
        );
    }

    public void remediateByApplicant(Procedure tramite, Long idSolicitante, String detalleSubsanacion) {
        if (!tramite.getApplicantId().equals(idSolicitante)) {
            throw new InvalidTransitionException(String.format(
                    "Solo el solicitante original [id=%d] puede subsanar el trámite. " +
                    "Usuario que intenta subsanar: [id=%d]",
                    tramite.getApplicantId(), idSolicitante
            ));
        }
        // OBSERVADO → SUBSANADO (acción del solicitante)
        tramite.transitionTo(
                ProcedureStatus.SUBSANADO,
                null,
                idSolicitante,
                SUBSANADO_POR_SOLICITANTE,
                detalleSubsanacion,
                null
        );
        // SUBSANADO → PENDIENTE_COORDINADOR (reenvío automático — RN-07)
        tramite.transitionTo(
                ProcedureStatus.PENDIENTE_COORDINADOR,
                null,
                idSolicitante,
                REENVIADO_A_COORDINADOR,
                null,
                RoleEnum.COORDINADOR_GRUPO
        );
    }

    public void aprobarPorDirector(Procedure tramite, Long idDirector) {
        validateReviewerRole(tramite, RoleEnum.DIRECTOR_INVESTIGACION);
        tramite.transitionTo(
                ProcedureStatus.PENDIENTE_DECANATO,
                RoleEnum.DIRECTOR_INVESTIGACION,
                idDirector,
                APROBADO_POR_DIRECTOR,
                null,
                RoleEnum.DECANO
        );
    }

    public void observarPorDirector(Procedure tramite, Long idDirector, String observacion) {
        validateReviewerRole(tramite, RoleEnum.DIRECTOR_INVESTIGACION);
        // RN-07/RN-08: Director observe returns to student for thesis plans, to coordinator for projects
        RoleEnum targetRole;
        if (tramite.getProcedureType() != null &&
            (tramite.getProcedureType() == ProcedureType.PLAN_TESIS ||
             tramite.getProcedureType() == ProcedureType.THESIS)) {
            targetRole = null;
        } else {
            targetRole = RoleEnum.COORDINADOR_GRUPO;
        }
        tramite.transitionTo(
                ProcedureStatus.OBSERVADO,
                RoleEnum.DIRECTOR_INVESTIGACION,
                idDirector,
                OBSERVADO_POR_DIRECTOR,
                observacion,
                targetRole
        );
    }

    public void rechazarPorDirector(Procedure tramite, Long idDirector) {
        validateReviewerRole(tramite, RoleEnum.DIRECTOR_INVESTIGACION);
        tramite.transitionTo(
                ProcedureStatus.RECHAZADO,
                RoleEnum.DIRECTOR_INVESTIGACION,
                idDirector,
                RECHAZADO_POR_DIRECTOR,
                null,
                null
        );
    }

    public void registrarResolucionPorDecano(Procedure tramite, Long idDecano) {
        validateReviewerRole(tramite, RoleEnum.DECANO);
        // PENDIENTE_DECANATO → APROBADO_CON_RESOLUCION (acción del Decano — RN-10)
        tramite.transitionTo(
                ProcedureStatus.APROBADO_CON_RESOLUCION,
                RoleEnum.DECANO,
                idDecano,
                RESOLUCION_REGISTRADA,
                null,
                null
        );
        // APROBADO_CON_RESOLUCION → FINALIZADO (cierre automático del sistema)
        tramite.transitionTo(
                ProcedureStatus.FINALIZADO,
                null,
                idDecano,
                TRAMITE_FINALIZADO,
                null,
                null
        );
    }

    public void observarPorDecano(Procedure tramite, Long idDecano, String observacion) {
        validateReviewerRole(tramite, RoleEnum.DECANO);
        // RN-07: la observación del Decano devuelve al Director
        tramite.transitionTo(
                ProcedureStatus.OBSERVADO,
                RoleEnum.DECANO,
                idDecano,
                OBSERVADO_POR_DECANO,
                observacion,
                RoleEnum.DIRECTOR_INVESTIGACION
        );
    }

    private void validateReviewerRole(Procedure tramite, RoleEnum rolEsperado) {
        if (rolEsperado != tramite.getCurrentReviewerRole()) {
            throw new InvalidTransitionException(String.format(
                    "Acción no autorizada: se requiere rol [%s] pero el revisor actual del trámite es [%s]",
                    rolEsperado, tramite.getCurrentReviewerRole()
            ));
        }
    }
}
