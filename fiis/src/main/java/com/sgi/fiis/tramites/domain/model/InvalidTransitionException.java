package com.sgi.fiis.tramites.domain.model;

import com.sgi.fiis.users.domain.model.RoleEnum;

public class InvalidTransitionException extends RuntimeException {

    public InvalidTransitionException(ProcedureStatus origen, ProcedureStatus destino, RoleEnum rolQueEjecuta) {
        super(String.format(
                "Transición inválida: no se puede pasar de [%s] a [%s]. Rol que intentó la acción: [%s]",
                origen, destino, rolQueEjecuta != null ? rolQueEjecuta : "SISTEMA"
        ));
    }

    public InvalidTransitionException(String mensaje) {
        super(mensaje);
    }
}
