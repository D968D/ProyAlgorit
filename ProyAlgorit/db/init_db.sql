-- =========================================================
-- Script adaptado a PostgreSQL / pgAdmin
-- (el original del informe Avance 1 estaba en sintaxis de SQL Server:
--  IDENTITY, NVARCHAR, MONEY, DATETIME2, etc. Aqui esta el equivalente
--  en PostgreSQL para que corra tal cual en pgAdmin)
-- =========================================================

-- 1) Crea la base de datos (ejecuta esta linea sola, desde la base "postgres")
-- CREATE DATABASE cordonrosadb;

-- 2) Conectate a "cordonrosadb" en pgAdmin y luego ejecuta todo lo de abajo.

CREATE TABLE usuarios (
    idusuario   SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    username    VARCHAR(50) NOT NULL UNIQUE,
    "contraseña" VARCHAR(260) NOT NULL,  -- con eñe: en el codigo Java se escribe entre comillas dobles
    rol         VARCHAR(30)
);

CREATE TABLE platos (
    idplato     SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    precio      NUMERIC(10,2) NOT NULL,
    categoria   VARCHAR(50)
);

-- El nombre del plato no se puede repetir (sin distinguir mayusculas): es la clave del ABB.
CREATE UNIQUE INDEX ux_platos_nombre ON platos (lower(nombre));

CREATE TABLE mesas (
    numero_mesa INT PRIMARY KEY,
    capacidad   INT NOT NULL,
    estado      VARCHAR(20) DEFAULT 'LIBRE'
);

CREATE TABLE reservas (
    idreserva         SERIAL PRIMARY KEY,
    cliente           VARCHAR(100) NOT NULL,
    fecha             DATE NOT NULL,
    hora              TIME NOT NULL,
    cantidad_personas INT NOT NULL,
    numero_mesa       INT NOT NULL REFERENCES mesas(numero_mesa) ON DELETE CASCADE,
    estado            VARCHAR(20) DEFAULT 'Pendiente'  -- Pendiente / Atendida
);

CREATE TABLE pedidos (
    idpedido    SERIAL PRIMARY KEY,
    numero_mesa INT REFERENCES mesas(numero_mesa),
    idusuario   INT REFERENCES usuarios(idusuario),
    fecha_hora  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total       NUMERIC(10,2),
    estado      VARCHAR(20) DEFAULT 'PENDIENTE',
    metodo_pago VARCHAR(100)
);

CREATE TABLE detalle_pedido (
    iddetalle   SERIAL PRIMARY KEY,
    idpedido    INT REFERENCES pedidos(idpedido),
    idplato     INT REFERENCES platos(idplato),
    cantidad    INT NOT NULL,
    precio      NUMERIC(10,2) NOT NULL,
    observacion VARCHAR(250)
);

-- =========================================================
-- Datos de prueba (para que al ejecutar la aplicacion ya
-- aparezca algo en la tabla, igual que en tu imagen de ejemplo)
-- =========================================================

-- Las contraseñas se guardan con hash PBKDF2 (clase util.PasswordUtil). Ambos usuarios entran con: 123456
INSERT INTO usuarios (nombre, username, "contraseña", rol) VALUES
('Administrador', 'admin', 'pbkdf2$4VQvQ06IpWd9mdq/J1LVjg==$SRXDC6i3/Tm2FEZ7tu5AqTVBng4CsjjAjoTAIVU9Gjg=', 'ADMINISTRADOR'),
('Michelle', 'michelle', 'pbkdf2$m8HsEyWs+FFaVdzMLhr5OA==$EPzSS0SzWZ0Nv1ky8eNSACCSzlU0vWDSQ7Q+QU/+20s=', 'ESTANDAR');

INSERT INTO mesas (numero_mesa, capacidad, estado) VALUES
(1, 4, 'LIBRE'),
(2, 5, 'OCUPADO'),
(3, 4, 'LIBRE'),
(4, 6, 'LIBRE'),
(5, 2, 'OCUPADO');

INSERT INTO platos (nombre, precio, categoria) VALUES
('Lomo Saltado', 28.00, 'Fondos'),
('Ceviche Mixto', 32.00, 'Entradas'),
('Chicha Morada', 8.00, 'Bebidas');

INSERT INTO reservas (cliente, fecha, hora, cantidad_personas, numero_mesa, estado) VALUES
('Carlos Ramos',  '2026-10-15', '19:30', 4, 1, 'Pendiente'),
('Lucía Torres',  '2026-10-15', '20:00', 2, 5, 'Pendiente'),
('Familia Quispe','2026-10-16', '13:00', 6, 4, 'Pendiente'),
('Marco Salas',   '2026-10-14', '21:00', 3, 3, 'Atendida');
