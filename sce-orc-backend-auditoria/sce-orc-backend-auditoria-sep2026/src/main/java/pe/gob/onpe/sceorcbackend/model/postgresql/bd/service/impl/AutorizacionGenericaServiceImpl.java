package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionGenericaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;


@Service
public class AutorizacionGenericaServiceImpl implements AutorizacionGenericaService {

    @Value("${sce.nacion.url}")
    private String urlNacion;

    private final RestTemplate clientExport;
    private final ITabLogService logService;

    public AutorizacionGenericaServiceImpl(RestTemplate clientExport,
                                           ITabLogService logService) {
        this.clientExport = clientExport;
        this.logService = logService;
    }


    @Override
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, String proceso, String tipoAutorizacion) {
        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(tipoAutorizacion);

        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

        ResponseEntity<AutorizacionNacionResponseDto> response = this.clientExport.exchange(
                urlNacion + ConstantesComunes.URL_NACION_RECIBIR_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                AutorizacionNacionResponseDto.class);
        return response.getBody();
    }

    @Override
    public Boolean solicitaAutorizacionNacion(String usuario, String cc, String proceso, String tipoAutorizacion) {
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
            case ConstantesAutorizacion.TIPO_AUTORIZACION_ELIMINAR_OMISOS ->
                    String.format("Solicitud del usuario %s para eliminar omisos.", usuario);
            case ConstantesAutorizacion.TIPO_AUTORIZACION_REPROCESAR_MESA ->
                    String.format("Solicitud del usuario %s para realizar un reprocesamiento de mesa.", usuario);
            case ConstantesAutorizacion.TIPO_AUTORIZACION_PUESTA_CERO_ACTA ->
                    String.format("Solicitud del usuario %s para realizar reinicio de acta.", usuario);
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
