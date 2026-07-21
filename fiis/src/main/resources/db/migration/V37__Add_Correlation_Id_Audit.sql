ALTER TABLE auditoria_general ADD COLUMN IF NOT EXISTS correlation_id VARCHAR(36);
ALTER TABLE movimientos_tramite ADD COLUMN IF NOT EXISTS correlation_id VARCHAR(36);
CREATE INDEX IF NOT EXISTS idx_auditoria_correlation_id ON auditoria_general(correlation_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_correlation_id ON movimientos_tramite(correlation_id);
