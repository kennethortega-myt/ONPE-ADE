package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ProductividadDigitadorRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.reporte.ProductividadDigitadorService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;


@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("productividad-digitador")
public class ProductividadDigitadorController extends BaseController{

Logger logger = LoggerFactory.getLogger(ProductividadDigitadorController.class);
	
	private final ProductividadDigitadorService productividadDigitadorService;
	private final IConfiguracionProcesoElectoralService confProcesoService;
	
	public ProductividadDigitadorController(TokenDecoder tokenDecoder, 
			ProductividadDigitadorService productividadDigitadorService,
			IConfiguracionProcesoElectoralService confProcesoService) {
		super(tokenDecoder);
		this.productividadDigitadorService = productividadDigitadorService;
		this.confProcesoService = confProcesoService;
	}
	
	@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("usuarios/{idCentroComputo}")
    public ResponseEntity<GenericResponse> listUsuariosDigitador(                                                   
                                           @PathVariable("idCentroComputo") Integer idCentroComputo,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                           @RequestHeader("X-Tenant-Id") String tentat
    ) {
        GenericResponse response = new GenericResponse();
        try {

            if (authorization == null) {
                response.setMessage("Token Inválido");
                response.setSuccess(Boolean.FALSE);
                return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
                        .body(response);
            }

            String esquema = this.confProcesoService.getEsquema(tentat);

            response.setData(this.productividadDigitadorService.obtenerListaUsuariosDigitador(esquema, idCentroComputo));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se listaron los usuarios correctamente");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al listar los usuarios", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al listar los usuarios");
        }
        return ResponseEntity.ok(response);
    }
	
	@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getProductividadDigitadorPdf(
    		@Valid @RequestBody ProductividadDigitadorRequestDto filtro,
    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tentat) {
		
		LoginUserHeader user = getUserLogin(authorization);        
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tentat);
        filtro.setEsquema(esquema);
        
        byte[] resultado = this.productividadDigitadorService.getReporteProductividadDigitador(filtro);
        
        return getPdfResponse(resultado);
    }
}
