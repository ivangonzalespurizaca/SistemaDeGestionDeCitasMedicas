package com.cibertec.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cibertec.app.dto.ComprobanteDePagoDetalleDTO;
import com.cibertec.app.dto.ComprobanteDePagoRegistroDTO;
import com.cibertec.app.dto.ComprobanteDePagoResponseDTO;
import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.ComprobanteDePago;
import com.cibertec.app.entity.Medico;
import com.cibertec.app.entity.Paciente;
import com.cibertec.app.entity.Usuario;

@Mapper(componentModel = "spring")
public interface ComprobantePagoMapper {
	@Mapping(target = "idComprobante", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "cita", source = "cita")
    @Mapping(target = "usuarioEmisor", source = "emisor")
    @Mapping(target = "pagador.nombresPagador", source = "dto.nombresPagador")
    @Mapping(target = "pagador.apellidosPagador", source = "dto.apellidosPagador")
    @Mapping(target = "pagador.dniPagador", source = "dto.dniPagador")
    @Mapping(target = "pagador.contactoPagador", source = "dto.contactoPagador")
    ComprobanteDePago toEntityComprobanteDePago(ComprobanteDePagoRegistroDTO dto, Cita cita, Usuario emisor);
	
	@Mapping(target = "idCita", source = "cita.idCita")
	@Mapping(target = "pacienteNombreCompleto", source = "cita.paciente")
    @Mapping(target = "nombresPagador", source = "pagador.nombresPagador")
    @Mapping(target = "apellidosPagador", source = "pagador.apellidosPagador")
    @Mapping(target = "dniPagador", source = "pagador.dniPagador")
	@Mapping(target = "estadoCita", source = "cita.estado")
    ComprobanteDePagoResponseDTO toComprobantePagoResponseDTO(ComprobanteDePago compro);
	
    @Mapping(target = "nombresPagador", source = "pagador.nombresPagador")
    @Mapping(target = "apellidosPagador", source = "pagador.apellidosPagador")
    @Mapping(target = "dniPagador", source = "pagador.dniPagador")
    @Mapping(target = "contactoPagador", source = "pagador.contactoPagador")
    @Mapping(target = "idCita", source = "cita.idCita")
    @Mapping(target = "fechaCita", source = "cita.fecha")
    @Mapping(target = "horaCita", source = "cita.hora")
    @Mapping(target = "motivoCita", source = "cita.motivo")
    @Mapping(target = "pacienteNombreCompleto", source = "cita.paciente")
    @Mapping(target = "medicoNombreCompleto", source = "cita.medico")
    @Mapping(target = "cajeroNombreCompleto", source = "usuarioEmisor")
    ComprobanteDePagoDetalleDTO toComprobantePagoDetalleDTO(ComprobanteDePago compro);
    
    default String mapNombrePaciente(Paciente p) {
        if (p == null) return "Paciente no registrado";
        return p.getNombres() + " " + p.getApellidos();
    }

    default String mapNombreMedico(Medico m) {
        if (m == null || m.getUsuario() == null) return "Médico no asignado";
        return m.getUsuario().getNombres() + " " + m.getUsuario().getApellidos();
    }

    default String mapNombreUsuario(Usuario u) {
        if (u == null) return "Usuario desconocido";
        return u.getNombres() + " " + u.getApellidos();
    }
}
