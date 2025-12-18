package com.cibertec.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.app.dto.AgendaMedicoDTO;
import com.cibertec.app.service.SlotHorarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medico")
@RequiredArgsConstructor
public class MedicoAgendaController {
	private final SlotHorarioService slotHorarioService;

    @GetMapping("/{idMedico}/agenda")
    public ResponseEntity<List<AgendaMedicoDTO>> getAgenda(
            @PathVariable Long idMedico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate fechaBusqueda = (fecha != null) ? fecha : LocalDate.now();
        
        List<AgendaMedicoDTO> agenda = slotHorarioService.obtenerAgendaMedicoPorFecha(idMedico, fechaBusqueda);
        
        return ResponseEntity.ok(agenda);
    }
}
