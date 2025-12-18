package com.cibertec.app.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cibertec.app.dto.LoginRequest;
import com.cibertec.app.service.EspecialidadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestController {

	public final EspecialidadService especialidadService;
	@Autowired
    private AuthenticationManager authenticationManager;
	

	    @GetMapping("/login")
	    public ResponseEntity<String> login() {
	        return ResponseEntity.ok("LOGIN OK");
	    }
	    
	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
	        try {
	            Authentication auth = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
	            );
	            SecurityContextHolder.getContext().setAuthentication(auth);
	            Map<String, Object> data = new HashMap<>();
	            data.put("username", auth.getName());
	            data.put("roles", auth.getAuthorities());
	            return ResponseEntity.ok(data);
	        } catch (Exception e) {
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
