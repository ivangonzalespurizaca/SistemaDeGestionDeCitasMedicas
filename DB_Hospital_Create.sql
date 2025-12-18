-- =====================================================
-- CREACIÓN DE BASE DE DATOS
-- =====================================================
DROP DATABASE IF EXISTS DB_Hospital;
CREATE DATABASE DB_Hospital;
USE DB_Hospital;

-- =====================================================
-- TABLA ESPECIALIDAD
-- =====================================================
CREATE TABLE Especialidad (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    Nombre_Especialidad VARCHAR(100) NOT NULL UNIQUE
);

-- =====================================================
-- TABLA Usuario
-- =====================================================
CREATE TABLE Usuario (
    Id_Usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(50) NOT NULL UNIQUE,
    Contrasenia VARCHAR(200) NOT NULL,
    Nombres VARCHAR(100) NOT NULL,
    Apellidos VARCHAR(100) NOT NULL,
    DNI VARCHAR(8) NOT NULL UNIQUE,
    Telefono VARCHAR(9) NULL,
    Img_Perfil VARCHAR(200) NULL,
    Correo VARCHAR(100) NULL,
    Rol ENUM('ADMINISTRADOR', 'RECEPCIONISTA','CAJERO', 'MEDICO') NOT NULL,
    Estado ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'
);

-- =====================================================
-- TABLA MÉDICO
-- =====================================================
CREATE TABLE Medico (
    Id_Medico BIGINT AUTO_INCREMENT PRIMARY KEY,
    Id_Usuario BIGINT NOT NULL UNIQUE,
    Nro_Colegiatura VARCHAR(20) NOT NULL UNIQUE,
    Especialidad_ID BIGINT NOT NULL,
    FOREIGN KEY (Especialidad_ID) REFERENCES Especialidad(ID),
    FOREIGN KEY (Id_Usuario) REFERENCES Usuario(Id_Usuario)
);

-- =====================================================
-- TABLA PACIENTE
-- =====================================================
CREATE TABLE Paciente (
    ID_Paciente BIGINT AUTO_INCREMENT PRIMARY KEY,
    Nombres VARCHAR(100) NOT NULL,
    Apellidos VARCHAR(50) NOT NULL,
    DNI VARCHAR(8) NOT NULL UNIQUE,
	Fecha_nacimiento DATE,
    Telefono VARCHAR(15)
);

-- =====================================================
-- TABLA CITA (con referencia a trabajador)
-- =====================================================
CREATE TABLE Cita (
    ID_Cita BIGINT AUTO_INCREMENT PRIMARY KEY,
    ID_Medico BIGINT NOT NULL,
    ID_Paciente BIGINT NOT NULL,
	Id_Usuario BIGINT NOT NULL,
	Fecha DATE NOT NULL,
    Hora TIME NOT NULL,
    Motivo VARCHAR(255),
    Estado ENUM('PENDIENTE', 'PAGADO', 'CANCELADO', 'VENCIDO', 'ATENDIDO', 'NO_ASISTIO') DEFAULT 'PENDIENTE',
    FOREIGN KEY (ID_MEDICO) REFERENCES Medico(ID_MEDICO),
    FOREIGN KEY (ID_Paciente) REFERENCES Paciente(ID_PACIENTE),
    FOREIGN KEY (Id_Usuario) REFERENCES Usuario(Id_Usuario)
);

-- =====================================================
-- TABLA HORARIOS ATENCIÓN
-- =====================================================
CREATE TABLE Horarios_Atencion (
    ID_Horario BIGINT AUTO_INCREMENT PRIMARY KEY,
    ID_MEDICO BIGINT NOT NULL,
    Dia_Semana ENUM('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO') NOT NULL,
    Horario_Entrada TIME NOT NULL,
    Horario_Salida TIME NOT NULL,
    FOREIGN KEY (ID_MEDICO) REFERENCES Medico(Id_Medico),
    CONSTRAINT UC_Horario UNIQUE (ID_Medico, Dia_Semana, Horario_Entrada, Horario_Salida)
);

-- =====================================================
-- TABLA COMPROBANTE DE PAGO
-- =====================================================
CREATE TABLE Comprobante_Pago (
    ID_Comprobante BIGINT AUTO_INCREMENT PRIMARY KEY,
    ID_Cita BIGINT NOT NULL,
    ID_Usuario BIGINT NOT NULL,
	Nombres_Pagador VARCHAR(100) NOT NULL,
	Apellidos_Pagador VARCHAR(100) NOT NULL,
    DNI_Pagador VARCHAR(8),
    Contacto_Pagador VARCHAR(15),
    Fecha_Emision DATETIME NOT NULL DEFAULT NOW(),
    Monto DECIMAL(10,2) NOT NULL,
    Metodo_Pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA') NOT NULL,
    Estado ENUM('EMITIDO', 'ANULADO') DEFAULT 'EMITIDO',
    FOREIGN KEY (Id_Usuario) REFERENCES Usuario(Id_Usuario),
    FOREIGN KEY (ID_Cita) REFERENCES Cita(ID_Cita)
);

-- =====================================================
-- TABLA HISTORIAL_MEDICO
-- =====================================================
CREATE TABLE Historial_Medico (
    ID_Historial BIGINT AUTO_INCREMENT PRIMARY KEY,
    ID_Cita BIGINT NOT NULL UNIQUE, 
    ID_Usuario BIGINT NOT NULL,
    Diagnostico TEXT NULL,
    Tratamiento TEXT,
    Notas_Adicionales TEXT,
    Peso DECIMAL(5,2) NULL,
    Presion_Arterial VARCHAR(10) NULL,
    FOREIGN KEY (ID_Cita) REFERENCES Cita(ID_Cita),
    FOREIGN KEY (ID_Usuario) REFERENCES Usuario(Id_Usuario)
);

-- =====================================================
-- TABLA SLOTS DE DISPONIBILIDAD
-- =====================================================
CREATE TABLE Slot_Horario (
    ID_Slot BIGINT AUTO_INCREMENT PRIMARY KEY,
    ID_Medico BIGINT NOT NULL,
    Fecha DATE NOT NULL,
    Hora_Inicio TIME NOT NULL,
    Estado ENUM('DISPONIBLE', 'RESERVADO') NOT NULL DEFAULT 'DISPONIBLE',
    ID_Cita BIGINT NULL UNIQUE, -- Referencia a la cita que ocupa el slot
    FOREIGN KEY (ID_Medico) REFERENCES Medico(ID_Medico),
    FOREIGN KEY (ID_Cita) REFERENCES Cita(ID_Cita),
    CONSTRAINT UC_Slot UNIQUE (ID_Medico, Fecha, Hora_Inicio)
);

-- =====================================================
-- TABLA LOG DE ACCIONES DE CITA
-- =====================================================
CREATE TABLE Log_Cita (
    ID_Log BIGINT AUTO_INCREMENT PRIMARY KEY,
    ID_Cita BIGINT NOT NULL,
    ID_Usuario BIGINT NULL, -- Quién realizó la acción (Puede ser NULL si es un evento automático)
    Accion ENUM('CREACION', 'CANCELACION', 'REASIGNACION', 'PAGO', 'ANULACION', 'ATENDIDO', 'VENCIDO', 'NO_ATENDIDO') NOT NULL,
    Detalle VARCHAR(255) NULL,
    Fecha_Accion DATETIME NOT NULL DEFAULT NOW(),
    FOREIGN KEY (ID_Cita) REFERENCES Cita(ID_Cita),
    FOREIGN KEY (ID_Usuario) REFERENCES Usuario(Id_Usuario)
);