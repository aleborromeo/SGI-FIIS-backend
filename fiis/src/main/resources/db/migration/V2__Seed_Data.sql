-- ============================================================================
-- SGI-FIIS — Migración V2: Datos semilla consolidados y realistas
-- Contexto: UNAS - Universidad Nacional Agraria de la Selva, Tingo María, Huánuco
-- Compatibilidad: PostgreSQL 15+ / Flyway
--
-- Password para TODOS los usuarios: 00000000 (hash BCrypt)
-- Hash: $2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO
--
-- Cobertura: Roles, Grupos, Líneas, Usuarios, Convocatorias, Documentos,
--            Proyectos (6 estados), Planes de Tesis, Informes de Avance,
--            Trámites (9 estados), Movimientos, Observaciones, Subsanaciones,
--            Evaluaciones y Resoluciones.
-- ============================================================================

-- ============================================================================
-- 1. ROLES DEL SISTEMA (RF-09)
-- ============================================================================

INSERT INTO roles (codigo_rol, descripcion) VALUES
    ('ADMIN', 'Administrador del Sistema'),
    ('ESTUDIANTE', 'Estudiante / Tesista'),
    ('DOCENTE_INVESTIGADOR', 'Docente Investigador'),
    ('COORDINADOR_GRUPO', 'Coordinador de Grupo de Investigación'),
    ('DIRECTOR_INVESTIGACION', 'Director de Investigación de la FIIS'),
    ('DECANO', 'Decano de la Facultad'),
    ('EVALUADOR', 'Evaluador por Pares Externo o Interno');

-- ============================================================================
-- 2. GRUPOS DE INVESTIGACIÓN (RF-15)
-- ============================================================================

INSERT INTO grupos_investigacion (codigo_grupo, nombre_grupo, es_activo) VALUES
    ('GINSOFT', 'Grupo de Investigación en Ingeniería de Software', TRUE),
    ('RESEGTI', 'Red de Seguridad y Gestión de TI', TRUE),
    ('GISI', 'Grupo de Investigación en Sistemas de Información', TRUE),
    ('CICO', 'Círculo de Computación', TRUE),
    ('EAP', 'Estadística Aplicada', TRUE),
    ('MAP', 'Matemática Aplicada', TRUE),
    ('EU', 'Emprendimiento Universitario', TRUE);

-- ============================================================================
-- 3. LÍNEAS DE INVESTIGACIÓN (RF-24)
-- ============================================================================

INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
    ('Computacion', TRUE),
    ('Ingenieria de software', TRUE),
    ('Ciberseguridad y Auditoria de TI', TRUE),
    ('Ciencia de Datos e Inteligencia Artificial', TRUE),
    ('Redes y Telecomunicaciones', TRUE),
    ('Gestion de Tecnologias de Informacion', TRUE);

-- ============================================================================
-- 4. ASOCIACIÓN DE LÍNEAS POR GRUPO (RF-26, RN-12)
-- ============================================================================

-- GINSOFT: Computación + Ingeniería de Software
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'GINSOFT'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software');

-- RESEGTI: Ciberseguridad + Redes
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'RESEGTI'
  AND l.nombre_linea IN ('Ciberseguridad y Auditoria de TI', 'Redes y Telecomunicaciones');

-- GISI: Gestión TI + Computación
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'GISI'
  AND l.nombre_linea IN ('Gestion de Tecnologias de Informacion', 'Computacion');

-- CICO: Computación + Ciencia de Datos
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'CICO'
  AND l.nombre_linea IN ('Computacion', 'Ciencia de Datos e Inteligencia Artificial');

-- EAP: Ciencia de Datos
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'EAP'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial');

-- MAP: Computación
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'MAP'
  AND l.nombre_linea IN ('Computacion');

-- EU: Gestión TI
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'EU'
  AND l.nombre_linea IN ('Gestion de Tecnologias de Informacion');

-- ============================================================================
-- 5. USUARIOS DEL SISTEMA (15 usuarios — 2+ por cada rol)
-- Composición peruana: Nombre + 2 apellidos (andinos/amazónicos)
-- DNIs: 8 dígitos, rango válido (01000000 - 32000000)
-- Teléfonos: 9 dígitos (celulares peruanos: 9XX XXX XXX)
-- ============================================================================

INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, telefono, password_hash, es_activo, must_change_password, id_rol_principal) VALUES
    -- ADMIN (1 usuario)
    ('00000001', 'Admin', 'Sistema', 'admin@unas.edu.pe', '900000001', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),

    -- ESTUDIANTE (3 usuarios)
    ('40123456', 'José Carlos', 'Ramírez Tello', 'jose.ramirez@unas.edu.pe', '941234567', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('42865974', 'Lucía Fernanda', 'Fernández Ortiz', 'lucia.fernandez@unas.edu.pe', '942865974', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('50123456', 'Pedro Luis', 'Tello Gutiérrez', 'pedro.tello@unas.edu.pe', '950123456', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),

    -- DOCENTE_INVESTIGADOR (3 usuarios)
    ('41795328', 'María Elena', 'Quispe Huamán', 'maria.quispe@unas.edu.pe', '941795328', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('43198234', 'Humberto', 'Rojas Mendoza', 'humberto.rojas@unas.edu.pe', '943198234', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('49123789', 'Rosa Huamán', 'Quispe Condori', 'rosa.huaman@unas.edu.pe', '949123789', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),

    -- COORDINADOR_GRUPO (2 usuarios)
    ('33512689', 'Luis Alberto', 'Condori Rodríguez', 'luis.condori@unas.edu.pe', '933512689', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),
    ('44567891', 'Diana', 'Cárdenas Vela', 'diana.cardenas@unas.edu.pe', '944567891', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),

    -- DIRECTOR_INVESTIGACION (2 usuarios)
    ('45286137', 'Ana Lucía', 'Mendoza Sánchez', 'ana.mendoza@unas.edu.pe', '945286137', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
    ('46234567', 'Víctor Raúl', 'Aguilar Ríos', 'victor.aguilar@unas.edu.pe', '946234567', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),

    -- DECANO (2 usuarios)
    ('28956324', 'Pedro', 'García Morales', 'pedro.garcia@unas.edu.pe', '928956324', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),
    ('47891234', 'Patricia', 'Sánchez Vega', 'patricia.sanchez@unas.edu.pe', '947891234', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),

    -- EVALUADOR (2 usuarios)
    ('37894561', 'Rosa', 'Martínez Linares', 'rosa.martinez@unas.edu.pe', '937894561', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')),
    ('48903456', 'Alberto', 'Linares Castro', 'alberto.linares@unas.edu.pe', '948903456', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR'));

-- ============================================================================
-- 6. MULTI-ROL POR USUARIO (RF-09 ampliación)
-- ============================================================================

INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES
    -- Diana Cárdenas (COORDINADOR_GRUPO) también es EVALUADOR
    ((SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')),
    -- Alberto Linares (EVALUADOR) también es DOCENTE_INVESTIGADOR
    ((SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    -- Rosa Martínez (EVALUADOR) también es DOCENTE_INVESTIGADOR
    ((SELECT id_usuario FROM usuarios WHERE dni = '37894561'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR'));

-- ============================================================================
-- 7. ASIGNAR COORDINADORES A GRUPOS (RF-18)
-- ============================================================================

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '44567891')  -- Diana Cárdenas
WHERE codigo_grupo = 'GINSOFT';

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '33512689')  -- Luis Condori
WHERE codigo_grupo = 'RESEGTI';

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '49123789')  -- Rosa Huamán
WHERE codigo_grupo = 'GISI';

-- ============================================================================
-- 8. MEMBRESÍAS EN GRUPOS DE INVESTIGACIÓN (RF-19, RF-21)
-- ============================================================================

INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo, fecha_inicio) VALUES
    -- GINSOFT (grupo 1)
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE dni = '41795328'), TRUE, '2025-01-15'),  -- María Quispe
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), TRUE, '2025-01-15'),  -- Humberto Rojas
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'), TRUE, '2025-01-01'),  -- Diana Cárdenas (coord)

    -- RESEGTI (grupo 2)
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'RESEGTI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '33512689'), TRUE, '2025-02-01'),  -- Luis Condori (coord)
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'RESEGTI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), TRUE, '2025-02-01'),  -- Pedro Tello

    -- GISI (grupo 3)
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GISI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), TRUE, '2025-01-20'),  -- Rosa Huamán (coord)
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GISI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), TRUE, '2025-03-01'),  -- José Ramírez

    -- CICO (grupo 4)
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'CICO'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), TRUE, '2025-04-01'),  -- Lucía Fernández
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'CICO'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'), TRUE, '2025-04-01'),  -- Alberto Linares

    -- EU (grupo 7): Rosa Huamán es coord. de EU pero ya es activa en GISI,
    -- se agrega como miembro inactivo para indicar su rol de coordinadora
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'EU'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), FALSE, '2025-02-15');  -- Rosa Huamán (coord EU, ya activa en GISI)

-- ============================================================================
-- 9. CONVOCATORIAS (3 — una por cada estado)
-- ============================================================================

