package pe.gob.onpe.scebackend.model.service;

import org.springframework.data.domain.Page;
import pe.gob.onpe.scebackend.model.dto.request.AccesoPcRequest;
import pe.gob.onpe.scebackend.model.dto.response.AccesoPcResponse;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;

public interface IAccesoPcService {
    void registrarAcceso(String usuario, String ip);
    boolean esPrimerLogin(String ip);
    Page<AccesoPcResponse> listarTodosPaginado(int page, int size);
    AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, AccesoPcRequest accesoPcRequest);
    Boolean solicitarAutorizacion(String usuario, AccesoPcRequest accesoPcRequest);
    Boolean actualizarEstado(AccesoPcRequest accesoPcRequest, String usuarioModifica);
    byte[] getReportePcs(String usuario);
}
