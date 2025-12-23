package com.cibertec.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cibertec.app.dto.UsuarioActualizacionDTO;
import com.cibertec.app.dto.UsuarioRegistroDTO;
import com.cibertec.app.dto.UsuarioResponseDTO;
import com.cibertec.app.dto.UsuarioVistaModificarDTO;

public interface UsuarioService {
	
	UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto, MultipartFile archivo);
	
	UsuarioResponseDTO actualizarUsuario(UsuarioActualizacionDTO dto, MultipartFile archivo);
	
	UsuarioVistaModificarDTO buscarPorUserName(String userName);
	
	UsuarioResponseDTO buscarPorId(Long id);
	
	List<UsuarioResponseDTO> buscarUsuarioParaMedicosDisponibles(String dni);
	
	void cambiarEstado(Long id);
	
}
