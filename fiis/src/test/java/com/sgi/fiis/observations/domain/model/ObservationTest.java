package com.sgi.fiis.observations.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Observation Domain Unit Tests")
class ObservationTest {

    @Test
    @DisplayName("Should create a valid Observation with PENDING status")
    void shouldCreateValidObservation() {
        Observation obs = Observation.create(
                1, 10, ObservationType.TECNICA,
                "Falta firma", "COORDINADOR_GRUPO"
        );

        assertNotNull(obs);
        assertNull(obs.getId());
        assertEquals(1, obs.getProcedureId());
        assertEquals(10, obs.getReviewerId());
        assertEquals(ObservationType.TECNICA, obs.getType());
        assertEquals("Falta firma", obs.getDescription());
        assertEquals(ObservationStatus.PENDIENTE, obs.getStatus());
        assertEquals("COORDINADOR_GRUPO", obs.getReviewerRole());
        assertNotNull(obs.getCreatedAt());
        assertNotNull(obs.getUpdatedAt());
    }

    @Test
    @DisplayName("Should change status to REMEDIED when marking as remedied")
    void shouldMarkAsRemedied() {
        Observation obs = Observation.create(
                1, 10, ObservationType.TECNICA,
                "Falta firma", "COORDINADOR_GRUPO"
        );

        assertTrue(obs.isRemediable());
        boolean result = obs.markAsRemedied();

        assertTrue(result);
        assertEquals(ObservationStatus.SUBSANADA, obs.getStatus());
        assertFalse(obs.isRemediable());
    }

    @Test
    @DisplayName("Should fail to mark as remedied if already remedied")
    void shouldFailToMarkIfAlreadyRemedied() {
        Observation obs = Observation.builder()
                .status(ObservationStatus.SUBSANADA)
                .build();

        assertFalse(obs.isRemediable());
        boolean result = obs.markAsRemedied();

        assertFalse(result);
        assertEquals(ObservationStatus.SUBSANADA, obs.getStatus());
    }
}
