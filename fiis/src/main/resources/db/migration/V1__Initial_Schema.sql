-- ============================================================================
-- SGI-FIIS — Migración V1: Esquema completo consolidado
-- Fusiona: V1, V6, V7, V8, V10, V12, V13 del esquema anterior
-- Base de referencia: DB_FIIS_Investigacion_pgadmin.sql + correcciones JPA
-- Compatible: PostgreSQL 15+ / Flyway
-- ============================================================================

-- ============================================================================
-- MÓDULO 1: SEGURIDAD Y USUARIOS (RF-01 a RF-14, RN-01)
-- ============================================================================

CREATE TABLE roles (
    id_rol          SERIAL PRIMARY KEY,
    codigo_rol      VARCHAR(50) NOT NULL,
    descripcion     VARCHAR(255),
    CONSTRAINT uq_codigo_rol UNIQUE (codigo_rol)
);

CREATE TABLE usuarios (
    id_usuario              SERIAL PRIMARY KEY,
    dni                     VARCHAR(8) NOT NULL,
    nombres                 VARCHAR(100) NOT NULL,
    apellidos               VARCHAR(100) NOT NULL,
    correo_institucional    VARCHAR(150) NOT NULL,
    telefono                VARCHAR(20),
    password_hash           VARCHAR(255) NOT NULL,
    must_change_password    BOOLEAN DEFAULT TRUE NOT NULL,
    es_activo               BOOLEAN DEFAULT TRUE NOT NULL,
    id_rol_principal        INT NOT NULL,
    oauth_provider          VARCHAR(50),
    codigo_verificacion     VARCHAR(6),
    fecha_expiracion_codigo TIMESTAMP,
    fecha_creacion          TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion     TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_dni_usuario UNIQUE (dni),
    CONSTRAINT uq_correo_usuario UNIQUE (correo_institucional),
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (id_rol_principal) REFERENCES roles(id_rol)
);

