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
import pe.gob.onpe.scebackend.model.service.IReporteTotalCentroComputoService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("resporte-total-centro-computo")
public class ReporteTotalCentroComputoController extends BaseController {

    private final IReporteTotalCentroComputoService reporteTotalCentroComputoService;

    public ReporteTotalCentroComputoController(TokenDecoder tokenDecoder, IReporteTotalCentroComputoService reporteTotalCentroComputoService) {
        super(tokenDecoder);
        this.reporteTotalCentroComputoService = reporteTotalCentroComputoService;
    }

    @PostMapping("/")
    public ResponseEntity<GenericResponse> getTotalCentroComputo(@Valid
            @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        GenericResponse genericResponse = new GenericResponse();
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        AvanceEstadoActaReporteDto resumen = this.reporteTotalCentroComputoService.getAvanceEstadoActa(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(resumen);
        genericResponse.setMessage("Se listo correctamente");
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }


    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getTotalCentroComputoBase64(@Valid 
            @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteTotalCentroComputoService.getReporteAvanceEstadoActa(filtro);
        return getPdfResponse(reporte);
    }

    @PostMapping("/blob")
    public ResponseEntity<Resource> getTotalCentroComputoBlob(@Valid @RequestBody FiltroAvanceEstadoActaDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteTotalCentroComputoService.getReporteAvanceEstadoActa(filtro);

        if (reporte != null && reporte.length > 0) {
            String fileNamePdf = "avance_estado_de_actas.pdf";
            ByteArrayResource resource = new ByteArrayResource(reporte);

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
