package com.cibertec.app.dto;

import java.time.LocalTime;

import com.cibertec.app.enums.EstadoSlotHorario;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class AgendaMedicoDTO {
    LocalTime hora;
    EstadoSlotHorario estadoSlot;

    Long idCita; 
    String pacienteNombreCompleto;
    String motivo;
}
