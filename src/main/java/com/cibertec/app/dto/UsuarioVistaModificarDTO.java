package com.cibertec.app.dto;

import com.cibertec.app.enums.TipoRol;

import lombok.Value;

@Value
public class UsuarioVistaModificarDTO {

    private Long idUsuario; 
    private TipoRol rol;
    private String username;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String correo;
    
}
