-- ============================================================
-- V10: Inserción de datos de prueba completos (Proyectos, Tesis y Trámites)
-- ============================================================

-- 1. Insertamos un Proyecto de prueba
INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, estado_proyecto)
VALUES ('PROY-2026-001', 'Sistema de Gestión FIIS', 'Resumen del proyecto', 'Objetivo del proyecto', 1, 1, 5000.00, '2026-01-01', '2026-12-31', 'Tingo María', 2, 'POSTULADO');

-- 2. Insertamos un Plan de Tesis de prueba
INSERT INTO planes_tesis (titulo_tesis, resumen, id_estudiante, id_linea, id_grupo, estado_plan)
VALUES ('Tesis sobre IA en Educación', 'Resumen de tesis', 1, 4, 1, 'POSTULADO');

-- 3. Insertamos un trámite de prueba para el Proyecto (id_tramite = 1)
-- id_solicitante = 2 (Docente - Responsable)
-- id_referencia_proyecto = 1 (El proyecto recién insertado)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto) 
VALUES ('TRM-2026-001', 'PROYECTO', 2, 1, 'EN_REVISION', 'ADMIN', 1);

-- 4. Insertamos un trámite de prueba para el Plan de Tesis (id_tramite = 2)
-- id_solicitante = 1 (Juan Perez - Estudiante)
-- id_referencia_tesis = 1 (La tesis recién insertada)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis) 
VALUES ('TRM-2026-002', 'PLAN_TESIS', 1, 1, 'EN_REVISION', 'COORDINADOR_GRUPO', 1);
