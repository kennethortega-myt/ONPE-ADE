package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.CierreCentroComputoResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.EstadoCentroComputoResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.ReaperturaCentroComputoResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.ValidarUsuarioReaperturaResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabCierreCentroComputo;

public interface CierreActividadesService extends CrudService<TabCierreCentroComputo> {
    CierreCentroComputoResponse cerrarCC(String nombreUsuario, String codigoCentroComputo, String motivo, String clave) throws Exception;
    ReaperturaCentroComputoResponse reabrirCC(String nombreUsuario, String codigoCentroComputo, boolean conAutorizacionNacion) throws Exception;
    ValidarUsuarioReaperturaResponse validarUsuarioReapertura(String nombreUsuario, String codigoCentroComputo) throws Exception;
    EstadoCentroComputoResponse consultarEstadoCC(String codigoCentroComputo) throws Exception;

    EstadoCentroComputoResponse consultarEstadoCCAutenticacion(String codigoCentroComputo);

    AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String proceso, String cc);
    Boolean solicitaAutorizacionReapertura(String usuario, String cc, String proceso, String tipoAutorizacion);

    Boolean consultaAutorizacion(String usuario, String cc, String proceso, String tipoAutorizacion);
}