INSERT INTO convocatorias (titulo_convocatoria, descripcion, titulo_jsonb, descripcion_jsonb, fecha_inicio, fecha_fin, estado, id_creador) VALUES
    ('Convocatoria de Proyectos de Investigación 2026-I',
     'Convocatoria anual para la presentación de proyectos de investigación científica y tecnológica en las líneas priorizadas por la FIIS.',
     '{"es": "Convocatoria de Proyectos de Investigación 2026-I"}',
     '{"es": "Convocatoria anual para la presentación de proyectos de investigación científica y tecnológica."}',
     '2026-01-01', '2026-12-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891')),

    ('Convocatoria de Planes de Tesis 2026-I',
     'Convocatoria dirigida a estudiantes de pregrado para la presentación de planes de tesis en las líneas de investigación vigentes.',
     '{"es": "Convocatoria de Planes de Tesis 2026-I"}',
     '{"es": "Convocatoria dirigida a estudiantes de pregrado para presentación de planes de tesis."}',
     '2026-03-01', '2026-05-15', 'CERRADA',
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567')),

    ('Convocatoria de Informes de Avance 2025-II',
     'Convocatoria para la presentación de informes de avance parcial y final de los proyectos de investigación ejecutados en el semestre 2025-II.',
     '{"es": "Convocatoria de Informes de Avance 2025-II"}',
     '{"es": "Convocatoria para presentación de informes de avance de proyectos 2025-II."}',
     '2025-10-01', '2025-12-15', 'FINALIZADA',
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567'));

-- ============================================================================
-- 10. ASOCIACIÓN CONVOCATORIA - LÍNEAS
-- ============================================================================

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'ABIERTA'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software', 'Ciberseguridad y Auditoria de TI');

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'CERRADA'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial', 'Gestion de Tecnologias de Informacion');

-- FINALIZADA: líneas que cubrían la convocatoria expirada
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'FINALIZADA'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial', 'Redes y Telecomunicaciones');

-- ============================================================================
-- 11. DOCUMENTOS (6 — PDF y DOCX variados)
-- ============================================================================

INSERT INTO documentos (nombre_original, ruta_almacenamiento, tipo_extension, tamano_bytes, id_usuario_subio) VALUES
    ('Propuesta_Proyecto_Ciberseguridad.pdf', '/documentos/2026/propuesta_ciberseguridad.pdf', 'PDF', 245760,
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456')),
    ('Carta_Compromiso_Asesor.docx', '/documentos/2026/carta_compromiso_asesor.docx', 'DOCX', 153600,
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974')),
    ('Informe_Avance_Parcial_SGI.pdf', '/documentos/2026/informe_avance_parcial.pdf', 'PDF', 512000,
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234')),
    ('Resolucion_Decanal_001_2026.pdf', '/documentos/2026/resolucion_001_2026.pdf', 'PDF', 102400,
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324')),
    ('Plan_Tesis_IA_Educacion.pdf', '/documentos/2026/plan_tesis_ia_educacion.pdf', 'PDF', 180000,
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456')),
    ('Marco_Teórico_Red_Sensores.pdf', '/documentos/2026/marco_teorico_red_sensores.pdf', 'PDF', 320000,
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'));

-- ============================================================================
-- 12. PROYECTOS (6 — uno por cada estado del CHECK)
-- ============================================================================

INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, titulo_jsonb, resumen_jsonb, objetivo_general_jsonb, lugar_ejecucion_jsonb, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, id_convocatoria, estado_proyecto) VALUES
    -- POSTULADO
    ('PROY-2026-001', 'Plataforma de Ciberseguridad para la UNAS',
     'Proyecto orientado al desarrollo de una plataforma integral de ciberseguridad para proteger la infraestructura de TI de la Universidad Nacional Agraria de la Selva.',
     'Diseñar e implementar una plataforma de ciberseguridad que permita detectar, prevenir y mitigar amenazas informáticas en la red universitaria.',
     '{"es": "Plataforma de Ciberseguridad para la UNAS"}',
     '{"es": "Proyecto orientado al desarrollo de una plataforma integral de ciberseguridad."}',
     '{"es": "Diseñar e implementar una plataforma de ciberseguridad."}',
     '{"es": "Tingo María"}',
     3, 2, 25000.00, '2026-03-01', '2026-12-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'),
     (SELECT id_convocatoria FROM convocatorias WHERE estado = 'ABIERTA'),
     'POSTULADO'),

    -- OBSERVADO
    ('PROY-2026-002', 'Sistema Experto para Diagnóstico de Plagas Agrícolas',
     'Desarrollo de un sistema experto basado en reglas para el diagnóstico de plagas en cultivos de la región de la Selva Central.',
     'Construir un sistema experto que asista a los agricultores en la identificación y control de plagas agrícolas comunes.',
     '{"es": "Sistema Experto para Diagnóstico de Plagas Agrícolas"}',
     '{"es": "Desarrollo de un sistema experto basado en reglas para diagnóstico de plagas."}',
     '{"es": "Construir un sistema experto para identificación y control de plagas."}',
     '{"es": "Tingo María"}',
     4, 1, 15000.00, '2026-02-01', '2026-11-30', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     (SELECT id_convocatoria FROM convocatorias WHERE estado = 'ABIERTA'),
     'OBSERVADO'),

    -- APROBADO
    ('PROY-2026-003', 'Estudio de Factibilidad de una Red 5G para la UNAS',
     'Estudio de factibilidad técnica y económica para la implementación de una red de quinta generación (5G) en el campus universitario.',
     'Determinar la viabilidad técnica, económica y regulatoria de implementar una red 5G en la UNAS.',
     '{"es": "Estudio de Factibilidad de una Red 5G para la UNAS"}',
     '{"es": "Estudio de factibilidad técnica y económica para implementación de red 5G."}',
     '{"es": "Determinar la viabilidad técnica, económica y regulatoria de red 5G."}',
     '{"es": "Tingo María"}',
     5, 2, 12000.00, '2026-05-01', '2026-10-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'),
     (SELECT id_convocatoria FROM convocatorias WHERE estado = 'ABIERTA'),
     'APROBADO'),

    -- RECHAZADO
    ('PROY-2025-004', 'Red de Sensores IoT para Monitoreo Ambiental',
     'Propuesta de implementación de una red de sensores inalámbricos para el monitoreo de variables ambientales en la región de Huánuco.',
     'Implementar una red de sensores IoT que permita la recolección y análisis de datos ambientales en tiempo real.',
     '{"es": "Red de Sensores IoT para Monitoreo Ambiental"}',
     '{"es": "Propuesta de implementación de una red de sensores inalámbricos."}',
     '{"es": "Implementar una red de sensores IoT para monitoreo ambiental."}',
     '{"es": "Huánuco"}',
     5, 7, 30000.00, '2025-01-01', '2025-08-31', 'Huánuco',
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     (SELECT id_convocatoria FROM convocatorias WHERE estado = 'FINALIZADA'),
     'RECHAZADO'),

    -- EN_EJECUCION
    ('PROY-2026-005', 'Sistema de Gestión de Investigación FIIS',
     'Sistema integral para la gestión de los procesos de investigación de la Facultad de Ingeniería de Sistemas, incluyendo trámites, evaluaciones y seguimiento.',
     'Implementar una plataforma web que automatice los flujos de trabajo de investigación institucional.',
     '{"es": "Sistema de Gestión de Investigación FIIS"}',
     '{"es": "Sistema integral para la gestión de procesos de investigación de la FIIS."}',
     '{"es": "Implementar una plataforma web que automatice los flujos de trabajo de investigación."}',
     '{"es": "Tingo María"}',
     2, 1, 35000.00, '2026-01-01', '2026-12-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     (SELECT id_convocatoria FROM convocatorias WHERE estado = 'ABIERTA'),
     'EN_EJECUCION'),

    -- FINALIZADO
    ('PROY-2025-006', 'Análisis de Datos de Rendimiento Académico',
     'Proyecto de análisis de datos académicos mediante técnicas de machine learning para predecir el rendimiento estudiantil en la facultad.',
     'Desarrollar un modelo predictivo de rendimiento académico utilizando algoritmos de aprendizaje supervisado.',
     '{"es": "Análisis de Datos de Rendimiento Académico"}',
     '{"es": "Proyecto de análisis de datos académicos mediante machine learning."}',
     '{"es": "Desarrollar un modelo predictivo de rendimiento académico."}',
     '{"es": "Tingo María"}',
     4, 3, 18000.00, '2025-04-01', '2026-03-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'),
     (SELECT id_convocatoria FROM convocatorias WHERE estado = 'FINALIZADA'),
     'FINALIZADO');

-- ============================================================================
-- 13. INTEGRANTES DE PROYECTO
-- ============================================================================

INSERT INTO miembros_proyecto (id_proyecto, id_usuario, rol) VALUES
    -- PROY-2026-001 (Ciberseguridad)
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 'INVESTIGADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), 'COLABORADOR'),

    -- PROY-2026-002 (Plagas)
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'), 'COORDINADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 'TESISTA'),

    -- PROY-2026-003 (5G)
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), 'INVESTIGADOR'),

    -- PROY-2026-005 (SGI)
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'), 'COORDINADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 'TESISTA'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'), 'EVALUADOR'),

    -- PROY-2025-006 (Rendimiento)
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'), 'INVESTIGADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 'TESISTA');

