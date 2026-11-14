-- =========================================================
-- Parche para una base de datos YA CREADA (por ejemplo la de AWS).
-- Si empieza desde cero, use init_db.sql y no necesita este archivo.
-- =========================================================

-- 1) Revisar si hay platos con nombre repetido (debe devolver 0 filas):
SELECT lower(nombre) AS nombre, COUNT(*) AS veces
FROM platos
GROUP BY lower(nombre)
HAVING COUNT(*) > 1;

-- 2) Si el paso 1 devolvio filas, elimine o renombre los repetidos desde la aplicacion
--    y vuelva a ejecutar el paso 1. Cuando devuelva 0 filas, cree el indice unico:
CREATE UNIQUE INDEX IF NOT EXISTS ux_platos_nombre ON platos (lower (nombre));

-- Contrasenas: NO hace falta migrarlas a mano. Los usuarios que aun tengan la contrasena
-- en texto plano la ven convertida a hash automaticamente la primera vez que inician sesion.
