package com.cibertec.app.service.impl;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.app.dto.HorarioProyeccionDTO;
import com.cibertec.app.dto.HorarioRegistroDTO;
import com.cibertec.app.dto.HorarioResponseDTO;
import com.cibertec.app.entity.HorarioDeAtencion;
import com.cibertec.app.entity.Medico;
import com.cibertec.app.enums.DiaSemana;
import com.cibertec.app.mapper.HorarioMapper;
import com.cibertec.app.repository.HorarioRepository;
import com.cibertec.app.repository.MedicoRepository;
import com.cibertec.app.service.HorarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class HorarioServiceImpl implements HorarioService{
	
	public final HorarioRepository horarioRepository;
	public final HorarioMapper horarioMapper;
	public final MedicoRepository medicoRepository;

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
		List<HorarioDeAtencion> horarios = horarioRepository.findByMedico_IdMedico(idMedico);
		
		return horarios.stream()
				.map(horarioMapper::toHorarioProyeccionDTO)
				.toList();
	}	

	@Override
	@Transactional
	public void eliminarPorID(Long idHorario) {
		if(!horarioRepository.existsById(idHorario)) {
			throw new NoSuchElementException("Horario de Atención no Encontrado");
		}
		horarioRepository.deleteById(idHorario);
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
}
