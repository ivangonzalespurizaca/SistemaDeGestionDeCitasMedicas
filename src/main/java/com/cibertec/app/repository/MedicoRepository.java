package com.cibertec.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.app.entity.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long>{
	
	boolean existsByNroColegiatura(String colegiatura);
	
	@Query("SELECT m FROM Medico m JOIN m.usuario u WHERE "
		    + "(:criterio IS NULL OR TRIM(:criterio) = '') OR "
		    + "(LOWER(u.nombres) LIKE LOWER(CONCAT(:criterio, '%'))) OR "
		    + "(LOWER(u.apellidos) LIKE LOWER(CONCAT(:criterio, '%'))) OR "
		    + "(u.dni LIKE CONCAT(:criterio, '%'))")
		List<Medico> buscarPorCriterio(@Param("criterio") String criterio);
	
	@Query("SELECT m.idMedico FROM Medico m WHERE m.usuario.username = :username")
    Optional<Long> findIdMedicoByUsername(@Param("username") String username);
	
	boolean existsByUsuario_IdUsuario(Long id);
}