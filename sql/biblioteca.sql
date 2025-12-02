DROP
DATABASE IF EXISTS biblioteca_db;
CREATE
DATABASE biblioteca_db;
USE
biblioteca_db;

CREATE TABLE usuarios
(
    id_usuario   INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100)        NOT NULL,
    tipo_usuario ENUM('Encargado', 'Profesor', 'Alumno') NOT NULL,
    correo       VARCHAR(100) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL
);


CREATE TABLE materiales
(
    id_material         INT AUTO_INCREMENT PRIMARY KEY,
    tipo_material       ENUM('Libro', 'Revista', 'Audiovisual', 'Otro') NOT NULL,
    titulo              VARCHAR(200) NOT NULL,
    ubicacion           VARCHAR(100),
    cantidad_total      INT DEFAULT 1,
    cantidad_disponible INT DEFAULT 1,
    cantidad_prestados  INT default 0,
    cantidad_daniado    INT default 0
);

CREATE TABLE editoriales
(
    id_editorial INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    pais         VARCHAR(100)
);

CREATE TABLE generos
(
    id_genero INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE autores
(
    id_autor   INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    apellidos  VARCHAR(120),
    pais       VARCHAR(100)
);

CREATE TABLE libros
(
    id_material INT PRIMARY KEY,
    id_editorial INT,
    id_genero INT,
    isbn        VARCHAR(20),
    FOREIGN KEY (id_material) REFERENCES materiales (id_material) ON DELETE CASCADE,
	FOREIGN KEY (id_editorial) REFERENCES editoriales (id_editorial) ON DELETE SET NULL,
    FOREIGN KEY (id_genero) REFERENCES generos (id_genero) ON DELETE SET NULL
);

CREATE TABLE libro_autor
(
	id_libro_autor INT AUTO_INCREMENT PRIMARY KEY,
    id_material INT NOT NULL,
    id_autor    INT NOT NULL,
	FOREIGN KEY (id_material) REFERENCES libros (id_material) ON DELETE CASCADE,
	FOREIGN KEY (id_autor) REFERENCES autores (id_autor) ON DELETE CASCADE
);

CREATE TABLE revistas
(
    id_material       INT PRIMARY KEY,
    volumen           VARCHAR(50),
    numero            VARCHAR(50),
    fecha_publicacion DATE,
    FOREIGN KEY (id_material) REFERENCES materiales (id_material) ON DELETE CASCADE
);


CREATE TABLE audiovisuales
(
    id_material INT PRIMARY KEY,
    formato     VARCHAR(50),
    duracion    INT,
    FOREIGN KEY (id_material) REFERENCES materiales (id_material) ON DELETE CASCADE
);


CREATE TABLE otros_documentos
(
    id_material INT PRIMARY KEY,
    descripcion TEXT,
    FOREIGN KEY (id_material) REFERENCES materiales (id_material) ON DELETE CASCADE
);

CREATE TABLE moras
(
    id_mora       INT AUTO_INCREMENT PRIMARY KEY,
    anio_aplicable YEAR,
    tipo_usuario  ENUM('Profesor','Alumno'),
    tarifa_diaria DECIMAL(6, 2) DEFAULT 0.00
);

CREATE TABLE prestamos
(
    id_prestamo      INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario       INT  NOT NULL,
    id_material      INT  NOT NULL,
    id_mora          INT,
    mora_total       DECIMAL(6, 2),
    fecha_prestamo   DATE NOT NULL,
    fecha_estimada   DATE,
    fecha_devolucion DATE,
    estado           ENUM ('Pendiente','En_Curso','Devuelto','Denegado'),
    FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
    FOREIGN KEY (id_material) REFERENCES materiales (id_material),
    FOREIGN KEY (id_mora) REFERENCES moras (id_mora)
);

-- ========================================
-- DATOS DE EJEMPLO PARA PRUEBAS
-- ========================================

-- Insertar usuarios (password: "123456" para todos)
INSERT INTO usuarios (nombre, tipo_usuario, correo, password) VALUES
('Admin Principal', 'Encargado', 'admin@biblioteca.com', '123456'),
('Carlos Rodríguez', 'Profesor', 'carlos.rodriguez@udb.edu.sv', '123456'),
('Juan Pérez', 'Alumno', 'juan.perez@estudiante.udb.edu.sv', '123456'),
('María González', 'Profesor', 'maria.gonzalez@udb.edu.sv', '123456'),
('Ana Martínez', 'Alumno', 'ana.martinez@estudiante.udb.edu.sv', '123456'),
('Luis Hernández', 'Alumno', 'luis.hernandez@estudiante.udb.edu.sv', '123456');

-- Insertar editoriales
INSERT INTO editoriales (nombre, pais) VALUES
('Pearson Education', 'Estados Unidos'),
('McGraw-Hill', 'Estados Unidos'),
('O''Reilly Media', 'Estados Unidos'),
('Editorial Santillana', 'España'),
('Alfaomega', 'México');

-- Insertar géneros
INSERT INTO generos (nombre, descripcion) VALUES
('Programación', 'Libros sobre desarrollo de software y lenguajes de programación'),
('Base de Datos', 'Libros sobre sistemas de gestión de bases de datos'),
('Redes', 'Libros sobre redes de computadoras y telecomunicaciones'),
('Ciencia Ficción', 'Literatura de ciencia ficción'),
('Ingeniería de Software', 'Libros sobre metodologías y procesos de desarrollo');

-- Insertar autores
INSERT INTO autores (nombre, apellidos, pais) VALUES
('Herbert', 'Schildt', 'Estados Unidos'),
('Bruce', 'Eckel', 'Estados Unidos'),
('Joshua', 'Bloch', 'Estados Unidos'),
('Cay', 'Horstmann', 'Estados Unidos'),
('Robert', 'Martin', 'Estados Unidos'),
('Martin', 'Fowler', 'Reino Unido'),
('Andrew', 'Tanenbaum', 'Estados Unidos');

-- Insertar materiales (Libros)
INSERT INTO materiales (tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible, cantidad_prestados) VALUES
('Libro', 'Java: The Complete Reference', 'Estante A-1', 5, 3, 2),
('Libro', 'Effective Java', 'Estante A-2', 3, 2, 1),
('Libro', 'Core Java Volume I', 'Estante A-3', 4, 4, 0),
('Libro', 'Clean Code', 'Estante B-1', 6, 5, 1),
('Libro', 'Design Patterns', 'Estante B-2', 3, 2, 1),
('Libro', 'Refactoring', 'Estante B-3', 2, 1, 1),
('Libro', 'Computer Networks', 'Estante C-1', 4, 4, 0);

-- Insertar libros con relaciones
INSERT INTO libros (id_material, id_editorial, id_genero, isbn) VALUES
(1, 1, 1, '978-0072263855'),
(2, 3, 1, '978-0134685991'),
(3, 3, 1, '978-0135166307'),
(4, 1, 5, '978-0132350884'),
(5, 1, 5, '978-0201633610'),
(6, 1, 5, '978-0134757599'),
(7, 1, 3, '978-0132126953');

-- Insertar relaciones libro-autor
INSERT INTO libro_autor (id_material, id_autor) VALUES
(1, 1), -- Java Complete Reference por Herbert Schildt
(2, 3), -- Effective Java por Joshua Bloch
(3, 4), -- Core Java por Cay Horstmann
(4, 5), -- Clean Code por Robert Martin
(5, 5), (5, 6), -- Design Patterns por Martin & Fowler
(6, 6), -- Refactoring por Martin Fowler
(7, 7); -- Computer Networks por Andrew Tanenbaum

-- Insertar materiales (Revistas)
INSERT INTO materiales (tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible) VALUES
('Revista', 'IEEE Computer', 'Estante D-1', 2, 2),
('Revista', 'Communications of the ACM', 'Estante D-2', 3, 3),
('Revista', 'Journal of Software Engineering', 'Estante D-3', 2, 2);

INSERT INTO revistas (id_material, volumen, numero, fecha_publicacion) VALUES
(8, '2024', '10', '2024-10-01'),
(9, '67', '11', '2024-11-01'),
(10, '2024', '4', '2024-08-15');

-- Insertar materiales (Audiovisuales)
INSERT INTO materiales (tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible) VALUES
('Audiovisual', 'Java Programming Tutorial - DVD', 'Estante E-1', 3, 3),
('Audiovisual', 'Introduction to Databases', 'Estante E-2', 2, 2);

INSERT INTO audiovisuales (id_material, formato, duracion) VALUES
(11, 'DVD', 240),
(12, 'DVD', 180);

-- Insertar materiales (Otros documentos)
INSERT INTO materiales (tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible) VALUES
('Otro', 'Tesis: Optimización de Consultas SQL', 'Estante F-1', 1, 1),
('Otro', 'Manual de Configuración de Redes', 'Estante F-2', 2, 2);

INSERT INTO otros_documentos (id_material, descripcion) VALUES
(13, 'Tesis doctoral sobre optimización de consultas en bases de datos relacionales'),
(14, 'Manual técnico para configuración de redes empresariales');

-- Insertar tarifas de mora (año 2025)
INSERT INTO moras (anio_aplicable, tipo_usuario, tarifa_diaria) VALUES
(2025, 'Alumno', 1.50),
(2025, 'Profesor', 2.00);

-- Insertar préstamos de ejemplo

-- Préstamos PENDIENTES (esperando aprobación)
INSERT INTO prestamos (id_usuario, id_material, id_mora, mora_total, fecha_prestamo, fecha_estimada, estado) VALUES
(3, 3, 1, 0.00, CURDATE(), NULL, 'Pendiente'), -- Juan solicita Core Java
(5, 7, 1, 0.00, CURDATE(), NULL, 'Pendiente'); -- Ana solicita Computer Networks

-- Préstamos EN_CURSO (sin retraso)
INSERT INTO prestamos (id_usuario, id_material, id_mora, mora_total, fecha_prestamo, fecha_estimada, estado) VALUES
(2, 1, 2, 0.00, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'En_Curso'), -- Carlos con Java Complete (10 días, 3 usados)
(3, 4, 1, 0.00, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'En_Curso'); -- Juan con Clean Code (5 días, 2 usados)

-- Préstamos EN_CURSO (CON RETRASO - para probar cálculo de mora)
INSERT INTO prestamos (id_usuario, id_material, id_mora, mora_total, fecha_prestamo, fecha_estimada, estado) VALUES
(5, 2, 1, 0.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'En_Curso'), -- Ana con Effective Java (3 días de retraso = $4.50)
(4, 5, 2, 0.00, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'En_Curso'); -- María con Design Patterns (2 días retraso = $4.00)

-- Préstamos DEVUELTOS (sin mora)
INSERT INTO prestamos (id_usuario, id_material, id_mora, mora_total, fecha_prestamo, fecha_estimada, fecha_devolucion, estado) VALUES
(3, 6, 1, 0.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'Devuelto'), -- Juan devolvió Refactoring a tiempo
(2, 1, 2, 0.00, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 11 DAY), 'Devuelto'); -- Carlos devolvió Java Complete a tiempo

