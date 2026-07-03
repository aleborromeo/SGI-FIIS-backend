-- ============================================================================
-- SGI-FIIS - Módulo thesis: planes de tesis e informes finales
-- Migración idempotente para Flyway/PostgreSQL.
-- Si V1 ya creó estas tablas desde el script general, este archivo no duplica.
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
    CONSTRAINT fk_planes_estudiante FOREIGN KEY (id_estudiante) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_planes_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea),
    CONSTRAINT fk_planes_grupo FOREIGN KEY (id_grupo) REFERENCES grupos_investigacion(id_grupo),
    CONSTRAINT fk_planes_documento FOREIGN KEY (id_documento_actual) REFERENCES documentos(id_documento)
);

CREATE TABLE IF NOT EXISTS informes_tesis (
    id_informe_tesis   SERIAL PRIMARY KEY,
    id_plan_tesis      INT NOT NULL,
    titulo_final       VARCHAR(500) NOT NULL,
    id_documento_tesis INT NOT NULL,
    fecha_presentacion TIMESTAMP DEFAULT NOW() NOT NULL,
    estado_informe     VARCHAR(30) DEFAULT 'EN_REVISION' NOT NULL,
    CONSTRAINT chk_estado_informe_tesis CHECK (estado_informe IN ('EN_REVISION','APROBADO','OBSERVADO')),
    CONSTRAINT fk_informes_tesis_plan FOREIGN KEY (id_plan_tesis) REFERENCES planes_tesis(id_plan_tesis),
    CONSTRAINT fk_informes_tesis_doc FOREIGN KEY (id_documento_tesis) REFERENCES documentos(id_documento)
);

CREATE INDEX IF NOT EXISTS ix_planes_estudiante ON planes_tesis(id_estudiante);
CREATE INDEX IF NOT EXISTS ix_planes_grupo ON planes_tesis(id_grupo);
CREATE INDEX IF NOT EXISTS ix_planes_estado ON planes_tesis(estado_plan);
CREATE INDEX IF NOT EXISTS ix_informes_tesis_plan ON informes_tesis(id_plan_tesis);
CREATE INDEX IF NOT EXISTS ix_informes_tesis_estado ON informes_tesis(estado_informe);
