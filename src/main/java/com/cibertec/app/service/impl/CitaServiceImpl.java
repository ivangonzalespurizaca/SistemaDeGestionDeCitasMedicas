package com.cibertec.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.app.dto.CitaActualizacionDTO;
import com.cibertec.app.dto.CitaDetalleDTO;
import com.cibertec.app.dto.CitaRegistroDTO;
import com.cibertec.app.dto.CitaResponseDTO;
import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.Paciente;
import com.cibertec.app.entity.SlotHorario;
import com.cibertec.app.entity.Usuario;
import com.cibertec.app.enums.Accion;
import com.cibertec.app.enums.EstadoCita;
import com.cibertec.app.enums.EstadoSlotHorario;
import com.cibertec.app.mapper.CitaInputMapper;
import com.cibertec.app.mapper.CitaOutputMapper;
import com.cibertec.app.repository.CitaRepository;
import com.cibertec.app.repository.PacienteRepository;
import com.cibertec.app.repository.SlotHorarioRepository;
import com.cibertec.app.repository.UsuarioRepository;
import com.cibertec.app.service.CitaService;
import com.cibertec.app.service.LogService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CitaServiceImpl implements CitaService{

	private final CitaRepository citaRepository;
    private final SlotHorarioRepository slotRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaInputMapper citaInputMapper;
    private final CitaOutputMapper citaOutputMapper;
    private final UsuarioRepository usuarioRepository;
    private final LogService logService;
    
    @Transactional(readOnly = true)
	@Override
	public List<CitaResponseDTO> listarTodo() {
		return citaRepository.findAllByOrderByFechaDescHoraDesc().stream()
                .map(citaOutputMapper::toResponseDTO)
                .toList();
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CitaResponseDTO> buscarPorCriterio(String criterio) {
		return citaRepository.buscarPorCriterioGlobal(criterio).stream()
                .map(citaOutputMapper::toResponseDTO)
                .toList();
	}
	
	@Transactional
	@Override
	public CitaResponseDTO registrarCita(CitaRegistroDTO dto, String username) {
		SlotHorario slot = slotRepository.findById(dto.getIdSlot())
	            .orElseThrow(() -> new EntityNotFoundException("El horario seleccionado no existe."));
		
		if (slot.getEstadoSlot() != EstadoSlotHorario.DISPONIBLE) {
            throw new IllegalStateException("El horario ya no está disponible (alguien lo ganó).");
        }
		
		LocalDateTime fechaHoraCita = LocalDateTime.of(slot.getFecha(), slot.getHora());
        if (fechaHoraCita.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes reservar un horario que ya pasó.");
        }
        
        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado."));
            
        Usuario registrador = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario registrador no encontrado."));
        
        Cita nuevaCita = citaInputMapper.toEntityCita(dto, slot, paciente, registrador);
        
        Cita citaGuardada = citaRepository.save(nuevaCita);
        
        slot.setEstadoSlot(EstadoSlotHorario.RESERVADO);
        slot.setCita(citaGuardada);
        
        logService.registrarLog(citaGuardada, Accion.CREACION, "Cita Reservada", registrador);
        
		return citaOutputMapper.toResponseDTO(citaGuardada);
	}
	
	@Transactional
	@Override
	public CitaResponseDTO actualizarCita(CitaActualizacionDTO dto, String username) {
	    Cita cita = citaRepository.findById(dto.getIdCita())
	            .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada."));

	    if (cita.getEstado() != EstadoCita.PENDIENTE && cita.getEstado() != EstadoCita.CONFIRMADO) {
	        throw new IllegalStateException("No se puede modificar una cita en estado: " + cita.getEstado());
	    }
	    
	    Usuario usuarioActor = usuarioRepository.findByUsername(username)
	            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));

	    // CASO A: Se seleccionó un nuevo horario (Reprogramación)
	    if (dto.getIdSlotNuevo() != null) {
	        SlotHorario slotNuevo = slotRepository.findById(dto.getIdSlotNuevo())
	                .orElseThrow(() -> new EntityNotFoundException("El nuevo horario no existe."));

	        if (slotNuevo.getEstadoSlot() != EstadoSlotHorario.DISPONIBLE) {
	            throw new IllegalStateException("El nuevo horario seleccionado ya está ocupado.");
	        }

	        SlotHorario slotActual = slotRepository.findByCita(cita)
	                .orElseThrow(() -> new IllegalStateException("No se encontró el slot actual vinculado a la cita."));

	        if (!slotActual.getIdSlot().equals(slotNuevo.getIdSlot())) {
	            // 1. Liberar slot viejo (Crucial para el error Duplicate Entry)
	            slotActual.setEstadoSlot(EstadoSlotHorario.DISPONIBLE);
	            slotActual.setCita(null);
	            slotRepository.saveAndFlush(slotActual); 

	            // 2. Ocupar slot nuevo
	            slotNuevo.setEstadoSlot(EstadoSlotHorario.RESERVADO);
	            slotNuevo.setCita(cita);
	            slotRepository.saveAndFlush(slotNuevo);

	            // 3. Actualizar la entidad Cita (Mapper) y Logs
	            citaInputMapper.updateEntityCita(dto, cita, slotNuevo);
	            logService.registrarLog(cita, Accion.REASIGNACION, 
	                "Cita reprogramada del " + slotActual.getFecha() + " al " + slotNuevo.getFecha(), usuarioActor);
	        } else {
	            // El ID del slot es el mismo, pero quizá cambió el motivo
	            citaInputMapper.updateEntityCita(dto, cita, null);
	        }
	    } 
	    // CASO B: Solo se actualizan datos informativos (Motivo)
	    else {
	        citaInputMapper.updateEntityCita(dto, cita, null);
	        logService.registrarLog(cita, Accion.REASIGNACION, "Se actualizaron datos informativos (Motivo).", usuarioActor);
	    }

	    Cita citaGuardada = citaRepository.save(cita);
	    return citaOutputMapper.toResponseDTO(citaGuardada);
	}
	
	@Transactional(readOnly = true)
	@Override
	public CitaDetalleDTO buscarPorId(Long id) {
		Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + id));
        return citaOutputMapper.toDetalleDTO(cita);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CitaResponseDTO> listarPorEstado(EstadoCita estado) {
		return citaRepository.findByEstado(estado).stream()
                .map(citaOutputMapper::toResponseDTO)
                .toList();
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CitaResponseDTO> listarPendientesPorPaciente(String paciente) {
		return citaRepository.findPendientesByPaciente(paciente, EstadoCita.PENDIENTE).stream()
                .map(citaOutputMapper::toResponseDTO)
                .toList();
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CitaResponseDTO> listarConfirmadasPorPaciente(String paciente) {
		return citaRepository.findPendientesByPaciente(paciente, EstadoCita.CONFIRMADO).stream()
                .map(citaOutputMapper::toResponseDTO)
                .toList();
	}
	
	@Transactional
	@Override
	public void anularCita(Long id, String username) {
		Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada."));
        Usuario actor = usuarioRepository.findByUsername(username).orElseThrow();

        if (cita.getEstado() != EstadoCita.PENDIENTE && cita.getEstado() != EstadoCita.CONFIRMADO) {
            throw new IllegalStateException("No se puede anular la cita porque ya se encuentra en estado: " + cita.getEstado());
        }
        
        cita.setEstado(EstadoCita.CANCELADO);

        slotRepository.findByCita(cita).ifPresent(slot -> {
            slot.setEstadoSlot(EstadoSlotHorario.DISPONIBLE);
            slot.setCita(null);
        });

        logService.registrarLog(cita, Accion.ANULACION, "Cita anulada y horario liberado.", actor);
		
	}
	
	@Transactional
	@Override
	public void completarCita(Long id, String username) {
		Cita cita = citaRepository.findById(id).orElseThrow();
        Usuario actor = usuarioRepository.findByUsername(username).orElseThrow();

        if (cita.getEstado() != EstadoCita.CONFIRMADO) {
            throw new IllegalStateException("No se puede marcar como atendida una cita que está en estado: " + cita.getEstado() 
                + ". El paciente debe realizar el pago primero.");
        }
        
        cita.setEstado(EstadoCita.ATENDIDO);
        logService.registrarLog(cita, Accion.ATENDIDO, "Cita marcada como atendida.", actor);
	}
	
	@Transactional
	@Override
	public void marcarNoAsistio(Long id, String username) {
		Cita cita = citaRepository.findById(id).orElseThrow();
        Usuario actor = usuarioRepository.findByUsername(username).orElseThrow();

        if (cita.getEstado() != EstadoCita.CONFIRMADO) {
            throw new IllegalStateException("No se puede marcar como atendida una cita que está en estado: " + cita.getEstado() 
                + ". El paciente debe realizar el pago primero.");
        }
        
        cita.setEstado(EstadoCita.NO_ATENDIDO);
        logService.registrarLog(cita, Accion.NO_ATENDIDO, "Paciente no asistió.", actor);	
	}
}
