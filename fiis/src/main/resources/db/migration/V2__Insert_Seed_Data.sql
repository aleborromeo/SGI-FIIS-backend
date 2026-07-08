-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- DATABASE MIGRATION V2 - SEED DATA
-- ============================================================================

-- 1. Roles del Sistema
INSERT INTO roles (codigo_rol, descripcion) VALUES
    ('ADMIN', 'Administrador del sistema'),
    ('ESTUDIANTE', 'Estudiante / Tesista'),
    ('DOCENTE_INVESTIGADOR', 'Docente Investigador'),
    ('COORDINADOR_GRUPO', 'Coordinador de Grupo de Investigación'),
    ('DIRECTOR_INVESTIGACION', 'Director de Investigación'),
    ('DECANO', 'Decano de la Facultad'),
    ('EVALUADOR', 'Evaluador de proyectos');

-- 2. Grupos de Investigación
INSERT INTO grupos_investigacion (codigo_grupo, nombre_grupo, es_activo) VALUES
    ('GINSOFT', 'Grupo de Investigacion en Ingenieria de Software', true),
    ('RESEGTI', 'Red de Seguridad y Gestion de TI', true),
    ('GISI', 'Grupo de Investigacion en Sistemas de Informacion', true),
    ('CICO', 'Circulo de Computacion', true),
    ('EAP', 'Estadistica Aplicada', true),
    ('MAP', 'Matematica Aplicada', true),
    ('EU', 'Emprendimiento Universitario', true);

-- 3. Líneas de Investigación
INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
    ('Computacion', true),
    ('Ingenieria de software', true),
    ('Ciberseguridad y Auditoria de TI', true),
    ('Ciencia de Datos e Inteligencia Artificial', true),
    ('Redes y Telecomunicaciones', true),
    ('Gestion de Tecnologias de Informacion', true);

-- 4. Usuarios del Sistema
-- Contraseña por defecto: 00000000 (hasheada con BCrypt)
-- Hash: $2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, es_activo, must_change_password, id_rol_principal) VALUES
    ('00000000', 'Admin', 'Sistema', 'admin@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', true, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),
    ('11111111', 'Juan', 'Perez', 'jose.evaristo@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', true, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')),
    ('22222222', 'Maria', 'Gomez', 'maria.gomez@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', true, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
    ('33333333', 'Carlos', 'Ramos', 'carlos.ramos@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', true, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')),
    ('44444444', 'Ana', 'Torres', 'ana.torres@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', true, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
    ('55555555', 'Luis', 'Mendoza', 'luis.mendoza@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', true, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')),
    ('66666666', 'Jorge', 'Castro', 'jorge.castro@unas.edu.pe', '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO', true, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR'));

-- 5. Asignar coordinador a grupo GINSOFT
UPDATE grupos_investigacion
SET id_coordinador_actual = (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'carlos.ramos@unas.edu.pe')
WHERE codigo_grupo = 'GINSOFT';

-- 6. Membresías activas
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo, fecha_inicio) VALUES
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'maria.gomez@unas.edu.pe'), true, CURRENT_TIMESTAMP),
    ((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'),
     (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'carlos.ramos@unas.edu.pe'), true, CURRENT_TIMESTAMP);