-- ============================================================================
-- 14. PLANES DE TESIS (4 — varios estados)
-- ============================================================================

INSERT INTO planes_tesis (titulo_tesis, resumen, id_estudiante, id_linea, id_grupo, id_documento_actual, estado_plan) VALUES
    ('Sistema de Recomendación de Rutas Inteligentes',
     'Desarrollo de un sistema de recomendación de rutas óptimas utilizando algoritmos genéticos para la ciudad de Tingo María.',
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 1, 4, NULL, 'POSTULADO'),

    ('Algoritmo de Machine Learning para Diagnóstico de Enfermedades',
     'Implementación de un clasificador basado en redes neuronales para el diagnóstico temprano de enfermedades tropicales.',
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), 4, 1,
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Plan_Tesis_IA_Educacion.pdf'), 'OBSERVADO'),

    ('Plataforma Web de Gestión de Bibliotecas Universitarias',
     'Desarrollo de una plataforma web progresiva para la gestión automatizada de bibliotecas universitarias.',
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), 6, 1,
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Carta_Compromiso_Asesor.docx'), 'APROBADO'),

    ('Sistema IoT para Monitoreo Agrícola en la Selva Peruana',
     'Diseño de un sistema de bajo costo utilizando sensores IoT para el monitoreo de cultivos en la región de Huánuco.',
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 5, 7, NULL, 'RECHAZADO');

