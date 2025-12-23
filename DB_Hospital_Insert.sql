USE DB_Hospital;

INSERT INTO Especialidad (Nombre_Especialidad)
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

INSERT INTO Medico (ID_Usuario, Nro_Colegiatura, Especialidad_ID)
VALUES 
(4, 'COL-1001', 1),
(5, 'COL-1002', 2),
(6, 'COL-1003', 3);

INSERT INTO Paciente (Nombres, Apellidos, DNI, Fecha_nacimiento, Telefono)
VALUES 
('Javier', 'Mendoza Castillo', '74851236', '1990-05-12', '987321654'),
('Lucía', 'Ramírez Paredes', '71589423', '1985-09-27', '956478213'),
('Carlos', 'Torres Huamán', '73214589', '2000-01-08','945612378');

INSERT INTO Horarios_Atencion (Id_Medico, Dia_Semana, Horario_Entrada, Horario_Salida)
VALUES
(1, 'LUNES', '09:00:00', '13:00:00'),
(1, 'MIERCOLES', '09:00:00', '13:00:00'),
(1, 'VIERNES', '09:00:00', '13:00:00'),
(2, 'MARTES', '08:00:00', '12:00:00'),
(2, 'JUEVES', '08:00:00', '12:00:00'),
(3, 'LUNES', '14:00:00', '18:00:00'),
(3, 'MIERCOLES', '14:00:00', '18:00:00');

CALL GenerarSlots();

-- CITAS
-- CITAS FUTURAS (15 registros)
INSERT INTO Cita (ID_Medico, ID_Paciente, ID_Usuario, Fecha, Hora, Motivo, Estado)
VALUES
(2, 3, 2, '2025-12-18', '08:00:00', 'Dolor de columna', 'PENDIENTE'),
(1, 1, 2, '2025-12-22', '09:00:00', 'Consulta general', 'PENDIENTE'),
(1, 2, 2, '2025-12-24', '09:00:00', 'Dolor de garganta', 'PENDIENTE'),
(1, 3, 2, '2025-12-24', '11:00:00', 'Fiebre y malestar', 'CONFIRMADO'),
(2, 1, 2, '2025-12-9', '08:00:00', 'Revisión pediátrica', 'VENCIDO'),
(2, 2, 2, '2025-12-11', '08:00:00', 'Chequeo infantil', 'ATENDIDO'),
(2, 3, 2, '2025-12-11', '09:00:00', 'Vacunación', 'VENCIDO'),
(3, 1, 2, '2025-12-15', '14:00:00', 'Control ginecológico', 'NO_ATENDIDO'),
(3, 2, 2, '2025-12-15', '15:00:00', 'Consulta prenatal', 'ATENDIDO'),
(3, 3, 2, '2025-12-17', '16:00:00', 'Revisión anual', 'ATENDIDO');

-- Actualizar Slots
SET SQL_SAFE_UPDATES = 0;

UPDATE Slot_Horario s
INNER JOIN Cita c ON s.ID_Medico = c.ID_Medico 
                 AND s.Fecha = c.Fecha 
                 AND s.Hora_Inicio = c.Hora
SET s.Estado = 'RESERVADO',
    s.ID_Cita = c.ID_Cita;
  
  SET SQL_SAFE_UPDATES = 1;

-- COMPROBANTES DE PAGO CORRESPONDIENTES
INSERT INTO Comprobante_Pago (ID_Cita, ID_Usuario, Nombres_Pagador, Apellidos_Pagador, DNI_Pagador, Contacto_Pagador, Monto, Metodo_Pago)
VALUES
(4, 3, 'Javier', 'Mendoza Castillo', '74851236', '987321654', 90.00, 'EFECTIVO'),
(5, 3, 'Lucía', 'Ramírez Paredes', '71589423', '956478213', 95.00, 'TARJETA'),
(6, 3, 'Carlos', 'Torres Huamán', '73214589', '945612378', 110.00, 'TRANSFERENCIA'),
(8, 3, 'Lucía', 'Ramírez Paredes', '71589423', '956478213', 100.00, 'TARJETA'),
(9, 3, 'Carlos', 'Torres Huamán', '73214589', '945612378', 115.00, 'TRANSFERENCIA'),
(2, 3, 'Carlos', 'Torres Huamán', '73214589', '945612378', 115.00, 'TRANSFERENCIA');

INSERT INTO Historial_Medico (ID_Cita, ID_Usuario, Diagnostico, Tratamiento, Notas_Adicionales, Peso, Presion_Arterial)
VALUES
(6, 5, 'Rinorrea y congestión nasal leve. Paciente pediátrico estable.', 
 'Lavado nasal con suero fisiológico y paracetamol en gotas si presenta fiebre.', 
 'Se recomienda control en 48 horas si los síntomas persisten.', 15.50, '90/60'),

(9, 6, 'Embarazo de 24 semanas, evolución normal. Latidos fetales rítmicos.', 
 'Continuar con ácido fólico y sulfato ferroso. Dieta balanceada.', 
 'Próxima ecografía programada para la siguiente semana.', 62.30, '110/70'),

(10, 6, 'Lumbalgia mecánica debido a mala postura prolongada.', 
 'Diclofenaco 50mg cada 12 horas por 3 días. Reposo relativo.', 
 'Se deriva a terapia física para ejercicios de fortalecimiento.', 78.00, '120/80');

INSERT INTO Usuario (Username, Contrasenia, Nombres, Apellidos, DNI, Telefono, Rol)
VALUES
('medi4', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Rodrigo Gustavo', 'Rosales Palma', '77874547', '999985421', 'MEDICO'),
('medi5', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Carolina Fernanda', 'Talledo Salas', '73323215', '963252352', 'MEDICO'),
('medi6', '$2a$10$pehyH/xsPAjfSYDgzqvX5uZMKEHtT/z6Ikslr7x9Ej3UUZnq5gr3G', 'Vanessa Esperanza', 'Cardenas Palomino', '71114524', '952365236', 'MEDICO');

SELECT * FROM Slot_Horario;

SELECT * FROM Slot_Horario where ID_Medico = 1;

SELECT * FROM Cita;

SELECT * FROM Paciente;

SELECT * FROM comprobante_pago;

SELECT * FROM usuario;

SELECT * FROM medico;

SELECT * FROM historial_medico;

SELECT * FROM log_cita;