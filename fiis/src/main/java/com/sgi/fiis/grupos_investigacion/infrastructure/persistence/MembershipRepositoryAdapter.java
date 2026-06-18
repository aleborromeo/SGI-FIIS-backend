package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Component
public class MembershipRepositoryAdapter implements MembershipRepositoryPort {

    private final SpringDataMembershipRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public MembershipRepositoryAdapter(SpringDataMembershipRepository jpaRepository,
                                       JdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Membership save(Membership membership) {
        MembershipEntity entity = toEntity(membership);
        MembershipEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Membership> findById(Integer id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Membership> findActiveByUserInGroup(Integer userId, Integer groupId) {
        return jpaRepository.findByUserIdAndGroupIdAndActiveTrue(userId, groupId)
                .map(this::toDomain);
    }

    @Override
    public List<Membership> findActiveByGroup(Integer groupId) {
        String sql = """
                SELECT m.id_membresia, m.id_grupo, m.id_usuario, m.es_activo,
                       m.fecha_inicio, m.fecha_fin,
                       u.nombres AS user_first_names,
                       u.apellidos AS user_last_names,
                       u.correo_institucional AS user_email
                FROM membresias_grupo m
                JOIN usuarios u ON m.id_usuario = u.id_usuario
                WHERE m.id_grupo = ? AND m.es_activo = TRUE
                ORDER BY u.apellidos, u.nombres
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> fromRow(rs), groupId);
    }

    @Override
    public boolean existsActiveByUser(Integer userId) {
        return jpaRepository.existsByUserIdAndActiveTrue(userId);
    }

    private Membership fromRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Timestamp tsInicio = rs.getTimestamp("fecha_inicio");
        Timestamp tsFin = rs.getTimestamp("fecha_fin");
        return Membership.builder()
                .id(rs.getInt("id_membresia"))
                .groupId(rs.getInt("id_grupo"))
                .userId(rs.getInt("id_usuario"))
                .active(rs.getBoolean("es_activo"))
                .startDate(tsInicio != null ? tsInicio.toLocalDateTime() : null)
                .endDate(tsFin != null ? tsFin.toLocalDateTime() : null)
                .userFirstNames(rs.getString("user_first_names"))
                .userLastNames(rs.getString("user_last_names"))
                .userEmail(rs.getString("user_email"))
                .build();
    }

    private Membership toDomain(MembershipEntity entity) {
        return Membership.builder()
                .id(entity.getId())
                .groupId(entity.getGroupId())
                .userId(entity.getUserId())
                .active(entity.isActive())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    private MembershipEntity toEntity(Membership domain) {
        MembershipEntity entity = new MembershipEntity();
        entity.setId(domain.getId());
        entity.setGroupId(domain.getGroupId());
        entity.setUserId(domain.getUserId());
        entity.setActive(domain.isActive());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        return entity;
    }
}
