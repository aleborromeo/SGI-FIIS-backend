package com.sgi.fiis.tramites.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcedureEntity Unit Tests")
class ProcedureEntityTest {

    @Test
    @DisplayName("Should create entity using no-args constructor with null fields")
    void testNoArgsConstructor() {
        ProcedureEntity entity = new ProcedureEntity();
        assertNull(entity.getId());
        assertNull(entity.getCodigoTramite());
        assertNull(entity.getEstadoActual());
    }

    @Test
    @DisplayName("Should set and get all main fields via setters")
    void testSettersAndGetters() {
        ProcedureEntity entity = new ProcedureEntity();

        entity.setId(3L);
        entity.setCodigoTramite("TRM-2026-001");
        entity.setTipoTramite("PROYECTO");
        entity.setIdSolicitante(10L);
        entity.setIdGrupo(20L);
        entity.setEstadoActual("PENDING_COORDINATOR");
        entity.setRolRevisorActual("COORDINADOR_GRUPO");
        entity.setFechaEnvio(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0));
        entity.setFechaActualizacion(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0));

        assertEquals(3L, entity.getId());
        assertEquals("TRM-2026-001", entity.getCodigoTramite());
        assertEquals("PROYECTO", entity.getTipoTramite());
        assertEquals(10L, entity.getIdSolicitante());
        assertEquals(20L, entity.getIdGrupo());
        assertEquals("PENDING_COORDINATOR", entity.getEstadoActual());
        assertEquals("COORDINADOR_GRUPO", entity.getRolRevisorActual());
        assertNotNull(entity.getFechaEnvio());
        assertNotNull(entity.getFechaActualizacion());
    }

    @Test
    @DisplayName("Should allow setting tramites-specific fields")
    void testTramitesSpecificFields() {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setObservacionActual("Requiere corrección en el resumen");
        entity.setIdReferenciaProyecto(5L);
        entity.setIdReferenciaTesis(7L);
        entity.setIdReferenciaInforme(8L);

        assertEquals("Requiere corrección en el resumen", entity.getObservacionActual());
        assertEquals(5L, entity.getIdReferenciaProyecto());
        assertEquals(7L, entity.getIdReferenciaTesis());
        assertEquals(8L, entity.getIdReferenciaInforme());
    }

    @Test
    @DisplayName("Should return null for nullable fields when not set")
    void testNullableFields() {
        ProcedureEntity entity = new ProcedureEntity();

        assertNull(entity.getIdGrupo());
        assertNull(entity.getRolRevisorActual());
        assertNull(entity.getObservacionActual());
        assertNull(entity.getIdReferenciaProyecto());
        assertNull(entity.getIdReferenciaTesis());
        assertNull(entity.getIdReferenciaInforme());
    }

    @Test
    @DisplayName("Should allow overwriting fields with setters")
    void testOverwriteFields() {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setEstadoActual("PENDING_COORDINATOR");
        entity.setRolRevisorActual("COORDINADOR_GRUPO");

        entity.setEstadoActual("PENDING_DIRECTION");
        entity.setRolRevisorActual("DIRECTOR_INVESTIGACION");

        assertEquals("PENDING_DIRECTION", entity.getEstadoActual());
        assertEquals("DIRECTOR_INVESTIGACION", entity.getRolRevisorActual());
    }
}
