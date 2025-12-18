package com.cibertec.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.app.dto.MedicoActualizarDTO;
import com.cibertec.app.dto.MedicoRegistroDTO;
import com.cibertec.app.dto.MedicoResponseDTO;
import com.cibertec.app.service.MedicoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/administrador/medicos")
@RequiredArgsConstructor
public class MedicoController {
	private final MedicoService medicoService;
	
	@GetMapping 
    public ResponseEntity<List<MedicoResponseDTO>> listarTodo() {
        List<MedicoResponseDTO> listado = medicoService.listarTodo();
        return listado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(listado);
    }
	
	@PostMapping
    public ResponseEntity<MedicoResponseDTO> registrarMedico(@Valid @RequestBody MedicoRegistroDTO dto) {
		MedicoResponseDTO nuevoMedico = medicoService.registrarMedico(dto);
        return new ResponseEntity<>(nuevoMedico, HttpStatus.CREATED);
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> actualizarMedico(
            @PathVariable Long id, 
            @Valid @RequestBody MedicoActualizarDTO dto) {
        dto.setIdMedico(id);
        return ResponseEntity.ok(medicoService.actualizarMedico(dto));
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> buscarPorId(@PathVariable Long id) {
        MedicoResponseDTO response = medicoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/buscar") 
    public ResponseEntity<List<MedicoResponseDTO>> buscarPorCriterio(
            @RequestParam(required = false) String criterio) {
        List<MedicoResponseDTO> listado = medicoService.buscarPorCriterio(criterio);
        return listado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(listado);
	}
}
