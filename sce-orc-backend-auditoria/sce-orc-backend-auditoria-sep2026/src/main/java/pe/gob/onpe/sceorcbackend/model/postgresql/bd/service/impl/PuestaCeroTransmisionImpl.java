package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import pe.gob.onpe.sceorcbackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaTransmisionNacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroTransmision;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.utils.ConstantesTransmision;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;


@Service
public class PuestaCeroTransmisionImpl implements PuestaCeroTransmision {
	
	Logger logger = LoggerFactory.getLogger(PuestaCeroTransmisionImpl.class);
	
	private final ActaTransmisionNacionRepository actaTransmisionNacionRepository;
	
	private final CentroComputoService centroComputoService;

	private final WebClient.Builder webClientBuilder;
	
	public static final String URL_NACION_RECIBIR_TRANSMISION ="transmision-pc/recibir-puesta-cero/";

	@Value("${sce.nacion.url}")
    private String urlNacion;
	
	public PuestaCeroTransmisionImpl(
			ActaTransmisionNacionRepository actaTransmisionNacionRepository,
			WebClient.Builder webClientBuilder,
			CentroComputoService centroComputoService) {
		this.actaTransmisionNacionRepository = actaTransmisionNacionRepository;
		this.webClientBuilder = webClientBuilder;
		this.centroComputoService = centroComputoService;
	}
	
	@Override
	@Transactional
    public void sincronizar(String proceso, String cc, String usuario, Date fechaEjecucion, boolean transmitir) {
		this.guardar(proceso, cc, usuario, transmitir);
        if (transmitir) {
            this.transmitir(proceso, cc, usuario, fechaEjecucion);
        }
	}
	
    
	private void guardar(String proceso, String cc, String usuario, boolean transmitir) {
		ActaTransmisionNacion actaTransmitida = new ActaTransmisionNacion();
        actaTransmitida.setTransmite(ConstantesComunes.ACTIVO);
        actaTransmitida.setTipoTransmision(TransmisionNacionEnum.PUESTA_CERO.name());
        actaTransmitida.setEstadoTransmitidoNacion(ConstantesComunes.INACTIVO);
        actaTransmitida.setAccion(ConstantesTransmision.ACCION_PUESTA_CERO);
        actaTransmitida.setFechaTransmision(new Date());
        actaTransmitida.setUsuarioTransmision(usuario);
        actaTransmitida.setFechaCreacion(DateUtil.getFechaActualPeruana());
        actaTransmitida.setUsuarioCreacion(usuario);
        actaTransmitida.setIntento(0);
		actaTransmitida.setActivo(ConstantesComunes.ACTIVO);
        actaTransmisionNacionRepository.save(actaTransmitida);
	}
	

	@Transactional
	public void transmitir(String proceso, String cc, String usuarioTransmision, Date fechaEjecucion) {
		HttpHeaders headersHttp = this.getHeaderTransmision(proceso);

		Optional<ActaTransmisionNacion> opPuestoCeroTransmision = this.listRegistroMin();

		if (opPuestoCeroTransmision.isPresent()) {
			ActaTransmisionNacion puestoCeroTransmision = opPuestoCeroTransmision.get();
			List<TransmisionReqDto> actasTransmistidas = new ArrayList<>();
			TransmisionReqDto transmisionReqDto = new TransmisionReqDto();
			transmisionReqDto.setIdTransmision(puestoCeroTransmision.getId());
			transmisionReqDto.setAccion(puestoCeroTransmision.getAccion());
			transmisionReqDto.setUsuarioTransmision(usuarioTransmision);
			transmisionReqDto.setCentroComputo(cc);
			transmisionReqDto.setAcronimoProceso(proceso);
			transmisionReqDto.setFechaTransmision(DateUtil.getDateString(DateUtil.getFechaActualPeruana(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
			transmisionReqDto.setFechaRegistro(DateUtil.getDateString(fechaEjecucion, SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
			actasTransmistidas.add(transmisionReqDto);
			

			TransmisionRequestDto request = new TransmisionRequestDto();
			request.setProceso(proceso);
			request.setActasTransmitidas(actasTransmistidas);

			HttpEntity<TransmisionRequestDto> httpEntity = new HttpEntity<>(request, headersHttp);
			String url = urlNacion + URL_NACION_RECIBIR_TRANSMISION;
			webClientBuilder.build()
					.method(HttpMethod.PATCH)
					.uri(url)
					.bodyValue(httpEntity.getBody())
					.headers(headers -> headers.addAll(httpEntity.getHeaders()))
					.retrieve()
					.toEntity(TransmisionResponseDto.class)
					.doOnSuccess(response -> processActasTransmitidas(response))
					.doOnError(ex -> logger.error("Error al realizar la llamada HTTP: {}.", ex.getMessage()))
					.subscribe();
		}

	}
	
	
	private void  processActasTransmitidas(ResponseEntity<TransmisionResponseDto> response){
        TransmisionResponseDto responseData = response.getBody();
        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("La llamada fue exitosa (código de estado 200)");
            if(responseData!=null) {
                this.actualizarPostTransmision(responseData);
            }
        } else {
            logger.info("La llamada no fue exitosa. Código de estado: {}.", response.getStatusCodeValue());
            if(responseData!=null) {
                this.actualizarPostTransmision(responseData);
            }
        }
    }
	
	@Transactional(readOnly = true)
	protected Optional<ActaTransmisionNacion> listRegistroMin(){
		return this.actaTransmisionNacionRepository.listRegistroMin(ConstantesTransmision.ACCION_PUESTA_CERO);
	}
	
	
    protected void actualizarPostTransmision(TransmisionResponseDto rpta) {
        Optional<ActaTransmisionNacion> actaTransmisionOp = this.actaTransmisionNacionRepository.findById(rpta.getIdTransmision());
        if (actaTransmisionOp.isPresent()) {
        	ActaTransmisionNacion actaTransmision = actaTransmisionOp.get();
        	actaTransmision.setEstadoTransmitidoNacion(rpta.getEstado());
        	this.actaTransmisionNacionRepository.save(actaTransmision);
        }
    }
    
    private HttpHeaders getHeaderTransmision(String proceso) {
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
		} else {
			throw new IllegalStateException("No se encontró el centro de cómputo actual.");
		}
		return headers;
	}

}
