package com.sgi.fiis.tramites.domain.model;

import com.sgi.fiis.users.domain.model.RolEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class Tramite {

    private Long id;
    private String codigoTramite;
    private TipoTramite tipoTramite;
    private Long idSolicitante;
    private Long idGrupo;
    private EstadoTramite estadoActual;
    private RolEnum rolRevisorActual;
    private String observacionActual;

    // Arco excluyente: exactamente uno debe ser no-nulo según tipoTramite
    private Long idReferenciaProyecto;
    private Long idReferenciaTesis;
    private Long idReferenciaInforme;

    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    private List<MovimientoTramite> movimientos = new ArrayList<>();

    // Expuesto como vista inmutable: solo transicionarA puede agregar movimientos
    public List<MovimientoTramite> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }

    public void transicionarA(
            EstadoTramite nuevoEstado,
            RolEnum rolQueEjecuta,
            Long idUsuarioAccion,
            String accion,
            String observacion,
            RolEnum nuevoRolRevisor) {

        if (!estadoActual.puedeTransicionarA(nuevoEstado)) {
            throw new TransicionInvalidaException(estadoActual, nuevoEstado, rolQueEjecuta);
        }

        EstadoTramite estadoAnterior = this.estadoActual;
        this.estadoActual     = nuevoEstado;
        this.rolRevisorActual = nuevoRolRevisor;
        this.observacionActual = observacion;
        this.fechaActualizacion = LocalDateTime.now();

        movimientos.add(MovimientoTramite.builder()
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
    public void validarArcoExcluyente() {
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
