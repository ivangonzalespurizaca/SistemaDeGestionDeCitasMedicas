package com.cibertec.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.app.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	Optional<Usuario> findByUsername(String username);
	
	boolean existsByUsername(String username);
	
	@Query("SELECT u FROM Usuario u WHERE "
		       + "u.estado = com.cibertec.app.enums.EstadoUsuario.ACTIVO AND " 
		       + "u.rol = com.cibertec.app.enums.TipoRol.MEDICO AND " 
		       + "u.idUsuario NOT IN (SELECT m.usuario.idUsuario FROM Medico m) AND " 
		       + "(:criterio IS NULL OR TRIM(:criterio) = '' OR "
		       + "LOWER(u.dni) LIKE LOWER(CONCAT(:criterio, '%')) OR "
		       + "LOWER(u.nombres) LIKE LOWER(CONCAT('%', :criterio, '%')) OR " 
		       + "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :criterio, '%')))")
		List<Usuario> buscarUsuariosDisponiblesParaMedico(@Param("criterio") String criterio);
	
	boolean existsByUsernameAndIdUsuarioNot(String username, Long idUsuario);
}
