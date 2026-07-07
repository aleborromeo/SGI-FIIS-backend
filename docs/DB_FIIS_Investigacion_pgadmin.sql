-- ============================================================================
-- SISTEMA DE GESTIÓN DE INVESTIGACIÓN FIIS
-- SCRIPT DE BASE DE DATOS - POSTGRESQL (PGADMIN)
-- NORMALIZACIÓN HASTA 3NF & IMPLEMENTACIÓN DE INTEGRIDAD REFERENCIAL
-- VERSIÓN OPTIMIZADA - DB_FIIS_Investigacion_pgadmin
-- ============================================================================

-- ============================================================================
-- 1. CREACIÓN DE LA BASE DE DATOS
-- ============================================================================
-- Ejecutar desde pgAdmin o psql como superusuario:
-- DROP DATABASE IF EXISTS db_fiis_investigacion;
-- CREATE DATABASE db_fiis_investigacion WITH ENCODING 'UTF8';
-- \c db_fiis_investigacion

-- ============================================================================
-- MÓDULO 1: SEGURIDAD Y USUARIOS (RF-01 a RF-14, RN-01)
-- ============================================================================

-- Tabla de Roles del Sistema (RF-04, RF-09)
CREATE TABLE roles (
    id_rol          SERIAL PRIMARY KEY,
    codigo_rol      VARCHAR(30) NOT NULL,
    descripcion     VARCHAR(100),
    CONSTRAINT uq_codigo_rol UNIQUE (codigo_rol)
);

-- Tabla de Usuarios (RF-01, RF-02, RF-06, RF-07, RF-08, RN-01, RNF-04, RNF-38, RNF-40)
CREATE TABLE usuarios (
    id_usuario          SERIAL PRIMARY KEY,
    dni                 CHAR(8) NOT NULL,
    nombres             VARCHAR(100) NOT NULL,
    apellidos           VARCHAR(100) NOT NULL,
    correo_institucional VARCHAR(150) NOT NULL,
    telefono            VARCHAR(20),
    password_hash       VARCHAR(255) NOT NULL,
    must_change_password BOOLEAN DEFAULT TRUE NOT NULL,
    es_activo           BOOLEAN DEFAULT TRUE NOT NULL,
    id_rol_principal    INT NOT NULL,                      -- RN-01: Cada usuario tiene un rol principal
    ultimo_acceso       TIMESTAMP,                         -- RNF-09: auditoría de accesos
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_dni_usuario UNIQUE (dni),
    CONSTRAINT uq_correo_usuario UNIQUE (correo_institucional),
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (id_rol_principal) REFERENCES roles(id_rol)
);

-- Tabla intermedia para múltiples roles por usuario (RF-09 - Ampliación Multi-rol)
CREATE TABLE usuarios_roles (
    id_usuario      INT NOT NULL,
    id_rol          INT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_ur_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- ============================================================================
-- MÓDULO 2: GRUPOS Y LÍNEAS DE INVESTIGACIÓN (RF-15 a RF-28, RN-02, RN-03, RN-11, RN-12)
-- ============================================================================

-- Tabla de Grupos de Investigación (RF-15, RF-16, RF-18, RN-03)
CREATE TABLE grupos_investigacion (
    id_grupo                SERIAL PRIMARY KEY,
    codigo_grupo            VARCHAR(20) NOT NULL,
    nombre_grupo            VARCHAR(150) NOT NULL,
    id_coordinador_actual   INT,
    es_activo               BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT uq_codigo_grupo UNIQUE (codigo_grupo),
    CONSTRAINT fk_grupos_coordinador FOREIGN KEY (id_coordinador_actual) REFERENCES usuarios(id_usuario)
);

-- Historial de Membresías en Grupos (RF-17, RF-19, RF-20, RF-21, RNF-49, RNF-50, RN-02)
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

-- Restricción: máximo una membresía activa por usuario (RF-21)
CREATE UNIQUE INDEX uq_membresias_activas ON membresias_grupo(id_usuario) WHERE es_activo = TRUE;

-- Tabla de Líneas de Investigación (RF-24, RF-27, RF-28, RN-11)
CREATE TABLE lineas_investigacion (
    id_linea            SERIAL PRIMARY KEY,
    nombre_linea        VARCHAR(150) NOT NULL,
    es_activa           BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT uq_nombre_linea UNIQUE (nombre_linea)
);

-- Intersección de Líneas permitidas por Grupo (RF-26, RN-12)
CREATE TABLE lineas_por_grupo (
    id_grupo    INT NOT NULL,
    id_linea    INT NOT NULL,
    PRIMARY KEY (id_grupo, id_linea),
    CONSTRAINT fk_lpg_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_lpg_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea)
);

