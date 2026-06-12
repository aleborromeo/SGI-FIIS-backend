package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository springDataRepository;
    private final SpringDataRolRepository rolRepository;

    public UsuarioRepositoryAdapter(SpringDataUsuarioRepository springDataRepository,
                                     SpringDataRolRepository rolRepository) {
        this.springDataRepository = springDataRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByDni(String dni) {
        return springDataRepository.findByDni(dni).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByCorreo(String correoInstitucional) {
        return springDataRepository.findByCorreoInstitucional(correoInstitucional).map(this::toDomain);
    }

    @Override
    public List<Usuario> findAll() {
        return springDataRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> search(String query) {
        return springDataRepository.search(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByDni(String dni) {
        return springDataRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByCorreo(String correoInstitucional) {
        return springDataRepository.existsByCorreoInstitucional(correoInstitucional);
    }

    // ========== Mappers internos ==========

    private Usuario toDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .dni(entity.getDni())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .correoInstitucional(entity.getCorreoInstitucional())
                .telefono(entity.getTelefono())
                .passwordHash(entity.getPasswordHash())
                .activo(entity.isActivo())
                .mustChangePassword(entity.isMustChangePassword())
                .rolCodigo(entity.getRol().getCodigoRol())
                .rolDescripcion(entity.getRol().getDescripcion())
                .oauthProvider(entity.getOauthProvider())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private UsuarioEntity toEntity(Usuario domain) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setDni(domain.getDni());
        entity.setNombres(domain.getNombres());
        entity.setApellidos(domain.getApellidos());
        entity.setCorreoInstitucional(domain.getCorreoInstitucional());
        entity.setTelefono(domain.getTelefono());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setActivo(domain.isActivo());
        entity.setMustChangePassword(domain.isMustChangePassword());
        entity.setOauthProvider(domain.getOauthProvider());
        entity.setFechaCreacion(domain.getFechaCreacion());
        entity.setFechaActualizacion(domain.getFechaActualizacion());

        // Buscar la entidad de rol por código
        RolEntity rolEntity = rolRepository.findByCodigoRol(domain.getRolCodigo())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + domain.getRolCodigo()));
        entity.setRol(rolEntity);

        return entity;
    }
}
