-- V39: Agregar columna fecha_creacion a grupos_investigacion
-- Faltante en V1, requerida por ResearchGroupEntity (nullable=false, updatable=false)

ALTER TABLE grupos_investigacion
    ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT NOW() NOT NULL;
