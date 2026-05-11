package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.AvanceEstadoActaReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroAvanceEstadoActaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IAvanceEstadoActaService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("avance-estado-acta")
public class AvanceEstadoActaController extends BaseController {

    @Autowired
    private IAvanceEstadoActaService avanceEstadoActaService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @PostMapping("/")
    public ResponseEntity<GenericResponse<AvanceEstadoActaReporteDto>> getAvanceEstadoActa(@Valid
            @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setSchema(schema);
        GenericResponse<AvanceEstadoActaReporteDto> genericResponse = new GenericResponse<>();
        AvanceEstadoActaReporteDto resumen = this.avanceEstadoActaService.getAvanceEstadoActa(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(resumen);
        genericResponse.setMessage("Se listo correctamente");
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }


    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAvanceEstadoActaBase64(@Valid 
            @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setSchema(schema);
        byte[] reporte = this.avanceEstadoActaService.getReporteAvanceEstadoActa(filtro);
        
        return getPdfResponse(reporte);
    }

    @PostMapping("/blob")
    public ResponseEntity<Resource> getAvanceEstadoActaBlob(@Valid @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.avanceEstadoActaService.getReporteAvanceEstadoActa(filtro);

        if(reporte!=null) {
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
