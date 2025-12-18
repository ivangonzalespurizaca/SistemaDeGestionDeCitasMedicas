package com.cibertec.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsuarioActualizacionDTO {
	
    @NotNull(message = "El ID es obligatorio para la actualización.")
    private Long idUsuario; 
    
    @Pattern(regexp = "^$|^.{4,}$", message = "La contraseña debe tener al menos 4 caracteres si decide cambiarla.")
    private String contrasenia; 
    
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    private String username;

    @NotBlank(message = "Los nombres son obligatorios.")
    private String nombres;
    
    @NotBlank(message = "Los apellidos son obligatorios.")
    private String apellidos;
    
    @NotBlank(message = "El DNI es obligatorio.")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe contener solo 8 dígitos numéricos.")
    private String dni;
    
    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe contener exactamente 9 dígitos numéricos.")
    private String telefono;

    @Email(message = "Formato de correo inválido.")
    private String correo;

}
