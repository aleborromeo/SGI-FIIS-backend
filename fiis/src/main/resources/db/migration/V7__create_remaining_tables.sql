-- ============================================================================
-- V7: Creación de tablas restantes del Sistema de Gestión de Investigación FIIS
-- Basado en la normalización hasta 3NF e Integridad Referencial
-- ============================================================================

-- ============================================================================
-- MÓDULO 1: SEGURIDAD Y USUARIOS (Ampliación Multi-rol)
-- ============================================================================

-- Tabla intermedia para múltiples roles por usuario
CREATE TABLE usuarios_roles (
    id_usuario      BIGINT NOT NULL,
    id_rol          BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_ur_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- ============================================================================
-- MÓDULO 2: GRUPOS Y LÍNEAS DE INVESTIGACIÓN
-- ============================================================================

-- Tabla de Grupos de Investigación
CREATE TABLE grupos_investigacion (
    id_grupo                BIGSERIAL PRIMARY KEY,
    codigo_grupo            VARCHAR(20) NOT NULL UNIQUE,
    nombre_grupo            VARCHAR(150) NOT NULL,
    id_coordinador_actual   BIGINT,
    es_activo               BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT fk_grupos_coordinador FOREIGN KEY (id_coordinador_actual) REFERENCES usuarios(id_usuario)
);

-- Historial de Membresías en Grupos
CREATE TABLE membresias_grupo (
    id_membresia    BIGSERIAL PRIMARY KEY,
    id_grupo        BIGINT NOT NULL,
    id_usuario      BIGINT NOT NULL,
    es_activo       BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_inicio    TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_fin       TIMESTAMP,
    CONSTRAINT fk_membresias_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_membresias_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Restricción: máximo una membresía activa por usuario (RF-21)
CREATE UNIQUE INDEX uq_membresias_activas ON membresias_grupo(id_usuario) WHERE es_activo = TRUE;

-- Tabla de Líneas de Investigación
CREATE TABLE lineas_investigacion (
    id_linea            BIGSERIAL PRIMARY KEY,
    nombre_linea        VARCHAR(150) NOT NULL UNIQUE,
    es_activa           BOOLEAN DEFAULT TRUE NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL
);

-- Intersección de Líneas permitidas por Grupo
CREATE TABLE lineas_por_grupo (
    id_grupo    BIGINT NOT NULL,
    id_linea    BIGINT NOT NULL,
    PRIMARY KEY (id_grupo, id_linea),
    CONSTRAINT fk_lpg_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_lpg_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea)
);

-- ============================================================================
-- MÓDULO 4: CONVOCATORIAS Y PROYECTOS
-- ============================================================================

-- Tabla de Convocatorias
CREATE TABLE convocatorias (
    id_convocatoria     BIGSERIAL PRIMARY KEY,
    titulo_convocatoria VARCHAR(150) NOT NULL,
    descripcion         TEXT,
    fecha_inicio        DATE NOT NULL,
    fecha_fin           DATE NOT NULL,
    estado              VARCHAR(20) DEFAULT 'ABIERTA' NOT NULL,
    id_creador          BIGINT NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_estado_convocatoria CHECK (estado IN ('ABIERTA', 'CERRADA', 'FINALIZADA')),
    CONSTRAINT chk_fechas_convocatoria CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT fk_convocatorias_creador FOREIGN KEY (id_creador) REFERENCES usuarios(id_usuario)
);

-- Tabla de Proyectos de Investigación
CREATE TABLE proyectos (
    id_proyecto         BIGSERIAL PRIMARY KEY,
    codigo_proyecto     VARCHAR(30) NOT NULL UNIQUE,
    titulo_proyecto     VARCHAR(500) NOT NULL,
    resumen             TEXT NOT NULL,
    objetivo_general    TEXT NOT NULL,
    id_linea            BIGINT NOT NULL,
    id_grupo            BIGINT NOT NULL,
    presupuesto         DECIMAL(18,2) NOT NULL,
    fecha_inicio        DATE NOT NULL,
    fecha_fin           DATE NOT NULL,
    lugar_ejecucion     VARCHAR(255) NOT NULL,
    id_responsable      BIGINT NOT NULL,
    id_convocatoria     BIGINT,
    id_documento_actual BIGINT,
    estado_proyecto     VARCHAR(30) DEFAULT 'POSTULADO' NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_estado_proyecto CHECK (estado_proyecto IN ('POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO', 'EN_EJECUCION', 'FINALIZADO')),
    CONSTRAINT fk_proyectos_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_proyectos_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_proyectos_responsable FOREIGN KEY (id_responsable) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_proyectos_convocatoria FOREIGN KEY (id_convocatoria) REFERENCES convocatorias(id_convocatoria),
    CONSTRAINT fk_proyectos_documento FOREIGN KEY (id_documento_actual) REFERENCES documentos(id_documento)
);

-- Equipo de Trabajo del Proyecto
CREATE TABLE integrantes_proyecto (
    id_proyecto         BIGINT NOT NULL,
    id_usuario          BIGINT NOT NULL,
    rol_en_proyecto     VARCHAR(50) NOT NULL,
    fecha_integracion   TIMESTAMP DEFAULT NOW() NOT NULL,
    PRIMARY KEY (id_proyecto, id_usuario),
    CONSTRAINT fk_integrantes_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_integrantes_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- MÓDULO 5: PLANES DE TESIS
-- ============================================================================

-- Tabla de Planes de Tesis
CREATE TABLE planes_tesis (
    id_plan_tesis       BIGSERIAL PRIMARY KEY,
    titulo_tesis        VARCHAR(500) NOT NULL,
    resumen             TEXT,
    id_estudiante       BIGINT NOT NULL,
    id_linea            BIGINT NOT NULL,
    id_grupo            BIGINT NOT NULL,
    id_documento_actual BIGINT,
    estado_plan         VARCHAR(30) DEFAULT 'POSTULADO' NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_estado_plan CHECK (estado_plan IN ('POSTULADO', 'OBSERVADO', 'APROBADO', 'RECHAZADO')),
    CONSTRAINT fk_planes_estudiante FOREIGN KEY (id_estudiante) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_planes_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_planes_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_planes_documento FOREIGN KEY (id_documento_actual) REFERENCES documentos(id_documento)
);

-- Tabla de Informes Finales de Tesis
CREATE TABLE informes_tesis (
    id_informe_tesis    BIGSERIAL PRIMARY KEY,
    id_plan_tesis       BIGINT NOT NULL,
    titulo_final        VARCHAR(500) NOT NULL,
    id_documento_tesis  BIGINT NOT NULL,
    fecha_presentacion  TIMESTAMP DEFAULT NOW() NOT NULL,
    estado_informe      VARCHAR(30) DEFAULT 'EN_REVISION' NOT NULL,
    CONSTRAINT chk_estado_informe_tesis CHECK (estado_informe IN ('EN_REVISION', 'APROBADO', 'OBSERVADO')),
    CONSTRAINT fk_informes_tesis_plan FOREIGN KEY (id_plan_tesis) REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_informes_tesis_doc FOREIGN KEY (id_documento_tesis) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 6: INFORMES DE AVANCE DE PROYECTOS
-- ============================================================================

CREATE TABLE informes_avance (
    id_informe          BIGSERIAL PRIMARY KEY,
    id_proyecto         BIGINT NOT NULL,
    tipo_informe        VARCHAR(50) NOT NULL,
    periodo             VARCHAR(50) NOT NULL,
    porcentaje_avance   DECIMAL(5,2) NOT NULL,
    logros              TEXT NOT NULL,
    dificultades        TEXT NOT NULL,
    recomendaciones     TEXT NOT NULL,
    id_documento_adjunto BIGINT,
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
-- MÓDULO 7: WORKFLOW, TRÁMITES Y AUDITORÍA
-- ============================================================================

-- Tabla General de Trámites
CREATE TABLE tramites (
    id_tramite              BIGSERIAL PRIMARY KEY,
    codigo_tramite          VARCHAR(30) NOT NULL UNIQUE,
    tipo_tramite            VARCHAR(50) NOT NULL,
    id_solicitante          BIGINT NOT NULL,
    id_grupo                BIGINT NOT NULL,
    estado_actual           VARCHAR(50) NOT NULL,
    rol_revisor_actual      VARCHAR(50) NOT NULL,
    fecha_envio             TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion     TIMESTAMP DEFAULT NOW() NOT NULL,
    id_referencia_proyecto  BIGINT,
    id_referencia_tesis     BIGINT,
    id_referencia_informe   BIGINT,
    CONSTRAINT chk_tipo_tramite CHECK (tipo_tramite IN ('PROYECTO', 'PLAN_TESIS', 'INFORME_AVANCE')),
    CONSTRAINT fk_tramites_solicitante FOREIGN KEY (id_solicitante) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_tramites_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_tramites_proyecto FOREIGN KEY (id_referencia_proyecto) REFERENCES proyectos(id_proyecto),
    CONSTRAINT fk_tramites_tesis FOREIGN KEY (id_referencia_tesis) REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_tramites_informe FOREIGN KEY (id_referencia_informe) REFERENCES informes_avance(id_informe),
    -- Arco Excluyente
    CONSTRAINT chk_tramites_exclusividad CHECK (
        (id_referencia_proyecto IS NOT NULL AND id_referencia_tesis IS NULL AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL AND id_referencia_tesis IS NOT NULL AND id_referencia_informe IS NULL) OR
        (id_referencia_proyecto IS NULL AND id_referencia_tesis IS NULL AND id_referencia_informe IS NOT NULL)
    )
);

-- Trazabilidad de Movimientos
CREATE TABLE movimientos_tramite (
    id_movimiento       BIGSERIAL PRIMARY KEY,
    id_tramite          BIGINT NOT NULL,
    id_usuario_accion   BIGINT NOT NULL,
    accion              VARCHAR(50) NOT NULL,
    estado_anterior     VARCHAR(50) NOT NULL,
    estado_nuevo        VARCHAR(50) NOT NULL,
    observacion         TEXT,
    id_documento_adjunto BIGINT,
    fecha_movimiento    TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_movimientos_tramite FOREIGN KEY (id_tramite) REFERENCES tramites(id_tramite),
    CONSTRAINT fk_movimientos_usuario FOREIGN KEY (id_usuario_accion) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_movimientos_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

-- ============================================================================
-- MÓDULO 8: RESOLUCIONES Y EVALUACIONES
-- ============================================================================

-- Resoluciones Emitidas
CREATE TABLE resoluciones (
    id_resolucion       BIGSERIAL PRIMARY KEY,
    numero_resolucion   VARCHAR(100) NOT NULL UNIQUE,
    fecha_emision       DATE NOT NULL,
    asunto              VARCHAR(500) NOT NULL,
    id_tramite          BIGINT NOT NULL,
    id_documento_adjunto BIGINT NOT NULL,
    fecha_registro      TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_resoluciones_tramite FOREIGN KEY (id_tramite) REFERENCES tramites(id_tramite),
    CONSTRAINT fk_resoluciones_documento FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento)
);

-- Evaluaciones por Pares
CREATE TABLE evaluaciones (
    id_evaluacion       BIGSERIAL PRIMARY KEY,
    id_proyecto         BIGINT,
    id_plan_tesis       BIGINT,
    id_evaluador        BIGINT NOT NULL,
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
-- MÓDULO 9: AUDITORÍA GENERAL
-- ============================================================================

CREATE TABLE auditoria_general (
    id_auditoria        BIGSERIAL PRIMARY KEY,
    tabla_afectada      VARCHAR(100) NOT NULL,
    id_registro         BIGINT NOT NULL,
    accion              VARCHAR(30) NOT NULL,
    id_usuario          BIGINT NOT NULL,
    datos_anteriores    TEXT,
    datos_nuevos        TEXT,
    ip_origen           VARCHAR(45),
    fecha_accion        TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT chk_accion_auditoria CHECK (accion IN ('CREAR', 'EDITAR', 'ELIMINAR', 'ACTIVAR', 'DESACTIVAR', 'LOGIN', 'LOGOUT')),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ============================================================================
-- ÍNDICES ESTRATÉGICOS PARA RENDIMIENTO
-- ============================================================================

CREATE INDEX ix_usuarios_roles_rol ON usuarios_roles(id_rol);
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
-- INSERCIÓN DE DATOS SEMILLA RESTANTES
-- ============================================================================

-- Grupos de Investigación
INSERT INTO grupos_investigacion (codigo_grupo, nombre_grupo, es_activo) VALUES
('GINSOFT', 'Grupo de Investigación en Ingeniería de Software', TRUE),
('RESEGTI', 'Red de Seguridad y Gestión de TI', TRUE),
('GISI', 'Grupo de Investigación en Sistemas de Información', TRUE),
('CICO', 'Círculo de Computación', TRUE),
('EAP', 'Estadística Aplicada', TRUE),
('MAP', 'Matemática Aplicada', TRUE),
('EU', 'Emprendimiento Universitario', TRUE);

-- Líneas de Investigación
INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
('Computacion', TRUE),
('Ingenieria de software', TRUE),
('Ciberseguridad y Auditoria de TI', TRUE),
('Ciencia de Datos e Inteligencia Artificial', TRUE),
('Redes y Telecomunicaciones', TRUE),
('Gestion de Tecnologias de Informacion', TRUE);

-- Asociación de Líneas por Grupo - GINSOFT
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'GINSOFT' AND l.nombre_linea IN ('Computacion', 'Ingenieria de software');
