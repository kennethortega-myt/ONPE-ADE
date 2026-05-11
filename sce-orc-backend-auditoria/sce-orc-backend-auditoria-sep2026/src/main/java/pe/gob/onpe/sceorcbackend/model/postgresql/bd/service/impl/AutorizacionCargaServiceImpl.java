package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionCargaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.util.List;

@Service
public class AutorizacionCargaServiceImpl implements AutorizacionCargaService {

	Logger logger = LoggerFactory.getLogger(AutorizacionCargaServiceImpl.class);
	
	@Value("${sce.nacion.url}")
    private String urlNacion;
	
	private final RestTemplate clientExport;
	
	private final MaeProcesoElectoralService procesoElectoralService;

    private final UsuarioService usuarioService;
    private final ITabLogService logService;
	
	public AutorizacionCargaServiceImpl(
			RestTemplate clientExport,
			MaeProcesoElectoralService procesoElectoralService,
            UsuarioService usuarioService,
            ITabLogService logService) {
        this.clientExport = clientExport;
        this.procesoElectoralService = procesoElectoralService;
        this.usuarioService = usuarioService;
        this.logService = logService;
    }

	@Override
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, String proceso, String tipoAutorizacion) {

        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        AutorizacionNacionResponseDto responseBody;

        if(tipoAutorizacion.equals(ConstantesComunes.TIPO_AUTORIZACION_PUESTA_CERO)){
            StringBuilder usuariosConectados = new StringBuilder();
            List<Usuario> tabUsuarioList = this.usuarioService.usuarioActivos();
            for (Usuario tabUsuario : tabUsuarioList) {
                if (
                        tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_VERIFICADOR) ||
                                tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER) ||
                                tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION) ||
                                (tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC) &&
                                        !tabUsuario.getUsuario().equals(usuario))
                ) {
                    usuariosConectados.append("\n").append(tabUsuario.getUsuario()).append(",");
                }
            }

            if (!usuariosConectados.isEmpty()) {
                usuariosConectados.deleteCharAt(usuariosConectados.length() - 1);
                usuariosConectados.append(".");
                throw new InternalServerErrorException("Para realizar la puesta a cero, los siguientes usuarios deben cerrar sesión:" + usuariosConectados);
            }
        }



        boolean autorizado = procesoElectoralService.verificarHabilitacionDiaEleccion(proceso,  SceConstantes.PATTERN_DD_MM_YYYY_DASH);
        logger.info("verificarHabilitacionDiaEleccion - autorizado: {}", autorizado);
        if(autorizado) {
        	responseBody = new AutorizacionNacionResponseDto();
        	responseBody.setAutorizado(autorizado);
            responseBody.setIdAutorizacion("0");
            responseBody.setFromCentroComputo(Boolean.TRUE);
            responseBody.setMensaje("Autorizado desde cc, la fecha actual es antes a la fecha de convocatoria del proceso.");
        	logger.info(responseBody.getMensaje());
        } else {
            request.setCc(cc);
            request.setUsuario(usuario);
            request.setTipoAutorizacion(tipoAutorizacion);

            HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

            ResponseEntity<AutorizacionNacionResponseDto> response = this.clientExport.exchange(
                    urlNacion + ConstantesComunes.URL_NACION_RECIBIR_AUTORIZACION,
                    HttpMethod.PATCH,
                    httpEntity,
                    AutorizacionNacionResponseDto.class);
            responseBody = response.getBody();
        }
        
        return responseBody;
    }
	
	@Override
	public Boolean solicitaAutorizacionImportacion(String usuario, String cc, String proceso, String tipoAutorizacion) {
		
		AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(tipoAutorizacion);

        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

        @SuppressWarnings("rawtypes")
		ResponseEntity<GenericResponse> response = this.clientExport.exchange(
                urlNacion + ConstantesComunes.URL_NACION_RECIBIR_SOLICITUD_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                GenericResponse.class);

        GenericResponse<?> body = response.getBody();
        boolean isSuccess = body != null && body.isSuccess();
        if (isSuccess){
            logService.registrarLog(
                    usuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    this.generarMensajeLog(tipoAutorizacion, usuario, cc),
                    cc,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION
            );
        }
        return isSuccess;
	}

    private String generarMensajeLog(String tipoAutorizacion, String usuario, String cc){
        return switch (tipoAutorizacion){
            case ConstantesAutorizacion.TIPO_AUTORIZACION_IMPORTACION_DATOS ->
                    String.format("Solicitud del usuario %s para realizar una importación de datos.", usuario);
            case ConstantesAutorizacion.TIPO_AUTORIZACION_IMPORTACION_PADRONES ->
                    String.format("Solicitud del usuario %s para realizar una importación de padrones.", usuario);
            case ConstantesAutorizacion.TIPO_AUTORIZACION_PUESTA_CERO ->
                    String.format("Solicitud del usuario %s para realizar una puesta a cero por centro de cómputo %s.", usuario, cc);
            case ConstantesAutorizacion.TIPO_AUTORIZACION_REPROCESAR_MESA ->
                    String.format("Solicitud del usuario %s para realizar un reprocesamiento de mesa.", usuario);
            default -> "";
        };
    }
	
	private HttpHeaders getHeaderAutorizacion(String proceso){
        HttpHeaders headers = new HttpHeaders();
        headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
        headers.set(SceConstantes.TENANT_HEADER, proceso);
        headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

}
