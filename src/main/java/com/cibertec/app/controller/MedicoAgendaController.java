package com.cibertec.app.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.app.dto.AgendaMedicoDTO;
import com.cibertec.app.service.MedicoService;
import com.cibertec.app.service.SlotHorarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medico")
@RequiredArgsConstructor
public class MedicoAgendaController {
	private final SlotHorarioService slotHorarioService;
	private final MedicoService medicoService;

    @GetMapping("/agenda")
    public ResponseEntity<List<AgendaMedicoDTO>> getAgenda(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

    	Long idMedico = medicoService.obtenerIdMedicoPorUsername(principal.getName());
        LocalDate fechaBusqueda = (fecha != null) ? fecha : LocalDate.now();
        
        List<AgendaMedicoDTO> agenda = slotHorarioService.obtenerAgendaMedicoPorFecha(idMedico, fechaBusqueda);
        
        return ResponseEntity.ok(agenda);
    }
}
