-- ============================================================================
-- V37: Sembrar 10 convocatorias activas (ABIERTA) asociadas a sus líneas
-- ============================================================================

INSERT INTO convocatorias (titulo_convocatoria, descripcion, titulo_jsonb, descripcion_jsonb, fecha_inicio, fecha_fin, estado, id_creador, poblacion_objetivo) VALUES
    ('Convocatoria de Proyectos Multidisciplinarios 2026-II',
     'Convocatoria para proyectos de investigación multidisciplinarios integrando tecnologías de la información con otras disciplinas científicas.',
     '{"es": "Convocatoria de Proyectos Multidisciplinarios 2026-II"}',
     '{"es": "Convocatoria para proyectos de investigación multidisciplinarios integrando tecnologías de la información con otras disciplinas científicas."}',
     '2026-07-01', '2026-12-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'AMBOS'),

    ('Convocatoria de Equipamiento Científico para Laboratorios 2026',
     'Convocatoria para la adquisición y actualización de equipamiento tecnológico e investigación en los laboratorios de la FIIS.',
     '{"es": "Convocatoria de Equipamiento Científico para Laboratorios 2026"}',
     '{"es": "Convocatoria para la adquisición y actualización de equipamiento tecnológico e investigación en los laboratorios de la FIIS."}',
     '2026-05-01', '2026-12-15', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'DOCENTES'),

    ('Convocatoria de Tesis de Pregrado FIIS 2026-II',
     'Apoyo y financiamiento para el desarrollo de tesis de pregrado en Computación y Sistemas de Información.',
     '{"es": "Convocatoria de Tesis de Pregrado FIIS 2026-II"}',
     '{"es": "Apoyo y financiamiento para el desarrollo de tesis de pregrado en Computación y Sistemas de Información."}',
     '2026-07-01', '2026-11-30', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'ESTUDIANTES'),

    ('Convocatoria de Publicación de Artículos Indizados 2026',
     'Fomento a la publicación de resultados de investigación en revistas indizadas en Scopus y Web of Science.',
     '{"es": "Convocatoria de Publicación de Artículos Indizados 2026"}',
     '{"es": "Fomento a la publicación de resultados de investigación en revistas indizadas en Scopus y Web of Science."}',
     '2026-01-01', '2026-12-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'DOCENTES'),

    ('Convocatoria de Proyectos de Desarrollo e Impacto Social 2026',
     'Convocatoria de investigación aplicada orientada a resolver problemas críticos de la región Huánuco.',
     '{"es": "Convocatoria de Proyectos de Desarrollo e Impacto Social 2026"}',
     '{"es": "Convocatoria de investigación aplicada orientada a resolver problemas críticos de la región Huánuco."}',
     '2026-06-01', '2026-12-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'AMBOS'),

    ('Convocatoria de Tesis de Posgrado en TI 2026',
     'Convocatoria dirigida a maestrandos y doctorandos para financiamiento y publicación de sus trabajos de investigación en TI.',
     '{"es": "Convocatoria de Tesis de Posgrado en TI 2026"}',
     '{"es": "Convocatoria dirigida a maestrandos y doctorandos para financiamiento y publicación de sus trabajos de investigación en TI."}',
     '2026-04-01', '2026-12-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'ESTUDIANTES'),

    ('Convocatoria de Semilleros de Investigación FIIS 2026',
     'Fomento y creación de grupos formativos de investigación con participación activa de alumnos de ciclos iniciales.',
     '{"es": "Convocatoria de Semilleros de Investigación FIIS 2026"}',
     '{"es": "Fomento y creación de grupos formativos de investigación con participación activa de alumnos de ciclos iniciales."}',
     '2026-03-01', '2027-03-01', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'ESTUDIANTES'),

    ('Convocatoria de Proyectos de Innovación Docente en Ingeniería 2026',
     'Desarrollo de nuevas metodologías de aprendizaje activo y herramientas didácticas aplicadas a la ingeniería.',
     '{"es": "Convocatoria de Proyectos de Innovación Docente en Ingeniería 2026"}',
     '{"es": "Desarrollo de nuevas metodologías de aprendizaje activo y herramientas didácticas aplicadas a la ingeniería."}',
     '2026-05-01', '2026-12-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'DOCENTES'),

    ('Convocatoria de Fomento a Patentes y Propiedad Intelectual 2026',
     'Asesoramiento y soporte financiero para el registro de patentes de invención y modelos de utilidad ante INDECOPI.',
     '{"es": "Convocatoria de Fomento a Patentes y Propiedad Intelectual 2026"}',
     '{"es": "Asesoramiento y soporte financiero para el registro de patentes de invención y modelos de utilidad ante INDECOPI."}',
     '2026-01-01', '2027-06-30', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'AMBOS'),

    ('Convocatoria de Trabajos de Investigación de Bachillerato 2026',
     'Promoción de la obtención del grado de Bachiller mediante trabajos de investigación aplicada en líneas de la facultad.',
     '{"es": "Convocatoria de Trabajos de Investigación de Bachillerato 2026"}',
     '{"es": "Promoción de la obtención del grado de Bachiller mediante trabajos de investigación aplicada en líneas de la facultad."}',
     '2026-07-01', '2026-10-31', 'ABIERTA',
     (SELECT id_usuario FROM usuarios WHERE dni = '00000001'), 'ESTUDIANTES');

-- Asociaciones Convocatorias <-> Líneas de Investigación

-- 1. Convocatoria de Proyectos Multidisciplinarios 2026-II
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Proyectos Multidisciplinarios 2026-II'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software', 'Ciencia de Datos e Inteligencia Artificial');

-- 2. Convocatoria de Equipamiento Científico para Laboratorios 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Equipamiento Científico para Laboratorios 2026'
  AND l.nombre_linea IN ('Redes y Telecomunicaciones', 'Ciberseguridad y Auditoria de TI');

-- 3. Convocatoria de Tesis de Pregrado FIIS 2026-II
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Tesis de Pregrado FIIS 2026-II'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software', 'Gestion de Tecnologias de Informacion');

-- 4. Convocatoria de Publicación de Artículos Indizados 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Publicación de Artículos Indizados 2026'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial', 'Computacion', 'Redes y Telecomunicaciones');

-- 5. Convocatoria de Proyectos de Desarrollo e Impacto Social 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Proyectos de Desarrollo e Impacto Social 2026'
  AND l.nombre_linea IN ('Gestion de Tecnologias de Informacion', 'Ciberseguridad y Auditoria de TI');

-- 6. Convocatoria de Tesis de Posgrado en TI 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Tesis de Posgrado en TI 2026'
  AND l.nombre_linea IN ('Ciencia de Datos e Inteligencia Artificial', 'Ingenieria de software');

-- 7. Convocatoria de Semilleros de Investigación FIIS 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Semilleros de Investigación FIIS 2026'
  AND l.nombre_linea IN ('Computacion', 'Redes y Telecomunicaciones');

-- 8. Convocatoria de Proyectos de Innovación Docente en Ingeniería 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Proyectos de Innovación Docente en Ingeniería 2026'
  AND l.nombre_linea IN ('Ingenieria de software', 'Gestion de Tecnologias de Informacion');

-- 9. Convocatoria de Fomento a Patentes y Propiedad Intelectual 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Fomento a Patentes y Propiedad Intelectual 2026'
  AND l.nombre_linea IN ('Ciberseguridad y Auditoria de TI', 'Computacion');

-- 10. Convocatoria de Trabajos de Investigación de Bachillerato 2026
INSERT INTO convocatorias_lineas (id_convocatoria, id_linea)
SELECT c.id_convocatoria, l.id_linea
FROM convocatorias c, lineas_investigacion l
WHERE c.titulo_convocatoria = 'Convocatoria de Trabajos de Investigación de Bachillerato 2026'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software', 'Redes y Telecomunicaciones');
