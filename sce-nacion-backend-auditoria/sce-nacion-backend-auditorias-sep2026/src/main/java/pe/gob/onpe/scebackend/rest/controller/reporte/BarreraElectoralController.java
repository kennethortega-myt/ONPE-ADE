package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.BarreraElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.BarreraElectoralService;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("barrera-electoral")
public class BarreraElectoralController extends BaseController {

	private final BarreraElectoralService barreraElectoralService;
	private final IConfiguracionProcesoElectoralService confProcesoService;
	
	public BarreraElectoralController(TokenDecoder tokenDecoder, 
			BarreraElectoralService barreraElectoralService,
			IConfiguracionProcesoElectoralService confProcesoService) {
		super(tokenDecoder);
		this.barreraElectoralService = barreraElectoralService;
		this.confProcesoService = confProcesoService;
	}
	
	@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getBarreraElectoralPDF(
    						@Valid @RequestBody BarreraElectoralRequestDto filtro,
    						@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
    			            @RequestHeader("X-Tenant-Id") String tentat) {
		
		LoginUserHeader user = getUserLogin(authorization);        
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tentat);
        filtro.setEsquema(esquema);
        
        byte[] resultado = this.barreraElectoralService.getReporteBarreraElectoral(filtro);
        
        return getPdfResponse(resultado);
    }
	
	
}
