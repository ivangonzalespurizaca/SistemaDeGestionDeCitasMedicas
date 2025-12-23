package com.cibertec.app.dto;

import java.time.LocalDate;
import java.time.LocalTime;


import lombok.Value;

@Value
public class CitaDetalleDTO {
	private Long idCita;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String estado; 
    
    private String pacienteDni;
    private String pacienteNombreCompleto;

    private Long idMedico;
    private String medicoDni;
    private String medicoNombreCompleto;
    private String especialidadNombre;
    
    private String registradorNombreCompleto;
    private Long idUsuarioRegistrador;
}
