package com.sgi.fiis.grupos_investigacion.presentation.mapper;

import com.sgi.fiis.grupos_investigacion.application.dto.GrupoInvestigacionRequestDto;
import com.sgi.fiis.grupos_investigacion.application.dto.GrupoInvestigacionResponseDto;
import com.sgi.fiis.grupos_investigacion.application.dto.MembresiaResponseDto;
import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import org.springframework.stereotype.Component;

@Component
public class GrupoInvestigacionMapper {

    public GrupoInvestigacion toDomain(GrupoInvestigacionRequestDto dto) {
        return GrupoInvestigacion.builder()
                .codigoGrupo(dto.getCodigoGrupo())
                .nombreGrupo(dto.getNombreGrupo())
                .build();
    }

    public GrupoInvestigacionResponseDto toResponseDto(GrupoInvestigacion domain) {
        return GrupoInvestigacionResponseDto.builder()
                .id(domain.getId())
                .codigoGrupo(domain.getCodigoGrupo())
                .nombreGrupo(domain.getNombreGrupo())
                .idCoordinadorActual(domain.getIdCoordinadorActual())
                .coordinadorNombres(domain.getCoordinadorNombres())
                .coordinadorApellidos(domain.getCoordinadorApellidos())
                .esActivo(domain.isEsActivo())
                .build();
    }

    public MembresiaResponseDto toMembresiaResponseDto(Membresia membresia) {
        return MembresiaResponseDto.builder()
                .id(membresia.getId())
                .idGrupo(membresia.getIdGrupo())
                .idUsuario(membresia.getIdUsuario())
                .usuarioNombres(membresia.getUsuarioNombres())
                .usuarioApellidos(membresia.getUsuarioApellidos())
                .usuarioCorreo(membresia.getUsuarioCorreo())
                .esActivo(membresia.isEsActivo())
                .fechaInicio(membresia.getFechaInicio() != null ? membresia.getFechaInicio().toString() : null)
                .fechaFin(membresia.getFechaFin() != null ? membresia.getFechaFin().toString() : null)
                .build();
    }
}