-- Préstamos DEVUELTOS (con mora pagada completamente)
INSERT INTO prestamos (id_usuario, id_material, id_mora, mora_total, fecha_prestamo, fecha_estimada, fecha_devolucion, estado) VALUES
(5, 4, 1, 0.00, DATE_SUB(CURDATE(), INTERVAL 25 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'Devuelto'); -- Ana devolvió Clean Code con 5 días de retraso ($7.50) y pagó todo

-- Préstamos DEVUELTOS (con mora pendiente de pago)
INSERT INTO prestamos (id_usuario, id_material, id_mora, mora_total, fecha_prestamo, fecha_estimada, fecha_devolucion, estado) VALUES
(6, 5, 1, 6.00, DATE_SUB(CURDATE(), INTERVAL 18 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'Devuelto'); -- Luis devolvió Design Patterns con 4 días retraso ($6.00) sin pagar

-- Préstamos DENEGADOS
INSERT INTO prestamos (id_usuario, id_material, id_mora, mora_total, fecha_prestamo, fecha_estimada, estado) VALUES
(6, 2, 1, 0.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY), NULL, 'Denegado'); -- Solicitud de Luis denegada

-- ========================================
-- RESUMEN DE DATOS DE PRUEBA
-- ========================================
-- Usuarios:
--   - 1 Administrador (admin@biblioteca.com)
--   - 2 Profesores (límite: 6 préstamos)
--   - 3 Alumnos (límite: 3 préstamos)
--
-- Materiales:
--   - 7 Libros (varios autores y géneros)
--   - 3 Revistas
--   - 2 Audiovisuales
--   - 2 Otros documentos
--
-- Préstamos para probar:
--   - 2 PENDIENTES (aprobar/denegar)
--   - 4 EN_CURSO (2 sin retraso, 2 con retraso para calcular mora)
--   - 4 DEVUELTOS (2 sin mora, 1 con mora pagada, 1 con mora pendiente)
--   - 1 DENEGADO
--
-- Tarifas de mora 2025:
--   - Alumno: $1.50/día
--   - Profesor: $2.00/día
-- ========================================