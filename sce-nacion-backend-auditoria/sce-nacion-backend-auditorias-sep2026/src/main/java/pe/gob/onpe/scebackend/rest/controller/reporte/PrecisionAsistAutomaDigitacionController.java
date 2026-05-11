package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroPrecisionAsistAutomaDigitacionDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.reporte.IPrecisionAsistAutomaDigitacionService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("precision-asist-automa-digitacion")
public class PrecisionAsistAutomaDigitacionController extends BaseController {

    private final IPrecisionAsistAutomaDigitacionService precisionAsistAutomaDigitacionService;
    private final IConfiguracionProcesoElectoralService confProcesoService;

    public PrecisionAsistAutomaDigitacionController(TokenDecoder tokenDecoder, IPrecisionAsistAutomaDigitacionService precisionAsistAutomaDigitacionService, IConfiguracionProcesoElectoralService confProcesoService) {
        super(tokenDecoder);
        this.precisionAsistAutomaDigitacionService = precisionAsistAutomaDigitacionService;
        this.confProcesoService = confProcesoService;
    }

    @PostMapping("/resumen/base64")
    public ResponseEntity<GenericResponse> getPrecisionAsistAutomaDigitacionResumen(
            @RequestBody FiltroPrecisionAsistAutomaDigitacionDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tenant) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tenant);
        filtro.setEsquema(esquema);

        byte[] resultado = this.precisionAsistAutomaDigitacionService.getPrecisionAsistAutomaDigitacionResumen(filtro);

        return getPdfResponse(resultado);
    }

    @PostMapping("/detalle/base64")
    public ResponseEntity<GenericResponse> getPrecisionAsistAutomaDigitacionDetalle(
            @RequestBody FiltroPrecisionAsistAutomaDigitacionDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tenant) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tenant);
        filtro.setEsquema(esquema);

        byte[] resultado = this.precisionAsistAutomaDigitacionService.getPrecisionAsistAutomaDigitacionDetalle(filtro);

        return getPdfResponse(resultado);
    }
}
