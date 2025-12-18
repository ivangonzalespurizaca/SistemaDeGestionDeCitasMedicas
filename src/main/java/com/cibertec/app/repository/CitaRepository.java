package com.cibertec.app.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.app.entity.Cita;
import com.cibertec.app.enums.EstadoCita;

public interface CitaRepository extends JpaRepository<Cita, Long>{
	
//	List<Cita> findByPaciente_DniStartingWith(String dni);
//	
//	List<Cita> findByPaciente_NombresStartingWith(String nombre);
	
	
	@Query("SELECT c FROM Cita c " +
	           "JOIN FETCH c.paciente p " +
	           "JOIN FETCH c.medico m " +
	           "JOIN FETCH m.usuario mu " +
	           "JOIN FETCH m.especialidad e " +
	           "WHERE :filtro IS NULL OR :filtro = '' " +
	           "OR p.dni LIKE CONCAT('%', :filtro, '%') " +
	           "OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
	           "OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
	           "ORDER BY c.fecha DESC, c.hora DESC")
	    List<Cita> buscarPorCriterioGlobal(@Param("filtro") String filtro);
	
	
	
	List<Cita> findByMedico_IdMedicoAndFechaAndHora(Long id, LocalDate fecha, LocalTime hora);
	
	List<Cita> findByMedico_IdMedicoAndFecha(Long id, LocalDate fecha);
	
	List<Cita> findByEstado(EstadoCita estado);
	
	@Query("SELECT c FROM Cita c " +
		       "JOIN FETCH c.paciente p " +
		       "JOIN FETCH c.medico m " +
		       "WHERE c.estado = :estado " +
		       "AND (:filtro IS NULL OR :filtro = '' " +
		       "OR p.dni LIKE CONCAT('%', :filtro, '%') " +
		       "OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
		       "OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :filtro, '%')))")
		List<Cita> findPendientesByPaciente(@Param("filtro") String filtro, @Param("estado") EstadoCita estado);
}
