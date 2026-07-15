-- Fix seed data: EN_REVISION -> PENDIENTE_COORDINADOR (invalid enum value)
UPDATE tramites SET estado_actual = 'PENDIENTE_COORDINADOR', rol_revisor_actual = 'COORDINADOR_GRUPO'
WHERE estado_actual = 'EN_REVISION' AND tipo_tramite = 'PLAN_TESIS';

UPDATE tramites SET estado_actual = 'PENDIENTE_COORDINADOR', rol_revisor_actual = 'COORDINADOR_GRUPO'
WHERE estado_actual = 'EN_REVISION' AND tipo_tramite = 'PROYECTO';
