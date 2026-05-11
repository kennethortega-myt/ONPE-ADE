package pe.gob.onpe.scebackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.model.service.RespaldoService;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.service.TokenUtilService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
@RestController
@RequestMapping("/backup-restore")
public class RespaldoController {

    Logger logger = LoggerFactory.getLogger(RespaldoController.class);
    private final TokenUtilService tokenUtilService;
    private final RespaldoService respaldoService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String dbSchema;

    public RespaldoController(TokenUtilService tokenUtilService, RespaldoService respaldoService) {
        this.tokenUtilService = tokenUtilService;
        this.respaldoService = respaldoService;
    }

    @GetMapping("/backup")
    public ResponseEntity<GenericResponse<byte[]>> backupDatabase(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                  @RequestHeader("X-Tenant-Id") String tentId) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return respaldoService.backupDatabase(tentId,"NACION", tokenInfo.getNombreUsuario());
    }

    @PostMapping("/restore")
    public ResponseEntity<GenericResponse<String>> restoreDatabase(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tentId,
            @RequestParam("file") MultipartFile file) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return respaldoService.restoreDatabase(tentId, "NACION", tokenInfo.getNombreUsuario(), file);
    }

    @GetMapping("/validar-usuarios-conectados")
    public ResponseEntity<GenericResponse<String>> validarUsuarioConectados() {
        return respaldoService.validarUsuarioConectados();
    }

}