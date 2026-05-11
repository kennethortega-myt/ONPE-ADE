package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteAvanceDigitalizacionHaService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;


@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@CrossOrigin
@RequestMapping("reporte-avance-digitalizacion-ha")
public class AvanceDigitalizacionHaController extends BaseController {

    private final IReporteAvanceDigitalizacionHaService reporteAvanceDigitalizacionHaService;

    public AvanceDigitalizacionHaController(TokenDecoder tokenDecoder, IReporteAvanceDigitalizacionHaService reporteAvanceDigitalizacionHaService) {
        super(tokenDecoder);
        this.reporteAvanceDigitalizacionHaService = reporteAvanceDigitalizacionHaService;
    }


    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteElectoresOmisos(
            @Valid
            @RequestBody ReporteMesasObservacionesRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.reporteAvanceDigitalizacionHaService.reporteAvanceDigitalizacionHa(filtro);

        return getPdfResponse(reporte);
    }
}
