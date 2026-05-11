package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;

public interface ReprocesarService {
    AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, String proceso);
    Boolean solicitaAutorizacionReprocesar(String usuario, String cc, String proceso);
}
