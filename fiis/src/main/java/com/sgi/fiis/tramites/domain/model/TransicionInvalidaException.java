package com.sgi.fiis.tramites.domain.model;

import com.sgi.fiis.users.domain.model.RoleEnum;

public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(EstadoTramite origen, EstadoTramite destino, RoleEnum rolQueEjecuta) {
        super(String.format(
                "Transición inválida: no se puede pasar de [%s] a [%s]. Rol que intentó la acción: [%s]",
                origen, destino, rolQueEjecuta != null ? rolQueEjecuta : "SISTEMA"
        ));
    }

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
