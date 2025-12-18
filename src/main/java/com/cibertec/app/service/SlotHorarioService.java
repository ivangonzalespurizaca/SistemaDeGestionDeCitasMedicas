package com.cibertec.app.service;

import java.time.LocalDate;
import java.util.List;

import com.cibertec.app.dto.AgendaMedicoDTO;
import com.cibertec.app.dto.SlotHorarioResponseDTO;

public interface SlotHorarioService {
	
	List<SlotHorarioResponseDTO> listarDisponibilidadPorMedicoYFecha(Long idMedico, LocalDate fecha);

    List<AgendaMedicoDTO> obtenerAgendaMedicoPorFecha(Long idMedico, LocalDate fecha);
    
}
