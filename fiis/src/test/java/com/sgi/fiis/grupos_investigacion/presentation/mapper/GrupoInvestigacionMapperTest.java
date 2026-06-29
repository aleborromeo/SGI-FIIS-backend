package com.sgi.fiis.grupos_investigacion.presentation.mapper;

import com.sgi.fiis.grupos_investigacion.application.dto.GrupoInvestigacionRequestDto;
import com.sgi.fiis.grupos_investigacion.application.dto.GrupoInvestigacionResponseDto;
import com.sgi.fiis.grupos_investigacion.application.dto.MembresiaResponseDto;
import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GrupoInvestigacionMapperTest {

    private final GrupoInvestigacionMapper mapper = new GrupoInvestigacionMapper();

    @Test
    void toDomain() {
        GrupoInvestigacionRequestDto dto = new GrupoInvestigacionRequestDto();
        dto.setCodigoGrupo("GIN-001");
        dto.setNombreGrupo("Grupo Test");

        GrupoInvestigacion domain = mapper.toDomain(dto);
        assertEquals("GIN-001", domain.getCodigoGrupo());
        assertEquals("Grupo Test", domain.getNombreGrupo());
    }

    @Test
    void toResponseDto() {
        GrupoInvestigacion domain = GrupoInvestigacion.builder()
                .id(1)
                .codigoGrupo("GIN-001")
                .nombreGrupo("Grupo Test")
                .idCoordinadorActual(10)
                .coordinadorNombres("John")
                .coordinadorApellidos("Doe")
                .esActivo(true)
                .build();

        GrupoInvestigacionResponseDto response = mapper.toResponseDto(domain);
        assertEquals(1, response.getId());
        assertEquals("GIN-001", response.getCodigoGrupo());
        assertEquals("Grupo Test", response.getNombreGrupo());
        assertEquals(10, response.getIdCoordinadorActual());
        assertEquals("John", response.getCoordinadorNombres());
        assertEquals("Doe", response.getCoordinadorApellidos());
        assertTrue(response.isEsActivo());
    }

    @Test
    void toMembresiaResponseDtoWithNonNullDates() {
        LocalDateTime now = LocalDateTime.now();
        Membresia membresia = Membresia.builder()
                .id(1)
                .idGrupo(2)
                .idUsuario(3)
                .usuarioNombres("John")
                .usuarioApellidos("Doe")
                .usuarioCorreo("john@doe.com")
                .esActivo(true)
                .fechaInicio(now)
                .fechaFin(now.plusDays(5))
                .build();

        MembresiaResponseDto response = mapper.toMembresiaResponseDto(membresia);
        assertEquals(1, response.getId());
        assertEquals(2, response.getIdGrupo());
        assertEquals(3, response.getIdUsuario());
        assertEquals("John", response.getUsuarioNombres());
        assertEquals("Doe", response.getUsuarioApellidos());
        assertEquals("john@doe.com", response.getUsuarioCorreo());
        assertTrue(response.isEsActivo());
        assertEquals(now.toString(), response.getFechaInicio());
        assertEquals(now.plusDays(5).toString(), response.getFechaFin());
    }

    @Test
    void toMembresiaResponseDtoWithNullDates() {
        Membresia membresia = Membresia.builder()
                .id(1)
                .idGrupo(2)
                .idUsuario(3)
                .esActivo(true)
                .fechaInicio(null)
                .fechaFin(null)
                .build();

        MembresiaResponseDto response = mapper.toMembresiaResponseDto(membresia);
        assertNull(response.getFechaInicio());
        assertNull(response.getFechaFin());
    }
}
