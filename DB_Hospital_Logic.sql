USE DB_Hospital;
SET GLOBAL event_scheduler = ON;
SET lc_time_names = 'en_US';

DELIMITER $$

CREATE PROCEDURE GenerarSlots()
BEGIN
    DECLARE fechaInicio DATE;
    DECLARE fechaFin DATE;

    SET fechaInicio = CURDATE();
    SET fechaFin = DATE_ADD(CURDATE(), INTERVAL 30 DAY);

    -- Recorremos todas las fechas
    WHILE fechaInicio <= fechaFin DO
        
        -- Insertamos slots para cada médico y cada horario definido
        INSERT INTO Slot_Horario (ID_Medico, Fecha, Hora_Inicio, Estado)
        SELECT 
            h.ID_Medico,
            fechaInicio,
            ADDTIME(h.Horario_Entrada, SEC_TO_TIME(n.numero * 3600)), 
            'DISPONIBLE'
        FROM Horarios_Atencion h
        JOIN (
            SELECT 0 AS numero UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
            UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9
            UNION SELECT 10 UNION SELECT 11) AS n
        WHERE DAYNAME(fechaInicio) = 
            CASE h.Dia_Semana
                WHEN 'LUNES' THEN 'Monday'
                WHEN 'MARTES' THEN 'Tuesday'
                WHEN 'MIERCOLES' THEN 'Wednesday'
                WHEN 'JUEVES' THEN 'Thursday'
                WHEN 'VIERNES' THEN 'Friday'
                WHEN 'SABADO' THEN 'Saturday'
                WHEN 'DOMINGO' THEN 'Sunday'
            END
        AND ADDTIME(h.Horario_Entrada, SEC_TO_TIME(n.numero * 3600)) < h.Horario_Salida
        AND NOT EXISTS (
            SELECT 1 FROM Slot_Horario s
            WHERE s.ID_Medico = h.ID_Medico
              AND s.Fecha = fechaInicio
              AND s.Hora_Inicio = ADDTIME(h.Horario_Entrada, SEC_TO_TIME(n.numero * 3600))
        );
        
        SET fechaInicio = DATE_ADD(fechaInicio, INTERVAL 1 DAY);
    END WHILE;
END$$

DELIMITER ;

-- =========================================================
-- TRIGGER PARA CAMBIAR ESTADO DE CITA AL CREAR COMPROBANTE
-- =========================================================
DELIMITER $$

CREATE TRIGGER trg_After_Insert_Comprobante
AFTER INSERT ON Comprobante_Pago
FOR EACH ROW
BEGIN
	UPDATE Cita 
    SET Estado = 'PAGADO'
    WHERE ID_Cita = NEW.ID_Cita;

    INSERT INTO Log_Cita (ID_Cita, ID_Usuario, Accion, Detalle)
    VALUES (NEW.ID_Cita, NEW.ID_Usuario, 'PAGO', CONCAT('Comprobante emitido. Monto: ', NEW.Monto));
END$$

DELIMITER ;

-- =========================================================
-- TRIGGER PARA CAMBIAR ESTADO DE CITA AL ANULAR COMPROBANTE
-- =========================================================
DELIMITER $$

CREATE TRIGGER trg_After_Update_Comprobante
AFTER UPDATE ON Comprobante_Pago
FOR EACH ROW
BEGIN
    -- Verifica si el estado cambió de EMITIDO a ANULADO
    IF OLD.Estado = 'EMITIDO' AND NEW.Estado = 'ANULADO' THEN
        UPDATE Cita
        SET Estado = 'PENDIENTE'
        WHERE ID_Cita = NEW.ID_Cita;
        
        INSERT INTO Log_Cita (ID_Cita, ID_Usuario, Accion, Detalle)
        VALUES (NEW.ID_Cita, NEW.ID_Usuario, 'ANULACION', 'Comprobante anulado, Cita regresó a estado PENDIENTE.');
    END IF;
END $$

DELIMITER ;

-- =========================================================
-- TRIGGER PARA REGISTRAR HISTORIAL CLÍNICO -> CITA ATENDIDA
-- =========================================================
DELIMITER $$

