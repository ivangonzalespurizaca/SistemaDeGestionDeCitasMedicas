package com.cibertec.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.cibertec.app.dto.UsuarioActualizacionDTO;
import com.cibertec.app.dto.UsuarioRegistroDTO;
import com.cibertec.app.dto.UsuarioResponseDTO;
import com.cibertec.app.entity.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
	@Mapping(target = "estado", ignore = true)
	@Mapping(target = "idUsuario", ignore = true)
	@Mapping(target = "imgPerfil", ignore = true)
	@Mapping(target = "medico", ignore = true)
	Usuario toEntityUsuario(UsuarioRegistroDTO dto);
	
	UsuarioResponseDTO toUsuarioResponseDTO(Usuario entity);
	
	@Mapping(target = "estado", ignore = true)
	@Mapping(target = "imgPerfil", ignore = true)
	@Mapping(target = "rol", ignore = true)
	@Mapping(target = "medico", ignore = true)
	void toUsuarioUpdate(UsuarioActualizacionDTO dto, @MappingTarget Usuario entity);
}
