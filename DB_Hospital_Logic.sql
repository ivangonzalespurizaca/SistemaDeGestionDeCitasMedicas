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
-- EVENTO PARA CAMBIAR DE ESTADO DE CITA
-- =========================================================

DELIMITER $$

CREATE EVENT IF NOT EXISTS actualizar_estados_citas
ON SCHEDULE EVERY 10 MINUTE
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- 1. LIBERAR SLOTS (Hacerlo antes de cambiar los estados de la Cita)
    UPDATE Slot_Horario s
    INNER JOIN Cita c ON s.ID_Cita = c.ID_Cita
    SET s.Estado = 'DISPONIBLE', 
        s.ID_Cita = NULL
    WHERE (c.Estado = 'PENDIENTE' AND (c.Fecha < CURDATE() OR (c.Fecha = CURDATE() AND c.Hora < CURTIME())))
       OR (c.Estado = 'CONFIRMADO' AND (c.Fecha < CURDATE() OR (c.Fecha = CURDATE() AND c.Hora < SUBTIME(CURTIME(), '02:00:00'))));

    -- 2. LOGS Y UPDATE PARA 'VENCIDO'
    INSERT INTO Log_Cita (ID_Cita, Accion, Detalle, Fecha_Accion, ID_Usuario)
    SELECT ID_Cita, 'VENCIDO', 'Vencimiento automático: No pagó a tiempo', NOW(), NULL
    FROM Cita
    WHERE Estado = 'PENDIENTE'
      AND (Fecha < CURDATE() OR (Fecha = CURDATE() AND Hora < CURTIME()));
      
    UPDATE Cita
    SET Estado = 'VENCIDO'
    WHERE Estado = 'PENDIENTE'
      AND (Fecha < CURDATE() OR (Fecha = CURDATE() AND Hora < CURTIME()));
      
    -- 3. LOGS Y UPDATE PARA 'NO_ASISTIO'
    INSERT INTO Log_Cita (ID_Cita, Accion, Detalle, Fecha_Accion, ID_Usuario)
    SELECT ID_Cita, 'NO_ASISTIO', 'Inasistencia automática: Paciente pagó pero no registró atención (Tolerancia 2h)', NOW(), NULL
    FROM Cita
    WHERE Estado = 'CONFIRMADO'
      AND (Fecha < CURDATE() OR (Fecha = CURDATE() AND Hora < SUBTIME(CURTIME(), '02:00:00')));

    UPDATE Cita 
    SET Estado = 'NO_ASISTIO' 
    WHERE Estado = 'CONFIRMADO' 
    AND (Fecha < CURDATE() OR (Fecha = CURDATE() AND Hora < SUBTIME(CURTIME(), '02:00:00')));

END $$

DELIMITER ;

-- =========================================================
-- EVENTO PARA GENERAR SLOTS AUTOMATICAMENTE
-- =========================================================

DELIMITER //

CREATE EVENT IF NOT EXISTS Generar_Slots_Diario
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY + INTERVAL 2 HOUR)
ON COMPLETION PRESERVE
DO 
BEGIN
    CALL GenerarSlots();
END //

DELIMITER ;

SHOW EVENTS;