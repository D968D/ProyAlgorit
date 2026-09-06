CREATE TABLE usuarios (
    idusuario   SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    username    VARCHAR(50) NOT NULL UNIQUE,
    contraseña  VARCHAR(260) NOT NULL,   
    rol         VARCHAR(30)
);

CREATE TABLE platos (
    idplato     SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    precio      NUMERIC(10,2) NOT NULL,
    categoria   VARCHAR(50)
);

CREATE TABLE mesas (
    numero_mesa INT PRIMARY KEY,
    capacidad   INT NOT NULL,
    estado      VARCHAR(20) DEFAULT 'LIBRE'
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



INSERT INTO usuarios (nombre, username, contraseña, rol) VALUES
('Administrador', 'admin', '123456', 'ADMINISTRADOR'),
('Michelle', 'michelle', '123456', 'CAMARERO(A)');

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
