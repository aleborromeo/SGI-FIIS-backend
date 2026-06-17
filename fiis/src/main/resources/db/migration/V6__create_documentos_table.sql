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
    es_activo             BOOLEAN       NOT NULL DEFAULT TRUE,
    id_proyecto           BIGINT,
    id_tramite            BIGINT,
    id_plan_tesis         BIGINT,
    id_informe            BIGINT,
    es_subsanacion        BOOLEAN       NOT NULL DEFAULT FALSE
);

-- Índices para mejorar rendimiento de búsquedas y filtrados
CREATE INDEX idx_documentos_usuario ON documentos(id_usuario_subio);
CREATE INDEX idx_documentos_proyecto ON documentos(id_proyecto);
CREATE INDEX idx_documentos_tramite ON documentos(id_tramite);
CREATE INDEX idx_documentos_plan_tesis ON documentos(id_plan_tesis);
CREATE INDEX idx_documentos_informe ON documentos(id_informe);
