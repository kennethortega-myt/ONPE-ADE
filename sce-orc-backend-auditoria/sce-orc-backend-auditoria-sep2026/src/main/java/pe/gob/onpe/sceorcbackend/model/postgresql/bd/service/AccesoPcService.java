package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import org.springframework.data.domain.Page;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.AccesoPcRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.AccesoPcResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AccesoPc;

import java.util.List;

public interface AccesoPcService {

    AccesoPc registrarAcceso(String usuario, String ipAddress);
    boolean esPrimerLogin(String ipAddress);
    List<AccesoPcResponse> listarTodos();
    Page<AccesoPcResponse> listarTodosPaginado(int page, int size);
    List<AccesoPcResponse> listarPorUsuario(String usuario);
    AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, AccesoPcRequest accesoPcRequest, String abrevProceso);
    Boolean solicitarAutorizacion(String usuario, String cc, String nombreCentroComputo, String proceso, AccesoPcRequest accesoPcRequest);
    Boolean actualizarEstado(AccesoPcRequest accesoPcRequest, String usuarioModifica, String codigoCentroComputo);
    byte[] getReportePcs(String acronimoProceso, String usuario, String codigoCentroComputo, String nombreCentroComputo);
}
