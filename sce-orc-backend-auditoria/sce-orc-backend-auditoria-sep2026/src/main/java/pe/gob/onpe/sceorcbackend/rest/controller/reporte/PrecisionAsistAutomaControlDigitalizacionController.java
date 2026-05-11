package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroPrecisionAsistAutomaControlDigitalizacionDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IPrecisionAsistAutomaControlDigitalizacionService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.sql.SQLException;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("precision-asist-automa-control-digitalizacion")
public class PrecisionAsistAutomaControlDigitalizacionController extends BaseController {

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final IPrecisionAsistAutomaControlDigitalizacionService precisionAsistAutomaControlDigitalizacionService;

    public PrecisionAsistAutomaControlDigitalizacionController(IPrecisionAsistAutomaControlDigitalizacionService precisionAsistAutomaControlDigitalizacionService) {
        this.precisionAsistAutomaControlDigitalizacionService = precisionAsistAutomaControlDigitalizacionService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getPrecisionAsistAutomaControlDigitalizacion(
            @Valid
            @RequestBody FiltroPrecisionAsistAutomaControlDigitalizacionDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
            ) throws SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);

        byte[] resultado = this.precisionAsistAutomaControlDigitalizacionService.getPrecisionAsistAutomaControlDigitalizacion(filtro);

        return getPdfResponse(resultado);
    }
}
