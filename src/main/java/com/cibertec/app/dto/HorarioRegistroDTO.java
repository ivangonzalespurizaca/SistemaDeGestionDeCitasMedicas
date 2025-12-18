package com.cibertec.app.dto;

import com.cibertec.app.enums.DiaSemana;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HorarioRegistroDTO {

    @NotNull(message = "Debe seleccionar un médico antes de registrar el horario.")
    private Long idMedico; 
    
    @NotNull(message = "El día de la semana es obligatorio.")
    private DiaSemana diaSemana; 
    
    @NotBlank(message = "La hora de entrada es obligatoria.")
    private String horarioEntrada; 
    
    @NotBlank(message = "La hora de salida es obligatoria.")
    private String horarioSalida;
    
}
