-- ============================================================
-- V6: Creación de tabla de Documentos
-- Sistema de Gestión de Investigación FIIS
-- ============================================================

CREATE TABLE documentos (
    id_documento          BIGSERIAL     PRIMARY KEY,
    nombre_original       VARCHAR(255)  NOT NULL,
    ruta_almacenamiento   VARCHAR(500)  NOT NULL,
    tipo_extension        VARCHAR(10)   NOT NULL,
    tamano_bytes          BIGINT        NOT NULL,
    id_usuario_subio      BIGINT        NOT NULL REFERENCES usuarios(id_usuario),
    fecha_carga           TIMESTAMP     NOT NULL DEFAULT NOW(),
    es_activo             BOOLEAN       NOT NULL DEFAULT TRUE
);

-- Índices para mejorar rendimiento de búsquedas y filtrados
CREATE INDEX idx_documentos_usuario ON documentos(id_usuario_subio);
