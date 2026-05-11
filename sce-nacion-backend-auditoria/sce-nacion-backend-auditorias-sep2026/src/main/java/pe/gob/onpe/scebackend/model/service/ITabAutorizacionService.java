package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ResponseAutorizacionDTO;
import pe.gob.onpe.scebackend.model.orc.entities.TabAutorizacion;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITabAutorizacionService {
    List<TabAutorizacion> listAutorizaciones();

    Optional<TabAutorizacion> validaAutorizacion(String usr, String tipoAutorizacion);
    Optional<TabAutorizacion> validaAutorizacion(String usuario, String tipoAutorizacion, String tipoDocumento, Long idDocumento, String codigoCentroComputo);
    TabAutorizacion save(TabAutorizacion tab);
    Boolean aprobacionAutorizacion(Long id, String usuario);
    Boolean rechazarAutorizacion(Long id, String usuario);

    ResponseAutorizacionDTO verificarAutorizacion(Date fechaConvocatoria, String usuario, String tipoAutorizacion, String titulo, String detalleAutorizacion) throws GenericException;

    ResponseAutorizacionDTO verificarAutorizacionv2(Date fechaConvocatoria, String usuario, String tipoAutorizacion, String titulo, String detalleAutorizacion) throws GenericException;
}
