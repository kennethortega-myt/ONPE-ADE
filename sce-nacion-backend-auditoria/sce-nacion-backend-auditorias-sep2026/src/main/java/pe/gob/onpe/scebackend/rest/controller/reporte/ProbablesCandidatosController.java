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
import pe.gob.onpe.scebackend.model.dto.request.reporte.ProbableCandidatoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.ProbablesCandidatosElectosService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("probables-candidatos")
public class ProbablesCandidatosController extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(ProbablesCandidatosController.class);
	
	private final ProbablesCandidatosElectosService probablesCandidatosElectosService;
	private final IConfiguracionProcesoElectoralService confProcesoService;
	
	public ProbablesCandidatosController(TokenDecoder tokenDecoder, 
			ProbablesCandidatosElectosService probablesCandidatosElectosService,
			IConfiguracionProcesoElectoralService confProcesoService) {
		super(tokenDecoder);
		this.probablesCandidatosElectosService = probablesCandidatosElectosService;
		this.confProcesoService = confProcesoService;
	}
	

	@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("list-agrupol/{idEleccion}/{distritoElectoral}")
    public ResponseEntity<GenericResponse> listDistritoElectoral(                                                   
                                           @PathVariable("idEleccion") Integer idEleccion,
                                           @PathVariable("distritoElectoral") String distritoElectoral,
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

            ProbableCandidatoRequestDto filtro = new ProbableCandidatoRequestDto();
            filtro.setEsquema(esquema);
            filtro.setIdEleccion(idEleccion);
            filtro.setDistritoElectoral(distritoElectoral);
            
            response.setData(probablesCandidatosElectosService.listarAgrupolPorDE(filtro));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se listaron las agrupaciones políticas correctamente");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al listar agrupaciones políticas", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al listar agrupaciones políticas");
        }
        return ResponseEntity.ok(response);
    }
	
	@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
	@PostMapping("/")
    public ResponseEntity<GenericResponse> getProbablesCandidatosElectos(
    							@Valid @RequestBody ProbableCandidatoRequestDto filtro,
					    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
					            @RequestHeader("X-Tenant-Id") String tentat) {
		
        GenericResponse response = new GenericResponse();
        try {

            if (authorization == null) {
                response.setMessage("Token Inválido");
                response.setSuccess(Boolean.FALSE);
                return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
                        .body(response);
            }

            String esquema = this.confProcesoService.getEsquema(tentat);
            LoginUserHeader user = getUserLogin(authorization);
            filtro.setUsuario(user.getUsuario());

            filtro.setEsquema(esquema);
            
            response.setData(this.probablesCandidatosElectosService.listarProbablesCandidatosElectos(filtro));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se listaron los probables candidatos electos correctamente");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al listar los probables candidatos electos", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al listar los probables candidatos electos");
        }
        return ResponseEntity.ok(response);
        
        
    }
	
	@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getProbablesCandidatosElectosPDF(
    						@Valid @RequestBody ProbableCandidatoRequestDto filtro,
    						@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
    			            @RequestHeader("X-Tenant-Id") String tentat) {
		LoginUserHeader user = getUserLogin(authorization);        
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tentat);
        filtro.setEsquema(esquema);
        
        byte[] resultado = this.probablesCandidatosElectosService.getReporteProbablesCandidatos(filtro);
        
        return getPdfResponse(resultado);
    }

}
