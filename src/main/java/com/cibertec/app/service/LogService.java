package com.cibertec.app.service;

import com.cibertec.app.entity.Cita;
import com.cibertec.app.entity.Usuario;
import com.cibertec.app.enums.Accion;

public interface LogService {
	void registrarLog(Cita cita, Accion accion, String detalle, Usuario usuario);
}
