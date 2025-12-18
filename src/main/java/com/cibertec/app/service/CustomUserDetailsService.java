package com.cibertec.app.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cibertec.app.entity.Usuario;
import com.cibertec.app.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UsuarioRepository usuarioRepository;
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));

        boolean estaHabilitado = (usuario.getEstado() == com.cibertec.app.enums.EstadoUsuario.ACTIVO);

        UserDetails userDetails = User.builder()
                .username(usuario.getUsername())
                .password(usuario.getContrasenia())
                .roles(usuario.getRol().name())
                .disabled(!estaHabilitado)
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(false)
                .build();

        return userDetails;
    }
}
