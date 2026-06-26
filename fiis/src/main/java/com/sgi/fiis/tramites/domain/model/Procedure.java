package com.sgi.fiis.tramites.domain.model;

import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class Procedure {

    private Long id;
    private String codigoTramite;
    private ProcedureType tipoTramite;
    private Long idSolicitante;
    private Long idGrupo;
    private ProcedureStatus estadoActual;
    private RoleEnum rolRevisorActual;
    private String observacionActual;

    // Arco excluyente: exactamente uno debe ser no-nulo según tipoTramite
    private Long idReferenciaProyecto;
    private Long idReferenciaTesis;
    private Long idReferenciaInforme;

    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    private List<ProcedureMovement> movimientos = new ArrayList<>();

    // Expuesto como vista inmutable: solo transitionTo puede agregar movimientos
    public List<ProcedureMovement> getMovements() {
        return Collections.unmodifiableList(movimientos);
    }

    public void transitionTo(
            ProcedureStatus nuevoEstado,
            RoleEnum rolQueEjecuta,
            Long idUsuarioAccion,
            String accion,
            String observacion,
            RoleEnum nuevoRolRevisor) {

        if (!estadoActual.canTransitionTo(nuevoEstado)) {
            throw new InvalidTransitionException(estadoActual, nuevoEstado, rolQueEjecuta);
        }

        ProcedureStatus estadoAnterior = this.estadoActual;
        this.estadoActual     = nuevoEstado;
        this.rolRevisorActual = nuevoRolRevisor;
        this.observacionActual = observacion;
        this.fechaActualizacion = LocalDateTime.now();

        movimientos.add(ProcedureMovement.builder()
                .idUsuarioAccion(idUsuarioAccion)
                .accion(accion)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(nuevoEstado)
                .observacion(observacion)
                .fechaMovimiento(this.fechaActualizacion)
                .build());
    }

    // No se valida en el builder porque la capa de infraestructura reconstruye tramites
    // desde BD sin pasar por esta regla. Es responsabilidad del use case llamarla al crear.
    public void validateExclusiveReference() {
        int conteo = (idReferenciaProyecto != null ? 1 : 0)
                   + (idReferenciaTesis    != null ? 1 : 0)
                   + (idReferenciaInforme  != null ? 1 : 0);
        if (conteo != 1) {
            throw new IllegalArgumentException(
                    "El trámite debe referenciar exactamente una entidad origen " +
                    "(proyecto, tesis o informe). Referencias encontradas: " + conteo
            );
        }
    }
}
