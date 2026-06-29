package com.sgi.fiis.observations.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Remedy Domain Unit Tests")
class RemedyTest {

    @Test
    @DisplayName("Should create a valid Remedy")
    void shouldCreateValidRemedy() {
        Remedy remedy = Remedy.create(
                100, 5,
                "He subido el documento firmado correctamente.", 45
        );

        assertNotNull(remedy);
        assertNull(remedy.getId());
        assertEquals(100, remedy.getObservationId());
        assertEquals(5, remedy.getApplicantId());
        assertEquals("He subido el documento firmado correctamente.", remedy.getDescription());
        assertEquals(45, remedy.getAttachedDocumentId());
        assertNotNull(remedy.getCreatedAt());
        assertNotNull(remedy.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return true when remedy has document attached")
    void shouldCheckDocumentAttached() {
        Remedy remedyWithDoc = Remedy.builder()
                .attachedDocumentId(45)
                .build();
        Remedy remedyWithoutDoc = Remedy.builder()
                .attachedDocumentId(null)
                .build();

        assertTrue(remedyWithDoc.hasAttachedDocument());
        assertFalse(remedyWithoutDoc.hasAttachedDocument());
    }
}
