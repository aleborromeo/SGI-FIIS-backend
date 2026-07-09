package com.sgi.fiis.users.presentation.mapper;

import com.sgi.fiis.users.application.dto.UserRequestDto;
import com.sgi.fiis.users.application.dto.UserResponseDto;
import com.sgi.fiis.users.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserMapper Unit Tests")
class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    @DisplayName("Should map UserRequestDto to User domain successfully")
    void testToDomain() {
        UserRequestDto dto = UserRequestDto.builder()
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("987654321")
                .roleCode("DOCENTE")
                .build();

        User domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals("12345678", domain.getDni());
        assertEquals("Juan", domain.getFirstNames());
        assertEquals("Perez", domain.getLastNames());
        assertEquals("juan.perez@unas.edu.pe", domain.getInstitutionalEmail());
        assertEquals("987654321", domain.getPhone());
        assertEquals("DOCENTE", domain.getRoleCode());
    }

    @Test
    @DisplayName("Should map User domain to UserResponseDto successfully with non-null dates")
    void testToResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        User domain = User.builder()
                .id(1L)
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("987654321")
                .active(true)
                .mustChangePassword(false)
                .roleCode("DOCENTE")
                .roleDescription("Docente ordinario")
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("12345678", dto.getDni());
        assertEquals("Juan", dto.getFirstNames());
        assertEquals("Perez", dto.getLastNames());
        assertEquals("juan.perez@unas.edu.pe", dto.getInstitutionalEmail());
        assertEquals("987654321", dto.getPhone());
        assertTrue(dto.isActive());
        assertFalse(dto.isMustChangePassword());
        assertEquals("DOCENTE", dto.getRoleCode());
        assertEquals("Docente ordinario", dto.getRoleDescription());
        assertEquals(now.toString(), dto.getCreatedAt());
        assertEquals(now.toString(), dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map User domain to UserResponseDto successfully with null dates")
    void testToResponseDtoNullDates() {
        User domain = User.builder()
                .id(1L)
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("987654321")
                .active(true)
                .mustChangePassword(false)
                .roleCode("DOCENTE")
                .roleDescription("Docente ordinario")
                .createdAt(null)
                .updatedAt(null)
                .build();

        UserResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return null when mapping null User domain")
    void testToResponseDtoNull() {
        assertNull(mapper.toResponseDto(null));
    }
}
