package com.cibertec.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.SlotHorario;

public interface SlotHorarioRepository extends JpaRepository<SlotHorario, Long>{
	
	List<SlotHorario> findByMedico_IdMedicoAndFechaOrderByHoraAsc(Long idMedico, LocalDate fecha);
	
	@Query("SELECT s FROM SlotHorario s " +
	           "LEFT JOIN FETCH s.cita c " +
	           "LEFT JOIN FETCH c.paciente p " +
	           "WHERE s.medico.idMedico = :idMedico " +
	           "AND s.fecha = :fecha " +
	           "ORDER BY s.hora ASC")
	List<SlotHorario> obtenerVistaRecepcionCompleta(@Param("idMedico") Long idMedico, @Param("fecha") LocalDate fecha);
	
	Optional<SlotHorario> findByCita(Cita cita);
	
}
