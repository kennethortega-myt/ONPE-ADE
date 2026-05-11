package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteMesasUbigeoRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteMesasUbigeoService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("mesas-ubigeo")
@Log
public class ReporteMesasUbigeoController extends BaseController{
    private final IReporteMesasUbigeoService reporteMesasUbigeoService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    public ReporteMesasUbigeoController(IReporteMesasUbigeoService reporteMesasUbigeoService) {
        this.reporteMesasUbigeoService = reporteMesasUbigeoService;
    }
    
    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getMesasUbigeoPdf(@Valid @RequestBody ReporteMesasUbigeoRequestDto filtro) {
        filtro.setEsquema(schema);
        byte[] reporte = this.reporteMesasUbigeoService.reporteMesasUbigeo(filtro);
        
        return getPdfResponse(reporte);
    }
}
