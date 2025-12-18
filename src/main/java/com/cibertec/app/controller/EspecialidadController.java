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

import com.cibertec.app.dto.EspecialidadActualizacionDTO;
import com.cibertec.app.dto.EspecialidadRegistroDTO;
import com.cibertec.app.dto.EspecialidadResponseDTO;
import com.cibertec.app.service.EspecialidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/administrador/especialidades")
@RequiredArgsConstructor
@RestController
public class EspecialidadController {
	public final EspecialidadService especialidadService;
	
	@GetMapping
	ResponseEntity<List<EspecialidadResponseDTO>> listarEspecialidades(){
		List<EspecialidadResponseDTO> listado = especialidadService.listarTodo();
		return listado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(listado);
	}
	
    @GetMapping("/buscar")
    public ResponseEntity<List<EspecialidadResponseDTO>> buscarPorNombre(
            @RequestParam(required = false) String nombre) {
    	List<EspecialidadResponseDTO> listado = especialidadService.buscarPorNombre(nombre);    	
    	return listado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(listado);
    }
	
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDTO> buscarPorId(@PathVariable Long id) {
    	EspecialidadResponseDTO especialidad = especialidadService.buscarPorId(id);
    	return ResponseEntity.ok(especialidad);
    }
    
    @PostMapping
    public ResponseEntity<EspecialidadResponseDTO> registrar(@Valid @RequestBody EspecialidadRegistroDTO dto) {
		EspecialidadResponseDTO response = especialidadService.registrarEspecialidad(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
	
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadActualizacionDTO dto) {
        dto.setIdEspecialidad(id);
        return ResponseEntity.ok(especialidadService.actualizarEspecialidad(dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        especialidadService.eliminarPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
