package com.cibertec.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.app.dto.SlotHorarioResponseDTO;
import com.cibertec.app.service.SlotHorarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recepcionista/slots")
@RequiredArgsConstructor
public class SlotHorarioController {
	
	private final SlotHorarioService slotHorarioService;

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<SlotHorarioResponseDTO>> getDisponibilidad(
            @RequestParam Long idMedico,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<SlotHorarioResponseDTO> disponibilidad = slotHorarioService.listarDisponibilidadPorMedicoYFecha(idMedico, fecha);
        return disponibilidad.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(disponibilidad);
    }
}
