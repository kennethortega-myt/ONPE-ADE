package pe.gob.onpe.scebackend.model.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.dto.response.DescargaResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.FileStorageResponseDTO;

import java.io.IOException;

public interface IFileStorageService {
    FileStorageResponseDTO save(String guid, MultipartFile file)
            throws IOException;

    FileStorageResponseDTO save(String guid, MultipartFile file, String acronimo)
            throws IOException;

    FileStorageResponseDTO update(String identificador);

    void deleteAllByFolder(String folder) throws IOException;

    DescargaResponseDTO get(Long idArchivo) throws IOException;

    ArchivoDTO get(String identificador, Integer tipoSustento);

    FileStorageResponseDTO token(String identificador, String hash);

    ByteArrayResource getArchivo(Integer identificador, Integer tipoSustento) throws IOException;

    ByteArrayResource getAdjunto(String folder, String archivo) throws IOException;
}
