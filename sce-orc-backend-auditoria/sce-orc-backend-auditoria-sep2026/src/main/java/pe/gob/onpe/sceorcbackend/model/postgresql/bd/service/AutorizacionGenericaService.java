package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;

public interface AutorizacionGenericaService {

    AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, String proceso, String tipoAutorizacion);
    Boolean solicitaAutorizacionNacion(String usuario, String cc, String proceso, String tipoAutorizacion);
}
