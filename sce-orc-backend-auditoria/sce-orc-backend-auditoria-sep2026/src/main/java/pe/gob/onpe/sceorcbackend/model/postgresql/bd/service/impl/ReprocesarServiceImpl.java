package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ReprocesarService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Service
public class ReprocesarServiceImpl implements ReprocesarService {
    
    @Value("${sce.nacion.url}")
    
    private String urlNacion;
    
    private final RestTemplate clientExport;

    private final ITabLogService logService;
    
    Logger logger = LoggerFactory.getLogger(ReprocesarServiceImpl.class);
    public ReprocesarServiceImpl(RestTemplate clientExport,
                                 ITabLogService logService) {
        this.clientExport = clientExport;
        this.logService = logService;
    }



    @Override
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, String proceso) {

        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(ConstantesAutorizacion.TIPO_AUTORIZACION_REPROCESAR_ACTA);

        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

        ResponseEntity<AutorizacionNacionResponseDto> response = this.clientExport.exchange(
                urlNacion + ConstantesComunes.URL_NACION_RECIBIR_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                AutorizacionNacionResponseDto.class);
        return response.getBody();
    }

    @Override
    public Boolean solicitaAutorizacionReprocesar(String usuario, String cc, String proceso) {
        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(ConstantesAutorizacion.TIPO_AUTORIZACION_REPROCESAR_ACTA);

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
            String mensaje = String.format("Solicitud del usuario %s para realizar un reprocesamiento.",
                    usuario);
            logService.registrarLog(
                    usuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    mensaje,
                    cc,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION
            );
        }

        return isSuccess;
    }

    private HttpHeaders getHeaderAutorizacion(String proceso){
        HttpHeaders headers = new HttpHeaders();
        headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
        headers.set(SceConstantes.TENANT_HEADER, proceso);
        headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
