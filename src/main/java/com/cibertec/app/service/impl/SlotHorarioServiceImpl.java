package com.cibertec.app.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional(readOnly = true)
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
	
	@Transactional(readOnly = true)
	@Override
	public List<AgendaMedicoDTO> obtenerAgendaMedicoPorFecha(Long idMedico, LocalDate fecha) {
		
		// 1. Log de entrada para verificar parámetros
	    System.out.println("DEBUG: Iniciando consulta de agenda...");
	    System.out.println("DEBUG: idMedico recibido: " + idMedico);
	    System.out.println("DEBUG: fecha recibida: " + fecha);
		
		List<SlotHorario> slots = slotHorarioRepository.obtenerVistaRecepcionCompleta(idMedico, fecha);
		
		// 3. Log de salida de la DB
	    if (slots == null || slots.isEmpty()) {
	        System.out.println("DEBUG: La consulta al repositorio regresó CERO resultados.");
	    } else {
	        System.out.println("DEBUG: Se encontraron " + slots.size() + " registros en la base de datos.");
	        // Imprimir el primer registro para ver si los datos internos vienen nulos
	        System.out.println("DEBUG: Primer Slot ID: " + slots.get(0).getIdSlot());
	    }
		
        return slots.stream()
                .map(slotHorarioMapper::toAgendaMedicoDTO)
                .toList();
	}
}
