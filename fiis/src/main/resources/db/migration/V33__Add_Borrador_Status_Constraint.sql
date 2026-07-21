-- ============================================================================
-- V33: Agregar estado BORRADOR al CHECK constraint de proyectos
-- ============================================================================

-- El estado BORRADOR es necesario para que los borradores se guarden correctamente
-- sin violar la restricción CHECK de la tabla proyectos.
ALTER TABLE proyectos
    DROP CONSTRAINT IF EXISTS chk_estado_proyecto;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'proyectos' AND column_name = 'estado_proyecto'
    ) THEN
        ALTER TABLE proyectos
            ADD CONSTRAINT chk_estado_proyecto
            CHECK (estado_proyecto IN ('BORRADOR', 'POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO', 'EN_EJECUCION', 'FINALIZADO'));
    ELSE
        ALTER TABLE proyectos
            ADD CONSTRAINT chk_estado_proyecto
            CHECK (estado IN ('BORRADOR', 'POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO', 'EN_EJECUCION', 'FINALIZADO'));
    END IF;
END $$;
