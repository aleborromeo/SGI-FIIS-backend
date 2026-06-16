package com.sgi.fiis.users.presentation.mapper;

import com.sgi.fiis.users.application.dto.RolResponseDto;
import com.sgi.fiis.users.domain.model.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {

    public RolResponseDto toResponseDto(Rol domain) {
        if (domain == null) {
            return null;
        }
        return RolResponseDto.builder()
                .id(domain.getId())
                .codigoRol(domain.getCodigoRol())
                .descripcion(domain.getDescripcion())
                .build();
    }
}
