-- ============================================================
-- V1: Creación de tablas de Seguridad (Roles y Usuarios)
-- Sistema de Gestión de Investigación FIIS
-- ============================================================

-- Tabla de roles del sistema
CREATE TABLE roles (
    id_rol       BIGSERIAL    PRIMARY KEY,
    codigo_rol   VARCHAR(50)  NOT NULL UNIQUE,
    descripcion  VARCHAR(255)
);

-- Datos iniciales de roles (RF-09)
INSERT INTO roles (codigo_rol, descripcion) VALUES
    ('ADMIN',                  'Administrador del sistema'),
    ('ESTUDIANTE',             'Estudiante / Tesista'),
    ('DOCENTE_INVESTIGADOR',   'Docente Investigador'),
    ('COORDINADOR_GRUPO',      'Coordinador de Grupo de Investigación'),
    ('DIRECTOR_INVESTIGACION', 'Director de Investigación'),
    ('DECANO',                 'Decano de la Facultad'),
    ('EVALUADOR',              'Evaluador de proyectos');

-- Tabla de usuarios del sistema
CREATE TABLE usuarios (
    id_usuario            BIGSERIAL     PRIMARY KEY,
    dni                   VARCHAR(8)    NOT NULL UNIQUE,
    nombres               VARCHAR(100)  NOT NULL,
    apellidos             VARCHAR(100)  NOT NULL,
    correo_institucional  VARCHAR(150)  NOT NULL UNIQUE,
    telefono              VARCHAR(20),
    password_hash         VARCHAR(255)  NOT NULL,
    es_activo             BOOLEAN       NOT NULL DEFAULT TRUE,
    must_change_password  BOOLEAN       NOT NULL DEFAULT TRUE,
    id_rol_principal      BIGINT        NOT NULL REFERENCES roles(id_rol),
    fecha_creacion        TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_actualizacion   TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_usuarios_rol ON usuarios(id_rol_principal);
CREATE INDEX idx_usuarios_activo ON usuarios(es_activo);
CREATE INDEX idx_usuarios_dni ON usuarios(dni);
CREATE INDEX idx_usuarios_correo ON usuarios(correo_institucional);

-- Usuario administrador inicial
-- Contraseña: 00000000 (DNI por defecto, hasheada con BCrypt)
INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, es_activo, must_change_password, id_rol_principal)
VALUES (
    '00000000',
    'Admin',
    'Sistema',
    'admin@unas.edu.pe',
    '$2a$10$woQ5pX6m6t.KeVMOTNiZwuOJz6wsR.oM30jwUQ2EdUwEpoEN66ZYO',
    TRUE,
    TRUE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')
);

