-- V35: Renombrar columna 'estado' a 'estado_proyecto' en tabla proyectos
-- La columna fue creada como 'estado' en V1 pero el entity JPA espera 'estado_proyecto'
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'proyectos' AND column_name = 'estado'
    ) THEN
        ALTER TABLE proyectos RENAME COLUMN estado TO estado_proyecto;
    END IF;
END $$;
