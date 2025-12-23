package com.cibertec.app.dto;

import com.cibertec.app.enums.TipoRol;

import lombok.Value;

@Value
public class UsuarioResponseDTO {
	
	private Long idUsuario;
    private String dni;
    private String username;
    private String nombres;
    private String apellidos;
    private TipoRol rol;
    private String imgPerfil;
}
