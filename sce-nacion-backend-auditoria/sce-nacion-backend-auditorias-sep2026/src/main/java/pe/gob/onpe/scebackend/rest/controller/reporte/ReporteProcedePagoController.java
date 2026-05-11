package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteProcedePagoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteProcedePagoService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-procede-pago")
@Log
public class ReporteProcedePagoController extends BaseController {
    private final IReporteProcedePagoService reporteProcedePagoService;

    public ReporteProcedePagoController(TokenDecoder tokenDecoder, IReporteProcedePagoService reporteProcedePagoService) {
       super(tokenDecoder);
        this.reporteProcedePagoService = reporteProcedePagoService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteProcedePago(
            @Valid
            @RequestBody ReporteProcedePagoRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteProcedePagoService.reporteProcedePago(filtro);

        return getPdfResponse(reporte);
    }
}
