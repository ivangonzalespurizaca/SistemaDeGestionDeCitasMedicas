package com.cibertec.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cibertec.app.enums.EstadoComprobante;

import lombok.Value;

@Value
public class ComprobanteDePagoResponseDTO {
	
    private Long idComprobante;
    private LocalDateTime fechaEmision;
    private BigDecimal monto;
    private EstadoComprobante estado; 
    
    private String nombresPagador;
    private String apellidosPagador;
    private String dniPagador;
    
    private Long idCita; 
    private String pacienteNombreCompleto;
    private String estadoCita;
}
