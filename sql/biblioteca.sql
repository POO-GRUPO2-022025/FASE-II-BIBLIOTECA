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