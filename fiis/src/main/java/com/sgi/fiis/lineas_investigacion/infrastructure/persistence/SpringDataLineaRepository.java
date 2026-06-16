package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataLineaRepository extends JpaRepository<LineaInvestigacionEntity, Integer> {

    boolean existsByNombreLinea(String nombreLinea);

    List<LineaInvestigacionEntity> findByEsActivaTrue();

    @Query(value = """
            SELECT l.* FROM lineas_investigacion l
            INNER JOIN lineas_por_grupo lpg ON l.id_linea = lpg.id_linea
            WHERE lpg.id_grupo = :idGrupo AND l.es_activa = true
            ORDER BY l.nombre_linea
            """, nativeQuery = true)
    List<LineaInvestigacionEntity> findActivasByIdGrupo(@Param("idGrupo") Integer idGrupo);
}
