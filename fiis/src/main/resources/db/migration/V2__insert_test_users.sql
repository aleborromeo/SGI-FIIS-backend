-- ============================================================
-- V2: Inserción de usuarios de prueba para cada rol
-- Sistema de Gestión de Investigación SGI-FIIS
-- ============================================================

-- Contraseña por defecto: 00000000 (hasheada con BCrypt)
-- Hash: $2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO

-- Estudiante / Tesista
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, activo, must_change_password, id_rol)
VALUES (
    '11111111',
    'Juan',
    'Perez',
    'juan.perez@unas.edu.pe',
    '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO',
    TRUE,
    TRUE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')
);

-- Docente Investigador
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, activo, must_change_password, id_rol)
VALUES (
    '22222222',
    'Maria',
    'Gomez',
    'maria.gomez@unas.edu.pe',
    '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO',
    TRUE,
    TRUE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')
);

-- Coordinador de Grupo de Investigación
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, activo, must_change_password, id_rol)
VALUES (
    '33333333',
    'Carlos',
    'Ramos',
    'carlos.ramos@unas.edu.pe',
    '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO',
    TRUE,
    TRUE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO')
);

-- Director de Investigación
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, activo, must_change_password, id_rol)
VALUES (
    '44444444',
    'Ana',
    'Torres',
    'ana.torres@unas.edu.pe',
    '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO',
    TRUE,
    TRUE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')
);

-- Decano de la Facultad
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, activo, must_change_password, id_rol)
VALUES (
    '55555555',
    'Luis',
    'Mendoza',
    'luis.mendoza@unas.edu.pe',
    '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO',
    TRUE,
    TRUE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'DECANO')
);

-- Evaluador de proyectos
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, activo, must_change_password, id_rol)
VALUES (
    '66666666',
    'Jorge',
    'Castro',
    'jorge.castro@unas.edu.pe',
    '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO',
    TRUE,
    TRUE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'EVALUADOR')
);
