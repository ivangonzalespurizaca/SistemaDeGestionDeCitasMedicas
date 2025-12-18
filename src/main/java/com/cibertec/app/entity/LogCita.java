package com.cibertec.app.entity;

import java.time.LocalDateTime;
import com.cibertec.app.enums.Accion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "Log_Cita")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogCita {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Log")
    private Long idLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Cita", nullable = false)
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Usuario")
    private Usuario usuarioActor; 
    
    @Enumerated(EnumType.STRING)
    private Accion accion;

    private String detalle;

    @Column(name = "Fecha_Accion", nullable = false)
    private LocalDateTime fechaAccion;
    
    @PrePersist
    public void prePersist() {
        if (this.fechaAccion == null) this.fechaAccion = LocalDateTime.now();
    }
}
