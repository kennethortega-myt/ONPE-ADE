package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteMesasEstadoMesaRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteMesasEstadoMesaService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-mesas-estado-mesa")
@Log
public class ReporteMesasEstadoMesaController extends BaseController {

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final IReporteMesasEstadoMesaService reporteMesasEstadoMesaService;

    public ReporteMesasEstadoMesaController(IReporteMesasEstadoMesaService reporteMesasEstadoMesaService) {
        this.reporteMesasEstadoMesaService = reporteMesasEstadoMesaService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAvanceMesaPdf(
            @Valid
            @RequestBody ReporteMesasEstadoMesaRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        filtro.setEsquema(schema);
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.reporteMesasEstadoMesaService.reporteMesasEstadoMesa(filtro);

        return getPdfResponse(reporte);
    }
}
