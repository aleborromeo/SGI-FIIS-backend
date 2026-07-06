-- Add document to convocatorias
ALTER TABLE convocatorias ADD COLUMN IF NOT EXISTS id_documento_bases BIGINT;
ALTER TABLE convocatorias DROP CONSTRAINT IF EXISTS fk_convocatorias_documento;
ALTER TABLE convocatorias ADD CONSTRAINT fk_convocatorias_documento FOREIGN KEY (id_documento_bases) REFERENCES documentos(id_documento);

-- Create missing table convocatorias_lineas
CREATE TABLE IF NOT EXISTS convocatorias_lineas (
    id_convocatoria BIGINT NOT NULL,
    id_linea BIGINT NOT NULL,
    PRIMARY KEY (id_convocatoria, id_linea),
    CONSTRAINT fk_conv_lineas_conv FOREIGN KEY (id_convocatoria) REFERENCES convocatorias(id_convocatoria),
    CONSTRAINT fk_conv_lineas_linea FOREIGN KEY (id_linea) REFERENCES lineas_investigacion(id_linea)
);
