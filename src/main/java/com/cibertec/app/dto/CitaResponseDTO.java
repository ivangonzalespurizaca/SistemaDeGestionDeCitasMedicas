package com.cibertec.app.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.cibertec.app.enums.EstadoCita;

import lombok.Value;

@Value
public class CitaResponseDTO {

	private Long idCita; 
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado; 
    private String nombreCompletoMedico;
    private String especialidadNombre;
    private String dniPaciente;
    private String nombreCompletoPaciente;
    
}