-- ============================================================================
-- 15. INFORMES DE AVANCE (2 — PARCIAL y FINAL)
-- ============================================================================

INSERT INTO informes_avance (id_proyecto, tipo_informe, periodo, porcentaje_avance, logros, dificultades, recomendaciones, id_documento_adjunto, estado_informe) VALUES
    -- PARCIAL — APROBADO
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     'PARCIAL', 'Enero - Junio 2026', 60.00,
     'Módulo de autenticación completado. Módulo de usuarios implementado al 80%. Integración con OAuth2 de Microsoft finalizada.',
     'Retraso en la integración con el sistema de correo institucional. Curva de aprendizaje del equipo en Clean Architecture.',
     'Continuar con la capacitación en Clean Architecture. Asignar más recursos al módulo de trámites.',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Informe_Avance_Parcial_SGI.pdf'),
     'APROBADO'),

    -- FINAL — EN_REVISION
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     'FINAL', 'Julio - Diciembre 2026', 100.00,
     'Todos los módulos completados. Pruebas unitarias con cobertura superior al 80%. Despliegue en entorno de pruebas exitoso.',
     'Problemas de rendimiento en consultas JPA con múltiples joins. Se optimizaron con índices y consultas específicas.',
     'Realizar pruebas de carga con k6 antes del pase a producción. Documentar la guía de despliegue.',
     NULL, 'EN_REVISION');

-- ============================================================================
-- 16. TRÁMITES (9 — uno por cada estado de ProcedureStatus)
-- Estados válidos: REGISTRADO, PENDIENTE_COORDINADOR, OBSERVADO, SUBSANADO,
--                  PENDIENTE_DIRECCION, PENDIENTE_DECANATO,
--                  APROBADO_CON_RESOLUCION, FINALIZADO, RECHAZADO
-- ============================================================================

-- T1: Flujo feliz COMPLETO → FINALIZADO (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-001', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 1,
    'FINALIZADO', NULL,
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
    '2026-01-15 08:00:00', '2026-04-30 16:30:00');

-- T2: Observado por Coordinador → OBSERVADO (PLAN_TESIS)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-002', 'PLAN_TESIS',
    (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), 1,
    'OBSERVADO', NULL,
    (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis = 'Algoritmo de Machine Learning para Diagnóstico de Enfermedades'),
    '2026-02-01 09:00:00', '2026-02-10 14:00:00');

-- T3: Recién creado → PENDIENTE_COORDINADOR (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-003', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 2,
    'PENDIENTE_COORDINADOR', 'COORDINADOR_GRUPO',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
    '2026-03-01 10:30:00', '2026-03-01 10:30:00');

-- T4: Rechazado por Director → RECHAZADO (PLAN_TESIS)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-004', 'PLAN_TESIS',
    (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 7,
    'RECHAZADO', NULL,
    (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis = 'Sistema IoT para Monitoreo Agrícola en la Selva Peruana'),
    '2026-02-15 11:00:00', '2026-03-01 09:00:00');

-- T5: Aprobado por Coordinador → PENDIENTE_DIRECCION (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-005', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 2,
    'PENDIENTE_DIRECCION', 'DIRECTOR_INVESTIGACION',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
    '2026-03-10 08:00:00', '2026-03-20 15:00:00');

-- T6: Aprobado por Director → PENDIENTE_DECANATO (PLAN_TESIS)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-006', 'PLAN_TESIS',
    (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), 1,
    'PENDIENTE_DECANATO', 'DECANO',
    (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis = 'Plataforma Web de Gestión de Bibliotecas Universitarias'),
    '2026-03-05 09:00:00', '2026-04-01 11:00:00');

-- T7: Resolución registrada → APROBADO_CON_RESOLUCION (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-007', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 1,
    'APROBADO_CON_RESOLUCION', 'DECANO',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
    '2026-01-15 08:00:00', '2026-04-28 16:00:00');

-- T8: Subsanado (estaba observado) → SUBSANADO (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-008', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 1,
    'SUBSANADO', 'COORDINADOR_GRUPO',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
    '2026-03-10 08:00:00', '2026-04-05 10:00:00');

-- T9: Recién registrado → REGISTRADO (INFORME_AVANCE)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_informe, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-009', 'INFORME_AVANCE',
    (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 1,
    'REGISTRADO', 'COORDINADOR_GRUPO',
    (SELECT id_informe FROM informes_avance WHERE tipo_informe = 'FINAL' AND estado_informe = 'EN_REVISION'),
    '2026-06-01 07:00:00', '2026-06-01 07:00:00');

