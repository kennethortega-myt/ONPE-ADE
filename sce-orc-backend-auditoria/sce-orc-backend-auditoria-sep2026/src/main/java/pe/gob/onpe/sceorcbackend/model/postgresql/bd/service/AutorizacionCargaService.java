package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;

public interface AutorizacionCargaService {

	Boolean solicitaAutorizacionImportacion(String usuario, String cc, String proceso, String tipoAutorizacion);
	AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, String proceso, String tipoAutorizacion);
}
