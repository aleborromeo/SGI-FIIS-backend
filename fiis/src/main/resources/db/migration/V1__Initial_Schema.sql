-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE MIGRATION V1 - COMPLETE INITIAL SCHEMA
-- ============================================================================

-- 1. SECURITY & USERS
CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    codigo_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    dni VARCHAR(8) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo_institucional VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    es_activo BOOLEAN NOT NULL DEFAULT TRUE,
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    id_rol_principal INT NOT NULL REFERENCES roles(id_rol),
    oauth_provider VARCHAR(50),
    codigo_verificacion VARCHAR(6),
    fecha_expiracion_codigo TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. RESEARCH GROUPS
CREATE TABLE grupos_investigacion (
    id_grupo SERIAL PRIMARY KEY,
    codigo_grupo VARCHAR(50) NOT NULL UNIQUE,
    nombre_grupo VARCHAR(150) NOT NULL,
    id_coordinador_actual INT REFERENCES usuarios(id_usuario),
    es_activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. GROUP MEMBERSHIPS
CREATE TABLE membresias_grupo (
    id_membresia SERIAL PRIMARY KEY,
    id_grupo INT NOT NULL REFERENCES grupos_investigacion(id_grupo),
    id_usuario INT NOT NULL REFERENCES usuarios(id_usuario),
    es_activo BOOLEAN NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP
);

-- 4. RESEARCH LINES
CREATE TABLE lineas_investigacion (
    id_linea SERIAL PRIMARY KEY,
    nombre_linea VARCHAR(150) NOT NULL UNIQUE,
    es_activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. DOCUMENTS
CREATE TABLE documentos (
    id_documento SERIAL PRIMARY KEY,
    nombre_original VARCHAR(255) NOT NULL,
    ruta_almacenamiento VARCHAR(500) NOT NULL,
    tamano_bytes BIGINT NOT NULL,
    extension VARCHAR(10) NOT NULL,
    id_usuario_creador BIGINT NOT NULL REFERENCES usuarios(id_usuario),
    fecha_creacion TIMESTAMP NOT NULL
);

-- 6. RESEARCH CALLS (CONVOCATORIAS)
CREATE TABLE convocatorias (
    id_convocatoria SERIAL PRIMARY KEY,
    titulo_convocatoria VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL DEFAULT '',
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA',
    id_documento_bases INT REFERENCES documentos(id_documento),
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 7. CALL-RESEARCH LINES JOIN TABLE
CREATE TABLE convocatorias_lineas (
    id_convocatoria INT NOT NULL REFERENCES convocatorias(id_convocatoria) ON DELETE CASCADE,
    id_linea INT NOT NULL REFERENCES lineas_investigacion(id_linea) ON DELETE CASCADE,
    PRIMARY KEY (id_convocatoria, id_linea)
);

-- 8. PROJECTS
CREATE TABLE proyectos (
    id_proyecto SERIAL PRIMARY KEY,
    codigo_proyecto VARCHAR(30) NOT NULL UNIQUE,
    titulo_proyecto VARCHAR(500) NOT NULL,
    resumen TEXT NOT NULL,
    objetivo_general TEXT NOT NULL,
    id_linea INT NOT NULL REFERENCES lineas_investigacion(id_linea),
    id_grupo INT NOT NULL REFERENCES grupos_investigacion(id_grupo),
    presupuesto DECIMAL(12,2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    lugar_ejecucion VARCHAR(255) NOT NULL,
    id_responsable INT NOT NULL REFERENCES usuarios(id_usuario),
    id_convocatoria INT REFERENCES convocatorias(id_convocatoria),
    id_documento_propuesta INT REFERENCES documentos(id_documento),
    estado VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL
);

-- 9. PROJECT MEMBERS
CREATE TABLE miembros_proyecto (
    id_miembro SERIAL PRIMARY KEY,
    id_proyecto INT NOT NULL REFERENCES proyectos(id_proyecto) ON DELETE CASCADE,
    id_usuario INT NOT NULL REFERENCES usuarios(id_usuario),
    rol VARCHAR(30) NOT NULL DEFAULT 'INVESTIGADOR',
    UNIQUE (id_proyecto, id_usuario)
);

-- 10. PROCEDURES (TRAMITES)
CREATE TABLE tramites (
    id_tramite SERIAL PRIMARY KEY,
    codigo_tramite VARCHAR(30) NOT NULL UNIQUE,
    tipo_tramite VARCHAR(30) NOT NULL,
    id_solicitante INT NOT NULL REFERENCES usuarios(id_usuario),
    id_grupo INT NOT NULL REFERENCES grupos_investigacion(id_grupo),
    estado_actual VARCHAR(30) NOT NULL,
    rol_revisor_actual VARCHAR(30) NOT NULL,
    fecha_envio TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_referencia_proyecto INT REFERENCES proyectos(id_proyecto)
);

-- 11. PROCEDURE MOVEMENTS
CREATE TABLE movimientos_tramite (
    id_movimiento SERIAL PRIMARY KEY,
    id_tramite INT NOT NULL REFERENCES tramites(id_tramite),
    id_usuario_accion INT NOT NULL REFERENCES usuarios(id_usuario),
    accion VARCHAR(30) NOT NULL,
    estado_anterior VARCHAR(30) NOT NULL,
    estado_nuevo VARCHAR(30) NOT NULL,
    comentario TEXT,
    fecha_movimiento TIMESTAMP NOT NULL
);

-- 12. GENERAL AUDIT TABLE
CREATE TABLE auditoria_general (
    id_auditoria SERIAL PRIMARY KEY,
    usuario VARCHAR(150) NOT NULL,
    accion VARCHAR(100) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado_anterior VARCHAR(100),
    estado_nuevo VARCHAR(100),
    ip_origen VARCHAR(50)
);

-- 13. INDEXES
CREATE INDEX ix_usuarios_rol ON usuarios(id_rol_principal);
CREATE INDEX idx_usuarios_oauth_provider ON usuarios(oauth_provider);
CREATE INDEX ix_proyectos_responsable ON proyectos(id_responsable);
CREATE INDEX ix_tramites_estado ON tramites(estado_actual, rol_revisor_actual);
CREATE INDEX ix_movimientos_tramite ON movimientos_tramite(id_tramite);
CREATE UNIQUE INDEX uq_membresias_activas ON membresias_grupo(id_usuario) WHERE es_activo = TRUE;
