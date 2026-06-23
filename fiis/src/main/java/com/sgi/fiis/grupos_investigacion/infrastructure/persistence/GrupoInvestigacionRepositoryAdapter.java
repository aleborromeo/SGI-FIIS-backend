package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GrupoInvestigacionRepositoryAdapter implements GrupoInvestigacionRepositoryPort {

    private final SpringDataGrupoRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public GrupoInvestigacionRepositoryAdapter(SpringDataGrupoRepository jpaRepository,
                                               JdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public GrupoInvestigacion save(GrupoInvestigacion grupo) {
        GrupoInvestigacionEntity entity = toEntity(grupo);
        GrupoInvestigacionEntity saved = jpaRepository.save(entity);
        return enrichWithCoordinator(toDomain(saved));
    }

    @Override
    public Optional<GrupoInvestigacion> findById(Integer id) {
        return jpaRepository.findById(id)
                .map(entity -> enrichWithCoordinator(toDomain(entity)));
    }

    @Override
    public List<GrupoInvestigacion> findAll() {
        String sql = """
                SELECT g.id_grupo, g.codigo_grupo, g.nombre_grupo,
                       g.id_coordinador_actual, g.es_activo,
                       u.nombres AS coordinador_nombres,
                       u.apellidos AS coordinador_apellidos
                FROM grupos_investigacion g
                LEFT JOIN usuarios u ON g.id_coordinador_actual = u.id_usuario
                ORDER BY g.nombre_grupo
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> GrupoInvestigacion.builder()
                .id(rs.getInt("id_grupo"))
                .codigoGrupo(rs.getString("codigo_grupo"))
                .nombreGrupo(rs.getString("nombre_grupo"))
                .idCoordinadorActual(rs.getObject("id_coordinador_actual") != null
                        ? rs.getInt("id_coordinador_actual") : null)
                .esActivo(rs.getBoolean("es_activo"))
                .coordinadorNombres(rs.getString("coordinador_nombres"))
                .coordinadorApellidos(rs.getString("coordinador_apellidos"))
                .build());
    }

    @Override
    public boolean existsByCodigo(String codigoGrupo) {
        return jpaRepository.existsByCodigoGrupo(codigoGrupo);
    }

    @Override
    public boolean existeUsuarioActivo(Integer idUsuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE id_usuario = ? AND es_activo = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idUsuario);
        return count != null && count > 0;
    }

    private GrupoInvestigacion enrichWithCoordinator(GrupoInvestigacion grupo) {
        if (grupo.getIdCoordinadorActual() == null) {
            return grupo;
        }
        String sql = "SELECT nombres, apellidos FROM usuarios WHERE id_usuario = ?";
        List<GrupoInvestigacion> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    grupo.setCoordinadorNombres(rs.getString("nombres"));
                    grupo.setCoordinadorApellidos(rs.getString("apellidos"));
                    return grupo;
                },
                grupo.getIdCoordinadorActual());
        return result.isEmpty() ? grupo : result.get(0);
    }

    private GrupoInvestigacion toDomain(GrupoInvestigacionEntity entity) {
        return GrupoInvestigacion.builder()
                .id(entity.getId())
                .codigoGrupo(entity.getCodigoGrupo())
                .nombreGrupo(entity.getNombreGrupo())
                .idCoordinadorActual(entity.getIdCoordinadorActual())
                .esActivo(entity.isEsActivo())
                .build();
    }

    private GrupoInvestigacionEntity toEntity(GrupoInvestigacion domain) {
        GrupoInvestigacionEntity entity = new GrupoInvestigacionEntity();
        entity.setId(domain.getId());
        entity.setCodigoGrupo(domain.getCodigoGrupo());
        entity.setNombreGrupo(domain.getNombreGrupo());
        entity.setIdCoordinadorActual(domain.getIdCoordinadorActual());
        entity.setEsActivo(domain.isEsActivo());
        return entity;
    }
}
