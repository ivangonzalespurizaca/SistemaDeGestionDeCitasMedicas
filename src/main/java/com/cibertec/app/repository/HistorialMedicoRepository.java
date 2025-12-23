package com.cibertec.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.app.entity.HistorialMedico;

public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long>{
	@Query("SELECT h FROM HistorialMedico h " +
	           "JOIN h.cita c " +
	           "JOIN c.paciente p " +
	           "WHERE (:criterio IS NULL OR :criterio = '' " +
	           "OR p.dni LIKE CONCAT('%', :criterio, '%')" +
	           "OR UPPER(p.nombres) LIKE UPPER(CONCAT('%', :criterio, '%')) " +
	           "OR UPPER(p.apellidos) LIKE UPPER(CONCAT('%', :criterio, '%')))" +
	           "ORDER BY c.fecha DESC, c.hora DESC")
	    List<HistorialMedico> buscarPorPacienteOOrderByFechaDesc(@Param("criterio") String criterio);
	
	List<HistorialMedico> findAllByOrderByCita_FechaDescCita_HoraDesc();
}