-- ============================================================================
-- 17. MOVIMIENTOS DE TRAZABILIDAD (flujo completo de T1 + parciales)
-- ============================================================================

INSERT INTO movimientos_tramite (id_tramite, id_usuario_accion, accion, estado_anterior, estado_nuevo, observacion, fecha_movimiento) VALUES
    -- T1 (TRM-2026-001): Flujo feliz completo PROYECTO → FINALIZADO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-01-15 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'El proyecto cumple con los requisitos formales.', '2026-02-01 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Proyecto viable y alineado a los objetivos institucionales.', '2026-03-01 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324'),
     'RESOLUCION_REGISTRADA', 'PENDIENTE_DECANATO', 'APROBADO_CON_RESOLUCION', 'Resolución N° RES-001-2026-FIIS-UNAS emitida.', '2026-04-28 16:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324'),
     'TRAMITE_FINALIZADO', 'APROBADO_CON_RESOLUCION', 'FINALIZADO', 'Trámite culminado exitosamente.', '2026-04-30 16:30:00'),

    -- T2 (TRM-2026-002): Observado por Coordinador PLAN_TESIS
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-02-01 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'OBSERVADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'OBSERVADO', 'El marco teórico no incluye referencias actualizadas.', '2026-02-10 14:00:00'),

    -- T3 (TRM-2026-003): Recién presentado PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-003'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-01 10:30:00'),

    -- T4 (TRM-2026-004): Rechazado por Director PLAN_TESIS
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-02-15 11:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'El plan de tesis cumple con la estructura requerida.', '2026-02-20 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567'),
     'RECHAZADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'RECHAZADO', 'El tema no se alinea con las líneas prioritarias.', '2026-03-01 09:00:00'),

    -- T5 (TRM-2026-005): Aprobado por Coordinador PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-10 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto bien formulado. Se recomienda su revisión.', '2026-03-20 15:00:00'),

    -- T6 (TRM-2026-006): Aprobado por Coordinador y Director PLAN_TESIS
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-05 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Plan de tesis completo y bien estructurado.', '2026-03-15 14:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Metodología adecuada. Se aprueba para revisión del Decano.', '2026-04-01 11:00:00'),

    -- T7 (TRM-2026-007): Resolución registrada PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-01-15 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto aprobado por el coordinador.', '2026-02-01 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Proyecto aprobado por dirección.', '2026-03-01 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324'),
     'RESOLUCION_REGISTRADA', 'PENDIENTE_DECANATO', 'APROBADO_CON_RESOLUCION', 'Resolución N° RES-002-2026-FIIS-UNAS emitida.', '2026-04-28 16:00:00'),

    -- T8 (TRM-2026-008): Observado y subsanado PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-10 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto aprobado.', '2026-03-20 15:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'OBSERVADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'OBSERVADO', 'El presupuesto no está detallado por partidas.', '2026-03-25 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'SUBSANADO_POR_SOLICITANTE', 'OBSERVADO', 'SUBSANADO', 'Se adjunta desglose de presupuesto por partidas.', '2026-04-05 10:00:00'),

    -- T9 (TRM-2026-009): Recién registrado INFORME_AVANCE
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-009'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'REGISTRADO', NULL, '2026-06-01 07:00:00');

-- ============================================================================
-- 18. OBSERVACIONES (4 — varios tipos y estados)
-- ============================================================================

INSERT INTO observaciones (id_tramite, id_revisor, tipo_observacion, descripcion, estado_observacion, rol_revisor) VALUES
    -- Observación DOCUMENTAL — PENDIENTE
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'DOCUMENTAL',
     'Falta carta de compromiso del asesor firmada y escaneada. El documento debe incluir firma original o digital.',
     'PENDIENTE', 'COORDINADOR_GRUPO'),

    -- Observación PRESUPUESTAL — SUBSANADA
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'PRESUPUESTAL',
     'El presupuesto no está detallado por partidas específicas. Debe incluir: equipos, materiales, viajes y personal.',
     'SUBSANADA', 'DIRECTOR_INVESTIGACION'),

    -- Observación FORMATO — VIGENTE
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'FORMATO',
     'El formato de citas bibliográficas no cumple con el estilo APA 7ma edición. Revisar normas de la facultad.',
     'VIGENTE', 'COORDINADOR_GRUPO'),

    -- Observación TÉCNICA — PENDIENTE
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567'),
     'TECNICA',
     'La metodología propuesta no describe claramente el diseño de la investigación. Especificar si es experimental, descriptiva o correlacional.',
     'PENDIENTE', 'DIRECTOR_INVESTIGACION');

-- ============================================================================
-- 19. SUBSANACIONES (1 — para la observación PRESUPUESTAL)
-- ============================================================================

