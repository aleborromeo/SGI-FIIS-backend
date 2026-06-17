-- ============================================================================
-- V2__create_observaciones_subsanaciones.sql
-- MÓDULO: OBSERVACIONES Y SUBSANACIONES (RF-60 a RF-64, RF-69, RF-76, RN-07, RN-08)
-- ============================================================================

-- ============================================================================
-- TABLA: observaciones
-- Almacena el detalle de cada observación registrada por un revisor
-- (Coordinador, Director, Decano) sobre un trámite.
-- ============================================================================
CREATE TABLE IF NOT EXISTS observaciones (
    id_observacion      SERIAL PRIMARY KEY,
    id_tramite          INT NOT NULL,
    id_revisor          INT NOT NULL,
    tipo_observacion    VARCHAR(30) NOT NULL,
    descripcion         TEXT NOT NULL,
    estado_observacion  VARCHAR(20) DEFAULT 'PENDIENTE' NOT NULL,
    rol_revisor         VARCHAR(50) NOT NULL,
    fecha_registro      TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion TIMESTAMP DEFAULT NOW() NOT NULL, -- Cumple RNF-40

    -- Constraints de integridad referencial (Evita borrado accidental de trámites históricos RNF-22)
    CONSTRAINT fk_observaciones_tramite
        FOREIGN KEY (id_tramite) REFERENCES tramites(id_tramite) ON DELETE RESTRICT,
    CONSTRAINT fk_observaciones_revisor
        FOREIGN KEY (id_revisor) REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,

    -- Constraints de validación
    CONSTRAINT chk_tipo_observacion
        CHECK (tipo_observacion IN ('TECNICA', 'DOCUMENTAL', 'PRESUPUESTAL', 'FORMATO')),
    CONSTRAINT chk_estado_observacion
        CHECK (estado_observacion IN ('PENDIENTE', 'SUBSANADA', 'VIGENTE'))
);

-- ============================================================================
-- TABLA: subsanaciones
-- Almacena las subsanaciones presentadas por los solicitantes para levantar
-- observaciones pendientes.
-- ============================================================================
CREATE TABLE IF NOT EXISTS subsanaciones (
    id_subsanacion       SERIAL PRIMARY KEY,
    id_observacion       INT NOT NULL,
    id_solicitante       INT NOT NULL,
    descripcion          TEXT NOT NULL,
    id_documento_adjunto INT,
    fecha_registro       TIMESTAMP DEFAULT NOW() NOT NULL,
    fecha_actualizacion  TIMESTAMP DEFAULT NOW() NOT NULL, -- Agregado para auditoría RNF-40

    -- Constraints de integridad referencial
    CONSTRAINT fk_subsanaciones_observacion
        FOREIGN KEY (id_observacion) REFERENCES observaciones(id_observacion) ON DELETE RESTRICT,
    CONSTRAINT fk_subsanaciones_solicitante
        FOREIGN KEY (id_solicitante) REFERENCES usuarios(id_usuario) ON DELETE RESTRICT,
    CONSTRAINT fk_subsanaciones_documento
        FOREIGN KEY (id_documento_adjunto) REFERENCES documentos(id_documento) ON DELETE RESTRICT
);

-- ============================================================================
-- ÍNDICES ESTRATÉGICOS (RNF-17: rendimiento en consultas frecuentes de bandejas)
-- ============================================================================

-- Búsqueda de observaciones por trámite (trazabilidad, RF-64)
CREATE INDEX IF NOT EXISTS ix_observaciones_tramite
    ON observaciones(id_tramite);

-- Filtrar observaciones por estado (identificación rápida de pendientes de subsanar)
CREATE INDEX IF NOT EXISTS ix_observaciones_estado
    ON observaciones(estado_observacion);

-- Búsqueda de subsanaciones por observación
CREATE INDEX IF NOT EXISTS ix_subsanaciones_observacion
    ON subsanaciones(id_observacion);

-- Búsqueda compuesta para optimizar la carga de la bandeja de pendientes del usuario
CREATE INDEX IF NOT EXISTS ix_observaciones_tramite_estado
    ON observaciones(id_tramite, estado_observacion);
