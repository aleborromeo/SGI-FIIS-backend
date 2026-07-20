-- ============================================================================
-- SGI-FIIS — Migración V2: Datos semilla consolidados
-- Contexto: UNAS - Universidad Nacional Agraria de la Selva, Tingo María, Huánuco
-- Compatibilidad: PostgreSQL 15+ / Flyway
--
-- Password para TODOS los usuarios: 00000000 (hash BCrypt)
-- Hash: $2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO
--
-- Cobertura: 5 usuarios por cada rol (35 total), 5+ registros por tabla.
-- ============================================================================

-- ============================================================================
-- 1. ROLES DEL SISTEMA (7)
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
-- 2. USUARIOS (35 — 5 por cada rol)
-- ============================================================================

INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, telefono, password_hash, es_activo, must_change_password, id_rol_principal) VALUES
    -- ADMIN (5)
    ('00000001', 'Admin', 'Sistema', 'admin@unas.edu.pe', '900000001', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),
    ('00000002', 'Carlos', 'Mendoza Torres', 'carlos.mendoza@unas.edu.pe', '900000002', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),
    ('00000003', 'Rosa', 'Pérez López', 'rosa.perez@unas.edu.pe', '900000003', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),
    ('00000004', 'Juan', 'Vargas Solís', 'juan.vargas@unas.edu.pe', '900000004', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),
    ('00000005', 'Martha', 'Luna Paredes', 'martha.luna@unas.edu.pe', '900000005', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),

    -- ESTUDIANTE (5)
    ('40123456', 'José Carlos', 'Ramírez Tello', 'jose.ramirez@unas.edu.pe', '941234567', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('42865974', 'Lucía Fernanda', 'Fernández Ortiz', 'lucia.fernandez@unas.edu.pe', '942865974', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('50123456', 'Pedro Luis', 'Tello Gutiérrez', 'pedro.tello@unas.edu.pe', '950123456', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('40123457', 'Ana María', 'Quispe Fernández', 'ana.quispe@unas.edu.pe', '940123457', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('40123458', 'Diego', 'Huamán Ramos', 'diego.huaman@unas.edu.pe', '940123458', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),

    -- DOCENTE_INVESTIGADOR (5)
    ('41795328', 'María Elena', 'Quispe Huamán', 'maria.quispe@unas.edu.pe', '941795328', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('43198234', 'Humberto', 'Rojas Mendoza', 'humberto.rojas@unas.edu.pe', '943198234', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('49123789', 'Rosa Huamán', 'Quispe Condori', 'rosa.huaman@unas.edu.pe', '949123789', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('41795330', 'Jorge', 'Cárdenas Mendoza', 'jorge.cardenas@unas.edu.pe', '941795330', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('41795331', 'Elena', 'Ríos Castillo', 'elena.rios@unas.edu.pe', '941795331', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),

    -- COORDINADOR_GRUPO (5)
    ('33512689', 'Luis Alberto', 'Condori Rodríguez', 'luis.condori@unas.edu.pe', '933512689', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),
    ('44567891', 'Diana', 'Cárdenas Vela', 'diana.cardenas@unas.edu.pe', '944567891', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),
    ('33512690', 'Marco', 'Álvarez Soto', 'marco.alvarez@unas.edu.pe', '933512690', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),
    ('33512691', 'Claudia', 'Reyes Gonzales', 'claudia.reyes@unas.edu.pe', '933512691', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),
    ('33512692', 'Fernando', 'Gutiérrez Paz', 'fernando.gutierrez@unas.edu.pe', '933512692', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),

    -- DIRECTOR_INVESTIGACION (5)
    ('45286137', 'Ana Lucía', 'Mendoza Sánchez', 'ana.mendoza@unas.edu.pe', '945286137', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
    ('46234567', 'Víctor Raúl', 'Aguilar Ríos', 'victor.aguilar@unas.edu.pe', '946234567', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
    ('45286138', 'Roberto', 'Sánchez Díaz', 'roberto.sanchez@unas.edu.pe', '945286138', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
    ('45286139', 'Gloria', 'Torres Vela', 'gloria.torres@unas.edu.pe', '945286139', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
    ('45286140', 'Miguel', 'Flores Aguirre', 'miguel.flores@unas.edu.pe', '945286140', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),

    -- DECANO (5)
    ('28956324', 'Pedro', 'García Morales', 'pedro.garcia@unas.edu.pe', '928956324', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),
    ('47891234', 'Patricia', 'Sánchez Vega', 'patricia.sanchez@unas.edu.pe', '947891234', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),
    ('28956325', 'Augusto', 'Herrera Medina', 'augusto.herrera@unas.edu.pe', '928956325', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),
    ('28956326', 'Carmen', 'Salazar Vega', 'carmen.salazar@unas.edu.pe', '928956326', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),
    ('28956327', 'Raúl', 'Montesinos Ríos', 'raul.montesinos@unas.edu.pe', '928956327', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),

    -- EVALUADOR (5)
    ('37894561', 'Rosa', 'Martínez Linares', 'rosa.martinez@unas.edu.pe', '937894561', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')),
    ('48903456', 'Alberto', 'Linares Castro', 'alberto.linares@unas.edu.pe', '948903456', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')),
    ('37894562', 'Silvia', 'Campos Ortega', 'silvia.campos@unas.edu.pe', '937894562', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')),
    ('37894563', 'Arturo', 'Ponce de León', 'arturo.ponce@unas.edu.pe', '937894563', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')),
    ('37894564', 'Mónica', 'Delgado Huanca', 'monica.delgado@unas.edu.pe', '937894564', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE,
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR'));

-- ============================================================================
-- 3. MULTI-ROL POR USUARIO (5)
-- ============================================================================

INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES
    ((SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')),
    ((SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ((SELECT id_usuario FROM usuarios WHERE dni = '37894561'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ((SELECT id_usuario FROM usuarios WHERE dni = '33512690'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ((SELECT id_usuario FROM usuarios WHERE dni = '45286138'),
     (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR'));

-- ============================================================================
-- 4. GRUPOS DE INVESTIGACIÓN (7)
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
-- 5. ASIGNAR COORDINADORES A GRUPOS (5)
-- ============================================================================

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '44567891')
WHERE codigo_grupo = 'GINSOFT';

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '33512689')
WHERE codigo_grupo = 'RESEGTI';

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '49123789')
WHERE codigo_grupo = 'GISI';

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '33512690')
WHERE codigo_grupo = 'CICO';

UPDATE grupos_investigacion SET id_coordinador_actual =
    (SELECT id_usuario FROM usuarios WHERE dni = '33512691')
WHERE codigo_grupo = 'EAP';

-- ============================================================================
-- 6. MEMBRESÍAS EN GRUPOS DE INVESTIGACIÓN (12)
-- ============================================================================

INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo, fecha_inicio) VALUES
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE dni = '41795328'), TRUE, '2025-01-15'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), TRUE, '2025-01-15'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'), TRUE, '2025-01-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'RESEGTI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '33512689'), TRUE, '2025-02-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'RESEGTI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), TRUE, '2025-02-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GISI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), TRUE, '2025-01-20'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GISI'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), TRUE, '2025-03-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'CICO'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), TRUE, '2025-04-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'CICO'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'), TRUE, '2025-04-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'EAP'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123457'), TRUE, '2025-05-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'MAP'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123458'), TRUE, '2025-05-01'),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'EU'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), FALSE, '2025-02-15');

-- ============================================================================
-- 7. LÍNEAS DE INVESTIGACIÓN (6)
-- ============================================================================

INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
    ('Computacion', TRUE),
    ('Ingenieria de software', TRUE),
    ('Ciberseguridad y Auditoria de TI', TRUE),
    ('Ciencia de Datos e Inteligencia Artificial', TRUE),
    ('Redes y Telecomunicaciones', TRUE),
    ('Gestion de Tecnologias de Informacion', TRUE);

-- ============================================================================
-- 8. ASOCIACIÓN DE LÍNEAS POR GRUPO (8)
-- ============================================================================

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'GINSOFT'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software');

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'RESEGTI'
  AND l.nombre_linea IN ('Ciberseguridad y Auditoria de TI', 'Redes y Telecomunicaciones');

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'GISI'
  AND l.nombre_linea IN ('Gestion de Tecnologias de Informacion', 'Computacion');

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'CICO'
  AND l.nombre_linea IN ('Computacion', 'Ciencia de Datos e Inteligencia Artificial');

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'EAP'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial');

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'MAP'
  AND l.nombre_linea IN ('Computacion');

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'EU'
  AND l.nombre_linea IN ('Gestion de Tecnologias de Informacion');

INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'RESEGTI'
  AND l.nombre_linea IN ('Computacion');

-- ============================================================================
-- 9. CONVOCATORIAS (6 — 2 por cada estado)
-- ============================================================================

INSERT INTO convocatorias (titulo_convocatoria, descripcion, titulo_jsonb, descripcion_jsonb, fecha_inicio, fecha_fin, estado, id_creador) VALUES
    ('Convocatoria de Proyectos de Investigación 2026-I',
     'Convocatoria anual para proyectos de investigación científica y tecnológica en las líneas priorizadas por la FIIS.',
     '{"es": "Convocatoria de Proyectos de Investigación 2026-I"}',
     '{"es": "Convocatoria anual para proyectos de investigación científica y tecnológica."}',
     '2026-01-01', '2026-12-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891')),

    ('Convocatoria de Planes de Tesis 2026-II',
     'Convocatoria dirigida a estudiantes de pregrado para la presentación de planes de tesis en las líneas vigentes.',
     '{"es": "Convocatoria de Planes de Tesis 2026-II"}',
     '{"es": "Convocatoria para presentación de planes de tesis."}',
     '2026-07-01', '2026-11-30', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567')),

    ('Convocatoria de Proyectos de Innovación 2025-II',
     'Convocatoria para proyectos de innovación tecnológica con impacto social en la región.',
     '{"es": "Convocatoria de Proyectos de Innovación 2025-II"}',
     '{"es": "Convocatoria para proyectos de innovación tecnológica."}',
     '2025-07-01', '2025-12-15', 'CERRADA',
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567')),

    ('Convocatoria de Informes de Avance 2026-I',
     'Convocatoria para la presentación de informes de avance parcial y final de proyectos en ejecución.',
     '{"es": "Convocatoria de Informes de Avance 2026-I"}',
     '{"es": "Convocatoria para informes de avance de proyectos."}',
     '2026-01-01', '2026-06-30', 'CERRADA',
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891')),

    ('Convocatoria de Proyectos 2025-I',
     'Primera convocatoria anual de proyectos de investigación para el ciclo 2025.',
     '{"es": "Convocatoria de Proyectos 2025-I"}',
     '{"es": "Primera convocatoria anual de proyectos 2025."}',
     '2025-01-01', '2025-06-30', 'FINALIZADA',
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567')),

    ('Convocatoria de Tesis de Pregrado 2025-II',
     'Convocatoria para la presentación de trabajos de tesis de pregrado en la FIIS.',
     '{"es": "Convocatoria de Tesis de Pregrado 2025-II"}',
     '{"es": "Convocatoria para trabajos de tesis de pregrado."}',
     '2025-07-01', '2025-12-20', 'FINALIZADA',
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'));

-- ============================================================================
-- 10. ASOCIACIÓN CONVOCATORIA - LÍNEAS
-- ============================================================================

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'ABIERTA' AND c.titulo_convocatoria LIKE '%Proyectos%'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software', 'Ciberseguridad y Auditoria de TI');

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'ABIERTA' AND c.titulo_convocatoria LIKE '%Tesis%'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial', 'Gestion de Tecnologias de Informacion');

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'CERRADA' AND c.titulo_convocatoria LIKE '%Innovación%'
  AND l.nombre_linea IN ('Ingenieria de software', 'Redes y Telecomunicaciones');

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'CERRADA' AND c.titulo_convocatoria LIKE '%Informes%'
  AND l.nombre_linea IN ('Computacion', 'Ciencia de Datos e Inteligencia Artificial');

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'FINALIZADA' AND c.titulo_convocatoria LIKE '%2025-I%'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software', 'Ciberseguridad y Auditoria de TI');

INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.estado = 'FINALIZADA' AND c.titulo_convocatoria LIKE '%Tesis%'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial', 'Redes y Telecomunicaciones');

-- ============================================================================
-- 11. DOCUMENTOS (15)
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
    ('Marco_Teorico_Red_Sensores.pdf', '/documentos/2026/marco_teorico_red_sensores.pdf', 'PDF', 320000,
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974')),
    ('Informe_Tesis_Final_Recomendacion.pdf', '/documentos/2026/informe_tesis_recomendacion.pdf', 'PDF', 280000,
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974')),
    ('Informe_Tesis_Final_ML_Diagnostico.pdf', '/documentos/2026/informe_tesis_ml_diagnostico.pdf', 'PDF', 350000,
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456')),
    ('Informe_Tesis_Final_Bibliotecas.pdf', '/documentos/2026/informe_tesis_bibliotecas.pdf', 'PDF', 290000,
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456')),
    ('Informe_Tesis_Final_IoT_Agricola.pdf', '/documentos/2026/informe_tesis_iot_agricola.pdf', 'PDF', 310000,
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456')),
    ('Informe_Tesis_Final_Red_IoT.pdf', '/documentos/2026/informe_tesis_red_iot.pdf', 'PDF', 275000,
     (SELECT id_usuario FROM usuarios WHERE dni = '40123457')),
    ('Resolucion_Decanal_002_2026.pdf', '/documentos/2026/resolucion_002_2026.pdf', 'PDF', 105000,
     (SELECT id_usuario FROM usuarios WHERE dni = '28956325')),
    ('Resolucion_Decanal_003_2026.pdf', '/documentos/2026/resolucion_003_2026.pdf', 'PDF', 108000,
     (SELECT id_usuario FROM usuarios WHERE dni = '28956326')),
    ('Resolucion_Decanal_004_2026.pdf', '/documentos/2026/resolucion_004_2026.pdf', 'PDF', 110000,
     (SELECT id_usuario FROM usuarios WHERE dni = '28956327')),
    ('Resolucion_Decanal_005_2026.pdf', '/documentos/2026/resolucion_005_2026.pdf', 'PDF', 112000,
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324'));

-- ============================================================================
-- 12. PROYECTOS (6 — uno por cada estado del CHECK)
-- ============================================================================

INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, titulo_jsonb, resumen_jsonb, objetivo_general_jsonb, lugar_ejecucion_jsonb, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, id_convocatoria, estado_proyecto) VALUES
    ('PROY-2026-001', 'Plataforma de Ciberseguridad para la UNAS',
     'Proyecto orientado al desarrollo de una plataforma integral de ciberseguridad para proteger la infraestructura de TI de la Universidad.',
     'Diseñar e implementar una plataforma de ciberseguridad que permita detectar, prevenir y mitigar amenazas informáticas.',
     '{"es": "Plataforma de Ciberseguridad para la UNAS"}',
     '{"es": "Proyecto orientado al desarrollo de una plataforma integral de ciberseguridad."}',
     '{"es": "Diseñar e implementar una plataforma de ciberseguridad."}',
     '{"es": "Tingo María"}',
     3, 2, 25000.00, '2026-03-01', '2026-12-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'),
     (SELECT id_convocatoria FROM convocatorias WHERE titulo_convocatoria LIKE '%Proyectos de Investigación 2026%'),
     'POSTULADO'),

    ('PROY-2026-002', 'Sistema Experto para Diagnóstico de Plagas Agrícolas',
     'Desarrollo de un sistema experto basado en reglas para el diagnóstico de plagas en cultivos de la Selva Central.',
     'Construir un sistema experto que asista a los agricultores en la identificación y control de plagas agrícolas.',
     '{"es": "Sistema Experto para Diagnóstico de Plagas Agrícolas"}',
     '{"es": "Desarrollo de un sistema experto para diagnóstico de plagas."}',
     '{"es": "Construir un sistema experto para identificación y control de plagas."}',
     '{"es": "Tingo María"}',
     4, 1, 15000.00, '2026-02-01', '2026-11-30', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     (SELECT id_convocatoria FROM convocatorias WHERE titulo_convocatoria LIKE '%Proyectos de Investigación 2026%'),
     'OBSERVADO'),

    ('PROY-2026-003', 'Estudio de Factibilidad de una Red 5G para la UNAS',
     'Estudio de factibilidad técnica y económica para la implementación de una red 5G en el campus universitario.',
     'Determinar la viabilidad técnica, económica y regulatoria de implementar una red 5G en la UNAS.',
     '{"es": "Estudio de Factibilidad de una Red 5G para la UNAS"}',
     '{"es": "Estudio de factibilidad técnica y económica para red 5G."}',
     '{"es": "Determinar la viabilidad de implementar una red 5G."}',
     '{"es": "Tingo María"}',
     5, 2, 12000.00, '2026-05-01', '2026-10-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'),
     (SELECT id_convocatoria FROM convocatorias WHERE titulo_convocatoria LIKE '%Proyectos de Investigación 2026%'),
     'APROBADO'),

    ('PROY-2025-004', 'Red de Sensores IoT para Monitoreo Ambiental',
     'Propuesta de implementación de una red de sensores inalámbricos para el monitoreo de variables ambientales.',
     'Implementar una red de sensores IoT para la recolección y análisis de datos ambientales en tiempo real.',
     '{"es": "Red de Sensores IoT para Monitoreo Ambiental"}',
     '{"es": "Propuesta de implementación de una red de sensores inalámbricos."}',
     '{"es": "Implementar una red de sensores IoT para monitoreo ambiental."}',
     '{"es": "Huánuco"}',
     5, 7, 30000.00, '2025-01-01', '2025-08-31', 'Huánuco',
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     (SELECT id_convocatoria FROM convocatorias WHERE titulo_convocatoria LIKE '%Proyectos 2025-I%'),
     'RECHAZADO'),

    ('PROY-2026-005', 'Sistema de Gestión de Investigación FIIS',
     'Sistema integral para la gestión de los procesos de investigación de la FIIS, incluyendo trámites, evaluaciones y seguimiento.',
     'Implementar una plataforma web que automatice los flujos de trabajo de investigación institucional.',
     '{"es": "Sistema de Gestión de Investigación FIIS"}',
     '{"es": "Sistema integral para la gestión de procesos de investigación de la FIIS."}',
     '{"es": "Implementar una plataforma web que automatice los flujos de trabajo."}',
     '{"es": "Tingo María"}',
     2, 1, 35000.00, '2026-01-01', '2026-12-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     (SELECT id_convocatoria FROM convocatorias WHERE titulo_convocatoria LIKE '%Proyectos de Investigación 2026%'),
     'EN_EJECUCION'),

    ('PROY-2025-006', 'Análisis de Datos de Rendimiento Académico',
     'Proyecto de análisis de datos académicos mediante machine learning para predecir el rendimiento estudiantil.',
     'Desarrollar un modelo predictivo de rendimiento académico utilizando algoritmos de aprendizaje supervisado.',
     '{"es": "Análisis de Datos de Rendimiento Académico"}',
     '{"es": "Proyecto de análisis de datos académicos mediante machine learning."}',
     '{"es": "Desarrollar un modelo predictivo de rendimiento académico."}',
     '{"es": "Tingo María"}',
     4, 3, 18000.00, '2025-04-01', '2026-03-31', 'Tingo María',
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'),
     (SELECT id_convocatoria FROM convocatorias WHERE titulo_convocatoria LIKE '%Proyectos 2025-I%'),
     'FINALIZADO');

-- ============================================================================
-- 13. INTEGRANTES DE PROYECTO (16)
-- ============================================================================

INSERT INTO miembros_proyecto (id_proyecto, id_usuario, rol) VALUES
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 'INVESTIGADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), 'COLABORADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'), 'COORDINADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 'TESISTA'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), 'INVESTIGADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'), 'COORDINADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 'TESISTA'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'), 'EVALUADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), 'RESPONSABLE'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'), 'INVESTIGADOR'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 'TESISTA'),
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '41795330'), 'COLABORADOR');

-- ============================================================================
-- 14. PLANES DE TESIS (5)
-- ============================================================================

INSERT INTO planes_tesis (titulo_tesis, resumen, id_estudiante, id_linea, id_grupo, id_documento_actual, estado_plan) VALUES
    ('Sistema de Recomendación de Rutas Inteligentes',
     'Desarrollo de un sistema de recomendación de rutas óptimas utilizando algoritmos genéticos para Tingo María.',
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
     'Diseño de un sistema de bajo costo utilizando sensores IoT para el monitoreo de cultivos en Huánuco.',
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 5, 7, NULL, 'RECHAZADO'),

    ('Análisis de Seguridad en Redes IoT para el Campus UNAS',
     'Evaluación de vulnerabilidades y propuesta de arquitectura de seguridad para redes IoT en campus universitario.',
     (SELECT id_usuario FROM usuarios WHERE dni = '40123457'), 3, 2, NULL, 'POSTULADO');

-- ============================================================================
-- 15. INFORMES DE TESIS (5)
-- ============================================================================

INSERT INTO informes_tesis (id_plan_tesis, titulo_final, id_documento_tesis, estado_informe) VALUES
    ((SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Rutas Inteligentes%'),
     'Sistema de Recomendación de Rutas Inteligentes usando Algoritmos Genéticos',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Informe_Tesis_Final_Recomendacion.pdf'),
     'APROBADO'),

    ((SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Machine Learning%'),
     'Clasificador por Redes Neuronales para Diagnóstico de Enfermedades Tropicales',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Informe_Tesis_Final_ML_Diagnostico.pdf'),
     'OBSERVADO'),

    ((SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Bibliotecas%'),
     'Plataforma Web PWA para Gestión de Bibliotecas Universitarias',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Informe_Tesis_Final_Bibliotecas.pdf'),
     'APROBADO'),

    ((SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%IoT para Monitoreo%'),
     'Red de Sensores IoT de Bajo Costo para Monitoreo Agrícola',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Informe_Tesis_Final_IoT_Agricola.pdf'),
     'EN_REVISION'),

    ((SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Seguridad en Redes IoT%'),
     'Análisis de Vulnerabilidades y Arquitectura de Seguridad para Redes IoT',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Informe_Tesis_Final_Red_IoT.pdf'),
     'EN_REVISION');

-- ============================================================================
-- 16. INFORMES DE AVANCE (5)
-- ============================================================================

INSERT INTO informes_avance (id_proyecto, tipo_informe, periodo, porcentaje_avance, logros, dificultades, recomendaciones, id_documento_adjunto, estado_informe) VALUES
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     'PARCIAL', 'Enero - Junio 2026', 60.00,
     'Módulo de autenticación completado. Módulo de usuarios al 80%. Integración OAuth2 finalizada.',
     'Retraso en integración con sistema de correo. Curva de aprendizaje en Clean Architecture.',
     'Continuar capacitación en Clean Architecture. Asignar más recursos al módulo de trámites.',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Informe_Avance_Parcial_SGI.pdf'),
     'APROBADO'),

    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     'FINAL', 'Julio - Diciembre 2026', 100.00,
     'Todos los módulos completados. Pruebas con cobertura >80%. Despliegue exitoso.',
     'Problemas de rendimiento en consultas JPA con múltiples joins.',
     'Realizar pruebas de carga con k6. Documentar guía de despliegue.',
     NULL, 'EN_REVISION'),

    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
     'PARCIAL', 'Febrero - Julio 2026', 30.00,
     'Recolecta de datos de plagas completada. Base de conocimiento parcialmente construida.',
     'Dificultad para obtener datos históricos de plagas de fuentes oficiales.',
     'Contactar SENASA para acceder a sus bases de datos fitosanitarias.',
     NULL, 'PENDIENTE'),

    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
     'FINAL', 'Mayo - Octubre 2026', 100.00,
     'Estudio de factibilidad completado. Documento técnico y económico presentado.',
     'Limitaciones de acceso a datos de cobertura 5G en zonas rurales.',
     'Presentar resultados al Comité de Infraestructura de la UNAS.',
     NULL, 'APROBADO'),

    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
     'PARCIAL', 'Abril - Septiembre 2025', 75.00,
     'Modelo predictivo entrenado con accuracy del 82%. Dataset de 5000 registros procesado.',
     'Sesgo en los datos de某些 facultades. Falta de datos de estudiantes nuevos.',
     'Ampliar la muestra incluyendo datos de los últimos 3 años.',
     NULL, 'EN_REVISION');

-- ============================================================================
-- 17. TRÁMITES (13 — uno por cada estado + extras)
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
    (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Machine Learning%'),
    '2026-02-01 09:00:00', '2026-02-10 14:00:00');

-- T3: PENDIENTE_COORDINADOR (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-003', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 2,
    'PENDIENTE_COORDINADOR', 'COORDINADOR_GRUPO',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-001'),
    '2026-03-01 10:30:00', '2026-03-01 10:30:00');

-- T4: Rechazado → RECHAZADO (PLAN_TESIS)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-004', 'PLAN_TESIS',
    (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 7,
    'RECHAZADO', NULL,
    (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%IoT para Monitoreo%'),
    '2026-02-15 11:00:00', '2026-03-01 09:00:00');

-- T5: PENDIENTE_DIRECCION (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-005', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 2,
    'PENDIENTE_DIRECCION', 'DIRECTOR_INVESTIGACION',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
    '2026-03-10 08:00:00', '2026-03-20 15:00:00');

-- T6: PENDIENTE_DECANATO (PLAN_TESIS)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-006', 'PLAN_TESIS',
    (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), 1,
    'PENDIENTE_DECANATO', 'DECANO',
    (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Bibliotecas%'),
    '2026-03-05 09:00:00', '2026-04-01 11:00:00');

-- T7: APROBADO_CON_RESOLUCION (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-007', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 1,
    'APROBADO_CON_RESOLUCION', 'DECANO',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
    '2026-01-15 08:00:00', '2026-04-28 16:00:00');

-- T8: SUBSANADO (PROYECTO)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-008', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '42865974'), 1,
    'SUBSANADO', 'COORDINADOR_GRUPO',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-002'),
    '2026-03-10 08:00:00', '2026-04-05 10:00:00');

-- T9: REGISTRADO (INFORME_AVANCE)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_informe, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-009', 'INFORME_AVANCE',
    (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 1,
    'REGISTRADO', 'COORDINADOR_GRUPO',
    (SELECT id_informe FROM informes_avance WHERE tipo_informe = 'FINAL' AND estado_informe = 'EN_REVISION'),
    '2026-06-01 07:00:00', '2026-06-01 07:00:00');

-- T10: APROBADO_CON_RESOLUCION (PROYECTO - 5G)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-010', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '50123456'), 2,
    'APROBADO_CON_RESOLUCION', 'DECANO',
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
    '2026-03-15 09:00:00', '2026-05-10 14:00:00');

-- T11: APROBADO_CON_RESOLUCION (PLAN_TESIS - Bibliotecas)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-011', 'PLAN_TESIS',
    (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), 1,
    'APROBADO_CON_RESOLUCION', 'DECANO',
    (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Bibliotecas%'),
    '2026-03-05 09:00:00', '2026-05-15 10:00:00');

-- T12: PENDIENTE_COORDINADOR (INFORME_AVANCE - plagas)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_informe, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-012', 'INFORME_AVANCE',
    (SELECT id_usuario FROM usuarios WHERE dni = '43198234'), 1,
    'PENDIENTE_COORDINADOR', 'COORDINADOR_GRUPO',
    (SELECT id_informe FROM informes_avance WHERE tipo_informe = 'PARCIAL' AND porcentaje_avance = 30.00),
    '2026-07-01 08:00:00', '2026-07-01 08:00:00');

-- T13: FINALIZADO (PROYECTO - Rendimiento)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto, fecha_envio, fecha_actualizacion)
VALUES ('TRM-2026-013', 'PROYECTO',
    (SELECT id_usuario FROM usuarios WHERE dni = '49123789'), 3,
    'FINALIZADO', NULL,
    (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-006'),
    '2025-04-10 08:00:00', '2026-04-15 16:00:00');

-- ============================================================================
-- 18. MOVIMIENTOS DE TRAZABILIDAD (35)
-- ============================================================================

INSERT INTO movimientos_tramite (id_tramite, id_usuario_accion, accion, estado_anterior, estado_nuevo, observacion, fecha_movimiento) VALUES
    -- T1: Flujo completo PROYECTO → FINALIZADO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-01-15 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto cumple requisitos formales.', '2026-02-01 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Proyecto viable y alineado a objetivos.', '2026-03-01 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324'),
     'RESOLUCION_REGISTRADA', 'PENDIENTE_DECANATO', 'APROBADO_CON_RESOLUCION', 'Resolución RES-001-2026 emitida.', '2026-04-28 16:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324'),
     'TRAMITE_FINALIZADO', 'APROBADO_CON_RESOLUCION', 'FINALIZADO', 'Trámite culminado.', '2026-04-30 16:30:00'),

    -- T2: Observado PLAN_TESIS
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-02-01 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'OBSERVADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'OBSERVADO', 'Marco teórico sin referencias actualizadas.', '2026-02-10 14:00:00'),

    -- T3: Recién presentado PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-003'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-01 10:30:00'),

    -- T4: Rechazado PLAN_TESIS
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-02-15 11:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Plan cumple estructura requerida.', '2026-02-20 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567'),
     'RECHAZADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'RECHAZADO', 'Tema no alineado a líneas prioritarias.', '2026-03-01 09:00:00'),

    -- T5: PENDIENTE_DIRECCION PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-10 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto bien formulado.', '2026-03-20 15:00:00'),

    -- T6: PENDIENTE_DECANATO PLAN_TESIS
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-05 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Plan completo y bien estructurado.', '2026-03-15 14:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-006'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Metodología adecuada.', '2026-04-01 11:00:00'),

    -- T7: APROBADO_CON_RESOLUCION PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-01-15 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto aprobado por coordinador.', '2026-02-01 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Proyecto aprobado por dirección.', '2026-03-01 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956324'),
     'RESOLUCION_REGISTRADA', 'PENDIENTE_DECANATO', 'APROBADO_CON_RESOLUCION', 'Resolución RES-002 emitida.', '2026-04-28 16:00:00'),

    -- T8: Observado y subsanado PROYECTO
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-10 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto aprobado.', '2026-03-20 15:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'OBSERVADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'OBSERVADO', 'Presupuesto no detallado por partidas.', '2026-03-25 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'SUBSANADO_POR_SOLICITANTE', 'OBSERVADO', 'SUBSANADO', 'Desglose de presupuesto adjuntado.', '2026-04-05 10:00:00'),

    -- T10: APROBADO_CON_RESOLUCION 5G
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-010'),
     (SELECT id_usuario FROM usuarios WHERE dni = '50123456'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2026-03-15 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-010'),
     (SELECT id_usuario FROM usuarios WHERE dni = '33512689'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Estudio bien fundamentado.', '2026-04-01 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-010'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Factibilidad confirmada.', '2026-04-20 11:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-010'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956325'),
     'RESOLUCION_REGISTRADA', 'PENDIENTE_DECANATO', 'APROBADO_CON_RESOLUCION', 'Resolución RES-003 emitida.', '2026-05-10 14:00:00'),

    -- T13: FINALIZADO Rendimiento
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-013'),
     (SELECT id_usuario FROM usuarios WHERE dni = '49123789'),
     'PRESENTADO_POR_SOLICITANTE', 'REGISTRADO', 'PENDIENTE_COORDINADOR', NULL, '2025-04-10 08:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-013'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'APROBADO_POR_COORDINADOR', 'PENDIENTE_COORDINADOR', 'PENDIENTE_DIRECCION', 'Proyecto relevante.', '2025-05-10 09:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-013'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'APROBADO_POR_DIRECTOR', 'PENDIENTE_DIRECCION', 'PENDIENTE_DECANATO', 'Aprobado por dirección.', '2025-06-01 10:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-013'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956326'),
     'RESOLUCION_REGISTRADA', 'PENDIENTE_DECANATO', 'APROBADO_CON_RESOLUCION', 'Resolución RES-005 emitida.', '2025-08-15 11:00:00'),
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-013'),
     (SELECT id_usuario FROM usuarios WHERE dni = '28956326'),
     'TRAMITE_FINALIZADO', 'APROBADO_CON_RESOLUCION', 'FINALIZADO', 'Proyecto completado.', '2026-04-15 16:00:00');

-- ============================================================================
-- 19. OBSERVACIONES (8)
-- ============================================================================

INSERT INTO observaciones (id_tramite, id_revisor, tipo_observacion, descripcion, estado_observacion, rol_revisor) VALUES
    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'DOCUMENTAL', 'Falta carta de compromiso del asesor firmada.', 'PENDIENTE', 'COORDINADOR_GRUPO'),

    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'PRESUPUESTAL', 'Presupuesto no detallado por partidas específicas.', 'SUBSANADA', 'DIRECTOR_INVESTIGACION'),

    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-002'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'FORMATO', 'Citas bibliográficas no cumplen APA 7ma edición.', 'VIGENTE', 'COORDINADOR_GRUPO'),

    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-004'),
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567'),
     'TECNICA', 'Metodología no describe claramente el diseño de investigación.', 'PENDIENTE', 'DIRECTOR_INVESTIGACION'),

    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-008'),
     (SELECT id_usuario FROM usuarios WHERE dni = '44567891'),
     'TECNICA', 'Análisis de riesgos no incluye escenarios de fallo.', 'SUBSANADA', 'COORDINADOR_GRUPO'),

    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_usuario FROM usuarios WHERE dni = '45286137'),
     'DOCUMENTAL', 'Falta ficha de postulación firmada por el responsable.', 'SUBSANADA', 'DIRECTOR_INVESTIGACION'),

    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-005'),
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567'),
     'FORMATO', 'Formato de presentación no ajustado a normas de la FIIS.', 'SUBSANADA', 'DIRECTOR_INVESTIGACION'),

    ((SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-003'),
     (SELECT id_usuario FROM usuarios WHERE dni = '33512689'),
     'PRESUPUESTAL', 'Presupuesto excede el tope permitido para la categoría.', 'SUBSANADA', 'COORDINADOR_GRUPO');

-- ============================================================================
-- 20. SUBSANACIONES (5)
-- ============================================================================

INSERT INTO subsanaciones (id_observacion, id_solicitante, descripcion, id_documento_adjunto) VALUES
    ((SELECT id_observacion FROM observaciones WHERE tipo_observacion = 'PRESUPUESTAL' AND estado_observacion = 'SUBSANADA' LIMIT 1),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'Desglose detallado: equipos S/12,000, materiales S/5,000, viajes S/3,000, personal S/15,000.',
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Carta_Compromiso_Asesor.docx')),

    ((SELECT id_observacion FROM observaciones WHERE tipo_observacion = 'TECNICA' AND estado_observacion = 'SUBSANADA' AND descripcion LIKE '%riesgos%'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'Se incluye análisis de riesgos con 5 escenarios de fallo y planes de mitigación.',
     NULL),

    ((SELECT id_observacion FROM observaciones WHERE tipo_observacion = 'DOCUMENTAL' AND estado_observacion = 'SUBSANADA' AND descripcion LIKE '%ficha%'),
     (SELECT id_usuario FROM usuarios WHERE dni = '42865974'),
     'Ficha de postulación firmada por el responsable y el decano adjuntada.',
     NULL),

    ((SELECT id_observacion FROM observaciones WHERE tipo_observacion = 'FORMATO' AND estado_observacion = 'SUBSANADA' AND descripcion LIKE '%normas%'),
     (SELECT id_usuario FROM usuarios WHERE dni = '40123456'),
     'Documento reformateado según normas APA 7ma edición y plantilla FIIS.',
     NULL),

    ((SELECT id_observacion FROM observaciones WHERE tipo_observacion = 'PRESUPUESTAL' AND estado_observacion = 'SUBSANADA' AND descripcion LIKE '%tope%'),
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     'Presupuesto ajustado a S/25,000 dentro del tope permitido para categoría B.',
     NULL);

-- ============================================================================
-- 21. EVALUACIONES (5)
-- ============================================================================

INSERT INTO evaluaciones (id_proyecto, id_plan_tesis, id_evaluador, resultado, puntaje, observaciones, fecha_asignacion, fecha_evaluacion) VALUES
    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'),
     NULL,
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     'APROBADO', 85,
     'Cumple todos los requisitos técnicos. Metodología adecuada y presupuesto justificado.',
     '2026-02-01 08:00:00', '2026-02-15 10:00:00'),

    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-004'),
     NULL,
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     'RECHAZADO', 45,
     'Deficiencias en planteamiento del problema y justificación. Presupuesto desalineado.',
     '2025-02-01 08:00:00', '2025-02-20 10:00:00'),

    (NULL,
     (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Bibliotecas%'),
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     'CON_OBSERVACIONES', 65,
     'Plan interesante pero requiere ajustes en marco teórico y metodología.',
     '2026-03-10 08:00:00', '2026-03-25 10:00:00'),

    (NULL,
     (SELECT id_plan_tesis FROM planes_tesis WHERE titulo_tesis LIKE '%Seguridad en Redes IoT%'),
     (SELECT id_usuario FROM usuarios WHERE dni = '37894562'),
     'CON_OBSERVACIONES', 70,
     'Tema relevante. Mejorar la revisión de literatura y definir métricas de evaluación.',
     '2026-06-15 08:00:00', '2026-06-30 10:00:00'),

    ((SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-003'),
     NULL,
     (SELECT id_usuario FROM usuarios WHERE dni = '37894562'),
     'APROBADO', 88,
     'Estudio de factibilidad sólido. Análisis técnico y económico bien sustentado.',
     '2026-06-01 08:00:00', '2026-06-20 10:00:00');

-- ============================================================================
-- 22. RESOLUCIONES (5)
-- ============================================================================

INSERT INTO resoluciones (numero_resolucion, fecha_emision, asunto, id_tramite, id_documento_adjunto) VALUES
    ('RES-001-2026-FIIS-UNAS', '2026-04-28',
     'Aprobar el Proyecto "Sistema de Gestión de Investigación FIIS" presentado por H. Rojas y L. Fernández.',
     (SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-007'),
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Resolucion_Decanal_001_2026.pdf')),

    ('RES-002-2026-FIIS-UNAS', '2026-04-28',
     'Aprobar el Proyecto "Sistema de Gestión de Investigación FIIS" en etapa de ejecución.',
     (SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-001'),
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Resolucion_Decanal_002_2026.pdf')),

    ('RES-003-2026-FIIS-UNAS', '2026-05-10',
     'Aprobar el Estudio de Factibilidad "Red 5G para la UNAS" presentado por P. Tello.',
     (SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-010'),
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Resolucion_Decanal_003_2026.pdf')),

    ('RES-004-2026-FIIS-UNAS', '2026-05-15',
     'Aprobar el Plan de Tesis "Plataforma Web de Gestión de Bibliotecas" de J. Ramírez.',
     (SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-011'),
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Resolucion_Decanal_004_2026.pdf')),

    ('RES-005-2025-FIIS-UNAS', '2025-08-15',
     'Aprobar el Proyecto "Análisis de Datos de Rendimiento Académico" de R. Quispe.',
     (SELECT id_tramite FROM tramites WHERE codigo_tramite = 'TRM-2026-013'),
     (SELECT id_documento FROM documentos WHERE nombre_original = 'Resolucion_Decanal_005_2026.pdf'));

-- ============================================================================
-- 23. AUDITORÍA GENERAL (5)
-- ============================================================================

INSERT INTO auditoria_general (tabla_afectada, id_registro, accion, id_usuario, datos_anteriores, datos_nuevos, ip_origen) VALUES
    ('usuarios', 1, 'LOGIN', (SELECT id_usuario FROM usuarios WHERE dni = '00000001'),
     NULL, '{"sesion": "activa"}', '192.168.1.100'),
    ('proyectos', (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2026-005'), 'CREAR',
     (SELECT id_usuario FROM usuarios WHERE dni = '43198234'),
     NULL, '{"codigo": "PROY-2026-005", "estado": "POSTULADO"}', '192.168.1.50'),
    ('convocatorias', (SELECT id_convocatoria FROM convocatorias WHERE titulo_convocatoria LIKE '%Proyectos 2025-I%'), 'EDITAR',
     (SELECT id_usuario FROM usuarios WHERE dni = '46234567'),
     '{"estado": "ABIERTA"}', '{"estado": "FINALIZADA"}', '192.168.1.75'),
    ('proyectos', (SELECT id_proyecto FROM proyectos WHERE codigo_proyecto = 'PROY-2025-004'), 'EDITAR',
     (SELECT id_usuario FROM usuarios WHERE dni = '48903456'),
     '{"estado": "POSTULADO"}', '{"estado": "RECHAZADO"}', '192.168.1.60'),
    ('usuarios', (SELECT id_usuario FROM usuarios WHERE dni = '40123456'), 'CREAR',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'),
     NULL, '{"dni": "40123456", "rol": "ESTUDIANTE"}', '192.168.1.100');

-- ============================================================================
-- 24. ACTUALIZAR SECUENCIAS SERIAL
-- ============================================================================

SELECT setval('roles_id_rol_seq', (SELECT COALESCE(MAX(id_rol), 7) FROM roles));
SELECT setval('usuarios_id_usuario_seq', (SELECT COALESCE(MAX(id_usuario), 35) FROM usuarios));
SELECT setval('grupos_investigacion_id_grupo_seq', (SELECT COALESCE(MAX(id_grupo), 7) FROM grupos_investigacion));
SELECT setval('lineas_investigacion_id_linea_seq', (SELECT COALESCE(MAX(id_linea), 6) FROM lineas_investigacion));
SELECT setval('documentos_id_documento_seq', (SELECT COALESCE(MAX(id_documento), 15) FROM documentos));
SELECT setval('convocatorias_id_convocatoria_seq', (SELECT COALESCE(MAX(id_convocatoria), 6) FROM convocatorias));
SELECT setval('proyectos_id_proyecto_seq', (SELECT COALESCE(MAX(id_proyecto), 6) FROM proyectos));
SELECT setval('miembros_proyecto_id_miembro_seq', (SELECT COALESCE(MAX(id_miembro), 16) FROM miembros_proyecto));
SELECT setval('planes_tesis_id_plan_tesis_seq', (SELECT COALESCE(MAX(id_plan_tesis), 5) FROM planes_tesis));
SELECT setval('informes_tesis_id_informe_tesis_seq', (SELECT COALESCE(MAX(id_informe_tesis), 5) FROM informes_tesis));
SELECT setval('informes_avance_id_informe_seq', (SELECT COALESCE(MAX(id_informe), 5) FROM informes_avance));
SELECT setval('tramites_id_tramite_seq', (SELECT COALESCE(MAX(id_tramite), 13) FROM tramites));
SELECT setval('movimientos_tramite_id_movimiento_seq', (SELECT COALESCE(MAX(id_movimiento), 35) FROM movimientos_tramite));
SELECT setval('observaciones_id_observacion_seq', (SELECT COALESCE(MAX(id_observacion), 8) FROM observaciones));
SELECT setval('subsanaciones_id_subsanacion_seq', (SELECT COALESCE(MAX(id_subsanacion), 5) FROM subsanaciones));
SELECT setval('evaluaciones_id_evaluacion_seq', (SELECT COALESCE(MAX(id_evaluacion), 5) FROM evaluaciones));
SELECT setval('resoluciones_id_resolucion_seq', (SELECT COALESCE(MAX(id_resolucion), 5) FROM resoluciones));
SELECT setval('auditoria_general_id_auditoria_seq', (SELECT COALESCE(MAX(id_auditoria), 5) FROM auditoria_general));

-- ============================================================================
-- 25. VERIFICACIÓN DE CONSISTENCIA
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

    -- Verificar al menos 5 usuarios por rol
    IF (SELECT COUNT(*) FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')) < 5 THEN
        v_errores := v_errores || 'ADMIN < 5 usuarios. ';
    END IF;
    IF (SELECT COUNT(*) FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')) < 5 THEN
        v_errores := v_errores || 'ESTUDIANTE < 5 usuarios. ';
    END IF;
    IF (SELECT COUNT(*) FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')) < 5 THEN
        v_errores := v_errores || 'DOCENTE < 5 usuarios. ';
    END IF;
    IF (SELECT COUNT(*) FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')) < 5 THEN
        v_errores := v_errores || 'COORDINADOR < 5 usuarios. ';
    END IF;
    IF (SELECT COUNT(*) FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')) < 5 THEN
        v_errores := v_errores || 'DIRECTOR < 5 usuarios. ';
    END IF;
    IF (SELECT COUNT(*) FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')) < 5 THEN
        v_errores := v_errores || 'DECANO < 5 usuarios. ';
    END IF;
    IF (SELECT COUNT(*) FROM usuarios WHERE id_rol_principal = (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')) < 5 THEN
        v_errores := v_errores || 'EVALUADOR < 5 usuarios. ';
    END IF;

    -- Verificar 5+ registros en tablas principales
    IF (SELECT COUNT(*) FROM convocatorias) < 5 THEN
        v_errores := v_errores || 'convocatorias < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM documentos) < 5 THEN
        v_errores := v_errores || 'documentos < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM proyectos) < 5 THEN
        v_errores := v_errores || 'proyectos < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM planes_tesis) < 5 THEN
        v_errores := v_errores || 'planes_tesis < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM informes_tesis) < 5 THEN
        v_errores := v_errores || 'informes_tesis < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM informes_avance) < 5 THEN
        v_errores := v_errores || 'informes_avance < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM tramites) < 5 THEN
        v_errores := v_errores || 'tramites < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM observaciones) < 5 THEN
        v_errores := v_errores || 'observaciones < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM subsanaciones) < 5 THEN
        v_errores := v_errores || 'subsanaciones < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM evaluaciones) < 5 THEN
        v_errores := v_errores || 'evaluaciones < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM resoluciones) < 5 THEN
        v_errores := v_errores || 'resoluciones < 5. ';
    END IF;
    IF (SELECT COUNT(*) FROM auditoria_general) < 5 THEN
        v_errores := v_errores || 'auditoria < 5. ';
    END IF;

    IF v_errores = '' THEN
        RAISE NOTICE 'VERIFICACION EXITOSA: % usuarios, % tramites, % movimientos. Todas las tablas >= 5 registros.', v_total_usuarios, v_total_tramites, v_total_movimientos;
    ELSE
        RAISE WARNING 'VERIFICACION INCOMPLETA: %', v_errores;
    END IF;
END $$;

-- ============================================================================
-- RESUMEN DE DATOS INSERTADOS
-- ============================================================================
--   + 35 usuarios     (5 por cada uno de los 7 roles)
--   + 5 multi-rol     + 12 membresías    + 9 asociaciones línea-grupo
--   + 6 convocatorias  + 6 conv_lineas    + 15 documentos
--   + 6 proyectos      + 16 integrantes   + 5 planes tesis
--   + 5 informes tesis + 5 informes avance + 13 trámites
--   + 35 movimientos   + 8 observaciones  + 5 subsanaciones
--   + 5 evaluaciones   + 5 resoluciones   + 5 auditorías
-- ============================================================================
