package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.DetOtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.OtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.ResumenOtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabOtroDocumento;
import java.util.List;

public interface CabOtroDocumentoService extends CrudService<CabOtroDocumento>{

    void deleteAllInBatch();

    void registrarNuevoDocumento(TokenInfo tokenInfo, Long idOtroDocumento ,String numeroDocumento, String abreviaturaDocumento, Integer numeroPaginas,MultipartFile file);

    List<OtroDocumentoDto> listarOtrosDocumentosDigitalizados(String nombreUsuario);

    List<OtroDocumentoDto> listarControlDigitalizacion(String nombreUsuario, String abreviaturaDocumento);

    void actualizarEstadoDigitalizacion(String nombreUsuario, Long idOtrosDocumento, String estadoDigitalizacion);

    ResumenOtroDocumentoDto resumenOtrosDocumentos(String numeroDocumento, String estadoDocumento, String estadoDigitalizacion);

    void registrarAsociacion(TokenInfo tokenInfo, OtroDocumentoDto otroDocumentoDto);

    DetOtroDocumentoDto validarMesaParaAsociacion(TokenInfo tokenInfo,DetOtroDocumentoDto detOtroDocumentoDto);

    void procesarAsociacion(TokenInfo tokenInfo, OtroDocumentoDto otroDocumentoDto);

    void anularDocumento(TokenInfo tokenInfo, OtroDocumentoDto otroDocumentoDto);
}
