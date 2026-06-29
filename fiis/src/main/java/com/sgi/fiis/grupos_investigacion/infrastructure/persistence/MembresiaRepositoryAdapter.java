package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.domain.port.MembresiaRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

@Component
public class MembresiaRepositoryAdapter implements MembresiaRepositoryPort {

    private final SpringDataMembresiaRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public MembresiaRepositoryAdapter(SpringDataMembresiaRepository jpaRepository,
                                      JdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Membresia save(Membresia membresia) {
        MembresiaEntity entity = toEntity(membresia);
        MembresiaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Membresia> findById(Integer id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Membresia> findActivaByUsuarioEnGrupo(Integer idUsuario, Integer idGrupo) {
        return jpaRepository.findByIdUsuarioAndIdGrupoAndEsActivoTrue(idUsuario, idGrupo)
                .map(this::toDomain);
    }

    @Override
    public List<Membresia> findActivasByGrupo(Integer idGrupo) {
        String sql = """
                SELECT m.id_membresia, m.id_grupo, m.id_usuario, m.es_activo,
                       m.fecha_inicio, m.fecha_fin,
                       u.nombres AS usuario_nombres,
                       u.apellidos AS usuario_apellidos,
                       u.correo AS usuario_correo
                FROM membresias_grupo m
                JOIN usuarios u ON m.id_usuario = u.id_usuario
                WHERE m.id_grupo = ? AND m.es_activo = TRUE
                ORDER BY u.apellidos, u.nombres
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> fromRow(rs), idGrupo);
    }

    @Override
    public boolean existsActivaByUsuario(Integer idUsuario) {
        return jpaRepository.existsByIdUsuarioAndEsActivoTrue(idUsuario);
    }

    private Membresia fromRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        java.time.LocalDateTime tsInicio = rs.getObject("fecha_inicio", java.time.LocalDateTime.class);
        java.time.LocalDateTime tsFin = rs.getObject("fecha_fin", java.time.LocalDateTime.class);
        return Membresia.builder()
                .id(rs.getInt("id_membresia"))
                .idGrupo(rs.getInt("id_grupo"))
                .idUsuario(rs.getInt("id_usuario"))
                .esActivo(rs.getBoolean("es_activo"))
                .fechaInicio(tsInicio)
                .fechaFin(tsFin)
                .usuarioNombres(rs.getString("usuario_nombres"))
                .usuarioApellidos(rs.getString("usuario_apellidos"))
                .usuarioCorreo(rs.getString("usuario_correo"))
                .build();
    }

    private Membresia toDomain(MembresiaEntity entity) {
        return Membresia.builder()
                .id(entity.getId())
                .idGrupo(entity.getIdGrupo())
                .idUsuario(entity.getIdUsuario())
                .esActivo(entity.isEsActivo())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .build();
    }

    private MembresiaEntity toEntity(Membresia domain) {
        MembresiaEntity entity = new MembresiaEntity();
        entity.setId(domain.getId());
        entity.setIdGrupo(domain.getIdGrupo());
        entity.setIdUsuario(domain.getIdUsuario());
        entity.setEsActivo(domain.isEsActivo());
        entity.setFechaInicio(domain.getFechaInicio());
        entity.setFechaFin(domain.getFechaFin());
        return entity;
    }
}