-- Multi-rol por usuario (RF-09 ampliación)
CREATE TABLE usuarios_roles (
    id_usuario  INT NOT NULL,
    id_rol      INT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_ur_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- ============================================================================
-- MÓDULO 2: GRUPOS Y LÍNEAS DE INVESTIGACIÓN (RF-15 a RF-28, RN-02, RN-03)
-- ============================================================================

CREATE TABLE grupos_investigacion (
    id_grupo                SERIAL PRIMARY KEY,
    codigo_grupo            VARCHAR(50) NOT NULL,
    nombre_grupo            VARCHAR(150) NOT NULL,
    id_coordinador_actual   INT,
    es_activo               BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT uq_codigo_grupo UNIQUE (codigo_grupo),
    CONSTRAINT fk_grupos_coordinador FOREIGN KEY (id_coordinador_actual) REFERENCES usuarios(id_usuario)
);

CREATE TABLE membresias_grupo (
    id_membresia    SERIAL PRIMARY KEY,
    id_grupo        INT NOT NULL,
    id_usuario      INT NOT NULL,
    es_activo       BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_inicio    TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_fin       TIMESTAMP,
    CONSTRAINT fk_membresias_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_membresias_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- RF-21: máximo una membresía activa por usuario
CREATE UNIQUE INDEX uq_membresias_activas ON membresias_grupo(id_usuario) WHERE es_activo = TRUE;

CREATE TABLE lineas_investigacion (
    id_linea            SERIAL PRIMARY KEY,
    nombre_linea        VARCHAR(150) NOT NULL,
    es_activa           BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_nombre_linea UNIQUE (nombre_linea)
);

-- Intersección líneas permitidas por grupo (RF-26, RN-12)
CREATE TABLE lineas_por_grupo (
    id_grupo    INT NOT NULL,
    id_linea    INT NOT NULL,
    PRIMARY KEY (id_grupo, id_linea),
    CONSTRAINT fk_lpg_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_lpg_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea)
);

-- ============================================================================
-- MÓDULO 3: DOCUMENTOS (RF-65 a RF-69, RNF-07, RNF-08, RN-10)
-- ============================================================================

CREATE TABLE documentos (
    id_documento        SERIAL PRIMARY KEY,
    nombre_original     VARCHAR(255) NOT NULL,
    ruta_almacenamiento VARCHAR(500) NOT NULL,
    tipo_extension      VARCHAR(10) NOT NULL,
    tamano_bytes        BIGINT NOT NULL,
    id_usuario_subio    INT NOT NULL,
    fecha_carga         TIMESTAMP DEFAULT NOW() NOT NULL,
    es_activo           BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT chk_extension_documento CHECK (tipo_extension IN ('PDF', 'DOC', 'DOCX')),
    CONSTRAINT fk_documentos_usuario FOREIGN KEY (id_usuario_subio) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- MÓDULO 4: CONVOCATORIAS Y PROYECTOS (RF-29 a RF-44, RNF-37)
-- ============================================================================

CREATE TABLE convocatorias (
    id_convocatoria     SERIAL PRIMARY KEY,
    titulo_convocatoria VARCHAR(150) NOT NULL,
    descripcion         TEXT NOT NULL DEFAULT '',
    fecha_inicio        DATE NOT NULL,
    fecha_fin           DATE NOT NULL,
    estado              VARCHAR(20) DEFAULT 'ABIERTA' NOT NULL,
    id_documento_bases  INT,
    id_creador          INT NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_estado_convocatoria CHECK (estado IN ('ABIERTA', 'CERRADA', 'FINALIZADA')),
    CONSTRAINT chk_fechas_convocatoria CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT fk_convocatorias_documento FOREIGN KEY (id_documento_bases) REFERENCES documentos(id_documento),
    CONSTRAINT fk_convocatorias_creador FOREIGN KEY (id_creador) REFERENCES usuarios(id_usuario)
);

-- Join table convocatorias <-> líneas (ManyToMany)
CREATE TABLE convocatorias_lineas (
    id_convocatoria INT NOT NULL,
    id_linea        INT NOT NULL,
    PRIMARY KEY (id_convocatoria, id_linea),
    CONSTRAINT fk_conv_lineas_conv FOREIGN KEY (id_convocatoria) REFERENCES convocatorias(id_convocatoria) ON DELETE CASCADE,
    CONSTRAINT fk_conv_lineas_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea) ON DELETE CASCADE
);

CREATE TABLE proyectos (
    id_proyecto             SERIAL PRIMARY KEY,
    codigo_proyecto         VARCHAR(30) NOT NULL,
    titulo_proyecto         VARCHAR(500) NOT NULL,
    resumen                 TEXT NOT NULL,
    objetivo_general        TEXT NOT NULL,
    titulo_jsonb            JSONB NOT NULL DEFAULT '{"es": ""}',
    resumen_jsonb           JSONB NOT NULL DEFAULT '{"es": ""}',
    objetivo_general_jsonb  JSONB NOT NULL DEFAULT '{"es": ""}',
    lugar_ejecucion_jsonb   JSONB NOT NULL DEFAULT '{"es": ""}',
    id_linea                INT NOT NULL,
    id_grupo                INT NOT NULL,
    presupuesto             DECIMAL(18,2) NOT NULL,
    fecha_inicio            DATE NOT NULL,
    fecha_fin               DATE NOT NULL,
    lugar_ejecucion         VARCHAR(255) NOT NULL,
    id_responsable          INT NOT NULL,
    id_convocatoria         INT,
    id_documento_propuesta  INT,
    estado                  VARCHAR(50) DEFAULT 'POSTULADO' NOT NULL,
    fecha_creacion          TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion     TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_codigo_proyecto UNIQUE (codigo_proyecto),
    CONSTRAINT chk_estado_proyecto CHECK (estado IN ('POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO', 'EN_EJECUCION', 'FINALIZADO')),
    CONSTRAINT fk_proyectos_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_proyectos_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_proyectos_responsable FOREIGN KEY (id_responsable) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_proyectos_convocatoria FOREIGN KEY (id_convocatoria) REFERENCES convocatorias(id_convocatoria),
    CONSTRAINT fk_proyectos_documento FOREIGN KEY (id_documento_propuesta) REFERENCES documentos(id_documento)
);

CREATE TABLE miembros_proyecto (
    id_miembro      SERIAL PRIMARY KEY,
    id_proyecto     INT NOT NULL,
    id_usuario      INT NOT NULL,
    rol             VARCHAR(30) DEFAULT 'INVESTIGADOR' NOT NULL,
    CONSTRAINT uq_miembro_proyecto UNIQUE (id_proyecto, id_usuario),
    CONSTRAINT fk_miembros_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto) ON DELETE CASCADE,
    CONSTRAINT fk_miembros_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- MÓDULO 5: PLANES DE TESIS (RF-45 a RF-53, RN-06)
-- ============================================================================

CREATE TABLE planes_tesis (
    id_plan_tesis       SERIAL PRIMARY KEY,
    titulo_tesis        VARCHAR(500) NOT NULL,
    resumen             TEXT,
    id_estudiante       INT NOT NULL,
    id_linea            INT NOT NULL,
    id_grupo            INT NOT NULL,
    id_documento_actual INT,
    estado_plan         VARCHAR(30) DEFAULT 'POSTULADO' NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_estado_plan CHECK (estado_plan IN ('POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO')),
    CONSTRAINT fk_planes_estudiante FOREIGN KEY (id_estudiante) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_planes_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_planes_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_planes_documento FOREIGN KEY (id_documento_actual) REFERENCES documentos(id_documento)
);

CREATE TABLE informes_tesis (
    id_informe_tesis    SERIAL PRIMARY KEY,
    id_plan_tesis       INT NOT NULL,
    titulo_final        VARCHAR(500) NOT NULL,
    id_documento_tesis  INT NOT NULL,
    fecha_presentacion  TIMESTAMP DEFAULT NOW() NOT NULL,
    estado_informe      VARCHAR(30) DEFAULT 'EN_REVISION' NOT NULL,
    CONSTRAINT chk_estado_informe_tesis CHECK (estado_informe IN ('EN_REVISION', 'APROBADO', 'OBSERVADO')),
    CONSTRAINT fk_informes_tesis_plan FOREIGN KEY (id_plan_tesis) REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_informes_tesis_doc FOREIGN KEY (id_documento_tesis) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 6: INFORMES DE AVANCE DE PROYECTOS (RF-70 a RF-77)
-- ============================================================================

CREATE TABLE informes_avance (
    id_informe          SERIAL PRIMARY KEY,
    id_proyecto         INT NOT NULL,
    tipo_informe        VARCHAR(50) NOT NULL,
    periodo             VARCHAR(50) NOT NULL,
    porcentaje_avance   DECIMAL(5,2) NOT NULL,
    logros              TEXT NOT NULL,
    dificultades        TEXT NOT NULL,
    recomendaciones     TEXT NOT NULL,
    id_documento_adjunto INT,
    estado_informe      VARCHAR(30) DEFAULT 'PENDIENTE' NOT NULL,
    fecha_registro      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_tipo_informe CHECK (tipo_informe IN ('PARCIAL', 'FINAL')),
    CONSTRAINT chk_estado_informe_avance CHECK (estado_informe IN ('PENDIENTE', 'EN_REVISION', 'APROBADO', 'OBSERVADO', 'RECHAZADO')),
    CONSTRAINT chk_porcentaje CHECK (porcentaje_avance >= 0 AND porcentaje_avance <= 100),
    CONSTRAINT fk_informes_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_informes_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 7: WORKFLOW, TRÁMITES Y AUDITORÍA (RF-54 a RF-64, RF-96 a RF-100)
-- ============================================================================

CREATE TABLE tramites (
    id_tramite              SERIAL PRIMARY KEY,
    codigo_tramite          VARCHAR(30) NOT NULL,
    tipo_tramite            VARCHAR(50) NOT NULL,
    id_solicitante          INT NOT NULL,
    id_grupo                INT,
    estado_actual           VARCHAR(50) NOT NULL,
    rol_revisor_actual      VARCHAR(50),
    fecha_envio             TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion     TIMESTAMP DEFAULT NOW() NOT NULL,
    id_referencia_proyecto  INT,
    id_referencia_tesis     INT,
    id_referencia_informe   INT,
    CONSTRAINT uq_codigo_tramite UNIQUE (codigo_tramite),
    CONSTRAINT chk_tipo_tramite CHECK (tipo_tramite IN ('PROYECTO', 'PLAN_TESIS', 'INFORME_AVANCE')),
    CONSTRAINT fk_tramites_solicitante FOREIGN KEY (id_solicitante) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_tramites_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_tramites_proyecto FOREIGN KEY (id_referencia_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_tramites_tesis FOREIGN KEY (id_referencia_tesis) REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_tramites_informe FOREIGN KEY (id_referencia_informe) REFERENCES informes_avance(id_informe),
    -- Arco excluyente 3NF
    CONSTRAINT chk_tramites_exclusividad CHECK (
        (id_referencia_proyecto IS NOT NULL AND id_referencia_tesis IS NULL AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL AND id_referencia_tesis IS NOT NULL AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL AND id_referencia_tesis IS NULL AND id_referencia_informe IS NOT NULL)
    )
);

CREATE TABLE movimientos_tramite (
    id_movimiento       SERIAL PRIMARY KEY,
    id_tramite          INT NOT NULL,
    id_usuario_accion   INT NOT NULL,
    accion              VARCHAR(50) NOT NULL,
    estado_anterior     VARCHAR(50) NOT NULL,
    estado_nuevo        VARCHAR(50) NOT NULL,
    observacion         TEXT,
    id_documento_adjunto INT,
    fecha_movimiento    TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_movimientos_tramite FOREIGN KEY (id_tramite) REFERENCES tramites(id_tramite),
    CONSTRAINT fk_movimientos_usuario FOREIGN KEY (id_usuario_accion) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_movimientos_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 8: OBSERVACIONES Y SUBSANACIONES (RF-60 a RF-64, RF-69, RF-76)
-- ============================================================================

CREATE TABLE observaciones (
    id_observacion      SERIAL PRIMARY KEY,
    id_tramite          INT NOT NULL,
    id_revisor          INT NOT NULL,
    tipo_observacion    VARCHAR(30) NOT NULL,
    descripcion         TEXT NOT NULL,
    estado_observacion  VARCHAR(20) DEFAULT 'PENDIENTE' NOT NULL,
    rol_revisor         VARCHAR(50) NOT NULL,
    fecha_registro      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_observaciones_tramite FOREIGN KEY (id_tramite) REFERENCES tramites(id_tramite) ON DELETE RESTRICT,
    CONSTRAINT fk_observaciones_revisor FOREIGN KEY (id_revisor) REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    CONSTRAINT chk_tipo_observacion CHECK (tipo_observacion IN ('TECNICA', 'DOCUMENTAL', 'PRESUPUESTAL', 'FORMATO')),
    CONSTRAINT chk_estado_observacion CHECK (estado_observacion IN ('PENDIENTE', 'SUBSANADA', 'VIGENTE'))
);

CREATE TABLE subsanaciones (
    id_subsanacion       SERIAL PRIMARY KEY,
    id_observacion       INT NOT NULL,
    id_solicitante       INT NOT NULL,
    descripcion          TEXT NOT NULL,
    id_documento_adjunto INT,
    fecha_registro       TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion  TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_subsanaciones_observacion FOREIGN KEY (id_observacion) REFERENCES observaciones(id_observacion) ON DELETE RESTRICT,
    CONSTRAINT fk_subsanaciones_solicitante FOREIGN KEY (id_solicitante) REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    CONSTRAINT fk_subsanaciones_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento) ON DELETE RESTRICT
);

-- ============================================================================
-- MÓDULO 9: RESOLUCIONES Y EVALUACIONES (RF-78 a RF-87)
-- ============================================================================

CREATE TABLE resoluciones (
    id_resolucion       SERIAL PRIMARY KEY,
    numero_resolucion   VARCHAR(100) NOT NULL,
    fecha_emision       DATE NOT NULL,
    asunto              VARCHAR(500) NOT NULL,
    id_tramite          INT NOT NULL,
    id_documento_adjunto INT NOT NULL,
    fecha_registro      TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_numero_resolucion UNIQUE (numero_resolucion),
    CONSTRAINT fk_resoluciones_tramite FOREIGN KEY (id_tramite) REFERENCES tramites(id_tramite),
    CONSTRAINT fk_resoluciones_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

CREATE TABLE evaluaciones (
    id_evaluacion       SERIAL PRIMARY KEY,
    id_proyecto         INT,
    id_plan_tesis       INT,
    id_evaluador        INT NOT NULL,
    resultado           VARCHAR(30),
    puntaje             INT,
    observaciones       TEXT,
    fecha_asignacion    TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_evaluacion    TIMESTAMP,
    CONSTRAINT chk_resultado_evaluacion CHECK (resultado IN ('APROBADO', 'RECHAZADO', 'CON_OBSERVACIONES')),
    CONSTRAINT fk_evaluaciones_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_evaluaciones_tesis FOREIGN KEY (id_plan_tesis) REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_evaluaciones_evaluador FOREIGN KEY (id_evaluador) REFERENCES usuarios(id_usuario),
    CONSTRAINT chk_evaluaciones_exclusividad CHECK (
        (id_proyecto IS NOT NULL AND id_plan_tesis IS NULL) OR
        (id_proyecto IS NULL AND id_plan_tesis IS NOT NULL)
    )
);

-- ============================================================================
-- MÓDULO 10: AUDITORÍA GENERAL (RF-100, RNF-09)
-- ============================================================================

CREATE TABLE auditoria_general (
    id_auditoria        SERIAL PRIMARY KEY,
    tabla_afectada      VARCHAR(100) NOT NULL,
    id_registro         INT NOT NULL,
    accion              VARCHAR(30) NOT NULL,
    id_usuario          INT NOT NULL,
    datos_anteriores    TEXT,
    datos_nuevos        TEXT,
    ip_origen           VARCHAR(45),
    fecha_accion        TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_accion_auditoria CHECK (accion IN ('CREAR', 'EDITAR', 'ELIMINAR', 'ACTIVAR', 'DESACTIVAR', 'LOGIN', 'LOGOUT')),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- ÍNDICES ESTRATÉGICOS PARA RENDIMIENTO (RNF-17, RNF-19)
-- ============================================================================

-- Usuarios
CREATE INDEX ix_usuarios_rol ON usuarios(id_rol_principal);
CREATE INDEX ix_usuarios_activo ON usuarios(es_activo);
CREATE INDEX ix_usuarios_roles_rol ON usuarios_roles(id_rol);

-- Proyectos
CREATE INDEX ix_proyectos_responsable ON proyectos(id_responsable);
CREATE INDEX ix_proyectos_grupo ON proyectos(id_grupo);
CREATE INDEX ix_proyectos_estado ON proyectos(estado);

-- Planes de tesis
CREATE INDEX ix_planes_estudiante ON planes_tesis(id_estudiante);
CREATE INDEX ix_planes_grupo ON planes_tesis(id_grupo);
CREATE INDEX ix_planes_estado ON planes_tesis(estado_plan);

-- Informes de tesis
CREATE INDEX ix_informes_tesis_plan ON informes_tesis(id_plan_tesis);
CREATE INDEX ix_informes_tesis_estado ON informes_tesis(estado_informe);

-- Trámites
CREATE INDEX ix_tramites_estado ON tramites(estado_actual, rol_revisor_actual);
CREATE INDEX ix_tramites_grupo ON tramites(id_grupo);
CREATE INDEX ix_tramites_solicitante ON tramites(id_solicitante);

-- Movimientos
CREATE INDEX ix_movimientos_tramite ON movimientos_tramite(id_tramite);
CREATE INDEX ix_movimientos_fecha ON movimientos_tramite(fecha_movimiento);

-- Informes de avance
CREATE INDEX ix_informes_proyecto ON informes_avance(id_proyecto);

-- Evaluaciones
CREATE INDEX ix_evaluaciones_evaluador ON evaluaciones(id_evaluador);

-- Observaciones (RNF-17: rendimiento en bandejas)
CREATE INDEX ix_observaciones_tramite ON observaciones(id_tramite);
CREATE INDEX ix_observaciones_estado ON observaciones(estado_observacion);
CREATE INDEX ix_observaciones_tramite_estado ON observaciones(id_tramite, estado_observacion);
CREATE INDEX ix_subsanaciones_observacion ON subsanaciones(id_observacion);

-- Auditoría
CREATE INDEX ix_auditoria_tabla ON auditoria_general(tabla_afectada, id_registro);
CREATE INDEX ix_auditoria_usuario ON auditoria_general(id_usuario);
CREATE INDEX ix_auditoria_fecha ON auditoria_general(fecha_accion);
