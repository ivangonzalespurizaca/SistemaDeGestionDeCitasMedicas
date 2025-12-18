package com.cibertec.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.app.dto.PacienteActualizacionDTO;
import com.cibertec.app.dto.PacienteRegistroDTO;
import com.cibertec.app.dto.PacienteResponseDTO;
import com.cibertec.app.service.PacienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recepcionista/pacientes")
@RequiredArgsConstructor
public class PacienteController {
	private final PacienteService pacienteService;

	@GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {
        List<PacienteResponseDTO> listado = pacienteService.listarTodo();
        return listado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(listado);
    }
	
	@GetMapping("/buscar")
    public ResponseEntity<List<PacienteResponseDTO>> buscarPorCriterio(
            @RequestParam(required = false) String criterio) {
        
        List<PacienteResponseDTO> pacientes = pacienteService.buscarPorNombreDNI(criterio); 
        return pacientes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pacientes);
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
		
        PacienteResponseDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
            
    }
	
	@PostMapping
    public ResponseEntity<PacienteResponseDTO> registrar(@Valid @RequestBody PacienteRegistroDTO dto) {
		
       	PacienteResponseDTO pacienteRegistrado = pacienteService.registrarPaciente(dto);
        return new ResponseEntity<>(pacienteRegistrado, HttpStatus.CREATED);
    }
	
	@PutMapping("/{id}")
	public ResponseEntity<PacienteResponseDTO> actualizar(
			@PathVariable Long id,
            @Valid @RequestBody PacienteActualizacionDTO dto){
		
		dto.setIdPaciente(id);
		return ResponseEntity.ok(pacienteService.actualizarPaciente(dto));
		
	}
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		
        pacienteService.eliminarPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
    }
}
