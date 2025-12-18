package com.cibertec.app.service;

import java.util.List;

import com.cibertec.app.dto.PacienteActualizacionDTO;
import com.cibertec.app.dto.PacienteRegistroDTO;
import com.cibertec.app.dto.PacienteResponseDTO;

public interface PacienteService {

	PacienteResponseDTO registrarPaciente(PacienteRegistroDTO dto);
	
	List<PacienteResponseDTO> listarTodo();
	
	PacienteResponseDTO actualizarPaciente(PacienteActualizacionDTO dto);
	
	void eliminarPorId(Long id);
	
	PacienteResponseDTO buscarPorId(Long id);
	
	List<PacienteResponseDTO> buscarPorNombreDNI(String criterio);
	
}
