-- ============================================================
-- V3: Agregar campo oauth_provider a usuarios
-- Para identificar usuarios autenticados vía OAuth (Microsoft)
-- ============================================================

-- Campo para identificar el proveedor OAuth (null = login local)
ALTER TABLE usuarios ADD COLUMN oauth_provider VARCHAR(50);

-- Índice para búsquedas por proveedor OAuth
CREATE INDEX idx_usuarios_oauth_provider ON usuarios(oauth_provider);
