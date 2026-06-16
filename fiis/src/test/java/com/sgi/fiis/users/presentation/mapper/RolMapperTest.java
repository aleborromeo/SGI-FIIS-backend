package com.sgi.fiis.users.presentation.mapper;

import com.sgi.fiis.users.application.dto.RolResponseDto;
import com.sgi.fiis.users.domain.model.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RolMapper Unit Tests")
class RolMapperTest {

    private final RolMapper mapper = new RolMapper();

    @Test
    @DisplayName("Should map Rol domain to response DTO successfully")
    void testToResponseDto() {
        Rol domain = Rol.builder()
                .id(1L)
                .codigoRol("ADMIN")
                .descripcion("Administrador")
                .build();

        RolResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("ADMIN", dto.getCodigoRol());
        assertEquals("Administrador", dto.getDescripcion());
    }

    @Test
    @DisplayName("Should return null when mapping null Rol domain")
    void testToResponseDtoNull() {
        assertNull(mapper.toResponseDto(null));
    }
}
