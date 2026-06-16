package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grupos_investigacion")
@Getter
@Setter
public class GrupoInvestigacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Integer id;

    @Column(name = "codigo_grupo", nullable = false, unique = true, length = 20)
    private String codigoGrupo;

    @Column(name = "nombre_grupo", nullable = false, length = 150)
    private String nombreGrupo;

    @Column(name = "id_coordinador_actual")
    private Integer idCoordinadorActual;

    @Column(name = "es_activo", nullable = false)
    private boolean esActivo;
}
