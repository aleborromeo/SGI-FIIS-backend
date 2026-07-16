package com.sgi.fiis.grupos_investigacion.presentation.mapper;

import com.sgi.fiis.grupos_investigacion.application.dto.ResearchGroupRequestDto;
import com.sgi.fiis.grupos_investigacion.application.dto.ResearchGroupResponseDto;
import com.sgi.fiis.grupos_investigacion.application.dto.MembershipResponseDto;
import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchGroupMapper Unit Tests")
class ResearchGroupMapperTest {

    private final ResearchGroupMapper mapper = new ResearchGroupMapper();

    @Test
    @DisplayName("Should map ResearchGroupRequestDto to ResearchGroup domain model")
    void toDomain_shouldMapDtoToDomain() {
        ResearchGroupRequestDto dto = ResearchGroupRequestDto.builder()
                .groupCode("GI-01")
                .groupName("Grupo de Investigacion de Inteligencia Artificial")
                .build();

        ResearchGroup domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals("GI-01", domain.getGroupCode());
        assertEquals("Grupo de Investigacion de Inteligencia Artificial", domain.getGroupName());
    }

    @Test
    @DisplayName("Should return null when toDomain receives null DTO")
    void toDomain_shouldReturnNullWhenDtoIsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Should map ResearchGroup domain model to ResearchGroupResponseDto")
    void toResponseDto_shouldMapDomainToDto() {
        ResearchGroup domain = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-01")
                .groupName("Grupo de Investigacion de Inteligencia Artificial")
                .currentCoordinatorId(10)
                .coordinatorFirstNames("Juan")
                .coordinatorLastNames("Perez")
                .active(true)
                .build();

        ResearchGroupResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("GI-01", dto.getGroupCode());
        assertEquals("Grupo de Investigacion de Inteligencia Artificial", dto.getGroupName());
        assertEquals(10, dto.getCurrentCoordinatorId());
        assertEquals("Juan", dto.getCoordinatorFirstNames());
        assertEquals("Perez", dto.getCoordinatorLastNames());
        assertTrue(dto.isActive());
    }

    @Test
    @DisplayName("Should map ResearchGroup with createdAt to ResponseDto")
    void toResponseDto_shouldMapCreatedAt() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 16, 10, 30);
        ResearchGroup domain = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-01")
                .groupName("Grupo Test")
                .active(true)
                .createdAt(now)
                .build();

        ResearchGroupResponseDto dto = mapper.toResponseDto(domain);

        assertNotNull(dto);
        assertEquals(now.toString(), dto.getCreatedAt());
    }

    @Test
    @DisplayName("Should return null when toResponseDto receives null domain model")
    void toResponseDto_shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toResponseDto(null));
    }

    @Test
    @DisplayName("Should map Membership domain model to MembershipResponseDto")
    void toMembershipResponseDto_shouldMapDomainToDto() {
        LocalDateTime now = LocalDateTime.now();
        Membership membership = Membership.builder()
                .id(5)
                .groupId(1)
                .userId(10)
                .userFirstNames("Juan")
                .userLastNames("Perez")
                .userEmail("juan.perez@unas.edu.pe")
                .active(true)
                .startDate(now)
                .endDate(now.plusDays(10))
                .build();

        MembershipResponseDto dto = mapper.toMembershipResponseDto(membership);

        assertNotNull(dto);
        assertEquals(5, dto.getId());
        assertEquals(1, dto.getGroupId());
        assertEquals(10, dto.getUserId());
        assertEquals("Juan", dto.getUserFirstNames());
        assertEquals("Perez", dto.getUserLastNames());
        assertEquals("juan.perez@unas.edu.pe", dto.getUserEmail());
        assertTrue(dto.isActive());
        assertEquals(now.toString(), dto.getStartDate());
        assertEquals(now.plusDays(10).toString(), dto.getEndDate());
    }

    @Test
    @DisplayName("Should map Membership to DTO with null dates")
    void toMembershipResponseDto_shouldMapDomainToDtoWithNullDates() {
        Membership membership = Membership.builder()
                .id(5)
                .groupId(1)
                .userId(10)
                .userFirstNames("Juan")
                .userLastNames("Perez")
                .userEmail("juan.perez@unas.edu.pe")
                .active(true)
                .startDate(null)
                .endDate(null)
                .build();

        MembershipResponseDto dto = mapper.toMembershipResponseDto(membership);

        assertNotNull(dto);
        assertNull(dto.getStartDate());
        assertNull(dto.getEndDate());
    }

    @Test
    @DisplayName("Should return null when toMembershipResponseDto receives null membership")
    void toMembershipResponseDto_shouldReturnNullWhenMembershipIsNull() {
        assertNull(mapper.toMembershipResponseDto(null));
    }
}
