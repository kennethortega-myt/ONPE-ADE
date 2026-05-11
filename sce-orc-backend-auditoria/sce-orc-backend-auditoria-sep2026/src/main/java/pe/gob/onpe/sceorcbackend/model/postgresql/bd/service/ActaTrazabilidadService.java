package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.trazabilidad.ActaTransmisionNacionTrazabilidadDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;

public interface ActaTrazabilidadService {

	List<ActaTransmisionNacion> traza(Long idActa);
	List<ActaTransmisionNacion> trazaActaExcluidos(Long idActa);
	List<ActaTransmisionNacionTrazabilidadDto> trazaActaConInicioFin(Long idActa);
	
}
