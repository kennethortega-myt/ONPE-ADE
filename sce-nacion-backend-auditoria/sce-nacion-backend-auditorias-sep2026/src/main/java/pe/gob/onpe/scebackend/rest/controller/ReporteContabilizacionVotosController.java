package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.ActaContabilizadaResumenReporte;
import pe.gob.onpe.scebackend.model.dto.FiltroContabilizacionActa;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IReporteContabilizacionVotoService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-contabilizacion-votos-nacion")
public class ReporteContabilizacionVotosController extends BaseController{

    private final IReporteContabilizacionVotoService reporteContabilizacionVotoService;

    public ReporteContabilizacionVotosController(TokenDecoder tokenDecoder, IReporteContabilizacionVotoService reporteContabilizacionVotoService) {
        super(tokenDecoder);
        this.reporteContabilizacionVotoService = reporteContabilizacionVotoService;
    }

    @PostMapping("/")
    public ResponseEntity<GenericResponse> getContabilizacionVotos(
            @RequestBody FiltroContabilizacionActa filtro
    ) {
        GenericResponse genericResponse = new GenericResponse();
        ActaContabilizadaResumenReporte resumen = this.reporteContabilizacionVotoService.contabilizarVotosPorMesa(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(resumen);
        genericResponse.setMessage("Se listo correctamente");
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/eleccion/{idEleccion}")
    public ResponseEntity<GenericResponse> getContabilizacionVotosAvancePorcen(
            @PathVariable("idEleccion") String idEleccion
    ) {
        GenericResponse genericResponse = new GenericResponse();
        Integer avance = 100;
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(avance);
        genericResponse.setMessage("ok");
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getContabilizacionVotosBase64(
            @RequestBody FiltroContabilizacionActa filtro
    ) {
        byte[] reporte = this.reporteContabilizacionVotoService.getReporteContabilizarVotosPorMesa(filtro);

        return getPdfResponse(reporte);

    }

    @PostMapping("/blob")
    public ResponseEntity<Resource> getContabilizacionVotosBlob(@RequestBody FiltroContabilizacionActa filtro) {

        byte[] reporte = this.reporteContabilizacionVotoService.getReporteContabilizarVotosPorMesa(filtro);

        if(reporte!=null) {
            String fileNamePdf = "contabilizacion_votos.pdf";
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
