package com.cibertec.app.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.app.dto.HorarioProyeccionDTO;
import com.cibertec.app.dto.HorarioRegistroDTO;
import com.cibertec.app.dto.HorarioResponseDTO;
import com.cibertec.app.entity.HorarioDeAtencion;
import com.cibertec.app.entity.Medico;
import com.cibertec.app.enums.DiaSemana;
import com.cibertec.app.enums.EstadoCita;
import com.cibertec.app.enums.EstadoSlotHorario;
import com.cibertec.app.mapper.HorarioMapper;
import com.cibertec.app.repository.CitaRepository;
import com.cibertec.app.repository.HorarioRepository;
import com.cibertec.app.repository.MedicoRepository;
import com.cibertec.app.repository.SlotHorarioRepository;
import com.cibertec.app.service.HorarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class HorarioServiceImpl implements HorarioService{
	
	public final HorarioRepository horarioRepository;
	public final HorarioMapper horarioMapper;
	public final MedicoRepository medicoRepository;
	public final CitaRepository citaRepository;
	public final SlotHorarioRepository slotHorarioRepository;

	@Override
	@Transactional(readOnly = true)
	public List<HorarioResponseDTO> buscarHorariosPorIdMedico(Long idMedico) {
		List<HorarioDeAtencion> horarios = horarioRepository.findByMedico_IdMedico(idMedico);
		
		return horarios.stream()
				.map(horarioMapper::toHorarioResponseDTO)
				.toList();
	}

	@Override
	@Transactional
	public HorarioResponseDTO registrarHorario(HorarioRegistroDTO dto) {
		LocalTime nuevaEntrada = LocalTime.parse(dto.getHorarioEntrada());
        LocalTime nuevaSalida = LocalTime.parse(dto.getHorarioSalida());

        if(dto.getIdMedico() == null) {
        	throw new IllegalArgumentException("Debe seleccionar un médico.");
        }
        if(dto.getDiaSemana() == null) {
        	throw new IllegalArgumentException("Debe seleccionar un día válido.");
        }
        if(nuevaEntrada == null || nuevaSalida == null) {
        	throw new IllegalArgumentException("Debe especificar hora de inicio y hora de fin");
        }
        
        if(!nuevaSalida.isAfter(nuevaEntrada)){
        	throw new IllegalArgumentException("La hora de salida debe ser posterior a la hora de entrada.");
        }
        
        validarCruceDeHorarios(dto.getIdMedico(), dto.getDiaSemana(), nuevaEntrada, nuevaSalida);
        
        Medico medico = medicoRepository.findById(dto.getIdMedico())
				.orElseThrow(() -> new NoSuchElementException("Médico no encontrado con ID: " + dto.getIdMedico()));
        HorarioDeAtencion entity = horarioMapper.toEntityHorarios(dto);
        entity.setMedico(medico);
        HorarioDeAtencion guardado = horarioRepository.save(entity);
        
        return horarioMapper.toHorarioResponseDTO(guardado);
	}
	
	@Override
	public List<HorarioProyeccionDTO> proyectarHorariosPorIdMedico(Long idMedico) {
		List<HorarioDeAtencion> horariosConfigurados = horarioRepository.findByMedico_IdMedico(idMedico);
		
	    List<LocalDate> fechasConSlots = slotHorarioRepository.findFechasConSlotsDisponibles(idMedico);
	    
	    Set<DiaSemana> diasDisponibles = fechasConSlots.stream()
	            .map(this::convertirLocalDateADiaSemana)
	            .collect(Collectors.toSet());

	    return horariosConfigurados.stream()
	            .filter(h -> diasDisponibles.contains(h.getDiaSemana()))
	            .map(horarioMapper::toHorarioProyeccionDTO)
	            .toList();
	}	

	@Override
	@Transactional
	public void eliminarPorID(Long idHorario) {
	    // 1. Cargar horario
	    HorarioDeAtencion horario = horarioRepository.findById(idHorario)
	            .orElseThrow(() -> new NoSuchElementException("Horario no encontrado"));

	    // 2. Convertir el Enum DiaSemana a un índice numérico para la DB
	    // Suponiendo que tu Enum DiaSemana mapea a java.time.DayOfWeek
	    // Para MySQL DAYOFWEEK: Domingo=1, Lunes=2, ..., Sábado=7
	    int indiceDiaDb = obtenerIndiceDb(horario.getDiaSemana());

	    List<EstadoCita> estadosCriticos = Arrays.asList(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADO);

	    // 3. Validar citas (Solo del mismo médico, hora y MISMO DÍA DE LA SEMANA)
	    boolean tieneCitas = citaRepository.existeCitaEnDiaYHora(
	            horario.getMedico().getIdMedico(), 
	            horario.getHorarioEntrada(), 
	            LocalDate.now(),
	            estadosCriticos,
	            indiceDiaDb
	    );

	    if (tieneCitas) {
	        throw new IllegalStateException("No se puede eliminar: Existen citas futuras para este día de la semana.");
	    }
	    
	    // 4. Eliminar solo los slots disponibles del futuro de ese día específico
	    slotHorarioRepository.eliminarSlotsDisponiblesSegunDia(
	            horario.getMedico().getIdMedico(),
	            horario.getHorarioEntrada(),
	            LocalDate.now(),
	            EstadoSlotHorario.DISPONIBLE,
	            indiceDiaDb
	    );

	    // 5. Borrar la configuración
	    horarioRepository.deleteById(idHorario);
	}

	// Función auxiliar para mapear tu Enum al índice de tu DB (Ejemplo MySQL)
	private int obtenerIndiceDb(DiaSemana dia) {
	    return switch (dia) {
	        case LUNES -> 2;
	        case MARTES -> 3;
	        case MIERCOLES -> 4;
	        case JUEVES -> 5;
	        case VIERNES -> 6;
	        case SABADO -> 7;
	        case DOMINGO -> 1;
	    };
	}
	
	private void validarCruceDeHorarios(Long idMedico, DiaSemana diaSemana, LocalTime nuevaEntrada, LocalTime nuevaSalida) {
        
        List<HorarioDeAtencion> horarioMismoDia = horarioRepository.findByMedico_IdMedicoAndDiaSemana(
        		idMedico,
        		diaSemana);
        
        boolean solapado = horarioMismoDia.stream().anyMatch(h ->
        	nuevaEntrada.isBefore(h.getHorarioSalida()) &&
        	nuevaSalida.isAfter(h.getHorarioEntrada()));
        
        if(solapado) {
        	throw new IllegalArgumentException(
        			"El horario se solapa con otro horario existente del mismo día para este médico."
        	);
        }
    }
	
	private DiaSemana convertirLocalDateADiaSemana(LocalDate fecha) {
	    String nombreDia = fecha.getDayOfWeek().name(); // Retorna "MONDAY", "SUNDAY", etc.
	    // Mapeo simple (puedes usar un switch o asegurar que tu Enum coincida)
	    return switch (nombreDia) {
	        case "MONDAY" -> DiaSemana.LUNES;
	        case "TUESDAY" -> DiaSemana.MARTES;
	        case "WEDNESDAY" -> DiaSemana.MIERCOLES;
	        case "THURSDAY" -> DiaSemana.JUEVES;
	        case "FRIDAY" -> DiaSemana.VIERNES;
	        case "SATURDAY" -> DiaSemana.SABADO;
	        case "SUNDAY" -> DiaSemana.DOMINGO;
	        default -> null;
	    };
	}
}
