package com.cibertec.app.dto;

import java.time.LocalTime;

import com.cibertec.app.enums.EstadoSlotHorario;

import lombok.Value;

@Value
public class SlotHorarioResponseDTO {
	
	private Long idSlot;
	private LocalTime hora;
	private EstadoSlotHorario estadoSlot;
	
}
