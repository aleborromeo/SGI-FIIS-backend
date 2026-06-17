package com.sgi.fiis.users.presentation.mapper;

import com.sgi.fiis.users.application.dto.UsuarioRequestDto;
import com.sgi.fiis.users.application.dto.UsuarioResponseDto;
import com.sgi.fiis.users.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioRequestDto dto) {
        return Usuario.builder()
                .dni(dto.getDni())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .correoInstitucional(dto.getCorreoInstitucional())
                .telefono(dto.getTelefono())
                .rolCodigo(dto.getRolCodigo())
                .build();
    }

    public UsuarioResponseDto toResponseDto(Usuario domain) {
        return UsuarioResponseDto.builder()
                .id(domain.getId())
                .dni(domain.getDni())
                .nombres(domain.getNombres())
                .apellidos(domain.getApellidos())
                .correoInstitucional(domain.getCorreoInstitucional())
                .telefono(domain.getTelefono())
                .activo(domain.isActivo())
                .mustChangePassword(domain.isMustChangePassword())
                .rolCodigo(domain.getRolCodigo())
                .rolDescripcion(domain.getRolDescripcion())
                .fechaCreacion(domain.getFechaCreacion() != null ? domain.getFechaCreacion().toString() : null)
                .fechaActualizacion(domain.getFechaActualizacion() != null ? domain.getFechaActualizacion().toString() : null)
                .build();
    }
}
