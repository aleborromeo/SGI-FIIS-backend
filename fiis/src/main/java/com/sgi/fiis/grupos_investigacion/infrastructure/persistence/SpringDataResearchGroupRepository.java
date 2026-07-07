package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataResearchGroupRepository extends JpaRepository<ResearchGroupEntity, Integer> {
    boolean existsByGroupCode(String groupCode);

    @org.springframework.data.jpa.repository.Query(value = """
            SELECT g.* FROM grupos_investigacion g
            INNER JOIN lineas_por_grupo lpg ON g.id_grupo = lpg.id_grupo
            WHERE lpg.id_linea = :lineId AND g.es_activo = true
            ORDER BY g.nombre_grupo
            """, nativeQuery = true)
    java.util.List<ResearchGroupEntity> findActiveByLineId(@org.springframework.data.repository.query.Param("lineId") Integer lineId);
}
