ALTER TABLE convocatorias
    ADD COLUMN poblacion_objetivo VARCHAR(30) DEFAULT 'AMBOS' NOT NULL;

ALTER TABLE convocatorias
    ADD CONSTRAINT chk_poblacion_objetivo
    CHECK (poblacion_objetivo IN ('DOCENTES', 'ESTUDIANTES', 'AMBOS'));
