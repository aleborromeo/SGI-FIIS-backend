-- V35: Renombrar columna 'estado' a 'estado_proyecto' en tabla proyectos
-- La columna fue creada como 'estado' en V1 pero el entity JPA espera 'estado_proyecto'
ALTER TABLE proyectos RENAME COLUMN estado TO estado_proyecto;
