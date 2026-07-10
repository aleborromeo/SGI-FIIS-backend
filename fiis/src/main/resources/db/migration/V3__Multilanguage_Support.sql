-- ============================================================================
-- SGI-FIIS — Migración V3: Soporte multilingüe (JSONB)
-- Agrega columnas JSONB a convocatorias para soporte es/en
-- Los JSONB de proyectos ya se incluyeron en V1 por dependencia de JPA
-- ============================================================================

DO $$
BEGIN
    -- Convocatorias: agregar columnas JSONB si no existen
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'convocatorias' AND column_name = 'titulo_jsonb'
    ) THEN
        ALTER TABLE convocatorias
            ADD COLUMN titulo_jsonb JSONB NOT NULL DEFAULT '{"es": ""}',
            ADD COLUMN descripcion_jsonb JSONB NOT NULL DEFAULT '{"es": ""}';
    END IF;
END $$;

-- Migrar datos existentes de columnas originales a JSONB
UPDATE convocatorias SET
    titulo_jsonb = jsonb_build_object('es', titulo_convocatoria),
    descripcion_jsonb = jsonb_build_object('es', descripcion)
WHERE titulo_jsonb = '{"es": ""}';