INSERT INTO subsanaciones (id_observacion, id_solicitante, descripcion, id_documento_adjunto) VALUES
    ((SELECT id_observacion FROM observaciones WHERE tipo_observacion = 'PRESUPUESTAL' AND estado_observacion = 'SUBSANADA'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'Se adjunta el desglose detallado del presupuesto por partidas: equipos (S/12,000), materiales (S/5,000), viajes (S/3,000), personal (S/15,000). Total: S/35,000.',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Carta_Compromiso_Asesor.docx'));

-- ============================================================================
-- 20. EVALUACIONES (3 — una por cada resultado)
-- ============================================================================

INSERT INTO evaluaciones (id_proyecto, id_plan_tesis, id_evaluador, resultado, puntaje, observaciones, fecha_asignacion, fecha_evaluacion) VALUES
    -- APROBADO — Proyecto EN_EJECUCION
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     NULL,
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     'APROBADO', 85,
     'El proyecto cumple con todos los requisitos técnicos y académicos. La metodología es adecuada y el presupuesto está bien justificado.',
     '2026-02-01 08:00:00', '2026-02-15 10:00:00'),

    -- RECHAZADO — Proyecto RECHAZADO
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-004'),
     NULL,
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     'RECHAZADO', 45,
     'El proyecto presenta deficiencias en el planteamiento del problema y la justificación. El presupuesto no está alineado con los objetivos propuestos.',
     '2025-02-01 08:00:00', '2025-02-20 10:00:00'),

    -- CON_OBSERVACIONES — Plan de tesis OBSERVADO
    (NULL,
     (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis = 'Plataforma Web de Gestión de Bibliotecas Universitarias'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     'CON_OBSERVACIONES', 65,
     'El plan de tesis es interesante pero requiere ajustes en el marco teórico y en la metodología de investigación propuesta.',
     '2026-03-10 08:00:00', '2026-03-25 10:00:00');

-- ============================================================================
-- 21. RESOLUCIONES (1 — asociada a T7)
-- ============================================================================

INSERT INTO resoluciones (numero_resolucion, fecha_emision, asunto, id_tramite, id_documento_adjunto) VALUES
    ('RES-001-2026-FIIS-UNAS', '2026-04-28',
     'Aprobar el Proyecto de Investigación "Sistema de Gestión de Investigación FIIS" presentado por el Docente Humberto García Morales y el Tesista Lucía Fernández Ortiz.',
     (SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Resolucion_Decanal_001_2026.pdf'));

-- ============================================================================
-- 22. ACTUALIZAR SECUENCIAS SERIAL (evitar conflictos)
-- ============================================================================

SELECT setval('roles_id_rol_seq', (SELECT COALESCE(MAX(id_rol), 7) FROM roles));
SELECT setval('usuarios_id_usuario_seq', (SELECT COALESCE(MAX(id_usuario), 15) FROM usuarios));
SELECT setval('grupos_investigacion_id_grupo_seq', (SELECT COALESCE(MAX(id_grupo), 7) FROM grupos_investigacion));
SELECT setval('lineas_investigacion_id_linea_seq', (SELECT COALESCE(MAX(id_linea), 6) FROM lineas_investigacion));
SELECT setval('documentos_id_documento_seq', (SELECT COALESCE(MAX(id_documento), 6) FROM documentos));
SELECT setval('convocatorias_id_convocatoria_seq', (SELECT COALESCE(MAX(id_convocatoria), 3) FROM convocatorias));
SELECT setval('proyectos_id_proyecto_seq', (SELECT COALESCE(MAX(id_proyecto), 6) FROM proyectos));
SELECT setval('miembros_proyecto_id_miembro_seq', (SELECT COALESCE(MAX(id_miembro), 14) FROM miembros_proyecto));
SELECT setval('planes_tesis_id_plan_tesis_seq', (SELECT COALESCE(MAX(id_plan_tesis), 4) FROM planes_tesis));
SELECT setval('informes_avance_id_informe_seq', (SELECT COALESCE(MAX(id_informe), 2) FROM informes_avance));
SELECT setval('tramites_id_tramite_seq', (SELECT COALESCE(MAX(id_tramite), 9) FROM tramites));
SELECT setval('movimientos_tramite_id_movimiento_seq', (SELECT COALESCE(MAX(id_movimiento), 24) FROM movimientos_tramite));
SELECT setval('observaciones_id_observacion_seq', (SELECT COALESCE(MAX(id_observacion), 4) FROM observaciones));
SELECT setval('subsanaciones_id_subsanacion_seq', (SELECT COALESCE(MAX(id_subsanacion), 1) FROM subsanaciones));
SELECT setval('evaluaciones_id_evaluacion_seq', (SELECT COALESCE(MAX(id_evaluacion), 3) FROM evaluaciones));
SELECT setval('resoluciones_id_resolucion_seq', (SELECT COALESCE(MAX(id_resolucion), 1) FROM resoluciones));

-- ============================================================================
-- 23. VERIFICACIÓN DE CONSISTENCIA
-- ============================================================================

DO $$
DECLARE
    v_total_usuarios    INT;
    v_total_tramites    INT;
    v_total_movimientos INT;
    v_errores           TEXT := '';
BEGIN
    SELECT COUNT(*) INTO v_total_usuarios FROM usuarios;
    SELECT COUNT(*) INTO v_total_tramites FROM tramites;
    SELECT COUNT(*) INTO v_total_movimientos FROM movimientos_tramite;

    -- Verificar al menos 1 usuario por rol
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')) THEN
        v_errores := v_errores || 'Falta usuario ESTUDIANTE. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')) THEN
        v_errores := v_errores || 'Falta usuario DOCENTE_INVESTIGADOR. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')) THEN
        v_errores := v_errores || 'Falta usuario COORDINADOR_GRUPO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')) THEN
        v_errores := v_errores || 'Falta usuario DIRECTOR_INVESTIGACION. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')) THEN
        v_errores := v_errores || 'Falta usuario DECANO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')) THEN
        v_errores := v_errores || 'Falta usuario EVALUADOR. ';
    END IF;

    -- Verificar al menos 1 trámite por estado
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'REGISTRADO') THEN
        v_errores := v_errores || 'Falta trámite REGISTRADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'PENDIENTE_COORDINADOR') THEN
        v_errores := v_errores || 'Falta trámite PENDIENTE_COORDINADOR. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'OBSERVADO') THEN
        v_errores := v_errores || 'Falta trámite OBSERVADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'SUBSANADO') THEN
        v_errores := v_errores || 'Falta trámite SUBSANADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'PENDIENTE_DIRECCION') THEN
        v_errores := v_errores || 'Falta trámite PENDIENTE_DIRECCION. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'PENDIENTE_DECANATO') THEN
        v_errores := v_errores || 'Falta trámite PENDIENTE_DECANATO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'APROBADO_CON_RESOLUCION') THEN
        v_errores := v_errores || 'Falta trámite APROBADO_CON_RESOLUCION. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'FINALIZADO') THEN
        v_errores := v_errores || 'Falta trámite FINALIZADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tramites WHERE estado_actual = 'RECHAZADO') THEN
        v_errores := v_errores || 'Falta trámite RECHAZADO. ';
    END IF;

    -- Verificar al menos 1 proyecto por estado
    IF NOT EXISTS (SELECT 1 FROM proyectos WHERE estado_proyecto = 'POSTULADO') THEN
        v_errores := v_errores || 'Falta proyecto POSTULADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM proyectos WHERE estado_proyecto = 'EN_EJECUCION') THEN
        v_errores := v_errores || 'Falta proyecto EN_EJECUCION. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM proyectos WHERE estado_proyecto = 'FINALIZADO') THEN
        v_errores := v_errores || 'Falta proyecto FINALIZADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM proyectos WHERE estado_proyecto = 'RECHAZADO') THEN
        v_errores := v_errores || 'Falta proyecto RECHAZADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM proyectos WHERE estado_proyecto = 'APROBADO') THEN
        v_errores := v_errores || 'Falta proyecto APROBADO. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM proyectos WHERE estado_proyecto = 'OBSERVADO') THEN
        v_errores := v_errores || 'Falta proyecto OBSERVADO. ';
    END IF;

    -- Verificar convocatorias
    IF NOT EXISTS (SELECT 1 FROM convocatorias WHERE estado = 'ABIERTA') THEN
        v_errores := v_errores || 'Falta convocatoria ABIERTA. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM convocatorias WHERE estado = 'CERRADA') THEN
        v_errores := v_errores || 'Falta convocatoria CERRADA. ';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM convocatorias WHERE estado = 'FINALIZADA') THEN
        v_errores := v_errores || 'Falta convocatoria FINALIZADA. ';
    END IF;

    IF v_errores = '' THEN
        RAISE NOTICE 'VERIFICACION EXITOSA: % usuarios, % tramites, % movimientos. Todos los estados cubiertos.', v_total_usuarios, v_total_tramites, v_total_movimientos;
    ELSE
        RAISE WARNING 'VERIFICACION INCOMPLETA: %', v_errores;
    END IF;
END $$;

-- ============================================================================
-- RESUMEN DE DATOS INSERTADOS
-- ============================================================================
--   + 15 usuarios (DNIs peruanos realistas: 40123456, 41795328, ...)
--   + 3 multi-rol        + 11 membresías     + 8 asociaciones línea-grupo
--   + 3 convocatorias    + 3 conv_lineas     + 6 documentos
--   + 6 proyectos        + 14 integrantes    + 4 planes tesis
--   + 2 informes avance  + 9 trámites        + 22 movimientos
--   + 4 observaciones    + 1 subsanación     + 3 evaluaciones
--   + 1 resolución
-- ============================================================================
