package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.exception.NotFoundException;
import pe.gob.onpe.sceorcbackend.model.dto.RespaldoDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.service.RespaldoService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/backup-restore")
public class RespaldoController {

    Logger logger = LoggerFactory.getLogger(RespaldoController.class);
    private final TokenUtilService tokenUtilService;
    private final RespaldoService respaldoService;
    private final StorageService storageService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String dbSchema;

    public RespaldoController(TokenUtilService tokenUtilService, RespaldoService respaldoService, StorageService storageService) {
        this.tokenUtilService = tokenUtilService;
        this.respaldoService = respaldoService;
        this.storageService = storageService;
    }

    @PreAuthorize(RoleAutority.BACKUP_RESTORE)
    @GetMapping("/backup")
    public ResponseEntity<GenericResponse<byte[]>> backupDatabase(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return respaldoService.backupDatabase(tokenInfo.getAbrevProceso(), tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario());
    }

    @PreAuthorize(RoleAutority.BACKUP_RESTORE)
    @PostMapping("/restore")
    public ResponseEntity<GenericResponse<String>> restoreDatabase(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("file") MultipartFile file) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return respaldoService.restoreDatabase(tokenInfo.getAbrevProceso(), tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario(), file);
    }

    @PreAuthorize(RoleAutority.BACKUP_RESTORE)
    @GetMapping("/validar-usuarios-conectados")
    public ResponseEntity<GenericResponse<String>> validarUsuarioConectados() {
        return respaldoService.validarUsuarioConectados();
    }

    @PreAuthorize(RoleAutority.BACKUP_RESTORE)
    @GetMapping("/list-bk")
    public ResponseEntity<GenericResponse<List<RespaldoDTO>>> listBk() {
        return respaldoService.listarArchivosSql();
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/dowloand")
    public ResponseEntity<Resource> getFIleSql(@RequestParam("id") String id) {
        try {
            Resource resource = storageService.loadFile(id, true);

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException("Archivo no encontrado: " + id);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\""
                    )
                    .body(resource);

        } catch (Exception e) {
            throw new InternalServerErrorException(
                    "No se pudo descargar el archivo: " + e.getMessage()
            );
        }
    }

}