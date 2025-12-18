package com.cibertec.app.mapper;

import org.springframework.stereotype.Component;

import com.cibertec.app.dto.CitaActualizacionDTO;
import com.cibertec.app.dto.CitaRegistroDTO;
import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.Paciente;
import com.cibertec.app.entity.SlotHorario;
import com.cibertec.app.entity.Usuario;

@Component
public class CitaInputMapper {

	public Cita toEntityCita(CitaRegistroDTO dto, SlotHorario slot, Paciente paciente, Usuario registrador) {
        return Cita.builder()
                .medico(slot.getMedico())
                .fecha(slot.getFecha())
                .hora(slot.getHora())
                .paciente(paciente)
                .usuarioRegistro(registrador)
                .motivo(dto.getMotivo())
                .build();
    }
	
	public void updateEntityCita(CitaActualizacionDTO dto, Cita cita, SlotHorario slotNuevo) {

        if (dto.getMotivo() != null) {
            cita.setMotivo(dto.getMotivo());
        }

        if (slotNuevo != null) {
            cita.setFecha(slotNuevo.getFecha());
            cita.setHora(slotNuevo.getHora());
            cita.setMedico(slotNuevo.getMedico());
        }
    }
	
}
