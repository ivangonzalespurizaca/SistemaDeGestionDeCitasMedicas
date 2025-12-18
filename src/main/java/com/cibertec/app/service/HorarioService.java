package com.cibertec.app.service;

import java.util.List;

import com.cibertec.app.dto.HorarioProyeccionDTO;
import com.cibertec.app.dto.HorarioRegistroDTO;
import com.cibertec.app.dto.HorarioResponseDTO;

public interface HorarioService {

	List<HorarioProyeccionDTO> proyectarHorariosPorIdMedico(Long idMedico);
	
	List<HorarioResponseDTO> buscarHorariosPorIdMedico(Long idMedico);
	
	HorarioResponseDTO registrarHorario(HorarioRegistroDTO dto);
	
	void eliminarPorID(Long idHorario);
	
}
