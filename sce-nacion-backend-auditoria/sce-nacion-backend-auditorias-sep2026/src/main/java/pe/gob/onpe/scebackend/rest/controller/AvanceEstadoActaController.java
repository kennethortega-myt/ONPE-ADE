package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.AvanceEstadoActaReporteDto;
import pe.gob.onpe.scebackend.model.dto.FiltroAvanceEstadoActaDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IAvanceEstadoActaService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@RestController
@RequestMapping("avance-estado-acta")
public class AvanceEstadoActaController extends BaseController {


    private final IAvanceEstadoActaService avanceEstadoActaService;

    public AvanceEstadoActaController(TokenDecoder tokenDecoder, IAvanceEstadoActaService avanceEstadoActaService) {
        super(tokenDecoder);
        this.avanceEstadoActaService = avanceEstadoActaService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/")
    public ResponseEntity<GenericResponse> getAvanceEstadoActa(@Valid
                                                               @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        GenericResponse genericResponse = new GenericResponse();
        AvanceEstadoActaReporteDto resumen = this.avanceEstadoActaService.getAvanceEstadoActa(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(resumen);
        genericResponse.setMessage("Se listo correctamente");
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAvanceEstadoActaBase64(@Valid
                                                                     @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.avanceEstadoActaService.getReporteAvanceEstadoActa(filtro);

        return getPdfResponse(reporte);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/blob")
    public ResponseEntity<Resource> getAvanceEstadoActaBlob(@Valid @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.avanceEstadoActaService.getReporteAvanceEstadoActa(filtro);

        if(reporte!=null && reporte.length > 0) {
            String fileNamePdf = "avance_estado_de_actas.pdf";
            ByteArrayResource resource  = new ByteArrayResource(reporte);

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileNamePdf)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(resource.contentLength())
                        .body(resource);

            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }


    }
}
