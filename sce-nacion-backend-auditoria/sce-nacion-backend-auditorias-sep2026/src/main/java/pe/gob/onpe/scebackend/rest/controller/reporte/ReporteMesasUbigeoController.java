package pe.gob.onpe.scebackend.rest.controller.reporte;

import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasUbigeoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IReporteMesasUbigeoService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("mesas-ubigeo")
@Log
public class ReporteMesasUbigeoController extends BaseController{
    private final IReporteMesasUbigeoService reporteMesasUbigeoService;
    
    public ReporteMesasUbigeoController(TokenDecoder tokenDecoder, IReporteMesasUbigeoService reporteMesasUbigeoService) {
        super(tokenDecoder);
        this.reporteMesasUbigeoService = reporteMesasUbigeoService;
    }
    
    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getMesasUbigeoPdf(@Valid @RequestBody ReporteMesasUbigeoRequestDto filtro) {
        byte[] reporte = this.reporteMesasUbigeoService.reporteMesasUbigeo(filtro);
        
        return getPdfResponse(reporte);
    }
}
