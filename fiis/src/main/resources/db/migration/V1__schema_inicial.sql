-- ============================================================================
-- V1__schema_inicial.sql
-- SISTEMA DE GESTIÓN DE INVESTIGACIÓN FIIS
-- Flyway Migration — Ejecutado automáticamente al arrancar Spring Boot
-- PostgreSQL | Normalización 3NF | 19 tablas
-- ============================================================================

-- ============================================================================
-- MÓDULO 1: SEGURIDAD Y USUARIOS (RF-01 a RF-14, RN-01)
-- ============================================================================

CREATE TABLE IF NOT EXISTS roles (
    id_rol          SERIAL PRIMARY KEY,
    codigo_rol      VARCHAR(30) NOT NULL,
    descripcion     VARCHAR(100),
    CONSTRAINT uq_codigo_rol UNIQUE (codigo_rol)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario           SERIAL PRIMARY KEY,
    dni                  CHAR(8) NOT NULL,
    nombres              VARCHAR(100) NOT NULL,
    apellidos            VARCHAR(100) NOT NULL,
    correo_institucional VARCHAR(150) NOT NULL,
    telefono             VARCHAR(20),
    password_hash        VARCHAR(255) NOT NULL,
    must_change_password BOOLEAN DEFAULT TRUE NOT NULL,
    es_activo            BOOLEAN DEFAULT TRUE NOT NULL,
    id_rol_principal     INT NOT NULL,
    ultimo_acceso        TIMESTAMP,
    fecha_creacion       TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion  TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_dni_usuario UNIQUE (dni),
    CONSTRAINT uq_correo_usuario UNIQUE (correo_institucional),
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (id_rol_principal) REFERENCES roles(id_rol)
);

CREATE TABLE IF NOT EXISTS usuarios_roles (
    id_usuario INT NOT NULL,
    id_rol     INT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_ur_rol     FOREIGN KEY (id_rol)     REFERENCES roles(id_rol)
);

-- ============================================================================
-- MÓDULO 2: GRUPOS Y LÍNEAS DE INVESTIGACIÓN (RF-15 a RF-28)
-- ============================================================================

