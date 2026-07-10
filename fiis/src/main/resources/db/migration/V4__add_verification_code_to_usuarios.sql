-- ============================================================
-- V4: Agregar columnas para verificación de 6 dígitos (2FA)
-- ============================================================

ALTER TABLE usuarios ADD COLUMN codigo_verificacion VARCHAR(6);
ALTER TABLE usuarios ADD COLUMN fecha_expiracion_codigo TIMESTAMP;
