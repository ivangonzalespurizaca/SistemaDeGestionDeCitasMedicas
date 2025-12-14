USE DB_Hospital;

INSERT INTO Especialidad (Nombre)
VALUES 
('Medicina General'),
('Pediatría'),
('Ginecología'),
('Cardiología'),
('Odontología');

INSERT INTO Usuario (Username, Contrasenia, Nombres, Apellidos, DNI, Telefono, Rol)
VALUES
('admin1', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Luis Alberto', 'Ramírez Torres', '72134567', '955623125', 'ADMINISTRADOR'),
('recep1', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'María Fernanda', 'Gonzales Huamán', '71234568', '988552541', 'RECEPCIONISTA'),
('caje1', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Carlos Enrique', 'Pérez Salazar', '73214569', '954125412', 'CAJERO'),
('medi1', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Pedro Carlos', 'Sanchez Valverde', '75214521', '955623125', 'MEDICO'),
('medi2', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Anthony Renato', 'Rojas Salazar', '98745215', '988552541', 'MEDICO'),
('medi3', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Sofia Lorena', 'Diaz Pereda', '87412541', '954125412', 'MEDICO');

INSERT INTO Medico (ID_Medico, Nro_Colegiatura, Especialidad_ID)
VALUES 
(4, 'COL-1001', 1),
(5, 'COL-1002', 2),
(6, 'COL-1003', 3);

INSERT INTO Paciente (Nombres, Apellidos, DNI, Fecha_nacimiento, Telefono)
VALUES 
('Javier', 'Mendoza Castillo', '74851236', '1990-05-12', '987321654'),
('Lucía', 'Ramírez Paredes', '71589423', '1985-09-27', '956478213'),
('Carlos', 'Torres Huamán', '73214589', '2000-01-08','945612378');

INSERT INTO Horarios_Atencion (ID_Medico, Dia_Semana, Horario_Entrada, Horario_Salida)
VALUES
(4, 'LUNES', '09:00:00', '13:00:00'),
(4, 'MIERCOLES', '09:00:00', '13:00:00'),
(4, 'VIERNES', '09:00:00', '13:00:00'),
(5, 'MARTES', '08:00:00', '12:00:00'),
(5, 'JUEVES', '08:00:00', '12:00:00'),
(6, 'LUNES', '14:00:00', '18:00:00'),
(6, 'MIERCOLES', '14:00:00', '18:00:00');

CALL GenerarSlots();

-- CITAS
-- CITAS FUTURAS (15 registros)
INSERT INTO Cita (ID_Medico, ID_Paciente, ID_Usuario, Fecha, Hora, Motivo)
VALUES
(4, 1, 2, '2025-12-22', '09:00:00', 'Consulta general'),
(4, 2, 2, '2025-12-24', '09:00:00', 'Dolor de garganta'),
(4, 3, 2, '2025-12-24', '11:00:00', 'Fiebre y malestar'),
(5, 1, 2, '2025-12-9', '08:00:00', 'Revisión pediátrica'),
(5, 2, 2, '2025-12-11', '08:00:00', 'Chequeo infantil'),
(5, 3, 2, '2025-12-11', '09:00:00', 'Vacunación'),
(6, 1, 2, '2025-12-15', '14:00:00', 'Control ginecológico'),
(6, 2, 2, '2025-12-15', '15:00:00', 'Consulta prenatal'),
(6, 3, 2, '2025-12-17', '16:00:00', 'Revisión anual');

-- COMPROBANTES DE PAGO CORRESPONDIENTES
INSERT INTO Comprobante_Pago (ID_Cita, ID_Usuario, Nombres_Pagador, Apellidos_Pagador, DNI_Pagador, Contacto_Pagador, Monto, Metodo_Pago)
VALUES
(1, 3, 'Javier', 'Mendoza Castillo', '74851236', '987321654', 80.00, 'EFECTIVO'),
(2, 3, 'Lucía', 'Ramírez Paredes', '71589423', '956478213', 100.00, 'TARJETA'),
(3, 3, 'Carlos', 'Torres Huamán', '73214589', '945612378', 120.00, 'TRANSFERENCIA'),
(4, 3, 'Javier', 'Mendoza Castillo', '74851236', '987321654', 90.00, 'EFECTIVO'),
(5, 3, 'Lucía', 'Ramírez Paredes', '71589423', '956478213', 95.00, 'TARJETA'),
(6, 3, 'Carlos', 'Torres Huamán', '73214589', '945612378', 110.00, 'TRANSFERENCIA'),
(7, 3, 'Javier', 'Mendoza Castillo', '74851236', '987321654', 85.00, 'EFECTIVO'),
(8, 3, 'Lucía', 'Ramírez Paredes', '71589423', '956478213', 100.00, 'TARJETA'),
(9, 3, 'Carlos', 'Torres Huamán', '73214589', '945612378', 115.00, 'TRANSFERENCIA');

SELECT * FROM Slot_Horario;

SELECT * FROM Slot_Horario where ID_Medico = 5;

SELECT * FROM Cita;

SELECT * FROM usuario;
