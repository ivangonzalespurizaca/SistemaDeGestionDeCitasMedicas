package com.cibertec.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.app.entity.ComprobanteDePago;

public interface ComprobanteDePagoRepository extends JpaRepository<ComprobanteDePago, Long>{
	
	@Query("SELECT c FROM ComprobanteDePago c " +
	           "JOIN FETCH c.cita ci " +
	           "JOIN FETCH ci.paciente p " +
	           "WHERE (:criterio IS NULL OR :criterio = '' " +
	           "OR c.pagador.dniPagador LIKE CONCAT('%', :criterio, '%')" +
	           "OR LOWER(c.pagador.nombresPagador) LIKE LOWER(CONCAT('%', :criterio, '%')) " +
	           "OR LOWER(c.pagador.apellidosPagador) LIKE LOWER(CONCAT('%', :criterio, '%')))"+
				"ORDER BY c.fechaEmision DESC")
	    List<ComprobanteDePago> buscarPorCriterio(@Param("criterio") String criterio);
	
	List<ComprobanteDePago> findAllByOrderByFechaEmisionDesc();
}
