package pe.gob.onpe.scebackend.rest.controller;


import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.FiltroAvanceMesaDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IActaService;
import pe.gob.onpe.scebackend.model.service.reporte.AvanceMesaMesaService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@RestController
@RequestMapping("avance-mesa")
public class AvanceMesaMesaController extends BaseController {

    private final AvanceMesaMesaService avanceMesaMesaService;

    public AvanceMesaMesaController(TokenDecoder tokenDecoder, AvanceMesaMesaService avanceMesaMesaService) {
        super(tokenDecoder);
        this.avanceMesaMesaService = avanceMesaMesaService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAvanceMesaPdf(@Valid @RequestBody FiltroAvanceMesaDto filtro,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) throws JRException {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        try {
            filtro.validarCamposNecesarios();
            byte[] reporte = this.avanceMesaMesaService.reporteAvanceMesa(filtro);
            return getPdfResponse(reporte);
        } catch (IllegalArgumentException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }
}
