package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProcedureRecentActivityTest {

    @Test
    void shouldSetAndGetAllProperties() {
        var now = LocalDateTime.now();
        var activity = new ProcedureRecentActivity();

        activity.setProcedureId(1);
        activity.setProcedureCode("CODE-001");
        activity.setProcedureType("PROYECTO");
        activity.setCurrentStatus("APROBADO");
        activity.setMovementCount(3);
        activity.setLastMovementDate(now);
        activity.setLastAction("REVISAR");
        activity.setLastUserName("Juan Perez");

        assertEquals(1, activity.getProcedureId());
        assertEquals("CODE-001", activity.getProcedureCode());
        assertEquals("PROYECTO", activity.getProcedureType());
        assertEquals("APROBADO", activity.getCurrentStatus());
        assertEquals(3, activity.getMovementCount());
        assertEquals(now, activity.getLastMovementDate());
        assertEquals("REVISAR", activity.getLastAction());
        assertEquals("Juan Perez", activity.getLastUserName());
    }

    @Test
    void shouldHandleNullValues() {
        var activity = new ProcedureRecentActivity();
        assertNull(activity.getProcedureId());
        assertNull(activity.getProcedureCode());
        assertNull(activity.getProcedureType());
        assertNull(activity.getCurrentStatus());
        assertNull(activity.getMovementCount());
        assertNull(activity.getLastMovementDate());
        assertNull(activity.getLastAction());
        assertNull(activity.getLastUserName());
    }
}
