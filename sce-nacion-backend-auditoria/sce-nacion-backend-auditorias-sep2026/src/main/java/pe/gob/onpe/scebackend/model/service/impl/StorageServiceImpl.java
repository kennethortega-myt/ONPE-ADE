package pe.gob.onpe.scebackend.model.service.impl;


import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.exeption.ArchivoProcesamientoException;
import pe.gob.onpe.scebackend.model.dto.response.MonitoreoGetFilesResponse;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;
import pe.gob.onpe.scebackend.model.service.StorageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

	Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
	
    @Value("${fileserver.files}")
    private String fileServerFiles;
    
    @Override
    public String getPathUpload() {
      return fileServerFiles;
    }

    public void storeFile(MultipartFile file, String fileName) throws IOException {
        // Normalizar y limpiar el nombre
        String cleanFileName = StringUtils.cleanPath(Objects.requireNonNull(fileName));

        // Construir ruta normalizada
        Path targetPath = Paths.get(fileServerFiles).resolve(cleanFileName).normalize();

        // Validar que la ruta está dentro del directorio permitido
        Path rootPath = Paths.get(fileServerFiles).toAbsolutePath().normalize();
        if (!targetPath.startsWith(rootPath)) {
            throw new SecurityException("Intento de acceso fuera del directorio permitido");
        }

        // Guardar el archivo
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        storeFile(file, fileName);
    }

    public Resource loadFile(String fileName) throws MalformedURLException {
        Path targetPath = Paths.get(fileServerFiles).resolve(fileName).normalize();

        Path rootPath = Paths.get(fileServerFiles).toAbsolutePath().normalize();
        if (!targetPath.startsWith(rootPath)) {
            throw new SecurityException("Acceso denegado fuera del directorio permitido");
        }

        Resource resource = new UrlResource(targetPath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new SecurityException("No se pudo cargar el archivo: " + fileName);
        }

        return resource;
    }

    @Override
    public void saveFile(String fileNameFile, byte[] byteBuffer) {
        String filePathGenerated = fileServerFiles + File.separator+ fileNameFile;
        try(FileOutputStream fileOuputStream = new FileOutputStream(filePathGenerated);) {
            fileOuputStream.write(byteBuffer);
            log.info("Archivo {} guardado en {}",fileNameFile, fileServerFiles);
        } catch (Exception ex) {
            log.error("Error saveFile: fileNameFile {}, detalle error: {}",fileNameFile, ex.getMessage());
        }
    }

    @Override
    public ByteArrayResource loadFileAsByteArrayResource(String fileName) throws IOException {
        Resource resource = loadFile(fileName);
        InputStream inputStream = resource.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        return new ByteArrayResource(bytes);
    }

    @Override
    public String getPathDownloadFiles() {
        return fileServerFiles;
    }

    @Override
    public CompletableFuture<String> loadOnlyFileToBase64(Archivo archivo) throws IOException, ExecutionException, InterruptedException {
        return procesarArchivoAsync(archivo)
                .thenApply(bytesPng -> Base64.getEncoder().encodeToString(bytesPng))
                .exceptionally(ex -> {
                    //logger.error("Error durante la conversión: {}", ex.getMessage());
                    return null;
                });
    }

    @Override
    public MonitoreoGetFilesResponse loadFilesToBase64(Archivo fileId1, Archivo fileId2) throws IOException, ExecutionException, InterruptedException, ArchivoProcesamientoException {
            CompletableFuture<byte[]> acta1Future = procesarArchivoAsync(fileId1);
            CompletableFuture<byte[]> acta2Future = procesarArchivoAsync(fileId2);
            MonitoreoGetFilesResponse dataBase64 = new MonitoreoGetFilesResponse();

            // Inicializar la lista de CompletableFuture
            List<CompletableFuture<byte[]>> futures = new ArrayList<>();

            // Añadir sólo las CompletableFuture no nulas
            futures.add(acta1Future);
            futures.add(acta2Future);

            // Convertir la lista a un array
            CompletableFuture<?>[] futuresArray = futures.toArray(new CompletableFuture<?>[0]);

            // Espera a que todas las tareas se completen
            CompletableFuture.allOf(futuresArray).join();

            if (acta1Future != null && acta2Future != null) {
                dataBase64.setActa1File(Base64.getEncoder().encodeToString(acta1Future.get()));
                dataBase64.setActa2File(Base64.getEncoder().encodeToString(acta2Future.get()));
            }
            if (acta1Future != null && acta2Future == null) {
                dataBase64.setActa1File(Base64.getEncoder().encodeToString(acta1Future.get()));

            }
            if (acta1Future == null && acta2Future != null) {
                dataBase64.setActa2File(Base64.getEncoder().encodeToString(acta2Future.get()));
            }

            return dataBase64;
    }


    private CompletableFuture<byte[]> procesarArchivoAsync(Archivo fileId) {

        File file = this.obtenerArchivoRuta(fileId);
        if (file == null) {
            return CompletableFuture.failedFuture(new ArchivoProcesamientoException("Archivo no encontrado para el ID: " + fileId.getId()));
        }


        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.convertTiffToBytesPng(file);
            } catch (Exception e) {
                throw new ArchivoProcesamientoException("Error al convertir TIFF a PNG", e);
            }
        });
    }
    public byte[] convertTiffToBytesPng(File file) throws IOException {

        try (FileInputStream fis = new FileInputStream(file.getAbsolutePath());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage tiff = ImageIO.read(fis);
            ImageIO.write(tiff, "png", baos);
            return baos.toByteArray();
        }
    }
    
    public File obtenerArchivoDirectorio(String fileId) {
        Path filePath = Paths.get(fileServerFiles).resolve(fileId);
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return null;
        }

        if (!resource.exists() || !resource.isReadable()) {
            return null;
        }

        return new File(filePath.toString());
    }
    
    public File obtenerArchivoRuta(Archivo fileId) {
    	logger.info("Buscando el archivo {} en la ruta {}", fileId.getId(), fileId.getRuta());
    	File file = new File(fileId.getRuta());

        if (!file.exists() || !file.canRead()) {
            logger.warn("El archivo no existe o no se puede leer: {}", fileId.getRuta());
            return null;
        }

        return file;
    }
    
    @Override
    public String detectFileType(MultipartFile file) throws IOException {
      Tika tika = new Tika();
      return tika.detect(file.getInputStream());
    }
}
