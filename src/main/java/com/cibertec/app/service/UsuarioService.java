package com.cibertec.app.service;

import java.util.List;

import com.cibertec.app.dto.UsuarioActualizacionDTO;
import com.cibertec.app.dto.UsuarioRegistroDTO;
import com.cibertec.app.dto.UsuarioResponseDTO;

public interface UsuarioService {
	
	UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto);
	
	UsuarioResponseDTO actualizarUsuario(UsuarioActualizacionDTO dto);
	
	UsuarioResponseDTO buscarPorUserName(String userName);
	
	UsuarioResponseDTO buscarPorId(Long id);
	
	List<UsuarioResponseDTO> buscarUsuarioParaMedicosDisponibles(String dni);
	
	void cambiarEstado(Long id);
	
}
