package com.cibertec.app.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.SlotHorario;
import com.cibertec.app.enums.EstadoSlotHorario;

public interface SlotHorarioRepository extends JpaRepository<SlotHorario, Long>{
	
	List<SlotHorario> findByMedico_IdMedicoAndFechaOrderByHoraAsc(Long idMedico, LocalDate fecha);
	
	@Query("SELECT DISTINCT s.fecha FROM SlotHorario s " +
		       "WHERE s.medico.idMedico = :idMedico " +
		       "AND s.fecha >= CURRENT_DATE " +
		       "AND s.estadoSlot = com.cibertec.app.enums.EstadoSlotHorario.DISPONIBLE")
	List<LocalDate> findFechasConSlotsDisponibles(@Param("idMedico") Long idMedico);
	
	@Query("SELECT s FROM SlotHorario s " +
	           "LEFT JOIN FETCH s.cita c " +
	           "LEFT JOIN FETCH c.paciente p " +
	           "WHERE s.medico.idMedico = :idMedico " +
	           "AND s.fecha = :fecha " +
	           "ORDER BY s.hora ASC")
	List<SlotHorario> obtenerVistaRecepcionCompleta(@Param("idMedico") Long idMedico, @Param("fecha") LocalDate fecha);
	
	Optional<SlotHorario> findByCita(Cita cita);

	    @Modifying
	    @Query("DELETE FROM SlotHorario s WHERE " +
	           "s.medico.idMedico = :idMedico AND " +
	           "s.hora = :hora AND " +
	           "s.fecha >= :fechaActual AND " +
	           "s.estadoSlot = :estado AND " +
	           "FUNCTION('DAYOFWEEK', s.fecha) = :indiceDia")
	    void eliminarSlotsDisponiblesSegunDia(
	        @Param("idMedico") Long idMedico, 
	        @Param("hora") LocalTime hora, 
	        @Param("fechaActual") LocalDate fechaActual, 
	        @Param("estado") EstadoSlotHorario estado,
	        @Param("indiceDia") int indiceDia
	    );
}
