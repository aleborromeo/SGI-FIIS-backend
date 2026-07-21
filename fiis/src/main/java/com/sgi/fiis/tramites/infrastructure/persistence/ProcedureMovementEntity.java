package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_tramite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedureMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tramite", nullable = false)
    private ProcedureEntity procedure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_accion", nullable = false)
    private UserEntity actionUser;

    @Column(name = "accion", nullable = false, length = 30)
    private String action;

    @Column(name = "estado_anterior", nullable = false, length = 30)
    private String previousState;

    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private String newState;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime movementAt;

    @Column(name = "id_documento_adjunto")
    private Long documentAttachmentId;

    @Column(name = "correlation_id", length = 36)
    private String correlationId;
}
