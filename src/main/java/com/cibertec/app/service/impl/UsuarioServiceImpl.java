package com.cibertec.app.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cibertec.app.dto.UsuarioActualizacionDTO;
import com.cibertec.app.dto.UsuarioRegistroDTO;
import com.cibertec.app.dto.UsuarioResponseDTO;
import com.cibertec.app.dto.UsuarioVistaModificarDTO;
import com.cibertec.app.entity.Usuario;
import com.cibertec.app.enums.EstadoUsuario;
import com.cibertec.app.enums.TipoRol;
import com.cibertec.app.mapper.UsuarioMapper;
import com.cibertec.app.repository.UsuarioRepository;
import com.cibertec.app.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService{
	
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final UsuarioMapper usuarioMapper;
	
	@Transactional
	@Override
	public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto, MultipartFile archivo) {
		
		if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
		
		if (dto.getRol() == TipoRol.ADMINISTRADOR) {
	        throw new IllegalArgumentException("No tiene permisos para registrar usuarios con el rol ADMINISTRADOR.");
	    }
		
		Usuario entity = usuarioMapper.toEntityUsuario(dto);
		String hash = passwordEncoder.encode(dto.getContrasenia());
		entity.setContrasenia(hash);
		
		if (archivo != null && !archivo.isEmpty()) {
	        try {
	            String nombreArchivo = guardarArchivoLocal(archivo);
	            entity.setImgPerfil(nombreArchivo); 
	        } catch (IOException e) {
	            throw new RuntimeException("Error al guardar la imagen de perfil: " + e.getMessage());
	        }
	    }
		
		Usuario guardado = usuarioRepository.save(entity);
		
		return usuarioMapper.toUsuarioResponseDTO(guardado);
		
	}
	
	@Transactional
	@Override
	public UsuarioResponseDTO actualizarUsuario(UsuarioActualizacionDTO dto, MultipartFile archivo) {
		
		Usuario entity = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		
		if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
	        boolean yaExisteEnOtro = usuarioRepository.existsByUsernameAndIdUsuarioNot(
	                dto.getUsername(), 
	                dto.getIdUsuario()
	        );

	        if (yaExisteEnOtro) {
	            throw new IllegalArgumentException("El nombre de usuario ya está registrado por otra cuenta.");
	        }
	    }	
		
		usuarioMapper.toUsuarioUpdate(dto, entity);
		
		String nuevaClave = dto.getContrasenia();
		
		if (nuevaClave != null && !nuevaClave.trim().isEmpty()) {
	        if (nuevaClave.length() < 4) {
	            throw new IllegalArgumentException("La nueva clave debe tener al menos 4 caracteres.");
	        }
	        entity.setContrasenia(passwordEncoder.encode(nuevaClave));
	    }
		
		if (archivo != null && !archivo.isEmpty()) {
	        try {
	            // Opcional: Eliminar la foto anterior físicamente si existe
	            if (entity.getImgPerfil() != null) {
	                Path rutaAnterior = Paths.get("uploads/perfiles/").resolve(entity.getImgPerfil());
	                Files.deleteIfExists(rutaAnterior);
	            }

	            // Guardar la nueva imagen
	            String nombreArchivo = guardarArchivoLocal(archivo);
	            entity.setImgPerfil(nombreArchivo); 
	        } catch (IOException e) {
	            throw new RuntimeException("Error al actualizar la imagen de perfil: " + e.getMessage());
	        }
	    }
        
        Usuario actualizado = usuarioRepository.save(entity);
        
		return usuarioMapper.toUsuarioResponseDTO(actualizado);
		
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuarioVistaModificarDTO buscarPorUserName(String userName) {
		Usuario entity = usuarioRepository.findByUsername(userName) 
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        
        return usuarioMapper.toVistaModificarDTO(entity);
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuarioResponseDTO buscarPorId(Long id) {
		Usuario entity = usuarioRepository.findById(id)
				.orElseThrow(()-> new NoSuchElementException("Usuario no encontrado"));
		return usuarioMapper.toUsuarioResponseDTO(entity);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuarioResponseDTO> buscarUsuarioParaMedicosDisponibles(String dni) {
		String dniBusqueda = (dni != null && !dni.trim().isEmpty()) ? dni.trim() : null;
	    
	    List<Usuario> usuariosEncontrados = usuarioRepository.buscarUsuariosDisponiblesParaMedico(dniBusqueda);
	    
	    return usuariosEncontrados.stream()
	            .map(usuarioMapper::toUsuarioResponseDTO).toList();
	}

	@Transactional
	@Override
	public void cambiarEstado(Long id) {
		Usuario entity = usuarioRepository.findById(id)
	            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		
		String usernameLogueado = org.springframework.security.core.context.SecurityContextHolder
	            .getContext().getAuthentication().getName();
		
		if (entity.getUsername().equals(usernameLogueado)) {
	        throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
	    }
	    
	    if (entity.getEstado() == EstadoUsuario.ACTIVO) {
	        entity.setEstado(EstadoUsuario.INACTIVO);
	    } else {
	        entity.setEstado(EstadoUsuario.ACTIVO);
	    }
	    
	    usuarioRepository.save(entity);
	}
	
	private String guardarArchivoLocal(MultipartFile archivo) throws IOException {
	    String carpetaDestino = "uploads/perfiles/";
	    Path pathDestino = Paths.get(carpetaDestino);
	    
	    if (!Files.exists(pathDestino)) {
	        Files.createDirectories(pathDestino);
	    }

	    // 1. Extraer la extensión del archivo original (ej: .png, .jpg)
	    String nombreOriginal = archivo.getOriginalFilename();
	    String extension = "";
	    if (nombreOriginal != null && nombreOriginal.contains(".")) {
	        extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
	    }

	    // 2. Generar nombre puramente único sin espacios ni caracteres raros
	    // Esto evita nombres como "UUID_Captura de pantalla.png" que rompen la URL
	    String nombreUnico = UUID.randomUUID().toString() + extension.toLowerCase();
	    
	    Path rutaCompleta = pathDestino.resolve(nombreUnico);

	    // 3. Copiar el archivo
	    Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

	    return nombreUnico; 
	}
}
