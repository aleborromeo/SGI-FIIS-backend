package com.sgi.fiis.users.presentation.mapper;

import com.sgi.fiis.users.application.dto.RoleResponseDto;
import com.sgi.fiis.users.domain.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoleMapper Unit Tests")
class RoleMapperTest {

    private final RoleMapper mapper = new RoleMapper();

    @Test
    @DisplayName("Should map Role domain to response DTO successfully")
    void testToResponseDto() {
        Role domain = Role.builder()
                .id(1L)
                .code("ADMIN")
                .description("Administrador")
                .build();

        RoleResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("ADMIN", dto.getCode());
        assertEquals("Administrador", dto.getDescription());
    }

    @Test
    @DisplayName("Should return null when mapping null Role domain")
    void testToResponseDtoNull() {
        assertNull(mapper.toResponseDto(null));
    }
}
