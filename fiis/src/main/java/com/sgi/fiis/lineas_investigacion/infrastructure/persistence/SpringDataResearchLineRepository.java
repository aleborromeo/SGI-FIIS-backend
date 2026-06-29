package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataResearchLineRepository extends JpaRepository<ResearchLineEntity, Integer> {

    boolean existsByLineName(String lineName);

    List<ResearchLineEntity> findByActiveTrue();

    @Query(value = """
            SELECT l.* FROM lineas_investigacion l
            INNER JOIN lineas_por_grupo lpg ON l.id_linea = lpg.id_linea
            WHERE lpg.id_grupo = :groupId AND l.es_activa = true
            ORDER BY l.nombre_linea
            """, nativeQuery = true)
    List<ResearchLineEntity> findActiveByGroupId(@Param("groupId") Integer groupId);
}
