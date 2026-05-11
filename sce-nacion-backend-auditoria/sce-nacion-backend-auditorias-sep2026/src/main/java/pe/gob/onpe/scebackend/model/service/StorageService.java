package pe.gob.onpe.scebackend.model.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.model.dto.response.MonitoreoGetFilesResponse;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface StorageService {

    void storeFile(MultipartFile file) throws IOException;

    void storeFile(MultipartFile file, String filename) throws IOException;

    Resource loadFile(String fileName) throws MalformedURLException;
    
    String getPathUpload();

    void saveFile(String fileNameFile, byte[]  byteBuffer);

    ByteArrayResource loadFileAsByteArrayResource(String fileName) throws IOException;

    String getPathDownloadFiles();
    CompletableFuture<String> loadOnlyFileToBase64(Archivo fileId) throws IOException, ExecutionException, InterruptedException;
    MonitoreoGetFilesResponse loadFilesToBase64(Archivo fileId1, Archivo fileId2)
            throws IOException, ExecutionException, InterruptedException;
    
    File obtenerArchivoRuta(Archivo archivo);
    
    public String detectFileType(MultipartFile file) throws IOException;
}
