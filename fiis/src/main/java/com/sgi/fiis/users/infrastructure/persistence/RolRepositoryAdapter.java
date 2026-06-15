package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.Rol;
import com.sgi.fiis.users.domain.port.RolRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final SpringDataRolRepository springDataRepository;

    public RolRepositoryAdapter(SpringDataRolRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Rol> findByCodigo(String codigoRol) {
        return springDataRepository.findByCodigoRol(codigoRol)
                .map(this::toDomain);
    }

    @Override
    public List<Rol> findAll() {
        return springDataRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Rol toDomain(RolEntity entity) {
        return Rol.builder()
                .id(entity.getId())
                .codigoRol(entity.getCodigoRol())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
