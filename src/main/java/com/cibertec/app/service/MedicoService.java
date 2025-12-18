package com.cibertec.app.service;

import java.util.List;

import com.cibertec.app.dto.MedicoActualizarDTO;
import com.cibertec.app.dto.MedicoRegistroDTO;
import com.cibertec.app.dto.MedicoResponseDTO;

public interface MedicoService {

	List<MedicoResponseDTO> listarTodo();
	
	MedicoResponseDTO registrarMedico(MedicoRegistroDTO dto);
	
	MedicoResponseDTO actualizarMedico(MedicoActualizarDTO dto);
	
	MedicoResponseDTO buscarPorId(Long id);
	
	List<MedicoResponseDTO> buscarPorCriterio(String criterio);
}
