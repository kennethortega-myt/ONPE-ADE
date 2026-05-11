package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.io.IOException;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.ProcesarResolucionRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationGetFilesResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.SeguimientoOficioDTO;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Oficio;

public interface OficioService extends CrudService<Oficio>{

	GenericResponse<Object> generarOficio(TokenInfo tokenInfo, List<ActaBean> actaBeanList) throws IOException;

	GenericResponse<Object> verificarDocumentoEnvio(TokenInfo tokenInfo, ActaBean actaBean, String tipoDocumento);

	GenericResponse<DigitizationGetFilesResponse> obtenerArchivosSobre(TokenInfo tokenInfo, ActaBean actaBean, String tipoSobre);

	GenericResponse<Boolean> transmitirOficio(Long idActa, String proceso, TransmisionNacionEnum estadoEnum, String usuario);

	List<SeguimientoOficioDTO> obtenerSeguimiento(TokenInfo tokenInfo);

	GenericResponse<Object> procesarResolucion(ProcesarResolucionRequest request, TokenInfo tokenInfo);

}
