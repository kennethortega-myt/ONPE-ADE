package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;

public interface AutorizacionGenericaService {

    AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String tipoAutorizacion);

    Boolean solicitaAutorizacionNacion(String usuario, String tipoAutorizacion);

}
