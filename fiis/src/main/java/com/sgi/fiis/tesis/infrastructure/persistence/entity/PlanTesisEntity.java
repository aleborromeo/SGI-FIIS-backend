package pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoPlanTesis;

@Entity
@Table(name = "planes_tesis")
public class PlanTesisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan_tesis")
    private Integer idPlanTesis;

    @Column(name = "titulo_tesis", nullable = false, length = 500)
    private String tituloTesis;

    @Column(name = "resumen", columnDefinition = "TEXT")
    private String resumen;

    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    @Column(name = "id_linea", nullable = false)
    private Integer idLinea;

    @Column(name = "id_grupo", nullable = false)
    private Integer idGrupo;

    @Column(name = "id_documento_actual")
    private Integer idDocumentoActual;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_plan", nullable = false, length = 30)
    private EstadoPlanTesis estadoPlan = EstadoPlanTesis.POSTULADO;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public Integer getIdPlanTesis() { return idPlanTesis; }
    public void setIdPlanTesis(Integer idPlanTesis) { this.idPlanTesis = idPlanTesis; }
    public String getTituloTesis() { return tituloTesis; }
    public void setTituloTesis(String tituloTesis) { this.tituloTesis = tituloTesis; }
    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }
    public Long getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(Long idEstudiante) { this.idEstudiante = idEstudiante; }
    public Integer getIdLinea() { return idLinea; }
    public void setIdLinea(Integer idLinea) { this.idLinea = idLinea; }
    public Integer getIdGrupo() { return idGrupo; }
    public void setIdGrupo(Integer idGrupo) { this.idGrupo = idGrupo; }
    public Integer getIdDocumentoActual() { return idDocumentoActual; }
    public void setIdDocumentoActual(Integer idDocumentoActual) { this.idDocumentoActual = idDocumentoActual; }
    public EstadoPlanTesis getEstadoPlan() { return estadoPlan; }
    public void setEstadoPlan(EstadoPlanTesis estadoPlan) { this.estadoPlan = estadoPlan; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
