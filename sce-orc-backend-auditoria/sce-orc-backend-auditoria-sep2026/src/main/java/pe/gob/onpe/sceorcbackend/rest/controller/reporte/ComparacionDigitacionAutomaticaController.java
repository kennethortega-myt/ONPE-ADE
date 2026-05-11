package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IComparacionDigitacionAutomaticaService;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.sql.SQLException;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@RequestMapping("comparacion-digitacion-asist-automa")
public class ComparacionDigitacionAutomaticaController extends BaseController {

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final IComparacionDigitacionAutomaticaService comparacionDigitacionAutomaticaService;

    public ComparacionDigitacionAutomaticaController(IComparacionDigitacionAutomaticaService comparacionDigitacionAutomaticaService) {
        this.comparacionDigitacionAutomaticaService = comparacionDigitacionAutomaticaService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getPrecisionAsistAutomaDigitacionDetalle(
            @Valid
            @RequestBody FiltroComparacionDigitacionAutomaticaDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization)  throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);

        try {
            filtro.validarCamposNecesarios();
            byte[] reporte = this.comparacionDigitacionAutomaticaService.getComparacionDigiAutoma(filtro);
            return getPdfResponse(reporte);
        } catch (IllegalArgumentException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }

    }
}
