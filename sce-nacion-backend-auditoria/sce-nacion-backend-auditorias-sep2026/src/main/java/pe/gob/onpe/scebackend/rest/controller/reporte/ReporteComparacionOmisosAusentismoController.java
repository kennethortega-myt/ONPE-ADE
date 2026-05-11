package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteComparacionOmisosAusentismoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteComparacionOmisosAusentismoService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-comparacion-omisos-ausentismo")
public class ReporteComparacionOmisosAusentismoController extends BaseController {

    private final IReporteComparacionOmisosAusentismoService reporteComparacionOmisosAusentismoService;
    private final IConfiguracionProcesoElectoralService confProcesoService;

    public ReporteComparacionOmisosAusentismoController(TokenDecoder tokenDecoder,
                                                        IReporteComparacionOmisosAusentismoService reporteComparacionOmisosAusentismoService,
                                                        IConfiguracionProcesoElectoralService confProcesoService) {
        super(tokenDecoder);
        this.reporteComparacionOmisosAusentismoService = reporteComparacionOmisosAusentismoService;
        this.confProcesoService = confProcesoService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteElectoresOmisos(
            @Valid
            @RequestBody ReporteComparacionOmisosAusentismoRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tentat) throws JRException {

        String esquema = this.confProcesoService.getEsquema(tentat);
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(esquema);
        byte[] reporte = this.reporteComparacionOmisosAusentismoService.reporteComparacionOmisosAusentismo(filtro);

        return getPdfResponse(reporte);
    }

}
