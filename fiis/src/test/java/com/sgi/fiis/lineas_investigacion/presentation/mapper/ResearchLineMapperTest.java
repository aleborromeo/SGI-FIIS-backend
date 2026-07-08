package com.sgi.fiis.lineas_investigacion.presentation.mapper;

import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineResponseDto;
import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchLineMapper Unit Tests")
class ResearchLineMapperTest {

    private final ResearchLineMapper mapper = new ResearchLineMapper();

    @Test
    @DisplayName("Should map ResearchLineRequestDto to ResearchLine domain model")
    void toDomain_shouldMapDtoToDomain() {
        ResearchLineRequestDto dto = ResearchLineRequestDto.builder()
                .lineName("Sistemas de Informacion")
                .build();

        ResearchLine domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals("Sistemas de Informacion", domain.getLineName());
    }

    @Test
    @DisplayName("Should return null when toDomain receives null DTO")
    void toDomain_shouldReturnNullWhenDtoIsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Should map ResearchLine domain model to ResearchLineResponseDto")
    void toResponseDto_shouldMapDomainToDto() {
        LocalDateTime now = LocalDateTime.now();
        ResearchLine domain = ResearchLine.builder()
                .id(1)
                .lineName("Sistemas de Informacion")
                .active(true)
                .createdAt(now)
                .updatedAt(now.plusDays(1))
                .build();

        ResearchLineResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Sistemas de Informacion", dto.getLineName());
        assertTrue(dto.isActive());
        assertEquals(now.toString(), dto.getCreatedAt());
        assertEquals(now.plusDays(1).toString(), dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map ResearchLine domain model to ResearchLineResponseDto with null timestamps")
    void toResponseDto_shouldMapDomainToDtoWithNullTimestamps() {
        ResearchLine domain = ResearchLine.builder()
                .id(1)
                .lineName("Sistemas de Informacion")
                .active(true)
                .createdAt(null)
                .updatedAt(null)
                .build();

        ResearchLineResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return null when toResponseDto receives null domain model")
    void toResponseDto_shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toResponseDto(null));
    }
}
