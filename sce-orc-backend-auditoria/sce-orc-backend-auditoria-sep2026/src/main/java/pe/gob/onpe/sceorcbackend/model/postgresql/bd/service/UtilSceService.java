package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.functionalinterface.ManejadorVotoIlegible;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaCelesteInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.MesaInfo;

import java.util.List;
import java.util.Optional;

public interface UtilSceService {

    Archivo guardarArchivoPdf(byte[] archivoPdf, String nombreArchivo, TokenInfo tokenInfo);

    String getVersionSistema();

    String getSinValorOficial();

    String getSinValorOficial(Integer idProceso);

    <T> void procesarYGuardarDetActaOpcion(Acta acta, T votoOpcion, Optional<DetActaOpcion> optionalDetActaOpcion,
                                                   List<DetActaOpcion> listaErrores, String usuario, ManejadorVotoIlegible<T> manejador);

    MesaInfo validarMesa(String nroMesa);

    MesaInfo validarMesaConEleccionPrincipal(String nroMesa);

    ActaInfo validarActa(String codigoBarras, String codigoCentroComputo, boolean isUploadDigtal);
    
    ActaCelesteInfo validarActaCeleste(String codigoBarras, String codigoCentroComputo, String usuario);
    
    Optional<ActaCeleste> findActaCelesteByActaId(Long actaId);

    Integer obtenerCantidadCandidatos(String esquema, Long idActa);

    void inactivarArchivo(Archivo archivo, TokenInfo tokenInfo);
    void validarGuidUnico(TokenInfo tokenInfo, MultipartFile file) ;

    void validarNombreArchivoUnico(MultipartFile file);

    void storeFile(MultipartFile file, String fileName);

    void validarMesaNoInstalada(Mesa mesa);

    void guardarActa(Acta acta);
    
    void guardarActaCeleste(ActaCeleste acta);

    String validarArchivoEscaneado(MultipartFile file, String tipoDocumento, String numeroMesa);

    void validarCodigoBarrasActas(String codeBar);

    void validarNumeroResolucion(String numeroResolucion);

    void validarPdfSeguro(byte[] fileBytes);

    void validarTiffSeguro(byte[] fileBytes);

    void validarNumeroMesa(String nroMesa);

    String obtenerNombreProcesoByAcronimo(String acronimo);

    String getResolucionJne();

}
