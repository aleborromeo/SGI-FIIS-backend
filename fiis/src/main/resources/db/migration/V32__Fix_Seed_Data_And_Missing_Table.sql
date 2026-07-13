-- ============================================================================
-- V32: Corregir datos semilla y crear tabla faltante
-- ============================================================================

-- 1. Corregir estado_actual de tramites semilla: EN_REVISION -> REGISTRADO
--    EN_REVISION no existe en el enum ProcedureStatus
UPDATE tramites SET estado_actual = 'REGISTRADO'
WHERE estado_actual = 'EN_REVISION';

-- 2. Crear tabla miembros_proyecto si no existe
--    La tabla fue definida en V1 pero puede no existir en DBs migradas desde V4-V13
CREATE TABLE IF NOT EXISTS miembros_proyecto (
    id_miembro      SERIAL PRIMARY KEY,
    id_proyecto     INT NOT NULL,
    id_usuario      INT NOT NULL,
    rol             VARCHAR(30) DEFAULT 'INVESTIGADOR' NOT NULL,
    CONSTRAINT uq_miembro_proyecto_v32 UNIQUE (id_proyecto, id_usuario),
    CONSTRAINT fk_miembros_proyecto_v32 FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto) ON DELETE CASCADE,
    CONSTRAINT fk_miembros_usuario_v32 FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);
