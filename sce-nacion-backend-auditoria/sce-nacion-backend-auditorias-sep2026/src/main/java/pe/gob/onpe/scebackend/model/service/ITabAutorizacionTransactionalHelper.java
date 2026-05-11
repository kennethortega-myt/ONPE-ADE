package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.orc.entities.TabAutorizacion;

public interface ITabAutorizacionTransactionalHelper {
    TabAutorizacion save2(TabAutorizacion tab);
    AutorizacionNacionResponseDto recibirAutorizacion(String usuario, String tipoAutorizacion);
    TabAutorizacion registrarAutorizacion(String usuario, String tipo, String detalle);
}
