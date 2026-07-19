-- Add missing fecha_creacion column to grupos_investigacion to match Java Entity mappings
ALTER TABLE grupos_investigacion ADD COLUMN fecha_creacion TIMESTAMP DEFAULT NOW() NOT NULL;
