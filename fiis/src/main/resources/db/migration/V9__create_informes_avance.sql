-- V9__create_informes_avance.sql
-- Progress Reports (Informes de Avance) table
-- RF-70 to RF-77

CREATE TABLE IF NOT EXISTS informes_avance (
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

CREATE INDEX IF NOT EXISTS ix_informes_proyecto ON informes_avance(id_proyecto);
