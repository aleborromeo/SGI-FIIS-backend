-- V9: Ajustes al esquema de tramites para compatibilidad con el dominio
-- La tabla fue creada en V7. Estos cambios corrigen restricciones NOT NULL incompatibles
-- con estados validos del dominio (ej. tramite RECHAZADO no tiene rol_revisor_actual).

ALTER TABLE tramites
    ALTER COLUMN rol_revisor_actual DROP NOT NULL;

ALTER TABLE tramites
    ALTER COLUMN id_grupo DROP NOT NULL;

ALTER TABLE tramites
    ADD COLUMN IF NOT EXISTS observacion_actual TEXT;
