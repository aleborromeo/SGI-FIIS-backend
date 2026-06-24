package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ResearchGroupRepositoryAdapter implements ResearchGroupRepositoryPort {

    private final SpringDataResearchGroupRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public ResearchGroupRepositoryAdapter(SpringDataResearchGroupRepository jpaRepository,
                                         JdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ResearchGroup save(ResearchGroup group) {
        ResearchGroupEntity entity = toEntity(group);
        ResearchGroupEntity saved = jpaRepository.save(entity);
        return enrichWithCoordinator(toDomain(saved));
    }

    @Override
    public Optional<ResearchGroup> findById(Integer id) {
        return jpaRepository.findById(id)
                .map(entity -> enrichWithCoordinator(toDomain(entity)));
    }

    @Override
    public List<ResearchGroup> findAll() {
        String sql = """
                SELECT g.id_grupo, g.codigo_grupo, g.nombre_grupo,
                       g.id_coordinador_actual, g.es_activo,
                       u.nombres AS coordinator_first_names,
                       u.apellidos AS coordinator_last_names
                 FROM grupos_investigacion g
                 LEFT JOIN usuarios u ON g.id_coordinador_actual = u.id_usuario
                 ORDER BY g.nombre_grupo
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> ResearchGroup.builder()
                .id(rs.getInt("id_grupo"))
                .groupCode(rs.getString("codigo_grupo"))
                .groupName(rs.getString("nombre_grupo"))
                .currentCoordinatorId(rs.getObject("id_coordinador_actual") != null
                        ? rs.getInt("id_coordinador_actual") : null)
                .active(rs.getBoolean("es_activo"))
                .coordinatorFirstNames(rs.getString("coordinator_first_names"))
                .coordinatorLastNames(rs.getString("coordinator_last_names"))
                .build());
    }

    @Override
    public boolean existsByCode(String groupCode) {
        return jpaRepository.existsByGroupCode(groupCode);
    }

    @Override
    public boolean existsActiveUser(Integer userId) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE id_usuario = ? AND es_activo = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsActiveUserWithRole(Integer userId, String roleCode) {
        String sql = """
                SELECT COUNT(*) FROM usuarios u
                WHERE u.id_usuario = ? AND u.es_activo = TRUE 
                  AND (
                    EXISTS (SELECT 1 FROM roles r WHERE r.id_rol = u.id_rol_principal AND r.codigo_rol = ?)
                    OR EXISTS (SELECT 1 FROM usuarios_roles ur JOIN roles r ON ur.id_rol = r.id_rol WHERE ur.id_usuario = u.id_usuario AND r.codigo_rol = ?)
                  )
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, roleCode, roleCode);
        return count != null && count > 0;
    }

    private ResearchGroup enrichWithCoordinator(ResearchGroup group) {
        if (group.getCurrentCoordinatorId() == null) {
            return group;
        }
        String sql = "SELECT nombres, apellidos FROM usuarios WHERE id_usuario = ?";
        List<ResearchGroup> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    group.setCoordinatorFirstNames(rs.getString("nombres"));
                    group.setCoordinatorLastNames(rs.getString("apellidos"));
                    return group;
                },
                group.getCurrentCoordinatorId());
        return result.isEmpty() ? group : result.get(0);
    }

    private ResearchGroup toDomain(ResearchGroupEntity entity) {
        return ResearchGroup.builder()
                .id(entity.getId())
                .groupCode(entity.getGroupCode())
                .groupName(entity.getGroupName())
                .currentCoordinatorId(entity.getCurrentCoordinatorId())
                .active(entity.isActive())
                .build();
    }

    private ResearchGroupEntity toEntity(ResearchGroup domain) {
        ResearchGroupEntity entity = new ResearchGroupEntity();
        entity.setId(domain.getId());
        entity.setGroupCode(domain.getGroupCode());
        entity.setGroupName(domain.getGroupName());
        entity.setCurrentCoordinatorId(domain.getCurrentCoordinatorId());
        entity.setActive(domain.isActive());
        return entity;
    }
}
