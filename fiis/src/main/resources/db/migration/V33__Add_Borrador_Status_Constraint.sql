-- ============================================================================
-- V33: Agregar estado BORRADOR al CHECK constraint de proyectos
-- ============================================================================

-- El estado BORRADOR es necesario para que los borradores se guarden correctamente
-- sin violar la restricción CHECK de la tabla proyectos.
ALTER TABLE proyectos
    DROP CONSTRAINT IF EXISTS chk_estado_proyecto;

ALTER TABLE proyectos
    ADD CONSTRAINT chk_estado_proyecto
    CHECK (estado IN ('BORRADOR', 'POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO', 'EN_EJECUCION', 'FINALIZADO'));
