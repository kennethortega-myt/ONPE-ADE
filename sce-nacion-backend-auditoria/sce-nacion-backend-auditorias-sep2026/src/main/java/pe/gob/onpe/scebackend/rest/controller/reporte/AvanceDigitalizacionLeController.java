package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteAvanceDigitalizacionLeService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;



@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@CrossOrigin
@RequestMapping("reporte-avance-digitalizacion-le")
public class AvanceDigitalizacionLeController extends BaseController {

    private final IReporteAvanceDigitalizacionLeService reporteAvanceDigitalizacionLeService;
    private final IConfiguracionProcesoElectoralService confProcesoService;


    public AvanceDigitalizacionLeController(TokenDecoder tokenDecoder, IReporteAvanceDigitalizacionLeService reporteAvanceDigitalizacionLeService,
    		IConfiguracionProcesoElectoralService confProcesoService) {
        super(tokenDecoder);
        this.reporteAvanceDigitalizacionLeService = reporteAvanceDigitalizacionLeService;
        this.confProcesoService = confProcesoService;
    }


    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteElectoresOmisos(
            @Valid
            @RequestBody ReporteMesasObservacionesRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tentat) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tentat);
        
        filtro.setEsquema(esquema);

        byte[] reporte = this.reporteAvanceDigitalizacionLeService.reporteAvanceDigitalizacionLe(filtro);

        return getPdfResponse(reporte);
    }
}
