package com.cibertec.app.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cibertec.app.dto.LoginRequest;
import com.cibertec.app.entity.Usuario;
import com.cibertec.app.repository.UsuarioRepository;
import com.cibertec.app.service.EspecialidadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestController {

	public final EspecialidadService especialidadService;
	public final UsuarioRepository usuarioRepository;
	
	@Autowired
    private AuthenticationManager authenticationManager;
	

	    @GetMapping("/login")
	    public ResponseEntity<String> login() {
	        return ResponseEntity.ok("LOGIN OK");
	    }
	    
	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
	        try {
	            // 1. Autenticar credenciales
	            Authentication auth = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
	            );
	            
	            // 2. Establecer el contexto de seguridad
	            SecurityContextHolder.getContext().setAuthentication(auth);
	            
	            // 3. Persistir la sesión en el repositorio de Spring Security
	            HttpSession session = request.getSession(true);
	            session.setAttribute(
	                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
	                SecurityContextHolder.getContext()
	            );

	            // 4. NUEVO: Buscar los datos completos del usuario en la base de datos
	            Usuario usuario = usuarioRepository.findByUsername(req.getUsername())
	                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado post-autenticación"));

	            // 5. Preparar la respuesta para el Frontend (Proyectar credenciales)
	            Map<String, Object> data = new HashMap<>();
	            data.put("idUsuario", usuario.getIdUsuario());
	            data.put("username", usuario.getUsername());
	            data.put("nombres", usuario.getNombres());
	            data.put("apellidos", usuario.getApellidos());
	            data.put("rol", usuario.getRol().name());
	            data.put("imgPerfil", usuario.getImgPerfil()); // Aquí va la foto para tu Layout

	            return ResponseEntity.ok(data);

	        } catch (Exception e) {
	            // Log del error para depuración
	            System.out.println("Error en Login: " + e.getMessage());
	            return ResponseEntity.status(401).body("Credenciales incorrectas");
	        }
	    }

	    
	    @GetMapping("/me")
	    public ResponseEntity<?> me(Authentication auth) {
	        Map<String, Object> data = new HashMap<>();
	        data.put("username", auth.getName());
	        data.put("roles", auth.getAuthorities());
	        return ResponseEntity.ok(data);
	    }


    
}
