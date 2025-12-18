package com.cibertec.app.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.LogCita;
import com.cibertec.app.entity.Usuario;
import com.cibertec.app.enums.Accion;
import com.cibertec.app.repository.LogCitaRepository;
import com.cibertec.app.service.LogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService{
	
	private final LogCitaRepository logCitaRepository;

    @Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void registrarLog(Cita cita, Accion accion, String detalle, Usuario usuarioActor) {
        LogCita log = LogCita.builder()
                .cita(cita)
                .accion(accion)
                .usuarioActor(usuarioActor)
                .detalle(detalle)
                .fechaAccion(LocalDateTime.now())
                .build();
        
        logCitaRepository.save(log);
	}
}