CREATE TABLE IF NOT EXISTS grupos_investigacion (
    id_grupo              SERIAL PRIMARY KEY,
    codigo_grupo          VARCHAR(20) NOT NULL,
    nombre_grupo          VARCHAR(150) NOT NULL,
    id_coordinador_actual INT,
    es_activo             BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT uq_codigo_grupo UNIQUE (codigo_grupo),
    CONSTRAINT fk_grupos_coordinador FOREIGN KEY (id_coordinador_actual) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS membresias_grupo (
    id_membresia SERIAL PRIMARY KEY,
    id_grupo     INT NOT NULL,
    id_usuario   INT NOT NULL,
    es_activo    BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_inicio TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_fin    TIMESTAMP,
    CONSTRAINT fk_membresias_grupo   FOREIGN KEY (id_grupo)   REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_membresias_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Restricción: máximo una membresía activa por usuario (RF-21)
CREATE UNIQUE INDEX IF NOT EXISTS uq_membresias_activas ON membresias_grupo(id_usuario) WHERE es_activo = TRUE;

CREATE TABLE IF NOT EXISTS lineas_investigacion (
    id_linea            SERIAL PRIMARY KEY,
    nombre_linea        VARCHAR(150) NOT NULL,
    es_activa           BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_nombre_linea UNIQUE (nombre_linea)
);

CREATE TABLE IF NOT EXISTS lineas_por_grupo (
    id_grupo INT NOT NULL,
    id_linea INT NOT NULL,
    PRIMARY KEY (id_grupo, id_linea),
    CONSTRAINT fk_lpg_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_lpg_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea)
);

-- ============================================================================
-- MÓDULO 3: DOCUMENTOS BASE (RF-65, RF-66)
-- ============================================================================

CREATE TABLE IF NOT EXISTS documentos (
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
-- MÓDULO 4: CONVOCATORIAS Y PROYECTOS (RF-29 a RF-44)
-- ============================================================================

CREATE TABLE IF NOT EXISTS convocatorias (
    id_convocatoria     SERIAL PRIMARY KEY,
    titulo_convocatoria VARCHAR(150) NOT NULL,
    descripcion         TEXT,
    fecha_inicio        DATE NOT NULL,
    fecha_fin           DATE NOT NULL,
    estado              VARCHAR(20) DEFAULT 'ABIERTA' NOT NULL,
    id_creador          INT NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_estado_convocatoria CHECK (estado IN ('ABIERTA', 'CERRADA', 'FINALIZADA')),
    CONSTRAINT chk_fechas_convocatoria CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT fk_convocatorias_creador FOREIGN KEY (id_creador) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS proyectos (
    id_proyecto         SERIAL PRIMARY KEY,
    codigo_proyecto     VARCHAR(30) NOT NULL,
    titulo_proyecto     VARCHAR(500) NOT NULL,
    resumen             TEXT NOT NULL,
    objetivo_general    TEXT NOT NULL,
    id_linea            INT NOT NULL,
    id_grupo            INT NOT NULL,
    presupuesto         DECIMAL(18,2) NOT NULL,
    fecha_inicio        DATE NOT NULL,
    fecha_fin           DATE NOT NULL,
    lugar_ejecucion     VARCHAR(255) NOT NULL,
    id_responsable      INT NOT NULL,
    id_convocatoria     INT,
    id_documento_actual INT,
    estado_proyecto     VARCHAR(30) DEFAULT 'POSTULADO' NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_codigo_proyecto UNIQUE (codigo_proyecto),
    CONSTRAINT chk_estado_proyecto CHECK (estado_proyecto IN ('POSTULADO','OBSERVADO','APROBADO','RECHAZADO','EN_EJECUCION','FINALIZADO')),
    CONSTRAINT fk_proyectos_linea        FOREIGN KEY (id_linea)            REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_proyectos_grupo        FOREIGN KEY (id_grupo)            REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_proyectos_responsable  FOREIGN KEY (id_responsable)      REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_proyectos_convocatoria FOREIGN KEY (id_convocatoria)     REFERENCES convocatorias(id_convocatoria),
    CONSTRAINT fk_proyectos_documento    FOREIGN KEY (id_documento_actual)  REFERENCES documentos(id_documento)
);

CREATE TABLE IF NOT EXISTS integrantes_proyecto (
    id_proyecto       INT NOT NULL,
    id_usuario        INT NOT NULL,
    rol_en_proyecto   VARCHAR(50) NOT NULL,
    fecha_integracion TIMESTAMP DEFAULT NOW() NOT NULL,
    PRIMARY KEY (id_proyecto, id_usuario),
    CONSTRAINT fk_integrantes_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_integrantes_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- MÓDULO 5: PLANES DE TESIS (RF-45 a RF-53)
-- ============================================================================

CREATE TABLE IF NOT EXISTS planes_tesis (
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
    CONSTRAINT chk_estado_plan CHECK (estado_plan IN ('POSTULADO','OBSERVADO','APROBADO','RECHAZADO')),
    CONSTRAINT fk_planes_estudiante FOREIGN KEY (id_estudiante)       REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_planes_linea      FOREIGN KEY (id_linea)            REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_planes_grupo      FOREIGN KEY (id_grupo)            REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_planes_documento  FOREIGN KEY (id_documento_actual) REFERENCES documentos(id_documento)
);

CREATE TABLE IF NOT EXISTS informes_tesis (
    id_informe_tesis   SERIAL PRIMARY KEY,
    id_plan_tesis      INT NOT NULL,
    titulo_final       VARCHAR(500) NOT NULL,
    id_documento_tesis INT NOT NULL,
    fecha_presentacion TIMESTAMP DEFAULT NOW() NOT NULL,
    estado_informe     VARCHAR(30) DEFAULT 'EN_REVISION' NOT NULL,
    CONSTRAINT chk_estado_informe_tesis CHECK (estado_informe IN ('EN_REVISION','APROBADO','OBSERVADO')),
    CONSTRAINT fk_informes_tesis_plan FOREIGN KEY (id_plan_tesis)      REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_informes_tesis_doc  FOREIGN KEY (id_documento_tesis) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 6: INFORMES DE AVANCE (RF-70 a RF-77)
-- ============================================================================

CREATE TABLE IF NOT EXISTS informes_avance (
    id_informe           SERIAL PRIMARY KEY,
    id_proyecto          INT NOT NULL,
    tipo_informe         VARCHAR(50) NOT NULL,
    periodo              VARCHAR(50) NOT NULL,
    porcentaje_avance    DECIMAL(5,2) NOT NULL,
    logros               TEXT NOT NULL,
    dificultades         TEXT NOT NULL,
    recomendaciones      TEXT NOT NULL,
    id_documento_adjunto INT,
    estado_informe       VARCHAR(30) DEFAULT 'PENDIENTE' NOT NULL,
    fecha_registro       TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion  TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_tipo_informe         CHECK (tipo_informe IN ('PARCIAL','FINAL')),
    CONSTRAINT chk_estado_informe_avance CHECK (estado_informe IN ('PENDIENTE','EN_REVISION','APROBADO','OBSERVADO','RECHAZADO')),
    CONSTRAINT chk_porcentaje           CHECK (porcentaje_avance >= 0 AND porcentaje_avance <= 100),
    CONSTRAINT fk_informes_proyecto  FOREIGN KEY (id_proyecto)          REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_informes_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 7: WORKFLOW, TRÁMITES Y AUDITORÍA (RF-54 a RF-64)
-- ============================================================================

CREATE TABLE IF NOT EXISTS tramites (
    id_tramite             SERIAL PRIMARY KEY,
    codigo_tramite         VARCHAR(30) NOT NULL,
    tipo_tramite           VARCHAR(50) NOT NULL,
    id_solicitante         INT NOT NULL,
    id_grupo               INT NOT NULL,
    estado_actual          VARCHAR(50) NOT NULL,
    rol_revisor_actual     VARCHAR(50) NOT NULL,
    fecha_envio            TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion    TIMESTAMP DEFAULT NOW() NOT NULL,
    id_referencia_proyecto INT,
    id_referencia_tesis    INT,
    id_referencia_informe  INT,
    CONSTRAINT uq_codigo_tramite UNIQUE (codigo_tramite),
    CONSTRAINT chk_tipo_tramite  CHECK (tipo_tramite IN ('PROYECTO','PLAN_TESIS','INFORME_AVANCE')),
    CONSTRAINT fk_tramites_solicitante FOREIGN KEY (id_solicitante)        REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_tramites_grupo       FOREIGN KEY (id_grupo)              REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_tramites_proyecto    FOREIGN KEY (id_referencia_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_tramites_tesis       FOREIGN KEY (id_referencia_tesis)    REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_tramites_informe     FOREIGN KEY (id_referencia_informe)  REFERENCES informes_avance(id_informe),
    CONSTRAINT chk_tramites_exclusividad CHECK (
        (id_referencia_proyecto IS NOT NULL AND id_referencia_tesis IS NULL    AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL    AND id_referencia_tesis IS NOT NULL AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL    AND id_referencia_tesis IS NULL    AND id_referencia_informe IS NOT NULL)
    )
);

CREATE TABLE IF NOT EXISTS movimientos_tramite (
    id_movimiento        SERIAL PRIMARY KEY,
    id_tramite           INT NOT NULL,
    id_usuario_accion    INT NOT NULL,
    accion               VARCHAR(50) NOT NULL,
    estado_anterior      VARCHAR(50) NOT NULL,
    estado_nuevo         VARCHAR(50) NOT NULL,
    observacion          TEXT,
    id_documento_adjunto INT,
    fecha_movimiento     TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_movimientos_tramite   FOREIGN KEY (id_tramite)           REFERENCES tramites(id_tramite),
    CONSTRAINT fk_movimientos_usuario   FOREIGN KEY (id_usuario_accion)    REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_movimientos_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 8: RESOLUCIONES Y EVALUACIONES (RF-78 a RF-87)
-- ============================================================================

CREATE TABLE IF NOT EXISTS resoluciones (
    id_resolucion        SERIAL PRIMARY KEY,
    numero_resolucion    VARCHAR(100) NOT NULL,
    fecha_emision        DATE NOT NULL,
    asunto               VARCHAR(500) NOT NULL,
    id_tramite           INT NOT NULL,
    id_documento_adjunto INT NOT NULL,
    fecha_registro       TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_numero_resolucion       UNIQUE (numero_resolucion),
    CONSTRAINT fk_resoluciones_tramite    FOREIGN KEY (id_tramite)           REFERENCES tramites(id_tramite),
    CONSTRAINT fk_resoluciones_documento  FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

CREATE TABLE IF NOT EXISTS evaluaciones (
    id_evaluacion    SERIAL PRIMARY KEY,
    id_proyecto      INT,
    id_plan_tesis    INT,
    id_evaluador     INT NOT NULL,
    resultado        VARCHAR(30),
    puntaje          INT,
    observaciones    TEXT,
    fecha_asignacion TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_evaluacion TIMESTAMP,
    CONSTRAINT chk_resultado_evaluacion    CHECK (resultado IN ('APROBADO','RECHAZADO','CON_OBSERVACIONES')),
    CONSTRAINT fk_evaluaciones_proyecto    FOREIGN KEY (id_proyecto)   REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_evaluaciones_tesis       FOREIGN KEY (id_plan_tesis) REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_evaluaciones_evaluador   FOREIGN KEY (id_evaluador)  REFERENCES usuarios(id_usuario),
    CONSTRAINT chk_evaluaciones_exclusividad CHECK (
        (id_proyecto IS NOT NULL AND id_plan_tesis IS NULL) OR
        (id_proyecto IS NULL    AND id_plan_tesis IS NOT NULL)
    )
);

-- ============================================================================
-- MÓDULO 9: AUDITORÍA GENERAL (RF-100, RNF-09)
-- ============================================================================

CREATE TABLE IF NOT EXISTS auditoria_general (
    id_auditoria     SERIAL PRIMARY KEY,
    tabla_afectada   VARCHAR(100) NOT NULL,
    id_registro      INT NOT NULL,
    accion           VARCHAR(30) NOT NULL,
    id_usuario       INT NOT NULL,
    datos_anteriores TEXT,
    datos_nuevos     TEXT,
    ip_origen        VARCHAR(45),
    fecha_accion     TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_accion_auditoria CHECK (accion IN ('CREAR','EDITAR','ELIMINAR','ACTIVAR','DESACTIVAR','LOGIN','LOGOUT')),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- ÍNDICES ESTRATÉGICOS (RNF-17, RNF-19)
-- ============================================================================

CREATE INDEX IF NOT EXISTS ix_usuarios_rol              ON usuarios(id_rol_principal);
CREATE INDEX IF NOT EXISTS ix_usuarios_activo           ON usuarios(es_activo);
CREATE INDEX IF NOT EXISTS ix_usuarios_roles_rol        ON usuarios_roles(id_rol);
CREATE INDEX IF NOT EXISTS ix_proyectos_responsable     ON proyectos(id_responsable);
CREATE INDEX IF NOT EXISTS ix_proyectos_grupo           ON proyectos(id_grupo);
CREATE INDEX IF NOT EXISTS ix_proyectos_estado          ON proyectos(estado_proyecto);
CREATE INDEX IF NOT EXISTS ix_planes_estudiante         ON planes_tesis(id_estudiante);
CREATE INDEX IF NOT EXISTS ix_planes_grupo              ON planes_tesis(id_grupo);
CREATE INDEX IF NOT EXISTS ix_tramites_estado           ON tramites(estado_actual, rol_revisor_actual);
CREATE INDEX IF NOT EXISTS ix_tramites_grupo            ON tramites(id_grupo);
CREATE INDEX IF NOT EXISTS ix_tramites_solicitante      ON tramites(id_solicitante);
CREATE INDEX IF NOT EXISTS ix_movimientos_tramite       ON movimientos_tramite(id_tramite);
CREATE INDEX IF NOT EXISTS ix_movimientos_fecha         ON movimientos_tramite(fecha_movimiento);
CREATE INDEX IF NOT EXISTS ix_informes_proyecto         ON informes_avance(id_proyecto);
CREATE INDEX IF NOT EXISTS ix_evaluaciones_evaluador    ON evaluaciones(id_evaluador);
CREATE INDEX IF NOT EXISTS ix_auditoria_tabla           ON auditoria_general(tabla_afectada, id_registro);
CREATE INDEX IF NOT EXISTS ix_auditoria_usuario         ON auditoria_general(id_usuario);
CREATE INDEX IF NOT EXISTS ix_auditoria_fecha           ON auditoria_general(fecha_accion);

-- ============================================================================
-- DATOS SEMILLA
-- ============================================================================

-- Roles del Sistema (RF-09)
INSERT INTO roles (codigo_rol, descripcion) VALUES
    ('ADMIN',                  'Administrador del Sistema'),
    ('ESTUDIANTE',             'Estudiante / Tesista'),
    ('DOCENTE_INVESTIGADOR',   'Docente Investigador'),
    ('COORDINADOR_GRUPO',      'Coordinador de Grupo de Investigación'),
    ('DIRECTOR_INVESTIGACION', 'Director de Investigación de la FIIS'),
    ('DECANO',                 'Decano de la Facultad'),
    ('EVALUADOR',              'Evaluador por Pares Externo o Interno')
ON CONFLICT (codigo_rol) DO NOTHING;

-- Grupos de Investigación (RF-15)
INSERT INTO grupos_investigacion (codigo_grupo, nombre_grupo, es_activo) VALUES
    ('GINSOFT',  'Grupo de Investigación en Ingeniería de Software', TRUE),
    ('RESEGTI',  'Red de Seguridad y Gestión de TI',                TRUE),
    ('GISI',     'Grupo de Investigación en Sistemas de Información',TRUE),
    ('CICO',     'Círculo de Computación',                          TRUE),
    ('EAP',      'Estadística Aplicada',                            TRUE),
    ('MAP',      'Matemática Aplicada',                             TRUE),
    ('EU',       'Emprendimiento Universitario',                    TRUE)
ON CONFLICT (codigo_grupo) DO NOTHING;

-- Líneas de Investigación (RF-24)
INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
    ('Computacion',                                 TRUE),
    ('Ingenieria de software',                      TRUE),
    ('Ciberseguridad y Auditoria de TI',            TRUE),
    ('Ciencia de Datos e Inteligencia Artificial',  TRUE),
    ('Redes y Telecomunicaciones',                  TRUE),
    ('Gestion de Tecnologias de Informacion',       TRUE)
ON CONFLICT (nombre_linea) DO NOTHING;

-- Líneas por Grupo — GINSOFT (RF-26)
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g
CROSS JOIN lineas_investigacion l
WHERE g.codigo_grupo = 'GINSOFT'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- FIN — V1__schema_inicial.sql
-- 19 tablas | 8 módulos + auditoría | Normalización 3NF
-- ============================================================================
