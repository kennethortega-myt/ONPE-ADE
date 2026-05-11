package pe.gob.onpe.sceorcbackend.rest.controller.reporte;


import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteHistoricoCierreReaperturaRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.ReporteCierreRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteCierreActividadesService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-historico-cierre-reapertura")
public class ReporteCierreActividadesController extends BaseController {

	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final IReporteCierreActividadesService reporteCierreActividadesService;
    private final TokenUtilService tokenUtilService;
    private final CentroComputoService centroComputoService;

    public ReporteCierreActividadesController(IReporteCierreActividadesService reporteCierreActividadesService, TokenUtilService tokenUtilService, 
    		CentroComputoService centroComputoService) {
        this.reporteCierreActividadesService = reporteCierreActividadesService;
        this.tokenUtilService = tokenUtilService;
        this.centroComputoService = centroComputoService;
    }


    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> reporteHistoricoCierreReapertura(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            ReporteHistoricoCierreReaperturaRequestDto filtro = crearFiltroDesdeToken(tokenInfo);
            byte[] reporte = this.reporteCierreActividadesService.reporteHistoricoCierreReapertura(filtro);
            return getPdfResponse(reporte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }


    @PostMapping("/cierre")
    public ResponseEntity<GenericResponse<String>> reporteCierreActividades(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody @Valid ReporteCierreRequest request) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            ReporteHistoricoCierreReaperturaRequestDto filtro = crearFiltroDesdeToken(tokenInfo);
            filtro.setCorrelativo(request.getCorrelativo());
            byte[] reporte = this.reporteCierreActividadesService.reporteCierreActividades(filtro);
            return getPdfResponse(reporte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/reapertura")
    public ResponseEntity<GenericResponse<String>> reporteReaperturaActividades(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            ReporteHistoricoCierreReaperturaRequestDto filtro = crearFiltroDesdeToken(tokenInfo);
            byte[] reporte = this.reporteCierreActividadesService.reporteReaperturaActividades(filtro);
            return getPdfResponse(reporte);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    private ReporteHistoricoCierreReaperturaRequestDto crearFiltroDesdeToken(TokenInfo tokenInfo) {
        ReporteHistoricoCierreReaperturaRequestDto filtro = new ReporteHistoricoCierreReaperturaRequestDto();

        filtro.setUsuario(tokenInfo.getNombreUsuario());
        filtro.setEsquema(schema);

        if (tokenInfo.getAbrevProceso() != null) {
            String nombreProceso = obtenerNombreCompletoProceso(tokenInfo.getAbrevProceso());
            filtro.setProceso(nombreProceso);

            if (tokenInfo.getIdProceso() != null) {
                filtro.setIdProceso(tokenInfo.getIdProceso());
            } else {
                filtro.setIdProceso(1L);
            }
        } else {
            filtro.setProceso("PROCESO ELECTORAL");
            filtro.setIdProceso(1L);
        }

        if (tokenInfo.getCodigoCentroComputo() != null && tokenInfo.getNombreCentroComputo() != null) {
            filtro.setCodigoCentroComputo(tokenInfo.getCodigoCentroComputo());
            filtro.setCentroComputo(tokenInfo.getNombreCentroComputo());
            if (tokenInfo.getIdCentroComputo() != null) {
                filtro.setIdCentroComputo(tokenInfo.getIdCentroComputo());
            } else {
                String codigo = tokenInfo.getCodigoCentroComputo();
                if (codigo != null && !codigo.isEmpty()) {
                	
                	Optional<CentroComputo> ccOptional = centroComputoService.findByCodigo(codigo);
                	
                	if(ccOptional.isPresent()) {
                		filtro.setIdCentroComputo(ccOptional.get().getId());
                	}
                	
                }
            }
        }

        return filtro;
    }

    private String obtenerNombreCompletoProceso(String abrevProceso) {
        switch (abrevProceso.toUpperCase()) {
            case "EG2026":
                return "ELECCIONES GENERALES 2026";
            case "ERM2024":
                return "ELECCIONES REGIONALES Y MUNICIPALES 2024";
            default:
                return "PROCESO ELECTORAL";
        }
    }


}
