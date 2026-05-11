package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.dto.queue.EnvioTransmisionQueue;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabOtroDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOficio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetOtroDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Formato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MesaDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaCola;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaEscrutinio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaSorteado;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoMiembroMesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoVotante;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Personero;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ActaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.CabActaFormatoPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetActaAccionPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetActaFormatoPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetActaOpcionPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetActaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetActaPreferencialPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetActaResolucionPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetOficioTransmistirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetOtroDocumentoPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.MesaArchivoPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.MesaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.MiembroMesaColaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.MiembroMesaEscrutinioPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.MiembroMesaSorteadoPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.OficioPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.OmisoMiembroMesaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.OmisoVotantePorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.OtroDocumentoPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.PersoneroPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ResolucionPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.TabFormatoPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.TransmisionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaCelesteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaTransmisionNacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ArchivoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CabActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CabOtroDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaAccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaOficioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaOpcionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaPreferencialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetOtroDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.FormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MiembroMesaColaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MiembroMesaEscrutinioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MiembroMesaSorteadoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.OmisoMiembroMesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.OmisoVotanteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.PersoneroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.ArchivoTransmisionReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.CabActaFormatoPorTransmitirReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.DetActaResolucionPorTransmitirReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.DetOficioTransmistirReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.OficioPorTransmitirReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.ResolucionPorTransmitirReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TabFormatoPorTransmitirReqDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionReqDto;
import pe.gob.onpe.sceorcbackend.utils.ArchivoUtils;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesTransmision;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.PathUtils;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static pe.gob.onpe.sceorcbackend.utils.TransmisionUtils.CC_FORMATO;

@Service
public class ActaTransmisionDataServiceImpl implements ActaTransmisionDataService {
	
	static Logger logger = LoggerFactory.getLogger(ActaTransmisionDataServiceImpl.class);
	
	@Autowired
	private DetActaResolucionRepository detActaResolucionRepository;

	@Autowired
    private DetActaRepository detActaRepository;
    
	@Autowired
    private DetActaAccionRepository detActaAccionRepository;
	
	@Autowired
	private ActaTransmisionNacionRepository actaTransmisionNacionRepository;
	
	@Autowired
	private OmisoVotanteRepository omisoVotanteRepository;

	@Autowired
	private MesaRepository mesaRepository;
	
	@Autowired
	private MiembroMesaSorteadoRepository miembroMesaSorteadoRepository;
	
	@Autowired
	private MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository;
	
	@Autowired
	private MiembroMesaColaRepository miembroMesaColaRepository;
	
	@Autowired
	private OmisoMiembroMesaRepository omisoMiembroMesaRepository;
	
	@Autowired
	private PersoneroRepository personeroRepository;
	
	@Autowired
	private MesaDocumentoRepository mesaDocumentoRepository;
	
	@Autowired
	private CentroComputoService centroComputoService;
	
	@Autowired
	private ActaCelesteRepository actaCelesteRepository;
	
	@Autowired
	private DetActaOficioRepository detOficioRepository;
	
	@Autowired
	private CabActaFormatoRepository cabActaFormatoRepository;
	
	@Autowired
	private DetActaFormatoRepository detActaFormatoRepository;
	
	@Autowired
	private FormatoRepository tabFormatoRepository;
	
	@Autowired
	private ActaRepository actaRepository;
	
	@Autowired
	private DetActaPreferencialRepository detActaPreferencialRepository;
	
	@Autowired
	private DetActaOpcionRepository detActaOpcionRepository;
	
	@Autowired
	private ArchivoRepository archivoRepository;
	
	@Autowired
	private CabOtroDocumentoRepository cabOtroDocumentoRepository;
	
	@Autowired
	private DetOtroDocumentoRepository detOtroDocumentoRepository;
	
	@Value("${file.upload-dir}")
    private String imagePath;

	@Value("${file.upload-subcarpeta:}")
    private String uploadSubcarpeta;

