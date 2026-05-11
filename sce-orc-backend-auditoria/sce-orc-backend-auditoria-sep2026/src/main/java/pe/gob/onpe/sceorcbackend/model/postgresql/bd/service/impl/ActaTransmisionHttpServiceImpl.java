package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.ArrayList;
import java.util.List;
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
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionHttpService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionMapperService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import static pe.gob.onpe.sceorcbackend.utils.TransmisionUtils.*;

import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.EnvioRequest;

@Service
public class ActaTransmisionHttpServiceImpl implements ActaTransmisionHttpService {
	
	static Logger logger = LoggerFactory.getLogger(ActaTransmisionHttpServiceImpl.class);
	
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
	public TransmisionResponseDto transmitir(Long idTransmision, String proceso, String usuarioTransmision){
		
		TransmisionResponseDto rpta = null;
		
		Optional<ActaTransmisionNacion> actaTransmitida = this.actaTransmisionDataService.findByIdActaTransmiion(idTransmision);
	    
	    if (actaTransmitida.isPresent()) {
	        try {
	            logger.info("Se ejecuta la transmisión id {}", actaTransmitida.get().getId());
	            
	            List<ActaTransmisionNacion> actasTransmitidas = new ArrayList<>();
	            actasTransmitidas.add(actaTransmitida.get());
	           
	            this.actaTransmisionDataService.ejecutandose(idTransmision);

	            if (!actasTransmitidas.isEmpty()) {
	                TransmisionRequestDto request = new TransmisionRequestDto();
	                request.setProceso(proceso);
	                request.setActasTransmitidas(
	                		this.actaTransmisionDataService.adjuntar(
	                		actaTransmisionMapperService.mapperRequest(actasTransmitidas)));

	                HttpEntity<TransmisionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderTransmision(
	                		proceso,
	                		 Integer.toString(actaTransmitida.get().getOrden())));
	                String url = urlNacion + URL_NACION_RECIBIR_TRANSMISION;
	                
	                ResponseEntity<TransmisionResponseDto> response = clientExport.exchange(
	                    url, HttpMethod.PATCH, httpEntity, TransmisionResponseDto.class);
	                
	                rpta = response.getBody();
	                
	                if (response.getStatusCode() == HttpStatus.OK && rpta!=null) {
	                    this.actaTransmisionDataService.actualizarEstado(
	                    		rpta.getIdTransmision(),
	                    		rpta.getEstado(),
	                    		rpta.getMensaje()
	                    );
	                    logger.info("Transmisión exitosa con código 200");
	                } else {
	                    logger.error("Respuesta con código diferente a 200: {}", response.getStatusCode());
	                    if(rpta!=null){
	                    	this.actaTransmisionDataService.actualizarFallido(
		                    		idTransmision,
		                    		rpta.getMensaje()
		                    );
		                } else {
		                	this.actaTransmisionDataService.actualizarFallido(
		                    		idTransmision,
		                    		"Eror: No se pudo capturar la respuesta para determinar el mensaje"
		                    );
		                }
	                }
	            } else {
	                logger.info("No se realizó ninguna transmisión ya que no hay ningún cambio para transmitir");
	            }
	            
	        } catch (Exception e) {
	            logger.error("Error en transmisión asíncrona para id: {}", idTransmision, e);
	            this.actaTransmisionDataService.actualizarFallido(idTransmision, e.getMessage());
	        }
	        
	    } else {
	        logger.info("No existe la transmisión con id: {}", idTransmision);
	    }
		
	    return rpta;
		
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
			headers.set(SceConstantes.HEADER_ORDEN, orden);
		} else {
			throw new IllegalStateException("No se encontró el centro de cómputo actual.");
		}
		return headers;
	}

	
}