CREATE TRIGGER trg_After_Insert_Historial
AFTER INSERT ON Historial_Medico
FOR EACH ROW
BEGIN
    DECLARE v_MedicoID INT;
    
    SELECT ID_Medico INTO v_MedicoID
    FROM Cita
    WHERE ID_Cita = NEW.ID_Cita;

    UPDATE Cita
    SET Estado = 'ATENDIDO'
    WHERE ID_Cita = NEW.ID_Cita;
    
    INSERT INTO Log_Cita (ID_Cita, ID_Usuario, Accion, Detalle)
    VALUES (NEW.ID_Cita, v_MedicoID, 'ATENDIDO', 'Registro clínico completado por el médico.');
END $$

DELIMITER ;

-- =========================================================
-- TRIGGER: AL CREAR UNA CITA -> MARCAR SLOT COMO RESERVADO Y REGISTRAR CREACIÓN
-- =========================================================
DELIMITER $$
CREATE TRIGGER trg_After_Insert_Cita
AFTER INSERT ON Cita
FOR EACH ROW
BEGIN
    UPDATE Slot_Horario
    SET Estado = 'RESERVADO',
        ID_Cita = NEW.ID_Cita
    WHERE ID_Medico = NEW.ID_Medico
      AND Fecha = NEW.Fecha
      AND Hora_Inicio = NEW.Hora;

    INSERT INTO Log_Cita (ID_Cita, ID_Usuario, Accion, Detalle)
    VALUES (NEW.ID_Cita, NEW.Id_Usuario, 'CREACION', 'Cita reservada.');
END $$

DELIMITER ;

-- =========================================================
-- TRIGGER: AL ACTUALIZAR EL ESTADO DE CITA -> LIBERAR SLOT Y REGISTRAR LOG
-- =========================================================

DELIMITER $$
CREATE TRIGGER trg_After_Update_Cita_Status
AFTER UPDATE ON Cita
FOR EACH ROW
BEGIN
    -- 1. Si el estado cambia a CANCELADO o VENCIDO, libera el slot en Slot_Horario
    IF OLD.Estado <> NEW.Estado AND NEW.Estado IN ('CANCELADO', 'VENCIDO') THEN
        UPDATE Slot_Horario
        SET Estado = 'DISPONIBLE',
            ID_Cita = NULL
        WHERE ID_Cita = NEW.ID_Cita;
    END IF;
    
    -- 2. Registrar la acción CANCELACION o VENCIDO en Log_Cita
    -- Nota: El trigger se dispara antes de que se ejecuten los INSERTs de VENCIDO del evento.
    
    IF OLD.Estado <> NEW.Estado AND NEW.Estado = 'CANCELADO' THEN
         -- Esto cubre cancelaciones manuales (ej. por la Recepcionista)
         INSERT INTO Log_Cita (ID_Cita, ID_Usuario, Accion, Detalle)
         VALUES (NEW.ID_Cita, NEW.Id_Usuario, 'CANCELACION', 'Cita cancelada manualmente/administrativamente.');
    
    ELSEIF OLD.Estado <> NEW.Estado AND NEW.Estado = 'VENCIDO' THEN
         -- Esto cubre cuando la cita pasa de PENDIENTE/PAGADO a VENCIDO.
         -- Usamos NULL para ID_Usuario porque el cambio fue disparado por el sistema/tiempo.
         INSERT INTO Log_Cita (ID_Cita, ID_Usuario, Accion, Detalle)
         VALUES (NEW.ID_Cita, NULL, 'VENCIDO', 'Cita marcada como vencida por el sistema.');
    END IF;
END $$
DELIMITER ;

-- =========================================================
-- EVENTO PARA CAMBIAR DE ESTADO DE CITA
-- =========================================================

DELIMITER $$

CREATE EVENT IF NOT EXISTS actualizar_estados_citas
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- 1. Marcar citas pendientes como vencidas cuando pasa la hora
    UPDATE Cita
    SET Estado = 'VENCIDO'
    WHERE Estado = 'PENDIENTE'
      AND (Fecha < CURDATE() OR (Fecha = CURDATE() AND Hora < CURTIME()));
END $$

DELIMITER ;

-- =========================================================
-- EVENTO PARA GENERAR SLOTS AUTOMATICAMENTE
-- =========================================================

CREATE EVENT IF NOT EXISTS Generar_Slots_Diario
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 DAY
DO CALL GenerarSlots();