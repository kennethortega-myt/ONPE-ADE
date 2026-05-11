package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import java.io.File;
import java.util.List;
import java.util.Optional;

public interface ArchivoService extends CrudService<Archivo> {

    List<Archivo> findByNombre(String nombre);

    List<Archivo> findByNombreAndActivo(String nombre, Integer activo);

    List<Archivo> findByGuidAndActivo(String gui, Integer activo);

    Optional<Archivo> findById(Long idArchivo);
    Archivo getArchivoById(Long id);

    List<Archivo> findAllById(List<Long> ids);

    void deleteAllInBatch();

    void deleteAllOptimized();

    Archivo guardarArchivo(MultipartFile file, String usuario, String codigoCentroComputo, Optional<Integer> codigoDocumentoElectoralPr);

    String getPathUpload();


    void storeFile(MultipartFile file, String fileName);

    void storeFile(File file, String fileName);

    Archivo saveArchivo(String fileName, Long sizeFile, String guid, String usuario, String detectedType);

    boolean validarExtraerContenidoZipLe(Mesa mesa, String nombreArchivo);

    void validarExtraerContenidoZipMm(Mesa mesa, String guid);

    String obtegerUui(MultipartFile file, TokenInfo tokenInfo);

    void renameFile(String oldFileName, String newFileName);

    void deleteFile(String fileName);

    long contarArchivosOld(String guidOriginal);
}
