package com.cibertec.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cibertec.app.dto.UsuarioActualizacionDTO;
import com.cibertec.app.dto.UsuarioRegistroDTO;
import com.cibertec.app.dto.UsuarioResponseDTO;
import com.cibertec.app.dto.UsuarioVistaModificarDTO;
import com.cibertec.app.enums.TipoRol;
import com.cibertec.app.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("api/administrador/usuarios")
@RequiredArgsConstructor
@RestController
public class UsuarioController {
	public final UsuarioService usuarioService;
	
	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
			@Valid @ModelAttribute UsuarioRegistroDTO dto,
			@RequestParam(required = false) MultipartFile archivo){
		UsuarioResponseDTO response = usuarioService.registrarUsuario(dto, archivo);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id, 
            @Valid @ModelAttribute UsuarioActualizacionDTO dto,
            @RequestParam(required = false) MultipartFile archivo) {
        dto.setIdUsuario(id); 
		return ResponseEntity.ok(usuarioService.actualizarUsuario(dto, archivo));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
		UsuarioResponseDTO response = usuarioService.buscarPorId(id);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/username/{username}")
	public ResponseEntity<UsuarioVistaModificarDTO> buscarPorUserName(@PathVariable String username) {
		UsuarioVistaModificarDTO response = usuarioService.buscarPorUserName(username);
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
	
	@GetMapping("/roles")
	public ResponseEntity<List<Map<String, String>>> listarRoles() {
	    List<Map<String, String>> roles = Arrays.stream(TipoRol.values())
	            .filter(rol -> !rol.name().equalsIgnoreCase("ADMINISTRADOR") && 
	                           !rol.name().equalsIgnoreCase("ROLE_ADMINISTRADOR"))
	            .map(m -> Map.of(
	                "value", m.name(), 
	                "label", m.name().replace("_", " ") 
	            )).toList();
	    return ResponseEntity.ok(roles);
	}
}
