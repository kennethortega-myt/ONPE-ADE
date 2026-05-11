package pe.gob.onpe.sceorcbackend.rest.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.exception.NotFoundException;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationGetFilesResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ArchivoTransmisionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.utils.ArchivoUtils;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesFormatos;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@RestController
@RequestMapping("/archivo")
public class ArchivoController {

    private static final Logger logger = LoggerFactory.getLogger(ArchivoController.class);

    private final StorageService storageService;
    private final ArchivoService archivoService;

    public ArchivoController(StorageService storageService, ArchivoService archivoService) {
        this.storageService = storageService;
        this.archivoService = archivoService;
    }

    @Value("${file.upload-dir}")
    private String imagePath;

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/file/base64/{id}")
    public ResponseEntity<ArchivoTransmisionDto> getFileBase64(@PathVariable("id") Long id) {
        Optional<Archivo> archivoOptionalarchivo = this.archivoService.findById(id);
        ArchivoTransmisionDto archivoDto = null;
        if(archivoOptionalarchivo.isEmpty()) return new ResponseEntity<>(archivoDto, HttpStatus.OK);
        archivoDto = ArchivoUtils.convertToBase64(archivoOptionalarchivo.get(), storageService.getPathUpload());
        return new ResponseEntity<>(archivoDto, HttpStatus.OK);
    }


    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/file/{id}")
    public ResponseEntity<InputStreamResource> getFileDigitalizacion(@PathVariable("id") Long id) {
        try {
            Archivo archivo = this.archivoService.findById(id)
                    .orElseThrow(() -> new NotFoundException(ConstantesComunes.MENSAJE_FILE_NOT_FOUND));

            //logger.info("Iniciando descarga del archivo ID: {}, GUID: {}", id, archivo.getGuid());

            Resource res = this.storageService.loadFile(archivo.getGuid(), false);
            
            File file = res.getFile();
            long fileSize = file.length();

            //logger.info("Archivo ID: {} cargado, GUID: {}, tamaño: {} bytes", id, archivo.getGuid(), fileSize);

            InputStream inputStream = new LoggingInputStream(new FileInputStream(file), id, archivo.getGuid());

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", archivo.getNombre()))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                .contentType(MediaType.parseMediaType(archivo.getFormato()))
                .body(new InputStreamResource(inputStream));

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException(String.format("Failed to download the file. Error: %s", e.getMessage()));
        }
    }

    /**
     * InputStream wrapper que loguea cuando se completa la lectura del archivo
     */
    private static class LoggingInputStream extends FilterInputStream {
        private final Long archivoId;
        private final String guid;
        private long bytesRead = 0;

        protected LoggingInputStream(InputStream in, Long archivoId, String guid) {
            super(in);
            this.archivoId = archivoId;
            this.guid = guid;
        }

