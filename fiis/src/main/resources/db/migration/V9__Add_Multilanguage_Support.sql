-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE MIGRATION V9 - ADD MULTILANGUAGE SUPPORT (JSONB)
-- ============================================================================

DO $$
BEGIN
    -- Add JSONB columns to convocatorias if they don't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'convocatorias' AND column_name = 'titulo_jsonb'
    ) THEN
        ALTER TABLE convocatorias
            ADD COLUMN titulo_jsonb JSONB NOT NULL DEFAULT '{"es": ""}',
            ADD COLUMN descripcion_jsonb JSONB NOT NULL DEFAULT '{"es": ""}';
    END IF;

    -- Add JSONB columns to proyectos if they don't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'proyectos' AND column_name = 'titulo_jsonb'
    ) THEN
        ALTER TABLE proyectos
            ADD COLUMN titulo_jsonb JSONB NOT NULL DEFAULT '{"es": ""}',
            ADD COLUMN resumen_jsonb JSONB NOT NULL DEFAULT '{"es": ""}',
            ADD COLUMN objetivo_general_jsonb JSONB NOT NULL DEFAULT '{"es": ""}',
            ADD COLUMN lugar_ejecucion_jsonb JSONB NOT NULL DEFAULT '{"es": ""}';
    END IF;
END $$;

-- Migrate existing data from original columns to JSONB (safe for re-run)
UPDATE convocatorias SET
    titulo_jsonb = jsonb_build_object('es', titulo_convocatoria),
    descripcion_jsonb = jsonb_build_object('es', descripcion)
WHERE titulo_jsonb IS NULL OR titulo_jsonb = '{"es": ""}';

UPDATE proyectos SET
    titulo_jsonb = jsonb_build_object('es', titulo_proyecto),
    resumen_jsonb = jsonb_build_object('es', resumen),
    objetivo_general_jsonb = jsonb_build_object('es', objetivo_general),
    lugar_ejecucion_jsonb = jsonb_build_object('es', lugar_ejecucion)
WHERE titulo_jsonb IS NULL OR titulo_jsonb = '{"es": ""}';
