-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE MIGRATION V6 - ADD DESCRIPTION, DOCUMENT AND UPDATED_AT TO CALLS
-- ============================================================================

-- 1. Add description column to convocatorias
ALTER TABLE convocatorias
    ADD COLUMN descripcion TEXT NOT NULL DEFAULT '';

-- 2. Add document reference column to convocatorias
ALTER TABLE convocatorias
    ADD COLUMN id_documento_bases INT NULL REFERENCES documentos(id_documento);

-- 3. Add updated_at column to convocatorias
ALTER TABLE convocatorias
    ADD COLUMN fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
