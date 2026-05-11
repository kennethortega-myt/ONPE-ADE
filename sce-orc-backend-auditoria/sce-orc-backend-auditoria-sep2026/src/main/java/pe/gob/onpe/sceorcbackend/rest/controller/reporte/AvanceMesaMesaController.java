package pe.gob.onpe.sceorcbackend.rest.controller.reporte;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroAvanceMesaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.AvanceMesaMesaService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("avance-mesa")
public class AvanceMesaMesaController extends BaseController{

    @Autowired
    private AvanceMesaMesaService avanceMesaMesaService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAvanceMesaPdf(@Valid @RequestBody FiltroAvanceMesaDto filtro,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
                                                            ) throws JRException {
        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setSchema(schema);
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
