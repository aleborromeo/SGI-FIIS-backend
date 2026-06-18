-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE MIGRATION V7 - ADD CALL-RESEARCH LINES RELATIONSHIP (RN-11)
-- ============================================================================

-- Create join table for many-to-many relationship between convocatorias and lineas_investigacion
CREATE TABLE convocatorias_lineas (
    id_convocatoria INT NOT NULL REFERENCES convocatorias(id_convocatoria) ON DELETE CASCADE,
    id_linea INT NOT NULL REFERENCES lineas_investigacion(id_linea) ON DELETE CASCADE,
    PRIMARY KEY (id_convocatoria, id_linea)
);