        @Override
        public int read() throws IOException {
            int b = super.read();
            if (b != -1) {
                bytesRead++;
            }
            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int count = super.read(b, off, len);
            if (count > 0) {
                bytesRead += count;
            }
            return count;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
                logger.info("Descarga completada del archivo ID: {}, GUID: {}, bytes transferidos: {}", 
                        archivoId, guid, bytesRead);
            } catch (IOException e) {
                throw new IOException(String.format("Error al cerrar stream del archivo ID: %d, GUID: %s", archivoId, guid), e);
            }
        }
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @PostMapping("/filesPng")
    public ResponseEntity<GenericResponse<DigitizationGetFilesResponse>> getFilesPng(
        @RequestParam(value = "acta1FileId") Long acta1FileId,
        @RequestParam(value = "acta2FileId") Long acta2FileId
    ) {
        GenericResponse<DigitizationGetFilesResponse> response = new GenericResponse<>();
        DigitizationGetFilesResponse dataBase64 = new DigitizationGetFilesResponse();
        try {

            Archivo tabArchivoFile1 = validateAndGetFile(acta1FileId, response);
            Archivo tabArchivoFile2 = validateAndGetFile(acta2FileId, response);

            if (tabArchivoFile1 == null || tabArchivoFile2 == null) {
                handleSingleFileCase(tabArchivoFile1, tabArchivoFile2, dataBase64, response);
            } else {
                handleBothFilesCase(tabArchivoFile1, tabArchivoFile2, response);
            }

            return createResponseEntity(response);

        } catch (IOException | ExecutionException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private ResponseEntity<GenericResponse<DigitizationGetFilesResponse>> createResponseEntity(
        GenericResponse<DigitizationGetFilesResponse> response) {
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    private void handleSingleFileCase(Archivo file1, Archivo file2, DigitizationGetFilesResponse dataBase64,
                                      GenericResponse<DigitizationGetFilesResponse> response) throws IOException, ExecutionException, InterruptedException {
        if (file1 != null) {
            processFile(file1, dataBase64::setActa1File, response);
        }
        if (file2 != null) {
            processFile(file2, dataBase64::setActa2File, response);
        }
    }

    private void processFile(Archivo fileId, Consumer<String> fileSetter, GenericResponse<DigitizationGetFilesResponse> response)
        throws IOException, ExecutionException, InterruptedException {

        this.storageService.loadOnlyFileToBase64(fileId).thenAccept(dataOnlyFileBase64 -> {
            if (dataOnlyFileBase64 == null) {
                response.setMessage(ConstantesComunes.MENSAJE_ARCHIVO_NO_ENCONTRADO + fileId);
                response.setSuccess(false);
            } else {
                fileSetter.accept(dataOnlyFileBase64);
                response.setSuccess(true);
            }
        });
    }


    private void handleBothFilesCase(Archivo acta1FileId, Archivo acta2FileId,
                                     GenericResponse<DigitizationGetFilesResponse> response)
        throws IOException, ExecutionException, InterruptedException {

        DigitizationGetFilesResponse dataBase64 = this.storageService.loadFilesToBase64(acta1FileId, acta2FileId);
        if (dataBase64.getActa1File() == null && dataBase64.getActa2File() == null) {
            response.setMessage("Archivos no encontrados: " + acta1FileId + " " + acta2FileId);
            response.setSuccess(false);
        } else {
            setResponseMessage(dataBase64, acta1FileId.getId(), acta2FileId.getId(), response);
            response.setSuccess(true);
        }
    }

    private void setResponseMessage(DigitizationGetFilesResponse dataBase64, Long acta1FileId, Long acta2FileId,
                                    GenericResponse<DigitizationGetFilesResponse> response) {
        if (dataBase64.getActa1File() == null && dataBase64.getActa2File() != null) {
            response.setMessage(ConstantesComunes.MENSAJE_ARCHIVO_NO_ENCONTRADO + acta1FileId);
        } else if (dataBase64.getActa1File() != null && dataBase64.getActa2File() == null) {
            response.setMessage(ConstantesComunes.MENSAJE_ARCHIVO_NO_ENCONTRADO + acta2FileId);
        }
        response.setData(dataBase64);
    }

    private Archivo validateAndGetFile(Long fileId, GenericResponse<DigitizationGetFilesResponse> response) {
        Optional<Archivo> optionalArchivo = this.archivoService.findById(fileId);
        if (optionalArchivo.isEmpty()) {
            response.setMessage(fileId + " no encontrado en la base de datos.");
            return null;
        }
        return optionalArchivo.get();
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/file-pdf/{id}")
    public ResponseEntity<byte[]> getFileResolucionPDF(@PathVariable("id") Long id) throws IOException {
        Archivo archivo = this.archivoService.getArchivoById(id);
        if (archivo == null) {
            throw new NotFoundException(ConstantesComunes.MENSAJE_FILE_NOT_FOUND);
        }

        File pdfFile = obtenerPdf(archivo);
        String fileName = archivo.getNombre().replaceAll("(?i)\\.tif$", ".pdf");

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(Files.readAllBytes(pdfFile.toPath()));
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/file-pdf-base64/{id}")
    public ResponseEntity<GenericResponse<Object>> getFileResolucionPdfBase64(@PathVariable("id") Long id) throws IOException {
        GenericResponse<Object> genericResponse = new GenericResponse<>();

        Archivo archivo = this.archivoService.getArchivoById(id);
        if (archivo == null) {
            throw new NotFoundException(ConstantesComunes.MENSAJE_FILE_NOT_FOUND);
        }

        File pdfFile = obtenerPdf(archivo);
        String encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(pdfFile.toPath()));

        genericResponse.setSuccess(true);
        genericResponse.setMessage("pdf");
        genericResponse.setData(encodedString);

        return ResponseEntity.ok(genericResponse);
    }

    private File obtenerPdf(Archivo archivo) throws IOException {
        Resource res = this.storageService.loadFile(archivo.getGuid(), false);
        File file = res.getFile();
        if (ConstantesFormatos.IMAGE_TIF_VALUE.equalsIgnoreCase(archivo.getFormato())) {
            String fileNamePdf = archivo.getNombre().replaceAll("(?i)\\.tif$", ".pdf");
            return this.storageService.convertTIFFToPDF(file, fileNamePdf);
        }
        return file;
    }


}