-- ============================================================================
-- MÓDULO 3: DOCUMENTOS BASE (RF-65, RF-66, RNF-07, RNF-08)
-- ============================================================================

CREATE TABLE documentos (
    id_documento        SERIAL PRIMARY KEY,
    nombre_original     VARCHAR(255) NOT NULL,
    ruta_almacenamiento VARCHAR(500) NOT NULL,
    tipo_extension      VARCHAR(10) NOT NULL,
    tamano_bytes        BIGINT NOT NULL,
    id_usuario_subio    INT NOT NULL,
    fecha_carga         TIMESTAMP DEFAULT NOW() NOT NULL,
    es_activo           BOOLEAN DEFAULT TRUE NOT NULL,          -- RN-10: Soporte de borrado lógico
    CONSTRAINT chk_extension_documento CHECK (tipo_extension IN ('PDF', 'DOC', 'DOCX')),
    CONSTRAINT fk_documentos_usuario FOREIGN KEY (id_usuario_subio) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- MÓDULO 4: CONVOCATORIAS Y PROYECTOS (RF-29 a RF-44, RNF-37)
-- ============================================================================

-- Tabla de Convocatorias (RF-29, RF-30, RF-31, RF-34, RNF-09, RNF-40)
CREATE TABLE convocatorias (
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

-- Tabla de Proyectos de Investigación (RF-35, RF-36, RF-38, RF-39, RF-44, RNF-37)
CREATE TABLE proyectos (
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
    CONSTRAINT chk_estado_proyecto CHECK (estado_proyecto IN ('POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO', 'EN_EJECUCION', 'FINALIZADO')),
    CONSTRAINT fk_proyectos_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_proyectos_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_proyectos_responsable FOREIGN KEY (id_responsable) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_proyectos_convocatoria FOREIGN KEY (id_convocatoria) REFERENCES convocatorias(id_convocatoria),
    CONSTRAINT fk_proyectos_documento FOREIGN KEY (id_documento_actual) REFERENCES documentos(id_documento)
);

-- Equipo de Trabajo del Proyecto (RF-36 normalizado)
CREATE TABLE integrantes_proyecto (
    id_proyecto         INT NOT NULL,
    id_usuario          INT NOT NULL,
    rol_en_proyecto     VARCHAR(50) NOT NULL,
    fecha_integracion   TIMESTAMP DEFAULT NOW() NOT NULL,
    PRIMARY KEY (id_proyecto, id_usuario),
    CONSTRAINT fk_integrantes_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_integrantes_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- MÓDULO 5: PLANES DE TESIS (RF-45 a RF-53, RN-06)
-- ============================================================================

-- Tabla de Planes de Tesis (RF-45, RF-46, RF-47, RF-25, RN-06)
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

-- Tabla de Informes Finales de Tesis (RF-53)
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

-- Tabla General de Trámites (RF-40, RF-54, RF-55, RF-57, RNF-36, RN-04)
CREATE TABLE tramites (
    id_tramite              SERIAL PRIMARY KEY,
    codigo_tramite          VARCHAR(30) NOT NULL,
    tipo_tramite            VARCHAR(50) NOT NULL,
    id_solicitante          INT NOT NULL,
    id_grupo                INT NOT NULL,
    estado_actual           VARCHAR(50) NOT NULL,
    rol_revisor_actual      VARCHAR(50) NOT NULL,
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
    -- Arco Excluyente 3NF
    CONSTRAINT chk_tramites_exclusividad CHECK (
        (id_referencia_proyecto IS NOT NULL AND id_referencia_tesis IS NULL AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL AND id_referencia_tesis IS NOT NULL AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL AND id_referencia_tesis IS NULL AND id_referencia_informe IS NOT NULL)
    )
);

-- Trazabilidad de Movimientos (RF-61, RF-63, RF-64, RF-96, RF-97, RF-98, RF-100, RNF-46, RNF-47, RNF-48)
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
-- MÓDULO 8: RESOLUCIONES Y EVALUACIONES (RF-78 a RF-87)
-- ============================================================================

-- Resoluciones Emitidas (RF-52, RF-78, RF-79, RF-80, RN-09)
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

-- Evaluaciones por Pares (RF-83, RF-84, RF-85, RF-86)
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
    -- Arco Excluyente
    CONSTRAINT chk_evaluaciones_exclusividad CHECK (
        (id_proyecto IS NOT NULL AND id_plan_tesis IS NULL) OR
        (id_proyecto IS NULL AND id_plan_tesis IS NOT NULL)
    )
);

-- ============================================================================
-- MÓDULO 9: AUDITORÍA GENERAL (RF-100, RNF-09)
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

CREATE INDEX ix_usuarios_rol ON usuarios(id_rol_principal);
CREATE INDEX ix_usuarios_activo ON usuarios(es_activo);
CREATE INDEX ix_usuarios_roles_rol ON usuarios_roles(id_rol); -- Índice para multi-rol
CREATE INDEX ix_proyectos_responsable ON proyectos(id_responsable);
CREATE INDEX ix_proyectos_grupo ON proyectos(id_grupo);
CREATE INDEX ix_proyectos_estado ON proyectos(estado_proyecto);
CREATE INDEX ix_planes_estudiante ON planes_tesis(id_estudiante);
CREATE INDEX ix_planes_grupo ON planes_tesis(id_grupo);
CREATE INDEX ix_tramites_estado ON tramites(estado_actual, rol_revisor_actual);
CREATE INDEX ix_tramites_grupo ON tramites(id_grupo);
CREATE INDEX ix_tramites_solicitante ON tramites(id_solicitante);
CREATE INDEX ix_movimientos_tramite ON movimientos_tramite(id_tramite);
CREATE INDEX ix_movimientos_fecha ON movimientos_tramite(fecha_movimiento);
CREATE INDEX ix_informes_proyecto ON informes_avance(id_proyecto);
CREATE INDEX ix_evaluaciones_evaluador ON evaluaciones(id_evaluador);
CREATE INDEX ix_auditoria_tabla ON auditoria_general(tabla_afectada, id_registro);
CREATE INDEX ix_auditoria_usuario ON auditoria_general(id_usuario);
CREATE INDEX ix_auditoria_fecha ON auditoria_general(fecha_accion);

-- ============================================================================
-- INSERCIÓN DE DATOS SEMILLA
-- ============================================================================

-- 1. Roles del Sistema (RF-09)
INSERT INTO roles (codigo_rol, descripcion) VALUES
('ADMIN', 'Administrador del Sistema'),
('ESTUDIANTE', 'Estudiante / Tesista'),
('DOCENTE_INVESTIGADOR', 'Docente Investigador'),
('COORDINADOR_GRUPO', 'Coordinador de Grupo de Investigación'),
('DIRECTOR_INVESTIGACION', 'Director de Investigación de la FIIS'),
('DECANO', 'Decano de la Facultad'),
('EVALUADOR', 'Evaluador por Pares Externo o Interno');

-- 2. Grupos de Investigación (RF-15)
INSERT INTO grupos_investigacion (codigo_grupo, nombre_grupo, es_activo) VALUES
('GINSOFT', 'Grupo de Investigación en Ingeniería de Software', TRUE),
('RESEGTI', 'Red de Seguridad y Gestión de TI', TRUE),
('GISI', 'Grupo de Investigación en Sistemas de Información', TRUE),
('CICO', 'Círculo de Computación', TRUE),
('EAP', 'Estadística Aplicada', TRUE),
('MAP', 'Matemática Aplicada', TRUE),
('EU', 'Emprendimiento Universitario', TRUE);

-- 3. Líneas de Investigación (RF-24)
INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
('Computacion', TRUE),
('Ingenieria de software', TRUE),
('Ciberseguridad y Auditoria de TI', TRUE),
('Ciencia de Datos e Inteligencia Artificial', TRUE),
('Redes y Telecomunicaciones', TRUE),
('Gestion de Tecnologias de Informacion', TRUE);

-- 4. Asociación de Líneas por Grupo - GINSOFT (RF-26, RN-12)
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'GINSOFT' AND l.nombre_linea IN ('Computacion', 'Ingenieria de software');

-- ============================================================================
-- FIN DEL SCRIPT DEFINITIVO Y OPTIMIZADO
-- Total: 19 tablas | 8 módulos + auditoría general + multi-rol + borrado lógico
-- Normalización: 3NF con arcos excluyentes
-- Compatible: PostgreSQL / pgAdmin
-- ============================================================================
