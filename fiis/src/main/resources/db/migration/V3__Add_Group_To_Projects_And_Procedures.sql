-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE MIGRATION V3 - ADD RESEARCH GROUP TO PROJECTS AND PROCEDURES
-- ============================================================================

-- 1. Alter proyectos table to add id_grupo column and foreign key reference
ALTER TABLE proyectos 
    ADD COLUMN id_grupo INT NOT NULL,
    ADD CONSTRAINT fk_proyectos_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo);

-- 2. Alter tramites table to add id_grupo and fecha_actualizacion columns
ALTER TABLE tramites 
    ADD COLUMN id_grupo INT NOT NULL,
    ADD COLUMN fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD CONSTRAINT fk_tramites_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo);
