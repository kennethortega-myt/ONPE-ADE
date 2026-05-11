package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroPrecisionAsistAutomaDigitacionDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.reporte.IComparacionDigitacionAutomaticaService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("comparacion-digitacion-asist-automa")
public class ComparacionDigitacionAutomaticaController extends BaseController {
    private final IComparacionDigitacionAutomaticaService comparacionDigitacionAutomaticaService;
    private final IConfiguracionProcesoElectoralService confProcesoService;

    public ComparacionDigitacionAutomaticaController(TokenDecoder tokenDecoder, IComparacionDigitacionAutomaticaService comparacionDigitacionAutomaticaService, IConfiguracionProcesoElectoralService confProcesoService) {
        super(tokenDecoder);
        this.comparacionDigitacionAutomaticaService = comparacionDigitacionAutomaticaService;
        this.confProcesoService = confProcesoService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getPrecisionAsistAutomaDigitacionDetalle(
            @Valid
            @RequestBody FiltroComparacionDigitacionAutomaticaDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tenant) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tenant);

        try {
            filtro.validarCamposNecesarios();
            filtro.setEsquema(esquema);
            byte[] reporte = this.comparacionDigitacionAutomaticaService.getComparacionDigiAutoma(filtro);
            return getPdfResponse(reporte);
        } catch (IllegalArgumentException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }
}
