-- ============================================================================
-- SYSTEM OF RESEARCH MANAGEMENT FIIS (SGI-FIIS)
-- SEED DATA INSERTION
-- ============================================================================

-- 1. Roles del Sistema
INSERT INTO roles (codigo_rol, descripcion) VALUES
('ADMIN', 'System Administrator'),
('ESTUDIANTE', 'Student / Thesis Candidate'),
('DOCENTE_INVESTIGADOR', 'Research Teacher'),
('COORDINADOR_GRUPO', 'Research Group Coordinator'),
('DIRECTOR_INVESTIGACION', 'Director of Research (FIIS)'),
('DECANO', 'Dean of Faculty'),
('EVALUADOR', 'Peer Reviewer');

-- 2. Grupos de InvestigaciÃƒÂ³n
INSERT INTO grupos_investigacion (codigo_grupo, nombre_grupo, es_activo) VALUES
('GINSOFT', 'Grupo de Investigacion en Ingenieria de Software', true),
('RESEGTI', 'Red de Seguridad y Gestion de TI', true),
('GISI', 'Grupo de Investigacion en Sistemas de Informacion', true),
('CICO', 'Circulo de Computacion', true),
('EAP', 'Estadistica Aplicada', true),
('MAP', 'Matematica Aplicada', true),
('EU', 'Emprendimiento Universitario', true);

-- 3. LÃƒÂ­neas de InvestigaciÃƒÂ³n
INSERT INTO lineas_investigacion (nombre_linea, es_activa) VALUES
('Computacion', true),
('Ingenieria de software', true),
('Ciberseguridad y Auditoria de TI', true),
('Ciencia de Datos e Inteligencia Artificial', true),
('Redes y Telecomunicaciones', true),
('Gestion de Tecnologias
-- Todos los usuarios tienen el password "123456" hasheado con BCrypt:
-- Hash: $2a$10$X5t.u3l5J2K5h5Jk6yA1mef4QZtA80a/zP.G4c9/XW0X2d8L7rR6e

INSERT INTO usuarios (dni, nombres, apellidos, correo_institucional, telefono, password_hash, must_change_password, es_activo, id_rol_principal) VALUES
('11111111', 'Admin', 'SGI', 'admin.sgi@unas.edu.pe', '999888777', '$2a$10$X5t.u3l5J2K5h5Jk6yA1mef4QZtA80a/zP.G4c9/XW0X2d8L7rR6e', false, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'ADMIN')),
('22222222', 'Director', 'Research', 'director.inv@unas.edu.pe', '999888776', '$2a$10$X5t.u3l5J2K5h5Jk6yA1mef4QZtA80a/zP.G4c9/XW0X2d8L7rR6e', false, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'DIRECTOR_INVESTIGACION')),
('33333333', 'John', 'Doe', 'john.doe@unas.edu.pe', '999888775', '$2a$10$X5t.u3l5J2K5h5Jk6yA1mef4QZtA80a/zP.G4c9/XW0X2d8L7rR6e', false, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'DOCENTE_INVESTIGADOR')),
('44444444', 'Jane', 'Smith', 'jane.smith@unas.edu.pe', '999888774', '$2a$10$X5t.u3l5J2K5h5Jk6yA1mef4QZtA80a/zP.G4c9/XW0X2d8L7rR6e', false, true, (SELECT id_rol FROM roles WHERE codigo_rol = 'COORDINADOR_GRUPO'));

-- Asignar coordinadores a los grupos
UPDATE grupos_investigacion 
SET id_coordinador_actual = (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'jane.smith@unas.edu.pe') 
WHERE codigo_grupo = 'GINSOFT';

-- Registrar membresÃƒÂ­as activas para docentes
INSERT INTO membresias_grupo (id_grupo, id_usuario, es_activo, fecha_inicio) VALUES
((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'), (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'john.doe@unas.edu.pe'), true, CURRENT_TIMESTAMP),
((SELECT id_grupo FROM grupos_investigacion WHERE codigo_grupo = 'GINSOFT'), (SELECT id_usuario FROM usuarios WHERE correo_institucional = 'jane.smith@unas.edu.pe'), true, CURRENT_TIMESTAMP);
