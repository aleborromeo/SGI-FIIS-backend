package com.sgi.fiis.tramites.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_tramite")
@Getter
@Setter
public class ProcedureMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long id;

    @Column(name = "id_tramite", nullable = false)
    private Long idTramite;

    @Column(name = "id_usuario_accion", nullable = false)
    private Long idUsuarioAccion;

    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @Column(name = "estado_anterior", nullable = false, length = 50)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 50)
    private String estadoNuevo;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    // id_documento_adjunto existe en DB (V7) pero no es gestionado por este módulo
}
