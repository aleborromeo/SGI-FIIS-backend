-- ============================================================
-- V13: Inserción de datos de prueba para miembros y proyectos
-- ============================================================

-- 1. Insertar membresías activas para grupos de investigación
-- GINSOFT (id = 1)
-- Miembros: Maria Gomez (2), Carlos Ramos (3), Juan Perez (1)
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo) VALUES (1, 2, TRUE);
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo) VALUES (1, 3, TRUE);
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo) VALUES (1, 1, TRUE);

-- RESEGTI (id = 2)
-- Miembros: Ana Torres (4)
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo) VALUES (2, 4, TRUE);

-- GISI (id = 3)
-- Miembros: Luis Mendoza (5)
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo) VALUES (3, 5, TRUE);

-- CICO (id = 4)
-- Miembros: Jorge Castro (6)
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo) VALUES (4, 6, TRUE);


-- 2. Insertar proyectos de investigación para cada grupo
-- GINSOFT (id = 1) -> Ya tiene PROY-2026-001 (id = 1) en V11. Añadimos un segundo proyecto.
INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, estado_proyecto)
VALUES ('PROY-2026-002', 'Desarrollo de Metodologías Ágiles en la Región', 'Resumen del proyecto ágil', 'Objetivo de metodologías', 2, 1, 3500.00, '2026-03-01', '2026-11-30', 'Tingo María', 3, 'EN_EJECUCION');

-- RESEGTI (id = 2) -> 1 proyecto finalizado
INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, estado_proyecto)
VALUES ('PROY-2026-003', 'Auditoría de Seguridad en Redes de Computadoras', 'Resumen del proyecto de auditoría', 'Objetivo de ciberseguridad', 3, 2, 4500.00, '2026-02-01', '2026-10-31', 'Tingo María', 4, 'FINALIZADO');

-- GISI (id = 3) -> 1 proyecto finalizado
INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, estado_proyecto)
VALUES ('PROY-2026-004', 'Sistema de Información para la Gestión de Almacenes', 'Resumen del proyecto de almacén', 'Objetivo de SI', 1, 3, 3000.00, '2026-04-01', '2026-12-31', 'Tingo María', 2, 'FINALIZADO');

-- CICO (id = 4) -> 1 proyecto finalizado
INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, estado_proyecto)
VALUES ('PROY-2026-005', 'Algoritmos Paralelos en GPU para Simulación Física', 'Resumen del proyecto de algoritmos', 'Objetivo de cómputo paralelo', 1, 4, 6000.00, '2026-01-15', '2026-12-15', 'Tingo María', 6, 'FINALIZADO');
