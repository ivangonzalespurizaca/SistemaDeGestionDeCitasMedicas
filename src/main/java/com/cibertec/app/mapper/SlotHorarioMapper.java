package com.cibertec.app.mapper;

import org.springframework.stereotype.Component;

import com.cibertec.app.dto.AgendaMedicoDTO;
import com.cibertec.app.dto.SlotHorarioResponseDTO;
import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.SlotHorario;

@Component
public class SlotHorarioMapper {
	
	public SlotHorarioResponseDTO toSlotHorarioResponseDTO(SlotHorario entity) {
        if (entity == null) return null;
        
        return new SlotHorarioResponseDTO(
            entity.getIdSlot(),
            entity.getHora(),
            entity.getEstadoSlot()
        );
    }
	
	public AgendaMedicoDTO toAgendaMedicoDTO(SlotHorario entity) {
        if (entity == null) return null;

        AgendaMedicoDTO.AgendaMedicoDTOBuilder builder = AgendaMedicoDTO.builder()
                .hora(entity.getHora())
                .estadoSlot(entity.getEstadoSlot());

        if (entity.getCita() != null) {
            Cita cita = entity.getCita();
            
            builder.idCita(cita.getIdCita())
                   .motivo(cita.getMotivo());

            if (cita.getPaciente() != null) {
                String nombreCompleto = cita.getPaciente().getNombres() + " " + 
                                       cita.getPaciente().getApellidos();
                builder.pacienteNombreCompleto(nombreCompleto);
            }
        }

        return builder.build();
    }
	
}
