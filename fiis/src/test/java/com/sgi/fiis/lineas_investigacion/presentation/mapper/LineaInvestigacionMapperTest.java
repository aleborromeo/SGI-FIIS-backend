package com.sgi.fiis.lineas_investigacion.presentation.mapper;

import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LineaInvestigacionMapperTest {

    private final LineaInvestigacionMapper mapper = new LineaInvestigacionMapper();

    @Test
    void toDomainShouldMapRequestDto() {
        LineaInvestigacionRequestDto dto = new LineaInvestigacionRequestDto();
        dto.setNombreLinea("Test Linea");

        LineaInvestigacion domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals("Test Linea", domain.getNombreLinea());
    }

    @Test
    void toResponseDtoShouldMapDomain() {
        LineaInvestigacion domain = LineaInvestigacion.builder()
                .id(1)
                .nombreLinea("Test")
                .esActiva(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        LineaInvestigacionResponseDto response = mapper.toResponseDto(domain);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test", response.getNombreLinea());
        assertTrue(response.isEsActiva());
    }
}
