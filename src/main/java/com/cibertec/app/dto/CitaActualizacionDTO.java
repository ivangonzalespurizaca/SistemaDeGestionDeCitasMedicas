package com.cibertec.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CitaActualizacionDTO {

    @NotNull(message = "El ID de la cita es obligatorio.")
    private Long idCita;
    
    private Long idSlotNuevo; 
    
    @Size(max = 255, message = "El motivo no puede exceder los 255 caracteres.")
    private String motivo;
}
