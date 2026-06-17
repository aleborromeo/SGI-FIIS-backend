-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE SCHEMA INITIALIZATION (PostgreSQL Native Syntax)
-- ============================================================================

-- ============================================================================
-- 1. SECURITY & USERS
-- ============================================================================

CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    codigo_rol VARCHAR(30) NOT NULL UNIQUE, -- ADMIN, ESTUDIANTE, DOCENTE_INVESTIGADOR, etc.
    descripcion VARCHAR(100) NULL
);

CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    dni CHAR(8) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo_institucional VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20) NULL,
    password_hash VARCHAR(255) NOT NULL,
    must_change_password BOOLEAN DEFAULT TRUE NOT NULL,
    es_activo BOOLEAN DEFAULT TRUE NOT NULL,
    id_rol_principal INT NOT NULL REFERENCES roles(id_rol),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ============================================================================
-- 2. RESEARCH GROUPS & ACTIVE RESEARCH LINES
-- ===========================================
    id_plan_tesis INT NULL REFERENCES planes_tesis(id_plan_tesis),
    id_evaluador INT NOT NULL REFERENCES usuarios(id_usuario),
    resultado VARCHAR(30) NULL,
    puntaje INT NULL,
    observaciones TEXT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_evaluacion TIMESTAMP NULL,
    -- Exclusive Arc Constraint for Evaluations
    CONSTRAINT chk_evaluaciones_exclusividad CHECK (
        (id_proyecto IS NOT NULL AND id_plan_tesis IS NULL) OR
        (id_proyecto IS NULL AND id_plan_tesis IS NOT NULL)
    )
);

-- ============================================================================
-- 9. GENERAL AUDIT TABLE (auditoria_general)
-- ============================================================================

CREATE TABLE auditoria_general (
    id_auditoria SERIAL PRIMARY KEY,
    usuario VARCHAR(150) NOT NULL,
    accion VARCHAR(100) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado_anterior VARCHAR(100) NULL,
    estado_nuevo VARCHAR(100) NULL,
    ip_origen VARCHAR(50) NULL
);

-- ============================================================================
-- 10. INDEXES
-- ============================================================================
CREATE INDEX ix_usuarios_rol ON usuarios(id_rol_principal);
CREATE INDEX ix_proyectos_responsable ON proyectos(id_responsable);
CREATE INDEX ix_tramites_estado ON tramites(estado_actual, rol_revisor_actual);
CREATE INDEX ix_movimientos_tramite ON movimientos_tramite(id_tramite);

-- Ensure a user can only have at most one active membership (filtered index for Postgres)
CREATE UNIQUE INDEX uq_membresias_activas ON membresias_grupo(id_usuario) WHERE es_activo = TRUE;
