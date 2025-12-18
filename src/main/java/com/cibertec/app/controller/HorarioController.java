package com.cibertec.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.app.dto.HorarioProyeccionDTO;
import com.cibertec.app.dto.HorarioRegistroDTO;
import com.cibertec.app.dto.HorarioResponseDTO;
import com.cibertec.app.service.HorarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/administrador/horarios")
@RequiredArgsConstructor
public class HorarioController {

	private final HorarioService horarioService;
	
	@PostMapping
    public ResponseEntity<HorarioResponseDTO> registrar(@Valid @RequestBody HorarioRegistroDTO dto) {
        HorarioResponseDTO response = horarioService.registrarHorario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
	
	@GetMapping("/medico/{idMedico}")
    public ResponseEntity<List<HorarioResponseDTO>> buscarPorMedico(@PathVariable Long idMedico) {
        List<HorarioResponseDTO> listado = horarioService.buscarHorariosPorIdMedico(idMedico);
        return listado.isEmpty() ? ResponseEntity.noContent().build() :  ResponseEntity.ok(listado);
    }
	
	@GetMapping("/{idMedico}")
    public ResponseEntity<List<HorarioProyeccionDTO>> proyeccionPorMedico(@PathVariable Long idMedico) {
        List<HorarioProyeccionDTO> listado = horarioService.proyectarHorariosPorIdMedico(idMedico);
        return listado.isEmpty() ? ResponseEntity.noContent().build() :  ResponseEntity.ok(listado);
    }
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioService.eliminarPorID(id);
        return ResponseEntity.noContent().build();
    }
}
