package com.cibertec.app.dto;

import lombok.Value;

@Value
public class MedicoVistaModificarDTO {

	private Long idMedico;
	private String nombreCompletoUsuario;
	private Long idEspecialidad;
	private String nroColegiatura;
	private String nombreEspecialidad;
	
}
