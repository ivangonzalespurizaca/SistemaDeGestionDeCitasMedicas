package com.cibertec.app.service;

import java.util.List;

import com.cibertec.app.dto.CitaActualizacionDTO;
import com.cibertec.app.dto.CitaDetalleDTO;
import com.cibertec.app.dto.CitaRegistroDTO;
import com.cibertec.app.dto.CitaResponseDTO;
import com.cibertec.app.enums.EstadoCita;

public interface CitaService {
	
	List<CitaResponseDTO> listarTodo();
	
	List<CitaResponseDTO> buscarPorCriterio(String criterio);
	
	CitaResponseDTO registrarCita(CitaRegistroDTO dto, String username);
	
	CitaResponseDTO actualizarCita(CitaActualizacionDTO dto, String username);
	
	CitaDetalleDTO buscarPorId(Long id);
	
	List<CitaResponseDTO> listarPorEstado(EstadoCita estado);
	
	List<CitaResponseDTO> listarPendientesPorPaciente(String paciente);
	
	List<CitaResponseDTO> listarConfirmadasPorPaciente(String paciente);
	
	void anularCita(Long id, String username);
	
	void completarCita(Long id, String username);
    
    void marcarNoAsistio(Long id, String username);
	
}
