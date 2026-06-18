-- ============================================================
-- V8: Creación de tablas para el módulo de Resoluciones
-- y tabla de documentos para almacenar los adjuntos
-- ============================================================

-- Se utiliza la tabla de documentos de la migración V6

-- Tabla principal de resoluciones
CREATE TABLE resoluciones (
    id_resolucion         BIGSERIAL     PRIMARY KEY,
    numero_resolucion     VARCHAR(100)  NOT NULL UNIQUE,
    fecha_emision         DATE          NOT NULL,
    asunto                VARCHAR(500)  NOT NULL,
    id_tramite            BIGINT        NOT NULL, -- Referencia a la futura tabla de trámites
    id_documento_adjunto  BIGINT        NOT NULL REFERENCES documentos(id_documento),
    fecha_registro        TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Índices para búsquedas rápidas
CREATE INDEX idx_resoluciones_numero ON resoluciones(numero_resolucion);
CREATE INDEX idx_resoluciones_tramite ON resoluciones(id_tramite);
