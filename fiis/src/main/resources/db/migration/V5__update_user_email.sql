-- ============================================================
-- V5: Actualizar correo de usuario de pruebas a jose.evaristo
-- ============================================================

UPDATE usuarios 
SET correo_institucional = 'jose.evaristo@unas.edu.pe' 
WHERE correo_institucional = 'juan.perez@unas.edu.pe';
