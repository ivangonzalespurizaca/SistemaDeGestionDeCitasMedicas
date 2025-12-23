package com.cibertec.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.app.dto.ComprobanteDePagoDetalleDTO;
import com.cibertec.app.dto.ComprobanteDePagoRegistroDTO;
import com.cibertec.app.dto.ComprobanteDePagoResponseDTO;
import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.ComprobanteDePago;
import com.cibertec.app.entity.Usuario;
import com.cibertec.app.enums.Accion;
import com.cibertec.app.enums.EstadoCita;
import com.cibertec.app.mapper.ComprobantePagoMapper;
import com.cibertec.app.repository.CitaRepository;
import com.cibertec.app.repository.ComprobanteDePagoRepository;
import com.cibertec.app.repository.UsuarioRepository;
import com.cibertec.app.service.ComprobanteDePagoService;
import com.cibertec.app.service.LogService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ComprobanteDePagoServiceImpl implements ComprobanteDePagoService{
	
	private final ComprobanteDePagoRepository comprobanteRepository;
	private final CitaRepository citaRepository;
	private final UsuarioRepository usuarioRepository;
	private final ComprobantePagoMapper comprobanteMapper;
	private final LogService logService;

	@Transactional
	@Override
	public ComprobanteDePagoResponseDTO registrarComprobanteDePago(ComprobanteDePagoRegistroDTO dto, String username) {
		
		Cita cita = citaRepository.findById(dto.getIdCita())
	            .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));
		
		LocalDateTime fechaHoraCita = LocalDateTime.of(cita.getFecha(), cita.getHora());

		if (fechaHoraCita.isBefore(LocalDateTime.now())) {
	        cita.setEstado(EstadoCita.VENCIDO);
	        logService.registrarLog(cita, Accion.VENCIDO, "Vencimiento automático detectado al intentar pagar", null);
	        throw new IllegalStateException("No se puede pagar: El tiempo de la cita ha expirado.");
	    }
		
	    if (cita.getEstado() != EstadoCita.PENDIENTE) {
	        throw new IllegalStateException("La cita no está en estado PENDIENTE.");
	    }
	    
	    Usuario cajero = usuarioRepository.findByUsername(username).orElseThrow();

	    ComprobanteDePago pago = comprobanteMapper.toEntityComprobanteDePago(dto, cita, cajero);
	    ComprobanteDePago pagoGuardado = comprobanteRepository.save(pago);
	    
	    cita.setEstado(EstadoCita.CONFIRMADO);
	    
	    logService.registrarLog(cita, Accion.PAGO, "Cita Confirmada", cajero);
	    
		return comprobanteMapper.toComprobantePagoResponseDTO(pagoGuardado);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ComprobanteDePagoResponseDTO> listarTodos() {
		return comprobanteRepository.findAllByOrderByFechaEmisionDesc().stream()
                .map(comprobanteMapper::toComprobantePagoResponseDTO)
                .toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<ComprobanteDePagoResponseDTO> buscarPorCriterio(String criterio) {
		String filtro = (criterio == null) ? "" : criterio.trim();
        return comprobanteRepository.buscarPorCriterio(filtro).stream()
                .map(comprobanteMapper::toComprobantePagoResponseDTO)
                .toList();
	}

	@Transactional
	@Override
	public void anularComprobanteDePago(Long id, String username) {
		ComprobanteDePago pago = comprobanteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado con ID: " + id));
		
		Cita cita = pago.getCita();
		
		Usuario actor = usuarioRepository.findByUsername(username).orElseThrow();
		
		LocalDateTime fechaHoraCita = LocalDateTime.of(cita.getFecha(), cita.getHora());
	    if (fechaHoraCita.isBefore(LocalDateTime.now())) {
	        throw new IllegalStateException("No se puede anular el pago de una cita vencido o atendido (Fecha: " + cita.getFecha() + ")");
	    }
		
		if (cita.getEstado() == EstadoCita.ATENDIDO || cita.getEstado() == EstadoCita.NO_ATENDIDO) {
	        throw new IllegalStateException("No se puede anular el pago porque la cita ya fue procesada como: " + cita.getEstado());
	    }

        if (pago.getEstado() == com.cibertec.app.enums.EstadoComprobante.ANULADO) {
            throw new IllegalStateException("El comprobante ya se encuentra anulado.");
        }

        pago.setEstado(com.cibertec.app.enums.EstadoComprobante.ANULADO);
        
        if (cita.getEstado() == EstadoCita.CONFIRMADO) {
            cita.setEstado(EstadoCita.PENDIENTE);
        }
        comprobanteRepository.save(pago);
        
        logService.registrarLog(cita, Accion.CANCELACION, "Pago anulado", actor);
	}

	@Transactional
	@Override
	public ComprobanteDePagoDetalleDTO buscarPorId(Long id) {
		return comprobanteRepository.findById(id)
                .map(comprobanteMapper::toComprobantePagoDetalleDTO)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el comprobante con ID: " + id));
	}
	
}
