package com.cibertec.app.mapper;

import java.time.LocalTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.cibertec.app.dto.HorarioProyeccionDTO;
import com.cibertec.app.dto.HorarioRegistroDTO;
import com.cibertec.app.dto.HorarioResponseDTO;
import com.cibertec.app.entity.HorarioDeAtencion;

@Mapper(componentModel = "spring")
public interface HorarioMapper {
	@Mapping(target = "idHorario", ignore = true)
    @Mapping(target = "medico", ignore = true)
    @Mapping(target = "horarioEntrada", source = "horarioEntrada", qualifiedByName = "mapStringToLocalTime")
    @Mapping(target = "horarioSalida", source = "horarioSalida", qualifiedByName = "mapStringToLocalTime")
    HorarioDeAtencion toEntityHorarios(HorarioRegistroDTO dto);
	
	@Named("mapStringToLocalTime")
    default LocalTime mapStringToLocalTime(String hora) {
        if (hora == null || hora.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(hora);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora inválido. Se espera HH:mm.", e);
        }
    }
	
    HorarioResponseDTO toHorarioResponseDTO(HorarioDeAtencion entity);
	
	HorarioProyeccionDTO toHorarioProyeccionDTO(HorarioDeAtencion entity);
}
