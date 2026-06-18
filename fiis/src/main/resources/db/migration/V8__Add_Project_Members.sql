-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE MIGRATION V8 - ADD PROJECT TEAM MEMBERS (RF-36)
-- ============================================================================

CREATE TABLE miembros_proyecto (
    id_miembro SERIAL PRIMARY KEY,
    id_proyecto INT NOT NULL REFERENCES proyectos(id_proyecto) ON DELETE CASCADE,
    id_usuario INT NOT NULL REFERENCES usuarios(id_usuario),
    rol VARCHAR(30) NOT NULL DEFAULT 'INVESTIGADOR',
    UNIQUE (id_proyecto, id_usuario)
);
