package com.cibertec.app.dto;

import lombok.Value;

@Value
public class HorarioResponseDTO {
	
	private Long idHorario;
    private String diaSemana; 
    private String horarioEntrada;
    private String horarioSalida;
    
}
