package com.cibertec.app.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.app.dto.AgendaMedicoDTO;
import com.cibertec.app.dto.SlotHorarioResponseDTO;
import com.cibertec.app.entity.SlotHorario;
import com.cibertec.app.mapper.SlotHorarioMapper;
import com.cibertec.app.repository.SlotHorarioRepository;
import com.cibertec.app.service.SlotHorarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlotHorarioServiceImpl implements SlotHorarioService{
	
	private final SlotHorarioRepository slotHorarioRepository;
	private final SlotHorarioMapper slotHorarioMapper;
	
	@Override
	public List<SlotHorarioResponseDTO> listarDisponibilidadPorMedicoYFecha(Long idMedico, LocalDate fecha) {
		
		if (fecha.isBefore(LocalDate.now())) {
	        return List.of(); 
	    }
		
		List<SlotHorario> slots = slotHorarioRepository.findByMedico_IdMedicoAndFechaOrderByHoraAsc(idMedico, fecha);
		
		if (fecha.equals(LocalDate.now())) {
	        LocalTime horaActual = LocalTime.now();
	        return slots.stream()
	                .filter(s -> s.getHora().isAfter(horaActual)) 
	                .map(slotHorarioMapper::toSlotHorarioResponseDTO)
	                .toList();
	    }
		        
        return slots.stream()
                .map(slotHorarioMapper::toSlotHorarioResponseDTO)
                .toList();
	}
	
	@Override
	public List<AgendaMedicoDTO> obtenerAgendaMedicoPorFecha(Long idMedico, LocalDate fecha) {
		
		List<SlotHorario> slots = slotHorarioRepository.obtenerVistaRecepcionCompleta(idMedico, fecha);
		
        return slots.stream()
                .map(slotHorarioMapper::toAgendaMedicoDTO)
                .toList();
	}
}