	@Autowired
	private StorageService storageService;
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public EnvioTransmisionQueue guardarTransmisionAndSend(
												Long idActa, 
												TransmisionNacionEnum estadoEnum, 
												String usuario, 
												String proceso) {
		return this.guardarTransmisionCcAndSend(idActa, estadoEnum, usuario, proceso);
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void guardarTransmisionStae(Long idActa, TransmisionNacionEnum estadoEnum, String usuario, String proceso){
		ActaTransmisionNacion actaTransmitida = new ActaTransmisionNacion();
		actaTransmitida.setIdActa(idActa);
		actaTransmitida.setTipoTransmision(estadoEnum.name());
		actaTransmitida.setTransmite(ConstantesComunes.INACTIVO);
		actaTransmitida.setEstadoTransmitidoNacion(ConstantesComunes.ESTADO_TRANSMISION_ERROR);
		actaTransmitida.setAccion(this.determinarAccion(estadoEnum));
		actaTransmitida.setIntento(ConstantesComunes.CERO_INTENTO_TRANSMISION);
		actaTransmitida.setFechaTransmision(DateUtil.getFechaActualPeruana());
		actaTransmitida.setUsuarioTransmision(usuario);
		actaTransmitida.setFechaCreacion(DateUtil.getFechaActualPeruana());
		actaTransmitida.setUsuarioCreacion(usuario);
		actaTransmitida.setActivo(ConstantesComunes.ACTIVO);
		Optional<Acta> actaOp = this.actaRepository.findById(idActa);
		this.actaTransmisionNacionRepository.save(actaTransmitida);
		
		TransmisionDto request = buildRequestTransmisionStae(actaTransmitida, actaOp, estadoEnum, proceso);

		if (request != null) {
			request.setIdTransmision(actaTransmitida.getId());
			actaTransmitida.setRequestActaTransmision(request);
			this.actaTransmisionNacionRepository.save(actaTransmitida);
			
			logger.info("Se guardo la transmision {} a las {}", actaTransmitida.getId(), new Date());
		}
	}
	

	private EnvioTransmisionQueue guardarTransmisionCcAndSend(Long idActa, TransmisionNacionEnum estadoEnum, String usuario, String proceso) {

		Integer siguienteOrden = actaTransmisionNacionRepository.findMaxOrdenByIdActaWithLock(idActa)
				.map(max -> max + 1).orElse(1);

		Optional<Acta> actaOp = this.actaRepository.findById(idActa);
		Integer nTransmite = ConstantesComunes.ACTIVO;
		if (actaOp.isPresent()) {
			logger.info("se procede a guardar el acta transmitida");
			if (estadoEnum.equals(TransmisionNacionEnum.INITIAL_TRANSMISION)
					|| estadoEnum.equals(TransmisionNacionEnum.ACTA_PENDIENTE_TRANSMISION)
					|| estadoEnum.equals(TransmisionNacionEnum.DIGTAL_RECIBIDA_TRANSMISION)) {
				nTransmite = ConstantesComunes.INACTIVO;
			}

			ActaTransmisionNacion actaTransmitida = new ActaTransmisionNacion();
			actaTransmitida.setIdActa(idActa);

			if (nTransmite.equals(ConstantesComunes.INACTIVO)) {
				actaTransmitida.setOrden(null);
			} else {
				actaTransmitida.setOrden(siguienteOrden);
			}

			actaTransmitida.setTransmite(nTransmite);
			actaTransmitida.setTipoTransmision(estadoEnum.name());
			actaTransmitida.setEstadoTransmitidoNacion(ConstantesComunes.ESTADO_TRANSMISION_ERROR);
			actaTransmitida.setAccion(this.determinarAccion(estadoEnum));
			actaTransmitida.setIntento(ConstantesComunes.CERO_INTENTO_TRANSMISION);
			actaTransmitida.setFechaTransmision(DateUtil.getFechaActualPeruana());
			actaTransmitida.setUsuarioTransmision(usuario);
			actaTransmitida.setFechaCreacion(DateUtil.getFechaActualPeruana());
			actaTransmitida.setUsuarioCreacion(usuario);
			actaTransmitida.setActivo(ConstantesComunes.ACTIVO);

			
			this.actaTransmisionNacionRepository.save(actaTransmitida);
			logger.info("Se guardo la transmision {} a las {}", actaTransmitida.getId(), new Date());

			TransmisionDto request = buildRequestTransmision(actaTransmitida, actaOp, estadoEnum, proceso);

			if (request != null) {
				request.setIdTransmision(actaTransmitida.getId());
				actaTransmitida.setRequestActaTransmision(request);
				this.actaTransmisionNacionRepository.save(actaTransmitida);
			}

			Boolean success = true;

			if (nTransmite.equals(ConstantesComunes.INACTIVO) || estadoEnum.equals(TransmisionNacionEnum.PUESTA_CERO)) {
				success = false;
			}

			return EnvioTransmisionQueue
					.builder()
					.idTransmision(actaTransmitida.getId())
					.exitoso(success)
					.proceso(proceso)
					.usuario(usuario)
					.build();

		} else {
			logger.info("no se encuentra el acta");
			
			return null;
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void ejecutandose(Long idTransmision) {
		Optional<ActaTransmisionNacion> a = this.actaTransmisionNacionRepository.findById(idTransmision);
		if(a.isPresent()){
			ActaTransmisionNacion x = a.get();
			x.setEstadoTransmitidoNacion(ConstantesComunes.ESTADO_TRANSMISION_EJECUTANDOSE);
			x.setIntento(x.getIntento()+1);
			this.actaTransmisionNacionRepository.save(x);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualizarFallido(Long idTransmision, String mensaje) {
		Optional<ActaTransmisionNacion> a = this.actaTransmisionNacionRepository.findById(idTransmision);
		if(a.isPresent()){
			ActaTransmisionNacion x = a.get();
			x.setEstadoTransmitidoNacion(ConstantesComunes.ESTADO_TRANSMISION_ERROR);
			x.setIntento(x.getIntento()+1);
			x.setMensajeError(StringUtils.truncate(mensaje, 500));
			this.actaTransmisionNacionRepository.save(x);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualizarOk(Long idTransmision, String mensaje) {
		Optional<ActaTransmisionNacion> a = this.actaTransmisionNacionRepository.findById(idTransmision);
		if(a.isPresent()){
			ActaTransmisionNacion x = a.get();
			x.setEstadoTransmitidoNacion(ConstantesComunes.ESTADO_TRANSMISION_OK);
			x.setIntento(x.getIntento()+1);
			x.setMensajeError(StringUtils.truncate(mensaje, 500));
			this.actaTransmisionNacionRepository.save(x);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualizarEstado(Long idTransmision, Integer estado, String mensaje) {
		Optional<ActaTransmisionNacion> a = this.actaTransmisionNacionRepository.findById(idTransmision);
		if(a.isPresent()){
			ActaTransmisionNacion x = a.get();
			x.setEstadoTransmitidoNacion(estado);
			x.setIntento(x.getIntento()+1);
			x.setMensajeError(mensaje);
			this.actaTransmisionNacionRepository.save(x);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
    public Optional<ActaTransmisionNacion> findByIdActaTransmiion(Long idActaTransmision){
    	return this.actaTransmisionNacionRepository.findById(idActaTransmision);
    }
	
	private String determinarAccion(TransmisionNacionEnum estadoEnum){
		String resultado;
	    switch (estadoEnum) {
	    	case OMISOS_LISTA_ELECTORES_VOTANTES,
		         OMISOS_MIEMBRO_MESA,
		         REGISTRO_MIEMBRO_MESA_ESCRUTINIO,
		         REGISTRO_PERSONEROS,
		         DENUNCIAS:
	        	resultado = ConstantesTransmision.ACCION_MESA_DETALLE;
	            break;
	    	case SOBRES_CELESTES:
	    		resultado = ConstantesTransmision.ACCION_SOBRE_CELESTE;
	            break;
	        case PUESTA_CERO:
	        	resultado = ConstantesTransmision.ACCION_PUESTA_CERO;
	            break;
	        default:
	        	resultado = ConstantesTransmision.ACCION_ACTA;
	        	break;
	    }
	    return resultado;
	}
	
	private TransmisionDto buildRequestTransmisionStae(ActaTransmisionNacion actaTransmisionNacion,
			Optional<Acta> actaOp,
			TransmisionNacionEnum estadoEnum,
			String proceso) {
		TransmisionDto transmision = new TransmisionDto();
		transmision.setSubAccion(estadoEnum.name());
        transmision.setCentroComputo(this.getCodigoCentroComputoActual());
        transmision.setAccion(actaTransmisionNacion.getAccion());
        transmision.setActaTransmitida(this.mapActaStae(actaOp, estadoEnum));
        mapCommonFields(transmision, actaTransmisionNacion, proceso);  
        return transmision;
	}
	
	private TransmisionDto buildRequestTransmision(ActaTransmisionNacion actaTransmisionNacion,
			Optional<Acta> actaOp,
			TransmisionNacionEnum estadoEnum,
			String proceso) {
		TransmisionDto transmision = null;
		switch (actaTransmisionNacion.getAccion()) {
	        case ConstantesTransmision.ACCION_ACTA,
	             ConstantesTransmision.ACCION_MESA_DETALLE -> {
	            	 switch (estadoEnum) {
	            	    case DENUNCIAS:
	            	    	transmision = new TransmisionDto();
	            	    	transmision.setSubAccion(estadoEnum.name());
	        	            transmision.setCentroComputo(this.getCodigoCentroComputoActual());
	        	            transmision.setAccion(actaTransmisionNacion.getAccion());
	        	            transmision.setActaTransmitida(this.mapActaDenuncia(actaOp, estadoEnum));
	        	            mapCommonFields(transmision, actaTransmisionNacion, proceso);  
	            	        break;
	            	    default:
	            	    	transmision = new TransmisionDto();
	            	    	transmision.setSubAccion(estadoEnum.name());
	        	            transmision.setCentroComputo(this.getCodigoCentroComputoActual());
	        	            transmision.setAccion(actaTransmisionNacion.getAccion());
	        	            transmision.setActaTransmitida(this.mapActa(actaOp, estadoEnum));
	        	            mapCommonFields(transmision, actaTransmisionNacion, proceso);  
	            	}
	        }
	        case ConstantesTransmision.ACCION_SOBRE_CELESTE -> {
	        	transmision = new TransmisionDto();
	        	transmision.setSubAccion(estadoEnum.name());
	            transmision.setCentroComputo(this.getCodigoCentroComputoActual());
	            transmision.setAccion(actaTransmisionNacion.getAccion());
	            transmision.setActaTransmitida(this.mapActaCeleste(actaOp));
	            mapCommonFields(transmision, actaTransmisionNacion, proceso);
	        }
	        default -> {
	            // No hacer nada
	        }
	    }
		logger.info("termino el mensaje");
		return transmision;
	}
	
	private ActaPorTransmitirDto mapActaCeleste(Optional<Acta> actaOp) {
    	ActaPorTransmitirDto actaTransmitida = new ActaPorTransmitirDto();
        if (actaOp.isPresent()) {
        	Optional<ActaCeleste> actaCelesteOp = this.actaCelesteRepository.findByActa_Id(actaOp.get().getId());
        	if(actaCelesteOp.isPresent()){
        		ActaPorTransmitirDto actaCelesteTransmitidaDto = new ActaPorTransmitirDto();
        		ActaCeleste actaCeleste = actaCelesteOp.get();
        		actaCelesteTransmitidaDto.setIdActa(actaCeleste.getActa().getId());
                actaCelesteTransmitidaDto.setIdActaCeleste(actaCeleste.getId());
                actaCelesteTransmitidaDto.setIdCcCeleste(String.format(CC_FORMATO, this.getCodigoCentroComputoActual(), actaCeleste.getId().toString()));
                actaCelesteTransmitidaDto.setIdArchivoEscrutinio(actaCeleste.getArchivoEscrutinio()!=null ? actaCeleste.getArchivoEscrutinio().getId():null);
                actaCelesteTransmitidaDto.setIdArchivoInstalacionSufragio(actaCeleste.getArchivoInstalacionSufragio()!=null ? actaCeleste.getArchivoInstalacionSufragio().getId():null);
                actaCelesteTransmitidaDto.setNumeroCopia(actaCeleste.getNumeroCopia());
                actaCelesteTransmitidaDto.setDigitoChequeoEscrutinio(actaCeleste.getDigitoChequeoEscrutinio());
                actaCelesteTransmitidaDto.setDigitoChequeoInstalacion(actaCeleste.getDigitoChequeoInstalacion());
                actaCelesteTransmitidaDto.setDigitoChequeoSufragio(actaCeleste.getDigitoChequeoSufragio());
                actaCelesteTransmitidaDto.setEstadoDigitalizacion(actaCeleste.getEstadoDigitalizacion());
                actaCelesteTransmitidaDto.setDigitalizacionEscrutinio(actaCeleste.getDigitalizacionEscrutinio());
                actaCelesteTransmitidaDto.setDigitalizacionInstalacion(actaCeleste.getDigitalizacionInstalacion());
                actaCelesteTransmitidaDto.setDigitalizacionSufragio(actaCeleste.getDigitalizacionSufragio());
                actaCelesteTransmitidaDto.setDigitalizacionInstalacionSufragio(actaCeleste.getDigitalizacionInstalacionSufragio());
                actaCelesteTransmitidaDto.setObservDigEscrutinio(actaCeleste.getObservDigEscrutinio());
                actaCelesteTransmitidaDto.setObservDigInstalacionSufragio(actaCeleste.getObservDigInstalacionSufragio());
                actaCelesteTransmitidaDto.setActivo(actaCeleste.getActivo());
                actaCelesteTransmitidaDto.setAsignado(actaCeleste.getAsignado());
                actaCelesteTransmitidaDto.setAudFechaCreacion(DateUtil.getDateString(actaCeleste.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
                actaCelesteTransmitidaDto.setAudFechaModificacion(DateUtil.getDateString(actaCeleste.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
                actaCelesteTransmitidaDto.setAudUsuarioCreacion(actaCeleste.getUsuarioCreacion());
                actaCelesteTransmitidaDto.setAudUsuarioModificacion(actaCeleste.getUsuarioModificacion());
                actaCelesteTransmitidaDto.setDetallesOficio(mapOficioByIdActaCeleste(actaCeleste.getId()));
                actaTransmitida.setIdActa(actaCeleste.getActa().getId());
                actaTransmitida.setEstadoActa(actaCeleste.getActa().getEstadoActa());
                actaTransmitida.setEstadoActaResolucion(actaCeleste.getActa().getEstadoActaResolucion());
                actaTransmitida.setEstadoActaOriginal(actaCeleste.getActa().getEstadoActa());
                actaTransmitida.setEstadoCc(actaCeleste.getActa().getEstadoCc());
                actaTransmitida.setEstadoDigitalizacion(actaCeleste.getActa().getEstadoDigitalizacion());
                actaTransmitida.setEstadoErrorMaterial(actaCeleste.getActa().getEstadoErrorMaterial());
                actaTransmitida.setAudFechaCreacion(DateUtil.getDateString(actaCeleste.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
                actaTransmitida.setAudFechaModificacion(DateUtil.getDateString(actaCeleste.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
                actaTransmitida.setAudUsuarioCreacion(actaCeleste.getUsuarioCreacion());
                actaTransmitida.setAudUsuarioModificacion(actaCeleste.getUsuarioModificacion());
                actaTransmitida.setActaCeleste(actaCelesteTransmitidaDto); 
        	}
        }
       
        
        return actaTransmitida;
    }
	
	private ActaPorTransmitirDto mapActaDenuncia(Optional<Acta> actaOp, TransmisionNacionEnum estadoEnum) {
		ActaPorTransmitirDto actaTransmitidaDto = new ActaPorTransmitirDto();
        Acta acta = null;
        if (actaOp.isPresent()) {
        	acta = actaOp.get();
            actaTransmitidaDto.setIdActa(acta.getId());
            actaTransmitidaDto.setIdMesa(acta.getMesa()!=null ? acta.getMesa().getId() : null);
            if(acta.getMesa()!=null) {
            	actaTransmitidaDto.setMesa(
            			mapMesa(acta.getMesa().getId(),
            					false, 
            					false, 
                    			false, 
                    			false, 
                    			false, 
                    			false,
                    			true
            			));
            }
        }
		return actaTransmitidaDto;
	}
	
	private ActaPorTransmitirDto mapActaStae(Optional<Acta> actaOp, TransmisionNacionEnum estadoEnum) {
		
		ActaPorTransmitirDto actaTransmitidaDto = new ActaPorTransmitirDto();
		Acta acta = actaOp.get();
		actaTransmitidaDto.setIdActa(acta.getId());
        actaTransmitidaDto.setIdMesa(acta.getMesa()!=null ? acta.getMesa().getId() : null);
        actaTransmitidaDto.setEstadoActa(getEstadoActa(acta.getEstadoActa(),estadoEnum));
        actaTransmitidaDto.setEstadoCc(acta.getEstadoCc());
        actaTransmitidaDto.setEstadoActaResolucion(acta.getEstadoActaResolucion());
        actaTransmitidaDto.setEstadoActaOriginal(acta.getEstadoActa());
        actaTransmitidaDto.setEstadoDigitalizacion(acta.getEstadoDigitalizacion());
        actaTransmitidaDto.setEstadoErrorMaterial(acta.getEstadoErrorMaterial());
        actaTransmitidaDto.setAudFechaCreacion(DateUtil.getDateString(acta.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
        actaTransmitidaDto.setAudFechaModificacion(DateUtil.getDateString(acta.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
        actaTransmitidaDto.setAudUsuarioCreacion(acta.getUsuarioCreacion());
        actaTransmitidaDto.setAudUsuarioModificacion(acta.getUsuarioModificacion());
        return actaTransmitidaDto;
        
	}
	
	private ActaPorTransmitirDto mapActa(Optional<Acta> actaOp, TransmisionNacionEnum estadoEnum) {
    	
        ActaPorTransmitirDto actaTransmitidaDto = new ActaPorTransmitirDto();
        Acta acta = null;
        if (actaOp.isPresent()) {
        	acta = actaOp.get();
        	logger.info("Estado del acta: {}", acta.getEstadoActa());
            actaTransmitidaDto.setIdActa(acta.getId());
            actaTransmitidaDto.setIdMesa(acta.getMesa()!=null ? acta.getMesa().getId() : null);
            if(acta.getMesa()!=null) {
            	actaTransmitidaDto.setMesa(mapMesa(acta.getMesa().getId(), 
            			true, 
            			true, 
            			true, 
            			true, 
            			true, 
            			true,
            			true));
            }
            actaTransmitidaDto.setIdArchivoEscrutinio(acta.getArchivoEscrutinio()!=null ? acta.getArchivoEscrutinio().getId():null);
            actaTransmitidaDto.setIdArchivoInstalacionSufragio(acta.getArchivoInstalacionSufragio()!=null ? acta.getArchivoInstalacionSufragio().getId():null);
            actaTransmitidaDto.setIdDetUbigeoEleccion(acta.getUbigeoEleccion()!=null ? acta.getUbigeoEleccion().getId():null);
            actaTransmitidaDto.setNumeroCopia(acta.getNumeroCopia());
            actaTransmitidaDto.setNumeroLote(acta.getNumeroLote());
            actaTransmitidaDto.setDigitoChequeoEscrutinio(acta.getDigitoChequeoEscrutinio());
            actaTransmitidaDto.setDigitoChequeoInstalacion(acta.getDigitoChequeoInstalacion());
            actaTransmitidaDto.setDigitoChequeoSufragio(acta.getDigitoChequeoSufragio());
            actaTransmitidaDto.setTipoLote(acta.getTipoLote());
            actaTransmitidaDto.setElectoresHabiles(acta.getElectoresHabiles());
            actaTransmitidaDto.setCvas(acta.getCvas());
            actaTransmitidaDto.setCvasAutomatico(acta.getCvasAutomatico());
            actaTransmitidaDto.setCvasv1(acta.getCvasV1());
            actaTransmitidaDto.setCvasv2(acta.getCvasV2());
            actaTransmitidaDto.setIlegibleCvas(acta.getIlegibleCvas());
            actaTransmitidaDto.setIlegibleCvasv1(acta.getIlegibleCvasV1());
            actaTransmitidaDto.setIlegibleCvasv2(acta.getIlegibleCvasV2());
            actaTransmitidaDto.setVotosCalculados(acta.getVotosCalculados());
            actaTransmitidaDto.setTotalVotos(acta.getTotalVotos());
            actaTransmitidaDto.setEstadoActaOriginal(acta.getEstadoActa());
            actaTransmitidaDto.setEstadoActa(getEstadoActa(acta.getEstadoActa(),estadoEnum));
            actaTransmitidaDto.setEstadoCc(acta.getEstadoCc());
            actaTransmitidaDto.setEstadoActaResolucion(acta.getEstadoActaResolucion());
            actaTransmitidaDto.setEstadoDigitalizacion(acta.getEstadoDigitalizacion());
            actaTransmitidaDto.setEstadoErrorMaterial(acta.getEstadoErrorMaterial());
            actaTransmitidaDto.setDigitalizacionEscrutinio(acta.getDigitalizacionEscrutinio());
            actaTransmitidaDto.setDigitalizacionInstalacionSufragio(acta.getDigitalizacionInstalacionSufragio());
            actaTransmitidaDto.setControlDigEscrutinio(acta.getControlDigEscrutinio());
            actaTransmitidaDto.setControlDigInstalacionSufragio(acta.getControlDigInstalacionSufragio());
            actaTransmitidaDto.setObservDigEscrutinio(acta.getObservDigEscrutinio());
            actaTransmitidaDto.setObservDigInstalacionSufragio(acta.getObservDigInstalacionSufragio());
            actaTransmitidaDto.setDigitacionHoras(acta.getDigitacionHoras());
            actaTransmitidaDto.setDigitacionVotos(acta.getDigitacionVotos());
            actaTransmitidaDto.setDigitacionObserv(acta.getDigitacionObserv());
            actaTransmitidaDto.setDigitacionFirmasAutomatico(acta.getDigitacionFirmasAutomatico());
            actaTransmitidaDto.setDigitacionFirmasManual(acta.getDigitacionFirmasManual());
            actaTransmitidaDto.setControlDigitacion(acta.getControlDigitacion());
            actaTransmitidaDto.setHoraEscrutinioAutomatico(acta.getHoraEscrutinioAutomatico());
            actaTransmitidaDto.setHoraEscrutinioManual(acta.getHoraEscrutinioManual());
            actaTransmitidaDto.setHoraInstalacionAutomatico(acta.getHoraInstalacionAutomatico());
            actaTransmitidaDto.setHoraInstalacionManual(acta.getHoraInstalacionManual());
            actaTransmitidaDto.setDescripcionObservAutomatico(acta.getDescripcionObservAutomatico());
            actaTransmitidaDto.setEscrutinioFirmaMm1Automatico(acta.getEscrutinioFirmaMm1Automatico());
            actaTransmitidaDto.setEscrutinioFirmaMm2Automatico(acta.getEscrutinioFirmaMm2Automatico());
            actaTransmitidaDto.setEscrutinioFirmaMm3Automatico(acta.getEscrutinioFirmaMm3Automatico());
            actaTransmitidaDto.setInstalacionFirmaMm1Automatico(acta.getInstalacionFirmaMm1Automatico());
            actaTransmitidaDto.setInstalacionFirmaMm2Automatico(acta.getInstalacionFirmaMm2Automatico());
            actaTransmitidaDto.setInstalacionFirmaMm3Automatico(acta.getInstalacionFirmaMm3Automatico());
            actaTransmitidaDto.setSufragioFirmaMm1Automatico(acta.getSufragioFirmaMm1Automatico());
            actaTransmitidaDto.setSufragioFirmaMm2Automatico(acta.getSufragioFirmaMm2Automatico());
            actaTransmitidaDto.setSufragioFirmaMm3Automatico(acta.getSufragioFirmaMm3Automatico());
            actaTransmitidaDto.setTipoTransmision(acta.getTipoTransmision());
            actaTransmitidaDto.setActivo(acta.getActivo());
            actaTransmitidaDto.setVerificador(acta.getVerificador());
            actaTransmitidaDto.setVerificador2(acta.getVerificador2());
            actaTransmitidaDto.setAudFechaCreacion(DateUtil.getDateString(acta.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
            actaTransmitidaDto.setAudFechaModificacion(DateUtil.getDateString(acta.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
            actaTransmitidaDto.setAudUsuarioCreacion(acta.getUsuarioCreacion());
            actaTransmitidaDto.setAudUsuarioModificacion(acta.getUsuarioModificacion());
            actaTransmitidaDto.setDetalle(mapDetallesActa(acta.getId()));
            actaTransmitidaDto.setResoluciones(mapResoluciones(acta.getId()));
            actaTransmitidaDto.setAcciones(mapDetallesAccion(acta.getId()));
            actaTransmitidaDto.setSolucionTecnologica(acta.getSolucionTecnologica());
        }
        
        return actaTransmitidaDto;
    }
	

	private MesaPorTransmitirDto mapMesa(Long idMesa, 
			boolean mapearOmisosVotantes,
			boolean mapearMiembrosMesaSorteados,
			boolean mapearMiembrosMesaCola,
			boolean mapearDetallesArchivo,
			boolean mapearMiembrosMesaEscrutinio,
			boolean mapearPersoneros,
			boolean mapearOtroDocumento) {
		MesaPorTransmitirDto mesaDto = null;
		Optional<Mesa> mesa = mesaRepository.findById(idMesa);
		if(mesa.isPresent()) {
			mesaDto = new MesaPorTransmitirDto();
			mesaDto.setIdMesa(idMesa);
			mesaDto.setEstado(mesa.get().getEstadoMesa());
			
			// states
			mesaDto.setEstadoDigitalizacionLe(mesa.get().getEstadoDigitalizacionLe());
			mesaDto.setEstadoDigitalizacionMm(mesa.get().getEstadoDigitalizacionMm());
			mesaDto.setEstadoDigitalizacionPr(mesa.get().getEstadoDigitalizacionPr());
			mesaDto.setEstadoDigitalizacionMe(mesa.get().getEstadoDigitalizacionMe());
			
			//users
			mesaDto.setUsuarioAsignadoLe(mesa.get().getUsuarioAsignadoLe());
			mesaDto.setUsuarioAsignadoMm(mesa.get().getUsuarioAsignadoMm());
			mesaDto.setUsuarioAsignadoPr(mesa.get().getUsuarioAsignadoPr());
			mesaDto.setUsuarioAsignadoMe(mesa.get().getUsuarioAsignadoMe());
			
			// date
			mesaDto.setFechaAsignadoLe(DateUtil.getDateString(mesa.get().getFechaAsignadoLe(), SceConstantes.FORMATO_FECHA_TRANSMISION));
			mesaDto.setFechaAsignadoMm(DateUtil.getDateString(mesa.get().getFechaAsignadoMm(), SceConstantes.FORMATO_FECHA_TRANSMISION));
			mesaDto.setFechaAsignadoPr(DateUtil.getDateString(mesa.get().getFechaAsignadoPr(), SceConstantes.FORMATO_FECHA_TRANSMISION));
			mesaDto.setFechaAsignadoMe(DateUtil.getDateString(mesa.get().getFechaAsignadoMe(), SceConstantes.FORMATO_FECHA_TRANSMISION));
			
			mesaDto.setActivo(mesa.get().getActivo());
			mesaDto.setUsuarioCreacion(mesa.get().getUsuarioCreacion());
			mesaDto.setUsuarioModificacion(mesa.get().getUsuarioModificacion());
			mesaDto.setFechaCreacion(DateUtil.getDateString(mesa.get().getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
			mesaDto.setFechaModificacion(DateUtil.getDateString(mesa.get().getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
			
			if(mapearOmisosVotantes){
				mesaDto.setOmisosVotantes(mapOmivosVotantes(idMesa));
			}
			
			if(mapearMiembrosMesaSorteados){
				mesaDto.setMiembrosMesaSorteado(miembrosMesaSorteado(idMesa));
			}
			
			if(mapearMiembrosMesaCola){
				mesaDto.setMiembrosMesaCola(miembrosMesaCola(idMesa));
			}
			
			if(mapearDetallesArchivo){
				mesaDto.setDetalleArchivo(detallesArchivo(idMesa));
			}
			
			if(mapearMiembrosMesaEscrutinio){
				mesaDto.setMiembrosMesaEscrutinio(miembrosMesaEscrutinio(idMesa));
			}
			
			if(mapearPersoneros){
				mesaDto.setPersoneros(mapPersoneros(idMesa));
			}
			
			if(mapearOtroDocumento){
				mesaDto.setDetOtroDocumento(mapDetOtroDocumento(idMesa));
			}
			
			
		}
		return mesaDto;
	}
	
	
	private List<DetOtroDocumentoPorTransmitirDto> mapDetOtroDocumento(Long idMesa) {
	    List<DetOtroDocumentoPorTransmitirDto> otrosDocumentos = null;
	    List<DetOtroDocumento> otroDetalleDocumentos = this.detOtroDocumentoRepository.findByIdMesa(idMesa);
	    if (otroDetalleDocumentos != null && !otroDetalleDocumentos.isEmpty()) {
	        otrosDocumentos = new ArrayList<>();
	        DetOtroDocumentoPorTransmitirDto dto = null;
	        for (DetOtroDocumento otroDocumento : otroDetalleDocumentos) {
	            dto = mapDetOtroDocumentoDto(otroDocumento);
	            otrosDocumentos.add(dto);
	        }
	    }
	    return otrosDocumentos;
	}

	private DetOtroDocumentoPorTransmitirDto mapDetOtroDocumentoDto(DetOtroDocumento detOtroDocumento) {
	    DetOtroDocumentoPorTransmitirDto dto = new DetOtroDocumentoPorTransmitirDto();
	    dto.setId(detOtroDocumento.getId());
	    dto.setIdCc((String.format(CC_FORMATO, this.getCodigoCentroComputoActual(), detOtroDocumento.getId().toString()))); // id orc;
	    dto.setCodTipoDocumento(detOtroDocumento.getCodTipoDocumento());
	    dto.setCodTipoPerdida(detOtroDocumento.getCodTipoPerdida());
	    dto.setActivo(detOtroDocumento.getActivo());
	    dto.setAudUsuarioCreacion(detOtroDocumento.getAudUsuarioCreacion());
	    dto.setAudFechaCreacion(DateUtil.getDateString(
	        detOtroDocumento.getAudFechaCreacion(), 
	        SceConstantes.FORMATO_FECHA_TRANSMISION));
	    dto.setAudUsuarioModificacion(detOtroDocumento.getAudUsuarioModificacion());
	    dto.setAudFechaModificacion(DateUtil.getDateString(
	        detOtroDocumento.getAudFechaModificacion(), 
	        SceConstantes.FORMATO_FECHA_TRANSMISION));
	    if (detOtroDocumento.getMesa() != null) {
	        dto.setCodigoMesa(detOtroDocumento.getMesa().getCodigo());
	    }
	    if (detOtroDocumento.getCabOtroDocumento() != null) {
	        dto.setCabOtroDocumento(mapOtroDocumentoCabDto(detOtroDocumento.getCabOtroDocumento().getId()));
	    }
	    return dto;
	}

	private OtroDocumentoPorTransmitirDto mapOtroDocumentoCabDto(Long id) {
		
		Optional<CabOtroDocumento> cabOp = this.cabOtroDocumentoRepository.findById(id);
		
		if(!cabOp.isPresent()){
			return null;
		}
		
		CabOtroDocumento cab = cabOp.get();
		
	    OtroDocumentoPorTransmitirDto cabDto = new OtroDocumentoPorTransmitirDto();
	    cabDto.setId(cab.getId());
	    
	    if (cab.getCentroComputo() != null) {
	        cabDto.setCodigoCentroComputo(cab.getCentroComputo().getCodigo());
	    }
	    
	    cabDto.setIdCc((String.format(CC_FORMATO, this.getCodigoCentroComputoActual(), id.toString()))); // id orc;
	    cabDto.setNumeroDocumento(cab.getNumeroDocumento());
	    cabDto.setCodTipoDocumento(cab.getCodTipoDocumento());
	    cabDto.setNumeroPaginas(cab.getNumeroPaginas());
	    cabDto.setEstadoDigitalizacion(cab.getEstadoDigitalizacion());
	    cabDto.setEstadoDocumento(cab.getEstadoDocumento());
	    cabDto.setUsuarioControl(cab.getUsuarioControl());
	    cabDto.setFechaUsuarioControl(DateUtil.getDateString(
	        cab.getFechaUsuarioControl(), 
	        SceConstantes.FORMATO_FECHA_TRANSMISION));
	    cabDto.setActivo(cab.getActivo());
	    cabDto.setAudUsuarioCreacion(cab.getAudUsuarioCreacion());
	    cabDto.setAudFechaCreacion(DateUtil.getDateString(
	        cab.getAudFechaCreacion(), 
	        SceConstantes.FORMATO_FECHA_TRANSMISION));
	    cabDto.setAudUsuarioModificacion(cab.getAudUsuarioModificacion());
	    cabDto.setAudFechaModificacion(DateUtil.getDateString(
	        cab.getAudFechaModificacion(), 
	        SceConstantes.FORMATO_FECHA_TRANSMISION));
	    return cabDto;
	}
	
	
	private List<PersoneroPorTransmitirDto> mapPersoneros(Long idMesa){
		List<PersoneroPorTransmitirDto> personerosDto = null;
		List<Personero> personeros = personeroRepository.findByIdMesa(idMesa);
		if(personeros!=null) {
			personerosDto = new ArrayList<>();
			PersoneroPorTransmitirDto personeroDto = null;
			for(Personero personero:personeros) {
				personeroDto = new PersoneroPorTransmitirDto();
				personeroDto.setIdPersonero(personero.getId());
				personeroDto.setIdMesa(idMesa);
				if(personero.getAgrupacionPolitica()!=null) {
					personeroDto.setIdAgrupacionPolitica(personero.getAgrupacionPolitica().getId());
				}
				personeroDto.setDocumentoIdentidad(personero.getDocumentoIdentidad());
				personeroDto.setNombres(personero.getNombres());
				personeroDto.setApellidoPaterno(personero.getApellidoPaterno());
				personeroDto.setApellidoMaterno(personero.getApellidoMaterno());
				personeroDto.setActivo(personero.getActivo());
				personeroDto.setAudUsuarioCreacion(personero.getUsuarioCreacion());
				personeroDto.setAudUsuarioModificacion(personero.getUsuarioModificacion());
				personeroDto.setAudFechaCreacion(DateUtil.getDateString(personero.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				personeroDto.setAudFechaModificacion(DateUtil.getDateString(personero.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				personerosDto.add(personeroDto);
			}
		}
		return personerosDto;
	}
	

	private List<OmisoVotantePorTransmitirDto> mapOmivosVotantes(Long idMesa) {
		List<OmisoVotantePorTransmitirDto> omisosDto = null;
		List<OmisoVotante> omisos = omisoVotanteRepository.findByMesaId(idMesa);
		if(omisos!=null) {
			omisosDto = new ArrayList<>();
			OmisoVotantePorTransmitirDto omisoDto = null;
			for(OmisoVotante omiso:omisos) {
				omisoDto = new OmisoVotantePorTransmitirDto();
				omisoDto.setIdMesa(idMesa);
				omisoDto.setIdPadronElectoral(omiso.getPadronElectoral()!=null ? omiso.getPadronElectoral().getId() : null);
				omisoDto.setActivo(omiso.getActivo());
				omisoDto.setUsuarioCreacion(omiso.getUsuarioCreacion());
				omisoDto.setUsuarioModificacion(omiso.getUsuarioModificacion());
				omisoDto.setFechaCreacion(DateUtil.getDateString(omiso.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				omisoDto.setFechaModificacion(DateUtil.getDateString(omiso.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				omisosDto.add(omisoDto);
			}
		}
		return omisosDto;
	}
	
	private List<MiembroMesaSorteadoPorTransmitirDto> miembrosMesaSorteado(Long idMesa){
		List<MiembroMesaSorteadoPorTransmitirDto> omisosMesaSorteadoDto = null;
		List<MiembroMesaSorteado> miembrosMesaSorteado = miembroMesaSorteadoRepository.findByMesaId(idMesa);
		if(miembrosMesaSorteado!=null) {
			omisosMesaSorteadoDto = new ArrayList<>();
			MiembroMesaSorteadoPorTransmitirDto miembroMesaSorteadoDto = null;
			for(MiembroMesaSorteado miembroMesaSorteado:miembrosMesaSorteado) {
				miembroMesaSorteadoDto = new MiembroMesaSorteadoPorTransmitirDto();
				miembroMesaSorteadoDto.setIdMiembroMesaSorteado(miembroMesaSorteado.getId());
				miembroMesaSorteadoDto.setIdMesa(idMesa);
				miembroMesaSorteadoDto.setOmisosMiembroMesa(omisosMiembrosMesas(miembroMesaSorteado.getId(),idMesa));
				miembroMesaSorteadoDto.setAsistenciaAutomatico(miembroMesaSorteado.getAsistenciaAutomatico());
				miembroMesaSorteadoDto.setAsistenciaManual(miembroMesaSorteado.getAsistenciaManual());
				miembroMesaSorteadoDto.setActivo(miembroMesaSorteado.getActivo());
				miembroMesaSorteadoDto.setUsuarioCreacion(miembroMesaSorteado.getUsuarioCreacion());
				miembroMesaSorteadoDto.setUsuarioModificacion(miembroMesaSorteado.getUsuarioModificacion());
				miembroMesaSorteadoDto.setFechaCreacion(DateUtil.getDateString(miembroMesaSorteado.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				miembroMesaSorteadoDto.setFechaModificacion(DateUtil.getDateString(miembroMesaSorteado.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				omisosMesaSorteadoDto.add(miembroMesaSorteadoDto);
			}
		}
		return omisosMesaSorteadoDto;
	}
	
	private List<MiembroMesaEscrutinioPorTransmitirDto> miembrosMesaEscrutinio(Long idMesa){
		List<MiembroMesaEscrutinioPorTransmitirDto> miembrosMesaEscrutinioDto = null;
		List<MiembroMesaEscrutinio> miembrosMesaEscrutinio = miembroMesaEscrutinioRepository.findByMesaId(idMesa);
		if(miembrosMesaEscrutinio!=null) {
			miembrosMesaEscrutinioDto = new ArrayList<>();
			MiembroMesaEscrutinioPorTransmitirDto miembroMesaEscrutinioDto = null;
			for(MiembroMesaEscrutinio miembroMesaEscrutinio:miembrosMesaEscrutinio) {
				miembroMesaEscrutinioDto = new MiembroMesaEscrutinioPorTransmitirDto();
				miembroMesaEscrutinioDto.setIdMiembroMesaEscrutinio(miembroMesaEscrutinio.getId());
				miembroMesaEscrutinioDto.setIdMiembroMesaEscrutinioCc(String.format(CC_FORMATO, this.getCodigoCentroComputoActual(), miembroMesaEscrutinio.getId().toString()));				
				miembroMesaEscrutinioDto.setIdMesa(idMesa);
				miembroMesaEscrutinioDto.setDocumentoIdentidadPresidente(miembroMesaEscrutinio.getDocumentoIdentidadPresidente());
				miembroMesaEscrutinioDto.setDocumentoIdentidadSecretario(miembroMesaEscrutinio.getDocumentoIdentidadSecretario());
				miembroMesaEscrutinioDto.setDocumentoIdentidadTercerMiembro(miembroMesaEscrutinio.getDocumentoIdentidadTercerMiembro());
				miembroMesaEscrutinioDto.setActivo(miembroMesaEscrutinio.getActivo());
				miembroMesaEscrutinioDto.setUsuarioCreacion(miembroMesaEscrutinio.getUsuarioCreacion());
				miembroMesaEscrutinioDto.setUsuarioModificacion(miembroMesaEscrutinio.getUsuarioModificacion());
				miembroMesaEscrutinioDto.setFechaCreacion(DateUtil.getDateString(miembroMesaEscrutinio.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				miembroMesaEscrutinioDto.setFechaModificacion(DateUtil.getDateString(miembroMesaEscrutinio.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				miembrosMesaEscrutinioDto.add(miembroMesaEscrutinioDto);
			}
		}
		return miembrosMesaEscrutinioDto;
	}

	private List<OmisoMiembroMesaPorTransmitirDto> omisosMiembrosMesas(Long idMiembroMesaSorteado, Long idMesa){
		List<OmisoMiembroMesaPorTransmitirDto> omisosMiembroMesaDto = null;
		List<OmisoMiembroMesa> omisosMiembroMesa = omisoMiembroMesaRepository.findByMiembroMesaSorteadoId(idMiembroMesaSorteado);
		if(omisosMiembroMesa!=null) {
			omisosMiembroMesaDto = new ArrayList<>();
			OmisoMiembroMesaPorTransmitirDto omisoMiembroMesaDto = null;
			for(OmisoMiembroMesa omisoMiembroMesa:omisosMiembroMesa) {
				omisoMiembroMesaDto = new OmisoMiembroMesaPorTransmitirDto();
				omisoMiembroMesaDto.setIdOmisoMiembroMesa(omisoMiembroMesa.getId());
				omisoMiembroMesaDto.setIdOmisoMiembroMesaCc(String.format(CC_FORMATO, this.getCodigoCentroComputoActual(), omisoMiembroMesa.getId().toString()));
				omisoMiembroMesaDto.setIdMiembroMesaSorteado(idMiembroMesaSorteado);
				omisoMiembroMesaDto.setIdMesa(idMesa);
				omisoMiembroMesaDto.setActivo(omisoMiembroMesa.getActivo());
				omisoMiembroMesaDto.setUsuarioCreacion(omisoMiembroMesa.getUsuarioCreacion());
				omisoMiembroMesaDto.setUsuarioModificacion(omisoMiembroMesa.getUsuarioModificacion());
				omisoMiembroMesaDto.setFechaCreacion(DateUtil.getDateString(omisoMiembroMesa.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				omisoMiembroMesaDto.setFechaModificacion(DateUtil.getDateString(omisoMiembroMesa.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				omisosMiembroMesaDto.add(omisoMiembroMesaDto);
			}
		}
		return omisosMiembroMesaDto;
	}
	
	private List<MiembroMesaColaPorTransmitirDto> miembrosMesaCola(Long idMesa){
		List<MiembroMesaColaPorTransmitirDto> miembrosMesaColaDto = null;
		List<MiembroMesaCola> miembrosMesaCola = miembroMesaColaRepository.findByMesaId(idMesa);
		if(miembrosMesaCola!=null) {
			miembrosMesaColaDto = new ArrayList<>();
			MiembroMesaColaPorTransmitirDto miembroMesaColaDto = null;
			for(MiembroMesaCola miembroMesaCola:miembrosMesaCola) {
				miembroMesaColaDto = new MiembroMesaColaPorTransmitirDto();
				miembroMesaColaDto.setIdPadronElectoral(miembroMesaCola.getPadronElectoral()!=null 
						? miembroMesaCola.getPadronElectoral().getId()
						: null);
				miembroMesaColaDto.setIdMesa(idMesa);
				miembroMesaColaDto.setCargo(miembroMesaCola.getCargo());
				miembroMesaColaDto.setActivo(miembroMesaCola.getActivo());
				miembroMesaColaDto.setUsuarioCreacion(miembroMesaCola.getUsuarioCreacion());
				miembroMesaColaDto.setUsuarioModificacion(miembroMesaCola.getUsuarioModificacion());
				miembroMesaColaDto.setFechaCreacion(DateUtil.getDateString(miembroMesaCola.getFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
				miembroMesaColaDto.setFechaModificacion(DateUtil.getDateString(miembroMesaCola.getFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
				miembrosMesaColaDto.add(miembroMesaColaDto);
			}
		}
		return miembrosMesaColaDto;
	}
	

	private List<MesaArchivoPorTransmitirDto> detallesArchivo(Long idMesa){
		List<MesaArchivoPorTransmitirDto> detallesArchivoDto = null;
		List<MesaDocumento> mesasDocumento = mesaDocumentoRepository.findByMesaId(idMesa);
		if(mesasDocumento!=null) {
			detallesArchivoDto = new ArrayList<>();
			MesaArchivoPorTransmitirDto detalleArchivoDto = null;
			for(MesaDocumento mesaDocumento:mesasDocumento) {
				detalleArchivoDto = new MesaArchivoPorTransmitirDto();
				detalleArchivoDto.setIdMesa(idMesa);
				detalleArchivoDto.setIdDocumentoElectoral(mesaDocumento.getAdmDocumentoElectoral().getId());
				detalleArchivoDto.setIdMesaArchivoCc(String.format(CC_FORMATO, this.getCodigoCentroComputoActual(), mesaDocumento.getId().toString()));
				detalleArchivoDto.setTipoArchivo(mesaDocumento.getTipoArchivo());
				detalleArchivoDto.setPagina(mesaDocumento.getPagina());
				detalleArchivoDto.setDescripcionObservacion(mesaDocumento.getDescripcionObservacion());
				detalleArchivoDto.setDigitalizacion(mesaDocumento.getDigitalizacion());
				detalleArchivoDto.setEstadoDigitalizacion(mesaDocumento.getEstadoDigitalizacion());				
				detalleArchivoDto.setObservacionDigitalizacion(mesaDocumento.getObservacionDigitalizacion());	
				detalleArchivoDto.setActivo(mesaDocumento.getActivo());
				detalleArchivoDto.setUsuarioCreacion(mesaDocumento.getUsuarioCreacion());
				detalleArchivoDto.setUsuarioModificacion(mesaDocumento.getUsuarioModificacion());
				detalleArchivoDto.setFechaCreacion(DateUtil.getDateString(mesaDocumento.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				detalleArchivoDto.setFechaModificacion(DateUtil.getDateString(mesaDocumento.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
				detallesArchivoDto.add(detalleArchivoDto);
			}
		}
		return detallesArchivoDto;
	}


    private List<DetActaPorTransmitirDto> mapDetallesActa(Long idActa)
    {
        List<DetActaPorTransmitirDto> detActaTransmitidaDto = null;
        List<DetActa> detallesActa = this.detActaRepository.findByActa_Id(idActa);
        if (detallesActa != null) {
        	detActaTransmitidaDto = new ArrayList<>();
            for (DetActa detalle : detallesActa) {
                detActaTransmitidaDto.add(DetActaPorTransmitirDto
                        .builder()
                        .idActaDetalle(detalle.getId())
                        .idActa(idActa)
                        .idAgrupacionPolitica(detalle.getAgrupacionPolitica() != null ? detalle.getAgrupacionPolitica().getId() : null)
                        .posicion(detalle.getPosicion())
                        .votos(detalle.getVotos())
                        .votosAutomatico(detalle.getVotosAutomatico())
                        .votosManual1(detalle.getVotosManual1())
                        .votosManual2(detalle.getVotosManual2())
                        .estadoErrorMaterial(detalle.getEstadoErrorMaterial())
                        .estado(detalle.getEstado())
                        .ilegible(detalle.getIlegible())
                        .ilegiblev1(detalle.getIlegiblev1())
                        .ilegiblev2(detalle.getIlegiblev2())
                        .detActaPreferencial(mapActaPreferencial(detalle))
                        .detActaOpcion(mapDetallesOpcion(detalle))
                        .activo(detalle.getActivo())
                        .audFechaCreacion(DateUtil.getDateString(detalle.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .audFechaModificacion(DateUtil.getDateString(detalle.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .audUsuarioCreacion(detalle.getUsuarioCreacion())
                        .audUsuarioModificacion(detalle.getUsuarioModificacion())
                        .build());
            }
        }
        return detActaTransmitidaDto;
    }
	
	private List<DetActaAccionPorTransmitirDto> mapDetallesAccion(Long idActa){
		List<DetActaAccionPorTransmitirDto> detActaAccionTransmitidaDto = null;
		List<DetActaAccion> acciones = this.detActaAccionRepository.findByActa_Id(idActa);
		if (acciones != null) {
			detActaAccionTransmitidaDto = new ArrayList<>();
            for (DetActaAccion detalle : acciones) {
            	detActaAccionTransmitidaDto.add(DetActaAccionPorTransmitirDto
                        .builder()
                        .idDetActaAccion(detalle.getId())
                        .idCcDetActaAccion(String.format(CC_FORMATO, this.getCodigoCentroComputoActual(), detalle.getId().toString())) // id orc
                        .idActa(idActa)
                        .accion(detalle.getAccion())
                        .tiempo(detalle.getTiempo())
                        .iteracion(detalle.getIteracion())
                        .orden(detalle.getOrden())
                        .activo(detalle.getActivo())
                        .usuarioAccion(detalle.getUsuarioAccion())
                        .fechaAccion(DateUtil.getDateString(detalle.getFechaAccion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .audFechaCreacion(DateUtil.getDateString(detalle.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .audFechaModificacion(DateUtil.getDateString(detalle.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .audUsuarioCreacion(detalle.getUsuarioCreacion())
                        .audUsuarioModificacion(detalle.getUsuarioModificacion())
                        .build());
            }
        }
        return detActaAccionTransmitidaDto;
	}

	private List<DetActaOpcionPorTransmitirDto> mapDetallesOpcion(DetActa detActa){
		List<DetActaOpcion> detallesActaOpcion = this.detActaOpcionRepository.findByDetActa(detActa);
		List<DetActaOpcionPorTransmitirDto> actasOpcionDto = null;
		if(detallesActaOpcion!=null) {
			actasOpcionDto = detallesActaOpcion
					.stream()
					.map(tp -> DetActaOpcionPorTransmitirDto
			                .builder()
			                .idDetActaOpcion(tp.getId())
			                .idActaDetalle(tp.getDetActa().getId())
			                .idDetActaOpcionCc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), tp.getId().toString())) // id orc
			                .posicion(tp.getPosicion())
			                .votos(tp.getVotos())
			                .votosAutomatico(tp.getVotosAutomatico())
			                .votosManual1(tp.getVotosManual1())
			                .votosManual2(tp.getVotosManual2())
			                .estadoErrorMaterial(tp.getEstadoErrorMaterial())
			                .ilegible(tp.getIlegible())
			                .ilegibleAutomatico(tp.getIlegibleAutomatico())
			                .ilegiblev1(tp.getIlegiblev1())
			                .ilegiblev2(tp.getIlegiblev2())
			                .activo(tp.getActivo())
	                        .audFechaCreacion(DateUtil.getDateString(tp.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
	                        .audFechaModificacion(DateUtil.getDateString(tp.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
	                        .audUsuarioCreacion(tp.getUsuarioCreacion())
	                        .audUsuarioModificacion(tp.getUsuarioModificacion())
			                .build())
					.toList();		
		}
		return actasOpcionDto;
	}
	

    private List<DetActaPreferencialPorTransmitirDto> mapActaPreferencial(DetActa detActa) {
		List<DetActaPreferencial> preferenciales = this.detActaPreferencialRepository.findByDetActa(detActa);
		List<DetActaPreferencialPorTransmitirDto> preferencialesDto = null;
		if(preferenciales!=null) {
			preferencialesDto = preferenciales
					.stream()
					.map(tp -> DetActaPreferencialPorTransmitirDto
			                .builder()
			                .id(tp.getId())
			                .idOrc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), tp.getId().toString()))
			                .idDetalleActa(tp.getDetActa().getId())
			                .idDistritoElectoral(tp.getDistritoElectoral()!=null ? tp.getDistritoElectoral().getId() : null)
			                .posicion(tp.getPosicion())
			                .lista(tp.getLista())
			                .votos(tp.getVotos())
			                .votosAutomatico(tp.getVotosAutomatico())
			                .votosv1(tp.getVotosManual1())
			                .votosv2(tp.getVotosManual2())
			                .estadoErrorMaterial(tp.getEstadoErrorMaterial())
			                .ilegible(tp.getIlegible())
			                .ilegiblev1(tp.getIlegiblev1())
			                .ilegiblev2(tp.getIlegiblev2())
			                .activo(tp.getActivo())
			                .audFechaCreacion(DateUtil.getDateString(tp.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
	                        .audFechaModificacion(DateUtil.getDateString(tp.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
	                        .audUsuarioCreacion(tp.getUsuarioCreacion())
	                        .audUsuarioModificacion(tp.getUsuarioModificacion())
			                .build())
					.toList();		
		}
		return preferencialesDto;

	}

    private List<DetActaResolucionPorTransmitirDto> mapResoluciones(Long idActa) {
		List<DetActaResolucionPorTransmitirDto> detActaResolucionDto = null;
		List<DetActaResolucion> resoluciones = this.detActaResolucionRepository.findByActa_Id(idActa);
        if (resoluciones != null) {
        	detActaResolucionDto = new ArrayList<>();
            for (DetActaResolucion detalle : resoluciones) {
                detActaResolucionDto.add(DetActaResolucionPorTransmitirDto
                        .builder()
                        .idActa(detalle.getActa().getId())
                        .correlativo(detalle.getCorrelativo())
                        .estadoActa(detalle.getEstadoActa())
                        .activo(detalle.getActivo())
                        .audUsuarioCreacion(detalle.getUsuarioCreacion())
                        .audUsuarioModificacion(detalle.getUsuarioModificacion())
                        .audFechaCreacion(DateUtil.getDateString(detalle.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .audFechaModificacion(DateUtil.getDateString(detalle.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .resolucionDto(ResolucionPorTransmitirDto
                                .builder()
                                .id(detalle.getResolucion().getId())
                                .idCc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), detalle.getResolucion().getId().toString()))
                                .idArchivoResolucion(detalle.getResolucion().getArchivoResolucion()!=null ? detalle.getResolucion().getArchivoResolucion().getId() : null)
                                .fechaResolucion(DateUtil.getDateString(detalle.getResolucion().getFechaResolucion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                                .procedencia(detalle.getResolucion().getProcedencia())
                                .numeroExpediente(detalle.getResolucion().getNumeroExpediente())
                                .numeroResolucion(detalle.getResolucion().getNumeroResolucion())
                                .tipoResolucion(detalle.getResolucion().getTipoResolucion())
                                .estadoResolucion(detalle.getResolucion().getEstadoResolucion())
                                .estadoDigitalizacion(detalle.getResolucion().getEstadoDigitalizacion())
                                .observacionDigitalizacion(detalle.getResolucion().getObservacionDigitalizacion())
                                .numeroPaginas(detalle.getResolucion().getNumeroPaginas())
                                .audUsuarioCreacion(detalle.getResolucion().getAudUsuarioCreacion())
                                .audUsuarioModificacion(detalle.getResolucion().getAudUsuarioModificacion())
                                .audFechaCreacion(DateUtil.getDateString(detalle.getResolucion().getAudFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                                .audFechaModificacion(DateUtil.getDateString(detalle.getResolucion().getAudFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                                .activo(detalle.getResolucion().getActivo())
                                .build())
                        .build());
            }
        }
        
        return detActaResolucionDto;
	}
    
    
    private CabActaFormatoPorTransmitirDto mapCabActaFormatoDtoById(Long id){
    	Optional<CabActaFormato> cabActaFormatoOp = this.cabActaFormatoRepository.findById(id); 
    	if(cabActaFormatoOp.isPresent()){
    		CabActaFormato cabActaFormato = cabActaFormatoOp.get();
    		Archivo archivo = cabActaFormato.getArchivoFormatoPdf();
    		return CabActaFormatoPorTransmitirDto
    				.builder()
    				.id(cabActaFormato.getId())
    				.tabFormato(mapFormatoById(cabActaFormato.getFormato()!=null 
    				? cabActaFormato.getFormato().getId()
    				: null))
    				.idCc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), cabActaFormato.getId().toString()))
    				.correlativo(cabActaFormato.getCorrelativo())
    				.idArchivo(archivo!=null ? archivo.getId() : null)
    				.activo(cabActaFormato.getActivo())
                    .usuarioCreacion(cabActaFormato.getUsuarioCreacion())
                    .usuarioModificacion(cabActaFormato.getUsuarioModificacion())
                    .fechaCreacion(DateUtil.getDateString(cabActaFormato.getFechaCreacion(), 
                    		SceConstantes.FORMATO_FECHA_TRANSMISION))
                    .fechaModificacion(DateUtil.getDateString(cabActaFormato.getFechaModificacion(), 
                    		SceConstantes.FORMATO_FECHA_TRANSMISION))
                    .detalle(mapDetActaFormatoPorTransmitirByIdCabActaFormato(cabActaFormato.getId()))
                    .build();
    	}
    	return null;
    }
    
    private TabFormatoPorTransmitirDto mapFormatoById(Long id){
    	Optional<Formato> formatoOp = this.tabFormatoRepository.findById(id); 
    	if(formatoOp.isPresent()){
    		Formato formato = formatoOp.get();
    		Archivo archivo = formato.getArchivo();
    		return TabFormatoPorTransmitirDto
    				.builder()
    				.id(formato.getId())
    				.idCc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), formato.getId().toString()))
    				.tipoFormato(formato.getTipoFormato().longValue())
    				.correlativo(formato.getCorrelativo())
    				.idArchivo(archivo!=null ? archivo.getId() : null)
    				.activo(formato.getActivo())
                    .usuarioCreacion(formato.getUsuarioCreacion())
                    .usuarioModificacion(formato.getUsuarioModificacion())
                    .fechaCreacion(DateUtil.getDateString(formato.getFechaCreacion(), 
                    		SceConstantes.FORMATO_FECHA_TRANSMISION))
                    .fechaModificacion(DateUtil.getDateString(formato.getFechaModificacion(), 
                    		SceConstantes.FORMATO_FECHA_TRANSMISION))
                    .build();
    	}
    	return null;
    }
    
    private List<DetActaFormatoPorTransmitirDto> mapDetActaFormatoPorTransmitirByIdCabActaFormato(Long id){
    	List<DetActaFormato> detActaFormatos = this.detActaFormatoRepository.findByIdCabActaFormato(id); 
    	List<DetActaFormatoPorTransmitirDto> detalles = null;
    	if(detActaFormatos!=null){
    		detalles = new ArrayList<>();
    		for(DetActaFormato detActaFormato:detActaFormatos){
    			DetActaFormatoPorTransmitirDto x = DetActaFormatoPorTransmitirDto
        				.builder()
        				.id(detActaFormato.getId())
        				.idActa(detActaFormato.getActa().getId())        				
        				.idCc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), detActaFormato.getId().toString()))
        				.activo(detActaFormato.getActivo())
                        .usuarioCreacion(detActaFormato.getUsuarioCreacion())
                        .usuarioModificacion(detActaFormato.getUsuarioModificacion())
                        .fechaCreacion(DateUtil.getDateString(detActaFormato.getFechaCreacion(), 
                        		SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .fechaModificacion(DateUtil.getDateString(detActaFormato.getFechaModificacion(), 
                        		SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .build();
    			if(x!=null){
    				detalles.add(x);
    			}
    			
    		}
    	}
    	return detalles;
    }
    
    private List<DetOficioTransmistirDto> mapOficioByIdActaCeleste(Long idActaCeleste){
    	List<DetOficioTransmistirDto> detOficiosDto = null;
    	List<DetActaOficio> detOficios= this.detOficioRepository.findByActaCeleste_Id(idActaCeleste);
    	if(detOficios!=null){
    		detOficiosDto = new ArrayList<>();
    		for(DetActaOficio detOficio: detOficios){
    			detOficiosDto.add(
    					DetOficioTransmistirDto
    					.builder()
    					.cabActaFormato(mapCabActaFormatoDtoById(detOficio.getCabActaFormato()!=null 
				    					? detOficio.getCabActaFormato().getId()
				    					: null))
    					.idCc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), detOficio.getId().toString()))
    					.idActa(detOficio.getActa().getId())
    					.idActaCelesteCc(String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), detOficio.getActaCeleste().getId().toString()))
    					.cabOficio(
    							OficioPorTransmitirDto
    							.builder()
    							.idCc((String.format(CC_FORMATO,  this.getCodigoCentroComputoActual(), 
    									detOficio.getOficio().getId().toString())))
    							.idArchivo(detOficio.getOficio().getArchivo()!=null 
    								? detOficio.getOficio().getArchivo().getId()
    								: null)
    							.idCentroComputo(detOficio.getOficio().getCentroComputo())
    							.nombreOficio(detOficio.getOficio().getNombreOficio())
    							.estado(detOficio.getOficio().getEstadoJne())
    							.estadoOficio(detOficio.getOficio().getEstado())
    							.activo(detOficio.getOficio().getActivo())
    	                        .usuarioCreacion(detOficio.getOficio().getUsuarioCreacion())
    	                        .usuarioModificacion(detOficio.getOficio().getUsuarioModificacion())
    	                        .fechaCreacion(DateUtil.getDateString(detOficio.getOficio().getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
    	                        .fechaModificacion(DateUtil.getDateString(detOficio.getOficio().getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
    	                        .build()
    					)
    					.activo(detOficio.getActivo())
                        .usuarioCreacion(detOficio.getUsuarioCreacion())
                        .usuarioModificacion(detOficio.getUsuarioModificacion())
                        .fechaCreacion(DateUtil.getDateString(detOficio.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .fechaModificacion(DateUtil.getDateString(detOficio.getFechaModificacion(), SceConstantes.FORMATO_FECHA_TRANSMISION))
                        .build()
    				);
    		}
    	}
    	return detOficiosDto;
    }
	
	private void mapCommonFields(TransmisionDto transmision, ActaTransmisionNacion actaTransmitida, String proceso) {
        transmision.setIdTransmision(actaTransmitida.getId());
        transmision.setAcronimoProceso(proceso);
        transmision.setEstadoTransmitidoNacion(actaTransmitida.getEstadoTransmitidoNacion());
        transmision.setFechaRegistro(DateUtil.getDateString(actaTransmitida.getFechaCreacion(), SceConstantes.FORMATO_FECHA_TRANSMISION));
        transmision.setFechaTransmision(DateUtil.getDateString(actaTransmitida.getFechaTransmision(), SceConstantes.FORMATO_FECHA_TRANSMISION));
        transmision.setUsuarioTransmision(actaTransmitida.getUsuarioTransmision());
    }
	
	private String getCodigoCentroComputoActual(){
		Optional<CentroComputo> centroComputo = this.centroComputoService.getCentroComputoActual();
	    if (centroComputo.isPresent()) {
	        return centroComputo.get().getCodigo();
	    } else {
	        throw new IllegalStateException("No se encontró el centro de cómputo actual.");
	    }
	}
	
	private String getEstadoActa(String estadoActa, TransmisionNacionEnum estadoEnum) {
		if (estadoEnum.equals(TransmisionNacionEnum.REPROCESO)) {
            return ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_NORMAL;
        }
		return estadoActa;
	}
	
	
	@Override
	@Transactional(readOnly = true)
    public List<TransmisionReqDto> adjuntar(List<TransmisionReqDto> actasTransmistidas){
		return actasTransmistidas.stream()
		        .map(acta -> {
		        	
		        	
		        	if(acta.getActaTransmitida().getIdArchivoEscrutinio()!=null){
		        		acta.getActaTransmitida().setArchivoEscrutinio(getArchivoTransmisionReqDto(acta.getActaTransmitida().getIdArchivoEscrutinio()));
		        	}
		        	
		        	if(acta.getActaTransmitida().getIdArchivoInstalacionSufragio()!=null){
		        		acta.getActaTransmitida().setArchivoInstalacionSufragio(getArchivoTransmisionReqDto(acta.getActaTransmitida().getIdArchivoInstalacionSufragio()));
		        	}
		        	
	                List<DetActaResolucionPorTransmitirReqDto> resoluciones = acta.getActaTransmitida().getResoluciones();
	                if (resoluciones != null && !resoluciones.isEmpty()) {
	                    List<DetActaResolucionPorTransmitirReqDto> resolucionesDto = resoluciones.stream()
	                        .map(resolucion -> {
	                            ResolucionPorTransmitirReqDto rt = resolucion.getResolucionDto();
	                            if (rt != null) {
	                                rt.setArchivoResolucion(
	                                    getArchivoTransmisionReqDto(rt.getIdArchivoResolucion())
	                                );
	                                resolucion.setResolucionDto(rt);
	                            }
	                            return resolucion;
	                        })
	                        .toList();
	                    acta.getActaTransmitida().setResoluciones(resolucionesDto);
	                }
	                List<DetOficioTransmistirReqDto> detOficios = acta.getActaTransmitida().getDetallesOficio();
	                if(detOficios!= null && !detOficios.isEmpty()){
	                	List<DetOficioTransmistirReqDto> detOficiosDtos = detOficios.stream()
	                	.map( detOficio -> {
	                		OficioPorTransmitirReqDto ot = detOficio.getCabOficio();
	                		if(ot != null){
	                			ot.setArchivo(getArchivoTransmisionReqDto(ot.getIdArchivo()));
	                			detOficio.setCabOficio(ot);
	                		} // end-if
	                		
	                		CabActaFormatoPorTransmitirReqDto caf = detOficio.getCabActaFormato();
	                        if (caf != null) {
	                            caf.setArchivo(getArchivoTransmisionReqDto(caf.getIdArchivo()));
	                            TabFormatoPorTransmitirReqDto tf = caf.getTabFormato();
	                            if (tf != null) {
	                                tf.setArchivo(getArchivoTransmisionReqDto(tf.getIdArchivo()));
	                                caf.setTabFormato(tf);
	                            } // end-if
	                            detOficio.setCabActaFormato(caf);
	                        } // end-if
	                		
	                		return detOficio;
	                	})
	                	.toList();
	                	acta.getActaTransmitida().setDetallesOficio(detOficiosDtos);
	                }
	                
	                if(acta.getActaTransmitida().getActaCeleste()!=null){
	                	
	                	if(acta.getActaTransmitida().getActaCeleste().getIdArchivoEscrutinio()!=null){
	                		acta.getActaTransmitida().getActaCeleste().setArchivoEscrutinio(
			        				getArchivoTransmisionReqDto(acta.getActaTransmitida().getActaCeleste().getIdArchivoEscrutinio()));
	                	}
		        		
	                	if(acta.getActaTransmitida().getActaCeleste().getIdArchivoInstalacionSufragio()!=null){
	                		acta.getActaTransmitida().getActaCeleste().setArchivoInstalacionSufragio(
			        				getArchivoTransmisionReqDto(acta.getActaTransmitida().getActaCeleste().getIdArchivoInstalacionSufragio()));
	                	}
		        		
		        		
		        		List<DetOficioTransmistirReqDto> detOficiosCeleste = acta
		        				.getActaTransmitida()
		        				.getActaCeleste()
		        				.getDetallesOficio();
		        		
		                if(detOficiosCeleste!= null && !detOficiosCeleste.isEmpty()){
		                	List<DetOficioTransmistirReqDto> detOficiosCelesteDtos = detOficiosCeleste.stream()
		                	.map( detOficio -> {
		                		OficioPorTransmitirReqDto ot = detOficio.getCabOficio();
		                		if(ot != null){
		                			ot.setArchivo(getArchivoTransmisionReqDto(ot.getIdArchivo()));
		                			detOficio.setCabOficio(ot);
		                		} // end-if
		                		
		                		CabActaFormatoPorTransmitirReqDto caf = detOficio.getCabActaFormato();
		                        if (caf != null) {
		                            caf.setArchivo(getArchivoTransmisionReqDto(caf.getIdArchivo()));
		                            TabFormatoPorTransmitirReqDto tf = caf.getTabFormato();
		                            if (tf != null) {
		                                tf.setArchivo(getArchivoTransmisionReqDto(tf.getIdArchivo()));
		                                caf.setTabFormato(tf);
		                            } // end-if
		                            detOficio.setCabActaFormato(caf);
		                        } // end-if
		                		
		                		return detOficio;
		                	})
		                	.toList();
		                	acta.getActaTransmitida()
		                	.getActaCeleste()
		                	.setDetallesOficio(detOficiosCelesteDtos);
		                }
		        	} // end-if
	                
	                
		            return acta;
		        })
		        .toList();
	}
	
	private ArchivoTransmisionReqDto getArchivoTransmisionReqDto(Long id) {
		ArchivoTransmisionReqDto dto = null;
		if(id!=null) {
			Optional<Archivo> archivo = this.archivoRepository.findById(id);
			if (archivo.isPresent()) {
				try {
					String fullPath = storageService.getPathUpload();
					String base64 = convertToBase64(PathUtils.normalizePath(fullPath, archivo.get().getGuid()));
					dto = ArchivoTransmisionReqDto
							.builder()
							.base64(base64)
							.mimeType(archivo.get().getFormato())
							.guid(archivo.get().getGuid())
							.extension(ArchivoUtils.getExtension(archivo.get().getFormato()))
							.build();
				} catch (Exception e) {
					logger.error("error", e);
					return null;
				}
			}
		}
		return dto;
	}
	
	private String convertToBase64(String filePath) throws IOException {
		File file = new File(filePath);
		if(file.exists()){
			try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = fis.read(buf)) != -1) {
					bos.write(buf, 0, bytesRead);
				}
				byte[] fileBytes = bos.toByteArray();
				return Base64.getEncoder().encodeToString(fileBytes);
			}
		} else {
			throw new IOException("El archivo no existe");
		}
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ActaTransmisionNacion> findByIdActaConTransmisionesOrdenadas(Long idActa){
		return this.actaTransmisionNacionRepository.findByIdActaConTransmisionesOrdenadas(idActa);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer contarEnEjecucion(Long idActa){
		return this.actaTransmisionNacionRepository.contarEnEjecucion(idActa);	
	}

	@Override
	public void actualizarFallidoReseteo(Long idActa, String mensaje) {
		int cantidad = this.actaTransmisionNacionRepository.actualizarFallidoReseteo(idActa, mensaje);
		logger.info("Registros actualizados {}", cantidad);	
	}

	@Override
	public void actualizarOkReseteo(Long idActa, String mensaje) {
		int cantidad = this.actaTransmisionNacionRepository.actualizarOkReseteo(idActa, mensaje);
		logger.info("Registros actualizados {}", cantidad);	
	}
	
	@Override
	public void actualizarEstadoReseteo(Long idActa, Integer estado, String mensaje) {
		int cantidad = this.actaTransmisionNacionRepository.actualizarEstadoReseteo(idActa, estado, mensaje);
		logger.info("Registros actualizados {}", cantidad);	
	}

	@Override
	public List<ActaTransmisionNacion> findByIdActaTodasTransmisionesOrdenadas(Long idActa) {
		return this.actaTransmisionNacionRepository.findByIdActaConTodasTransmisionesOrdenadas(idActa);
	}
	
	
	
}
