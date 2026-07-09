package com.sgi.fiis.grupos_investigacion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Membership Domain Unit Tests")
class MembershipTest {

    @Test
    @DisplayName("Should build a Membership with all fields set")
    void shouldBuildMembership() {
        LocalDateTime start = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0);

        Membership membership = Membership.builder()
                .id(1)
                .groupId(2)
                .userId(3)
                .userFirstNames("Juan")
                .userLastNames("Perez")
                .userEmail("juan.perez@unas.edu.pe")
                .active(true)
                .startDate(start)
                .build();

        assertEquals(1, membership.getId());
        assertEquals(2, membership.getGroupId());
        assertEquals(3, membership.getUserId());
        assertEquals("Juan", membership.getUserFirstNames());
        assertEquals("Perez", membership.getUserLastNames());
        assertEquals("juan.perez@unas.edu.pe", membership.getUserEmail());
        assertTrue(membership.isActive());
        assertEquals(start, membership.getStartDate());
        assertNull(membership.getEndDate());
    }

    @Test
    @DisplayName("Should deactivate the membership and set endDate when remove() is called")
    void shouldDeactivateAndSetEndDateOnRemove() {
        Membership membership = Membership.builder()
                .id(1)
                .groupId(2)
                .userId(3)
                .active(true)
                .startDate(LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0))
                .build();

        LocalDateTime beforeRemove = LocalDateTime.now();
        membership.remove();
        LocalDateTime afterRemove = LocalDateTime.now();

        assertFalse(membership.isActive());
        assertNotNull(membership.getEndDate());
        assertFalse(membership.getEndDate().isBefore(beforeRemove));
        assertFalse(membership.getEndDate().isAfter(afterRemove));
    }

    @Test
    @DisplayName("Should overwrite a previous endDate when remove() is called again")
    void shouldOverwriteEndDateOnRepeatedRemove() {
        Membership membership = Membership.builder()
                .active(true)
                .build();

        membership.remove();
        LocalDateTime firstEndDate = membership.getEndDate();

        membership.remove();

        assertFalse(membership.isActive());
        assertNotNull(membership.getEndDate());
        assertFalse(membership.getEndDate().isBefore(firstEndDate));
    }
}