-- ============================================================
-- V14: Inserción de usuario de prueba Nilver Carhuapoma
-- ============================================================

INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, password_hash, es_activo, must_change_password, id_rol_principal)
VALUES (
    '88888888',
    'Nilver',
    'Carhuapoma',
    'nilver.carhuapoma@unas.edu.pe',
    '$2a$10$MI7cngNWraLumq944mNjZuNfVCDx1PeP/Q9qpQovUjjy8.Ige6SOm',
    TRUE,
    FALSE,
    (SELECT id_rol FROM roles WHERE codigo_rol = 'ESTUDIANTE')
);
