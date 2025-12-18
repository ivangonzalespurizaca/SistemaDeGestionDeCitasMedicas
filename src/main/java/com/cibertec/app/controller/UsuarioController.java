package com.cibertec.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.app.dto.UsuarioActualizacionDTO;
import com.cibertec.app.dto.UsuarioRegistroDTO;
import com.cibertec.app.dto.UsuarioResponseDTO;
import com.cibertec.app.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("api/administrador/usuarios")
@RequiredArgsConstructor
@RestController
public class UsuarioController {
	public final UsuarioService usuarioService;
	
	@PostMapping
	public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
			@Valid @RequestBody UsuarioRegistroDTO dto) {
		UsuarioResponseDTO response = usuarioService.registrarUsuario(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody UsuarioActualizacionDTO dto) {
        dto.setIdUsuario(id); 
		return ResponseEntity.ok(usuarioService.actualizarUsuario(dto));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
		UsuarioResponseDTO response = usuarioService.buscarPorId(id);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/username/{username}")
	public ResponseEntity<UsuarioResponseDTO> buscarPorUserName(@PathVariable String username) {
		UsuarioResponseDTO response = usuarioService.buscarPorUserName(username);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/buscar")
	public ResponseEntity<List<UsuarioResponseDTO>> buscarDisponiblesParaMedico(
			@RequestParam(required = false) String criterio) {
		List<UsuarioResponseDTO> listado = usuarioService.buscarUsuarioParaMedicosDisponibles(criterio);
		return listado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(listado);
	}
	
	@PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id) {
        usuarioService.cambiarEstado(id);
        return ResponseEntity.noContent().build();
    }
}
