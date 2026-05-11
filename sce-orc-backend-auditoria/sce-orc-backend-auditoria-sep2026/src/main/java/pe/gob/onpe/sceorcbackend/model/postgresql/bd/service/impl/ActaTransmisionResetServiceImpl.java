package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import static pe.gob.onpe.sceorcbackend.utils.TransmisionUtils.URL_NACION_RECIBIR_TRANSMISION_RESETEO;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pe.gob.onpe.sceorcbackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionMapperService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.EnvioRequest;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionResetService;

@Service
public class ActaTransmisionResetServiceImpl implements ActaTransmisionResetService {
	
	static Logger logger = LoggerFactory.getLogger(ActaTransmisionResetServiceImpl.class);
	
	@Autowired
	private ActaTransmisionDataService actaTransmisionDataService;
	
	@Autowired
	private CentroComputoService centroComputoService;
	
	@Autowired
	private RestTemplate clientExport;
	
	@Autowired
	private ActaTransmisionMapperService actaTransmisionMapperService;
	
	@Value("${sce.nacion.url}")
	private String urlNacion;

	@Override
	public boolean enviarConPrioridad(EnvioRequest requestEnvio){
		boolean brpta = false;
		TransmisionRequestDto request = new TransmisionRequestDto();
		request.setIdActa(requestEnvio.getIdActa());
        request.setProceso(requestEnvio.getProceso());
        request.setActasTransmitidas(
        		this.actaTransmisionDataService.adjuntar(
        		actaTransmisionMapperService.mapperRequest(requestEnvio.getTransmisiones())));
		
        HttpEntity<TransmisionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderTransmision(
        			requestEnvio.getProceso(), null));
        String url = urlNacion + URL_NACION_RECIBIR_TRANSMISION_RESETEO;
        
        ResponseEntity<TransmisionResponseDto> response = null;
        
        try {
        	response = clientExport.exchange(
                    url, HttpMethod.PATCH, httpEntity, TransmisionResponseDto.class);
        	
        	TransmisionResponseDto rpta = response.getBody();
            
            if (response.getStatusCode() == HttpStatus.OK && rpta!=null) {
                this.actaTransmisionDataService.actualizarEstadoReseteo(
                		rpta.getIdActa(),
                		rpta.getEstado(),
                		rpta.getMensaje()
                );
                logger.info("Transmisión exitosa con código 200");
                brpta = true;
            } else {
                logger.error("Respuesta con código diferente a 200");
                brpta = false;
            }
            
    		
        } catch (Exception e) {
        	logger.error("Error {}", e.getMessage());
        	brpta = false;
        }
        
        return brpta;
        
	}
	
	private HttpHeaders getHeaderTransmision(String proceso, String orden) {
		HttpHeaders headers = new HttpHeaders();
		Optional<CentroComputo> opt = this.centroComputoService.getCentroComputoActual();
		if(opt.isPresent() && opt.get()!=null){
			CentroComputo cc = opt.get();
			String token = cc.getApiTokenBackedCc();
			if(token==null || token.isEmpty()){
				throw new IllegalStateException("No se encuentra un token configurado.");
			} // end-if
			headers.setBearerAuth(cc.getApiTokenBackedCc()); 
			headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
			headers.set(SceConstantes.TENANT_HEADER, proceso);
			headers.set(SceConstantes.HEADER_CODIGO_CC, cc.getCodigo());
			headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			
		} else {
			throw new IllegalStateException("No se encontró el centro de cómputo actual.");
		}
		return headers;
	}
	
}
