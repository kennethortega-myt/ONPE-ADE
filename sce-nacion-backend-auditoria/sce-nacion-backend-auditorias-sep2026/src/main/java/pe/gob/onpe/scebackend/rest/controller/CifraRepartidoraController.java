package pe.gob.onpe.scebackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import pe.gob.onpe.scebackend.exeption.ResponseHelperException;
import pe.gob.onpe.scebackend.model.dto.DistritoElectoralEmpateDTO;
import pe.gob.onpe.scebackend.model.dto.request.*;
import pe.gob.onpe.scebackend.model.dto.response.ActualizarResolucionResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponseAlternative;
import pe.gob.onpe.scebackend.model.service.ICifraRepartidoraService;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.util.List;


@RestController
@Validated
@RequestMapping("/cifra-repartidora")
public class CifraRepartidoraController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(CifraRepartidoraController.class);
    private final ICifraRepartidoraService cifraRepartidoraService;
    private final IConfiguracionProcesoElectoralService confProcesoService;

    public CifraRepartidoraController(        
        ICifraRepartidoraService cifraRepartidoraService,
        TokenDecoder tokenDecoder,
        IConfiguracionProcesoElectoralService confProcesoService) {
            super(tokenDecoder);
            this.cifraRepartidoraService = cifraRepartidoraService;
            this.confProcesoService = confProcesoService;
    }


    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("list-distrito-electoral/{codEleccion}")
    public ResponseEntity<GenericResponse> listDistritoElectoral(                                                   
                                           @PathVariable("codEleccion") String codEleccion,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                           @RequestHeader("X-Tenant-Id") String tentat
    ) {
        GenericResponse response = new GenericResponse();
        try {

            if (authorization == null) {
                response.setMessage(ConstantesComunes.TOKEN_INVALIDO);
                response.setSuccess(Boolean.FALSE);
                return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
                        .body(response);
            }

            String esquema = this.confProcesoService.getEsquema(tentat);

            DistritoElectoralRequestDto filtro = new DistritoElectoralRequestDto();
            filtro.setEsquema(esquema);
            filtro.setCodEleccion(codEleccion);
            response.setData(cifraRepartidoraService.listDistritoElectoral(filtro));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se listaron los distritos electorales correctamente");
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al listar distritos electorales", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al listar distritos electorales");
        }
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping ("/consolida-votos-agrupacion")
	public ResponseEntity<GenericResponse> consolidaVotosAgrupacion(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
        @RequestHeader("X-Tenant-Id") String tentat,
        @RequestBody @Validated ConsolidarVotosAgrupacionRequestDto filtro
    )
    {
        logger.info("Iniciando consolidación de votos por agrupación - tipoEleccion: {}, distritoElectoral: {}", 
                filtro.getCodEleccion(), filtro.getCodDistritoElectoral());
        
        GenericResponse response = new GenericResponse();
        
        try { 
            if (authorization == null) {            
                response.setMessage(ConstantesComunes.TOKEN_INVALIDO);
                response.setSuccess(Boolean.FALSE);
                return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
                .body(response);
            }
        
            String esquema = this.confProcesoService.getEsquema(tentat);            
            
            
            response = cifraRepartidoraService.consolidarVotosAgrupacion(esquema, filtro);                      
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos en consolidación de votos: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al consolidar votos por agrupación", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al consolidar votos por agrupación");
        }
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping ("/consulta-cifra-repartidora")
    public ResponseEntity<GenericResponse> consultaCifraRepartidora(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
        @RequestHeader("X-Tenant-Id") String tentat,
        @RequestBody @Validated ConsultaCifraRepartidoraRequestDto filtro
    )
    {
        logger.info("Iniciando obtención de reporte de resultados - tipoEleccion: {}, distritoElectoral: {}, estadoCifra: {}, tipoCifra: {}", 
                filtro.getCodEleccion(), filtro.getCodDistritoElectoral(), filtro.getEstadoCifra(), filtro.getTipoCifra());

        GenericResponse response = new GenericResponse();
        try {
            if (authorization == null) {            
                response.setMessage(ConstantesComunes.TOKEN_INVALIDO);
                response.setSuccess(Boolean.FALSE);
                return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
                .body(response);
            }
        
            String esquema = this.confProcesoService.getEsquema(tentat);  
            response = cifraRepartidoraService.consultaCifraRepartidora(esquema, filtro);            
        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos en obtención de reporte de resultados: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al obtener reporte de resultados", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al obtener reporte de resultados");
        }

        return ResponseEntity.ok(response);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping ("/reparte-curules")
	public ResponseEntity<GenericResponse> reparteCurules(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
        @RequestHeader("X-Tenant-Id") String tentat,
        @RequestBody @Validated ReparteCurulesRequestDto filtro
    )
    {
        logger.info("Iniciando reparto de curules - tipoEleccion: {}, distritoElectoral: {}", 
                   filtro.getCodEleccion(), filtro.getCodDistritoElectoral());
        
        GenericResponse response = new GenericResponse();
        try {
            if (authorization == null) {            
                response.setMessage(ConstantesComunes.TOKEN_INVALIDO);
                response.setSuccess(Boolean.FALSE);
                return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
                .body(response);
            }
        
            String esquema = this.confProcesoService.getEsquema(tentat);    
            filtro.setForzarCalculo(0);            
            response = cifraRepartidoraService.reparteCurules(esquema, filtro);
            logger.info("Reparto de curules completado exitosamente");

        } catch (IllegalArgumentException e) {
            logger.warn("Parámetros inválidos en reparto de curules: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al repartir curules", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al repartir curules");
        }
        
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping ("/votos-empate")
    public ResponseEntity<GenericResponseAlternative<List<DistritoElectoralEmpateDTO>>> votosEmpate(
            @RequestHeader("X-Tenant-Id") String tentat,
            @RequestBody @Validated ConsultaCifraRepartidoraRequestDto filtro
    ){
        try {
            String esquema = this.confProcesoService.getEsquema(tentat);
            List<DistritoElectoralEmpateDTO> response = cifraRepartidoraService.obtenerVotosEmpate(esquema, filtro);
            return ResponseHelperException.createSuccessResponse("Consulta realizada con éxito", response);

        } catch(Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "CifraRepartidoraController.votosEmpate");
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping ("/actualizar-resolucion")
    public ResponseEntity<GenericResponseAlternative<ActualizarResolucionResponseDto>> actualizarResolucion(
            @RequestHeader("X-Tenant-Id") String tentat,
            @RequestBody @Validated ActualizarResolucionRequestDto filtro
    ){
        try {
            String esquema = this.confProcesoService.getEsquema(tentat);
            ActualizarResolucionResponseDto response = cifraRepartidoraService.actualizarResolucion(esquema, filtro);
            if (response.getResolucionesExitosas() > 0){
                return ResponseHelperException.createSuccessResponse(response.getMensajeResumen(), response);
            }else{
                return ResponseHelperException.createBusinessErrorResponse(response.getMensajeResumen());
            }

        } catch(Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "CifraRepartidoraController.actualizarResolucion");
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> reporteCifraRepartidora(    		
            @RequestBody @Validated ConsultaCifraRepartidoraRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tentat
            ) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);        
        filtro.setUsuario(user.getUsuario());
        String esquema = this.confProcesoService.getEsquema(tentat);
        byte[] reporte = this.cifraRepartidoraService.reporteCifraRepartidora(esquema, filtro);

        return getPdfResponse(reporte);
    }
}
