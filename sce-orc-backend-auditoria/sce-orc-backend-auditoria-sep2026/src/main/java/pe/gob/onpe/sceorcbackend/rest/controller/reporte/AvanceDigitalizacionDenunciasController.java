package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.request.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteAvanceDigitalizacionDenunciasService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-avance-digitalizacion-denuncias")
public class AvanceDigitalizacionDenunciasController extends BaseController {

    private final IReporteAvanceDigitalizacionDenunciasService reporteAvanceDigitalizacionDenunciasService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    public AvanceDigitalizacionDenunciasController(IReporteAvanceDigitalizacionDenunciasService reporteAvanceDigitalizacionDenunciasService) {
        this.reporteAvanceDigitalizacionDenunciasService = reporteAvanceDigitalizacionDenunciasService;
    }


    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getReporteElectoresOmisos(
            @Valid
            @RequestBody ReporteMesasObservacionesRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);

        byte[] reporte = this.reporteAvanceDigitalizacionDenunciasService.reporteAvanceDigitalizacionDenuncias(filtro);

        return getPdfResponse(reporte);
    }
}
