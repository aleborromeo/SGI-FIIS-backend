-- ============================================================================
-- SGI-FIIS — Migración V2: Datos semilla consolidados
-- Fusiona: V2 (seed data) + V7 (grupos/lineas) + V11 (datos prueba)
-- ============================================================================

-- 1. Roles del Sistema (RF-09)
INSERT INTO roles (codigo_rol, descripcion) VALUES
    ('ADMIN', 'Administrador del Sistema'),
    ('ESTUDIANTE', 'Estudiante / Tesista'),
    ('DOCENTE_INVESTIGADOR', 'Docente Investigador'),
    ('COORDINADOR_GRUPO', 'Coordinador de Grupo de Investigación'),
    ('DIRECTOR_INVESTIGACION', 'Director de Investigación de la FIIS'),
    ('DECANO', 'Decano de la Facultad'),
    ('EVALUADOR', 'Evaluador por Pares Externo o Interno');

-- 2. Grupos de Investigación (RF-15)
INSERT INTO grupos_investigacion (codigo_grupo, nombre_grupo, es_activo) VALUES
    ('GINSOFT', 'Grupo de Investigación en Ingeniería de Software', TRUE),
    ('RESEGTI', 'Red de Seguridad y Gestión de TI', TRUE),
    ('GISI', 'Grupo de Investigación en Sistemas de Información', TRUE),
    ('CICO', 'Círculo de Computación', TRUE),
    ('EAP', 'Estadística Aplicada', TRUE),
    ('MAP', 'Matemática Aplicada', TRUE),
    ('EU', 'Emprendimiento Universitario', TRUE);

-- 3. Líneas de Investigación (RF-24)
INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
    ('Computacion', TRUE),
    ('Ingenieria de software', TRUE),
    ('Ciberseguridad y Auditoria de TI', TRUE),
    ('Ciencia de Datos e Inteligencia Artificial', TRUE),
    ('Redes y Telecomunicaciones', TRUE),
    ('Gestion de Tecnologias de Informacion', TRUE);

-- 4. Asociación de Líneas por Grupo - GINSOFT (RF-26, RN-12)
INSERT INTO lineas_por_grupo (id_grupo, id_linea)
SELECT g.id_grupo, l.id_linea
FROM grupos_investigacion g, lineas_investigacion l
WHERE g.codigo_grupo = 'GINSOFT'
  AND l.nombre_linea IN ('Computacion', 'Ingenieria de software');

-- 5. Usuarios del Sistema
-- Contraseña por defecto: 00000000 (hasheada con BCrypt)
-- Hash: $2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, es_activo, must_change_password, id_rol_principal) VALUES
    ('00000000', 'Admin', 'Sistema', 'admin@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE, (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),
    ('11111111', 'Juan', 'Perez', 'jose.evaristo@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE, (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('22222222', 'Maria', 'Gomez', 'maria.gomez@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE, (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('33333333', 'Carlos', 'Ramos', 'carlos.ramos@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE, (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),
    ('44444444', 'Ana', 'Torres', 'ana.torres@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE, (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
    ('55555555', 'Luis', 'Mendoza', 'luis.mendoza@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE, (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),
    ('66666666', 'Jorge', 'Castro', 'jorge.castro@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', TRUE, TRUE, (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR'));

-- 6. Asignar coordinador a grupo GINSOFT (RF-18)
UPDATE grupos_investigacion
SET id_coordinador_actual = (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'carlos.ramos@unas.edu.pe')
WHERE codigo_grupo = 'GINSOFT';

-- 7. Membresías activas (RF-19)
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo, fecha_inicio) VALUES
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'maria.gomez@unas.edu.pe'), TRUE, NOW()),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'carlos.ramos@unas.edu.pe'), TRUE, NOW());

-- 8. Proyecto de prueba (RF-35)
INSERT INTO proyectos (codigo_proyecto, titulo_proyecto, resumen, objetivo_general, id_linea, id_grupo, presupuesto, fecha_inicio, fecha_fin, lugar_ejecucion, id_responsable, estado)
VALUES ('PROY-2026-001', 'Sistema de Gestión FIIS', 'Resumen del proyecto', 'Objetivo del proyecto', 1, 1, 5000.00, '2026-01-01', '2026-12-31', 'Tingo María', 2, 'POSTULADO');

-- 9. Plan de tesis de prueba (RF-45)
INSERT INTO planes_tesis (titulo_tesis, resumen, id_estudiante, id_linea, id_grupo, estado_plan)
VALUES ('Tesis sobre IA en Educación', 'Resumen de tesis', 1, 4, 1, 'POSTULADO');

-- 10. Trámites de prueba (RF-54)
INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_proyecto)
VALUES ('TRM-2026-001', 'PROYECTO', 2, 1, 'EN_REVISION', 'COORDINADOR_GRUPO', 1);

INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual, rol_revisor_actual, id_referencia_tesis)
VALUES ('TRM-2026-002', 'PLAN_TESIS', 1, 1, 'EN_REVISION', 'COORDINADOR_GRUPO', 1);
