package pe.gob.onpe.sceorcbackend.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.controlcalidad.*;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ResolucionActaBean;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ControlCalidadService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("/control-calidad")
public class ControlCalidadController {
	
	Logger logger = LoggerFactory.getLogger(ControlCalidadController.class);
	
	private final ControlCalidadService controlCalidadService;
	private final TokenUtilService tokenUtilService;
	private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	public ControlCalidadController(
			ControlCalidadService controlCalidadService,
			TokenUtilService tokenUtilService,
			ActaTransmisionExecuteService actaTransmisionNacionStrategyService
			) {
		this.controlCalidadService = controlCalidadService;
		this.tokenUtilService = tokenUtilService;
		this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/summary/{codigoEleccion}")
	public ResponseEntity<ControlCalidadSumaryResponse> summary(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable(value = "codigoEleccion") String codigoEleccion) {
		
		return ResponseEntity.status(HttpStatus.OK).body(this.controlCalidadService.summaryControlCalidad(codigoEleccion));
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/listActasPendientes/{codigoEleccion}")
	public ResponseEntity<List<ControlCalidadActaPendiente>> listActasPendientes(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable("codigoEleccion") String codigoEleccion) {
	
		try{
			TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
			return ResponseEntity.status(HttpStatus.OK).body(this.controlCalidadService.actasPendientesControlCalidad(codigoEleccion, tokenInfo));
		}catch (Exception e){
			logger.error("Error al listar actas para control de calidad ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
		}
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/listResolucionesActa/{idActa}")
	public ResponseEntity<List<ResolucionActaResponse>> listResolucionesActa(@PathVariable("idActa") Long idActa) {
	
		try{
			return ResponseEntity.status(HttpStatus.OK).body(this.controlCalidadService.obtenerResolucionesPorActa(idActa));
		}catch (Exception e){
			logger.error("Error al listar las resoluciones del acta ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
		}
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/obtenerIdsArchivosPaso1/{idActa}")
	public ResponseEntity<ImagenesPaso1> obtenerIdsArchivosPaso1(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@PathVariable("idActa") Long idActa) {
	
		try{
			TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(this.controlCalidadService.obtenerIdsArchivosPaso1(idActa, tokenInfo));
		}catch (Exception e){
			logger.error("Error al obtener los Ids de los archivos del paso 1 ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@PostMapping("/rechazar")
	public ResponseEntity<GenericResponse<Boolean>> rechazarControlCalidad(
	      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
	      @RequestBody RechazarCcRequest rechazarRequest) {
	    try{
	      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
	      GenericResponse<Boolean> respuesta =  this.controlCalidadService.rechazarControlCalidad(rechazarRequest, tokenInfo);
	      if(respuesta.isSuccess()) {
	    	  sincronizarActa(rechazarRequest.getIdActa(), TransmisionNacionEnum.CC_RECHAZAR_RES_TRANSMISION , tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
	      }
	      return ResponseEntity
	    		  .status(HttpStatus.OK)
	    		  .body(respuesta);
	    } catch (Exception e) {
	      logger.error("Error al rechazar el control de calidad", e);
	      return ResponseEntity
	    		  .status(HttpStatus.INTERNAL_SERVER_ERROR)
	    		  .body(new GenericResponse<>(false, e.getMessage()));
	    }
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@PostMapping("/observar")
	public ResponseEntity<GenericResponse<Boolean>> observarControlCalidad(
	      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
	      @RequestParam Long idActa) {
	    try{
	      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
	      GenericResponse<Boolean> respuesta =  this.controlCalidadService.observarControlCalidad(idActa, tokenInfo);
	      
	      if(respuesta.isSuccess()) {
	    	  sincronizarActa(idActa, TransmisionNacionEnum.CONTROL_CALIDAD_TRANSMISION , tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
	      }
	      return ResponseEntity
	    		  .status(HttpStatus.OK)
	    		  .body(respuesta);
	    } catch (Exception e) {
	      logger.error("Error al observar el control de calidad", e);
	      return ResponseEntity
	    		  .status(HttpStatus.INTERNAL_SERVER_ERROR)
	    		  .body(new GenericResponse<>(false, e.getMessage()));
	    }
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/obtenerDataPaso2/{idActa}")
	public ResponseEntity<DataPaso2Response> obtenerDataPaso2(@PathVariable("idActa") Long idActa) {
		try{
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(this.controlCalidadService.obtenerDataPaso2(idActa));
		}catch (Exception e){
			logger.error("Error al obtener los Ids de los archivos del paso 2 ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@PostMapping("/aceptar")
	public ResponseEntity<GenericResponse<Boolean>> aceptarControlCalidad(
	      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
	      @RequestBody RechazarCcRequest rechazarRequest) {
	    try{
	      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
	      GenericResponse<Boolean> respuesta =  this.controlCalidadService.aceptarControlCalidad(rechazarRequest, tokenInfo);
	      
	      if(respuesta.isSuccess()) {
	    	  sincronizarActa(rechazarRequest.getIdActa(), TransmisionNacionEnum.CONTROL_CALIDAD_TRANSMISION , tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
	      }
	      
	      return ResponseEntity
	    		  .status(HttpStatus.OK)
	    		  .body(respuesta);
	      
	    } catch (Exception e) {
	      logger.error("Error al aceptar el control de calidad", e);
	      return ResponseEntity
	    		  .status(HttpStatus.INTERNAL_SERVER_ERROR)
	    		  .body(new GenericResponse<>(false, e.getMessage()));
	    }
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/historial-resolucion-antes-despues")
	public ResponseEntity<GenericResponse<ResolucionActaBean>> getHistorialResolucionAntesDespues(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestParam(value = "idActa") Long idActa,
			@RequestParam(value = "idResolucion") Long idResolucion) {

			ResolucionActaBean resolucionAntesDespues = this.controlCalidadService.getHistorialResolucionAntesDespues(idActa, idResolucion);
			return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(true, "Se obtuvo correctamente el historial de cambios del acta.", resolucionAntesDespues));

	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/obtenerDataPaso3/{idActa}")
	public ResponseEntity<GenericResponse<DataPaso3Response>> obtenerDataPaso3(@PathVariable("idActa") Long idActa) {
		try{
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(this.controlCalidadService.obtenerDataPaso3(idActa, schema));
		}catch (Exception e){
			logger.error("Error al obtener los datos del paso 3 ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@GetMapping("/obtenerAgrupacionesPoliticas/{idActa}")
	public ResponseEntity<GenericResponse<List<AgrupacionPoliticaResponse>>> obtenerAgrupaciones(@PathVariable("idActa") Long idActa) {
		try{
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(this.controlCalidadService.agrupacionesPoliticas(idActa, schema));
		}catch (Exception e){
			logger.error("Error al obtener los datos del paso 3 ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
	@PostMapping("/autorizacion/consulta")
    public ResponseEntity<GenericResponse<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam Long idDocumento,
            @RequestParam String tipoDocumento
    ) {
    	TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    	String ccc = tokenInfo.getCodigoCentroComputo();
    	String usr = tokenInfo.getNombreUsuario();
    	String proceso = tokenInfo.getAbrevProceso();        

        try{
            GenericResponse<AutorizacionNacionResponseDto> genericResponse = new GenericResponse<>();
            AutorizacionNacionResponseDto autorizacionNacion = this.controlCalidadService.getAutorizacionNacion(usr, ccc, proceso, idDocumento, tipoDocumento);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "El servicio de Nación no está disponible para consultar autorización."));
        }
    }

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @PostMapping("/autorizacion/solicitar")
    public ResponseEntity<GenericResponse<Boolean>> solicitaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
            @RequestParam Long idDocumento,
            @RequestParam String tipoDocumento,
			@RequestParam Long idActa
    ) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    	String ccc = tokenInfo.getCodigoCentroComputo();
    	String usr = tokenInfo.getNombreUsuario();
    	String proceso = tokenInfo.getAbrevProceso();

        try{
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            Boolean autorizacionNacion = this.controlCalidadService.solicitaAutorizacion(usr, ccc, proceso, idDocumento, tipoDocumento, idActa, tokenInfo);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "El servicio de Nación no está disponible para consultar autorización."));
        }
    }

	@PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @PostMapping("/cancelar")
	public ResponseEntity<GenericResponse<Boolean>> cancelarControlCalidad(
	      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
	      @RequestBody List<Long> idsActas) {
	    try{
	      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
	      	      
	      return ResponseEntity
	    		  .status(HttpStatus.OK)
	    		  .body(this.controlCalidadService.cancelarControlCalidad(idsActas, tokenInfo));
	      
	    } catch (Exception e) {
	      logger.error("Error al cancelar el control de calidad", e);
	      return ResponseEntity
	    		  .status(HttpStatus.INTERNAL_SERVER_ERROR)
	    		  .body(new GenericResponse<>(false, e.getMessage()));
	    }
	}
    
    private void sincronizarActa(Long idActa, TransmisionNacionEnum transmisionNacionEnum, String usuario, String proceso) {
        try {
            this.actaTransmisionNacionStrategyService.sincronizar(idActa, proceso, transmisionNacionEnum, usuario);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK, e);
        }
    }
	
}
