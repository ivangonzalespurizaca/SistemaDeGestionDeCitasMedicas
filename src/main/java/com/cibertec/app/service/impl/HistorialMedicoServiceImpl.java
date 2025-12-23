package com.cibertec.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.app.dto.HistorialMedicoDetalleDTO;
import com.cibertec.app.dto.HistorialMedicoRegistrarDTO;
import com.cibertec.app.dto.HistorialMedicoResponseDTO;
import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.HistorialMedico;
import com.cibertec.app.entity.Usuario;
import com.cibertec.app.enums.Accion;
import com.cibertec.app.enums.EstadoCita;
import com.cibertec.app.mapper.HistorialMedicoMapper;
import com.cibertec.app.repository.CitaRepository;
import com.cibertec.app.repository.HistorialMedicoRepository;
import com.cibertec.app.repository.UsuarioRepository;
import com.cibertec.app.service.HistorialMedicoService;
import com.cibertec.app.service.LogService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistorialMedicoServiceImpl implements HistorialMedicoService{
	
	private final HistorialMedicoRepository historialRepository;
	private final CitaRepository citaRepository;
	private final UsuarioRepository usuarioRepository;
	private final HistorialMedicoMapper historialMapper;
	private final LogService logService;
	
	@Transactional
	@Override
	public HistorialMedicoResponseDTO registrarHistorialMedico(HistorialMedicoRegistrarDTO dto, String username) {
        Cita cita = citaRepository.findById(dto.getIdCita())
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + dto.getIdCita()));

        if (cita.getEstado() != EstadoCita.CONFIRMADO) {
            throw new IllegalStateException("Solo se puede registrar historial de citas en estado CONFIRMADO. Estado actual: " + cita.getEstado());
        }

        Usuario medico = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Médico no encontrado: " + username));
        
        HistorialMedico historial = historialMapper.toHistorialEntity(dto, cita, medico);

        cita.setEstado(EstadoCita.ATENDIDO);
        
        HistorialMedico guardado = historialRepository.save(historial);

        logService.registrarLog(cita, Accion.ATENDIDO, "Diagnóstico registrado y consulta finalizada", medico);
        return historialMapper.toResponseHistorialDTO(guardado);
	}

	@Transactional(readOnly = true)
	@Override
	public List<HistorialMedicoResponseDTO> listarTodos() {
		return historialRepository.findAllByOrderByCita_FechaDescCita_HoraDesc().stream()
                .map(historialMapper::toResponseHistorialDTO)
                .toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<HistorialMedicoResponseDTO> buscarPorCriterio(String criterio) {
		String filtro = (criterio == null) ? "" : criterio.trim();
        return historialRepository.buscarPorPacienteOOrderByFechaDesc(filtro).stream()
                .map(historialMapper::toResponseHistorialDTO)
                .toList();
	}

	@Transactional(readOnly = true)
	@Override
	public HistorialMedicoDetalleDTO buscarPorId(Long id) {
		return historialRepository.findById(id)
                .map(historialMapper::toDetalleHistorialDTO)
                .orElseThrow(() -> new EntityNotFoundException("Historial médico no encontrado con ID: " + id));
	}

}
