package com.cibertec.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.cibertec.app.dto.MedicoActualizarDTO;
import com.cibertec.app.dto.MedicoRegistroDTO;
import com.cibertec.app.dto.MedicoResponseDTO;
import com.cibertec.app.dto.MedicoVistaModificarDTO;
import com.cibertec.app.entity.Medico;

@Mapper(componentModel = "spring")
public interface MedicoMapper {

	
	@Mapping(target = "idMedico", ignore = true) 
	@Mapping(target = "usuario", ignore = true) 
    @Mapping(target = "especialidad", ignore = true)     
	Medico toEntityMedico(MedicoRegistroDTO dto);
 
    @Mapping(target = "especialidad", ignore = true)   
    @Mapping(target = "usuario", ignore = true) 
    void toMedicoUpdate(MedicoActualizarDTO dto, @MappingTarget Medico medico);
    
    @Mapping(target = "nombres", source = "usuario.nombres")       
    @Mapping(target = "apellidos", source = "usuario.apellidos")   
    @Mapping(target = "dni", source = "usuario.dni")
    @Mapping(target = "nombreEspecialidad", source = "especialidad.nombreEspecialidad")
    MedicoResponseDTO toMedicoResponseDTO(Medico medico);
    
    @Mapping(target = "idEspecialidad", source = "especialidad.idEspecialidad")
    @Mapping(target = "nombreCompletoUsuario", source = "medico")
    @Mapping(target = "nombreEspecialidad", source = "especialidad.nombreEspecialidad")
    MedicoVistaModificarDTO toVistaModificarDTO(Medico medico);

    // Este método se encarga de la lógica de concatenación de forma segura
    default String mapNombreCompleto(Medico medico) {
        if (medico == null || medico.getUsuario() == null) {
            return null;
        }
        String nombres = medico.getUsuario().getNombres() != null ? medico.getUsuario().getNombres() : "";
        String apellidos = medico.getUsuario().getApellidos() != null ? medico.getUsuario().getApellidos() : "";
        return (nombres + " " + apellidos).trim();
    }
}
