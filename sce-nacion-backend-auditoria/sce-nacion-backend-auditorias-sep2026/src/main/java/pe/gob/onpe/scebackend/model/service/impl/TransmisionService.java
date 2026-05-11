package pe.gob.onpe.scebackend.model.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import pe.gob.onpe.scebackend.ext.pr.dto.RegistroTramaParam;
import pe.gob.onpe.scebackend.ext.pr.dto.TipoEjecProcEnum;
import pe.gob.onpe.scebackend.ext.pr.service.EnvioTramaSceService;
import pe.gob.onpe.scebackend.model.dto.ParametroDto;
import pe.gob.onpe.scebackend.model.dto.transmision.ActaPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.ArchivoTransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.CabActaFormatoPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetActaAccionPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetActaFormatoPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetActaOpcionPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetActaPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetActaPreferencialPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetActaResolucionPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetOficioPorTransmistirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.DetOtroDocumentoPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.MesaArchivoPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.MesaPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.MiembroMesaColaPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.MiembroMesaEscrutinioPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.MiembroMesaSorteadoPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.OficioPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.OmisoMiembroMesaPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.OmisoVotantePorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.OtroDocumentoPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.PersoneroPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TabFormatoPorTransmitirDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.orc.entities.ActaCeleste;
import pe.gob.onpe.scebackend.model.orc.entities.ActaHistorial;
import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPolitica;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;
import pe.gob.onpe.scebackend.model.orc.entities.CabActaFormato;
import pe.gob.onpe.scebackend.model.orc.entities.CabOtroDocumento;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.DetActa;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaAccion;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaFormato;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaOficio;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaOpcion;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaPreferencial;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaResolucion;
import pe.gob.onpe.scebackend.model.orc.entities.DetOtroDocumento;
import pe.gob.onpe.scebackend.model.orc.entities.DistritoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.Formato;
import pe.gob.onpe.scebackend.model.orc.entities.Mesa;
import pe.gob.onpe.scebackend.model.orc.entities.MesaDocumento;
import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaCola;
import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaEscrutinio;
import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaSorteado;
import pe.gob.onpe.scebackend.model.orc.entities.Oficio;
import pe.gob.onpe.scebackend.model.orc.entities.OmisoMiembroMesa;
import pe.gob.onpe.scebackend.model.orc.entities.OmisoVotante;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDocumentoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.PadronElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.Personero;
import pe.gob.onpe.scebackend.model.orc.entities.TabResolucion;
import pe.gob.onpe.scebackend.model.orc.entities.UbigeoEleccion;
import pe.gob.onpe.scebackend.model.orc.repository.ActaCelesteRepository;
import pe.gob.onpe.scebackend.model.orc.repository.ActaHistorialRepository;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.CabActaFormatoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.CabOtroDocumentoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaAccionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaFormatoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaOficioRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaOpcionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaPreferencialRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaResolucionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetOtroDocumentoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DistritoElectoralRepository;
import pe.gob.onpe.scebackend.model.orc.repository.FormatoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.MesaDocumentoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.MesaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.MiembroMesaColaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.MiembroMesaEscrutinioRepository;
import pe.gob.onpe.scebackend.model.orc.repository.MiembroMesaSorteadoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.OficioRepository;
import pe.gob.onpe.scebackend.model.orc.repository.OmisoMiembroMesaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.OmisoVotanteRepository;
import pe.gob.onpe.scebackend.model.orc.repository.OrcDocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.orc.repository.PadronElectoralRepository;
import pe.gob.onpe.scebackend.model.orc.repository.PersoneroRepository;
import pe.gob.onpe.scebackend.model.orc.repository.TabArchivoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.TabResolucionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.UbigeoEleccionRepository;
import pe.gob.onpe.scebackend.model.service.IParametroService;
import pe.gob.onpe.scebackend.model.service.ITransmisionService;
import pe.gob.onpe.scebackend.model.service.TransmisionControlService;
import pe.gob.onpe.scebackend.utils.ArchivoUtils;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.DirectorioUtils;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.SceUtils;
import pe.gob.onpe.scebackend.utils.constantes.ConstanteTransmision;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesParametros;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesTipoDocumentoElectoral;

@Service
public class TransmisionService implements ITransmisionService {

	Logger logger = LoggerFactory.getLogger(TransmisionService.class);

	@Value("${fileserver.files}")
	private String ubicacionFile;

	private ActaRepository actaRepository;

	private ActaHistorialRepository actaHistorialRepository;

	private MesaRepository mesaRepository;

	private DetActaRepository detActaRepository;

	private DetActaAccionRepository detActaAccionRepository;

	private UbigeoEleccionRepository detUbigeoRepository;

	private TabResolucionRepository tabResolucionRepository;

	private DetActaResolucionRepository detActaResolucionRepository;

	private TabArchivoRepository archivoRepository;

	private DetActaPreferencialRepository detActaPreferencialRepository;

	private DetActaOpcionRepository detActaOpcionRepository;

	private DistritoElectoralRepository distritoElectoralRepository;

	private CentroComputoRepository centroComputoRepository;

	private OmisoVotanteRepository omisoVotanteRepository;
	
	private PadronElectoralRepository padronElectoralRepository;

	private MiembroMesaColaRepository miembroMesaColaRepository;

	private MiembroMesaSorteadoRepository miembroMesaSorteadoRepository;

	private MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository;

	private OmisoMiembroMesaRepository omisoMiembroMesaRepository;

	private PersoneroRepository personeroRepository;

	private AgrupacionPoliticaRepository agrupacionPoliticaRepository;

	private MesaDocumentoRepository mesaDocumentoRepository;
	
	private OrcDocumentoElectoralRepository documentoElectoralRepository;
	
	private ActaCelesteRepository actaCelesteRepository;
	
	private DetActaOficioRepository detActaOficioRepository;
	
	private OficioRepository oficioRepository;
	
	private CabActaFormatoRepository cabActaFormatoRepository;
	
	private DetActaFormatoRepository detActaFormatoRepository;
	
	private FormatoRepository formatoRepository;
	
	private CabOtroDocumentoRepository cabOtroDocumentoRepository;
	
	private DetOtroDocumentoRepository detOtroDocumentoRepository;

	private EnvioTramaSceService envioTramaSceService;
	
	private TransmisionControlService transmisionControlService;
	
	private IParametroService parametroService;
	
	public TransmisionService(
	        ActaRepository actaRepository,
	        ActaHistorialRepository actaHistorialRepository,
	        MesaRepository mesaRepository,
	        DetActaRepository detActaRepository,
	        DetActaAccionRepository detActaAccionRepository,
	        UbigeoEleccionRepository detUbigeoRepository,
	        TabResolucionRepository tabResolucionRepository,
	        DetActaResolucionRepository detActaResolucionRepository,
	        TabArchivoRepository archivoRepository,
	        DetActaPreferencialRepository detActaPreferencialRepository,
	        DetActaOpcionRepository detActaOpcionRepository,
	        DistritoElectoralRepository distritoElectoralRepository,
	        CentroComputoRepository centroComputoRepository,
	        OmisoVotanteRepository omisoVotanteRepository,
	        PadronElectoralRepository padronElectoralRepository,
	        MiembroMesaColaRepository miembroMesaColaRepository,
	        MiembroMesaSorteadoRepository miembroMesaSorteadoRepository,
	        MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository,
	        OmisoMiembroMesaRepository omisoMiembroMesaRepository,
	        PersoneroRepository personeroRepository,
	        AgrupacionPoliticaRepository agrupacionPoliticaRepository,
	        MesaDocumentoRepository mesaDocumentoRepository,
	        OrcDocumentoElectoralRepository documentoElectoralRepository,
	        EnvioTramaSceService envioTramaSceService,
	        ActaCelesteRepository actaCelesteRepository,
	        DetActaOficioRepository detActaOficioRepository,
	        OficioRepository oficioRepository,
	        CabActaFormatoRepository cabActaFormatoRepository,
	        DetActaFormatoRepository detActaFormatoRepository,
	        FormatoRepository formatoRepository,
	        CabOtroDocumentoRepository cabOtroDocumentoRepository,
	        DetOtroDocumentoRepository detOtroDocumentoRepository,
	        TransmisionControlService transmisionControlService,
	        IParametroService parametroService
	    ) {
	        this.actaRepository = actaRepository;
	        this.actaHistorialRepository = actaHistorialRepository;
	        this.mesaRepository = mesaRepository;
	        this.detActaRepository = detActaRepository;
	        this.detActaAccionRepository = detActaAccionRepository;
	        this.detUbigeoRepository = detUbigeoRepository;
	        this.tabResolucionRepository = tabResolucionRepository;
	        this.detActaResolucionRepository = detActaResolucionRepository;
	        this.archivoRepository = archivoRepository;
	        this.detActaPreferencialRepository = detActaPreferencialRepository;
	        this.detActaOpcionRepository = detActaOpcionRepository;
	        this.distritoElectoralRepository = distritoElectoralRepository;
	        this.centroComputoRepository = centroComputoRepository;
	        this.omisoVotanteRepository = omisoVotanteRepository;
	        this.padronElectoralRepository = padronElectoralRepository;
	        this.miembroMesaColaRepository = miembroMesaColaRepository;
	        this.miembroMesaSorteadoRepository = miembroMesaSorteadoRepository;
	        this.miembroMesaEscrutinioRepository = miembroMesaEscrutinioRepository;
	        this.omisoMiembroMesaRepository = omisoMiembroMesaRepository;
	        this.personeroRepository = personeroRepository;
	        this.agrupacionPoliticaRepository = agrupacionPoliticaRepository;
	        this.mesaDocumentoRepository = mesaDocumentoRepository;
	        this.documentoElectoralRepository = documentoElectoralRepository;
	        this.envioTramaSceService = envioTramaSceService;
	        this.actaCelesteRepository = actaCelesteRepository;
	        this.detActaOficioRepository = detActaOficioRepository;
	        this.cabActaFormatoRepository = cabActaFormatoRepository;
	        this.detActaFormatoRepository = detActaFormatoRepository;
	        this.formatoRepository = formatoRepository;
	        this.oficioRepository = oficioRepository;
	        this.cabOtroDocumentoRepository = cabOtroDocumentoRepository;
	        this.detOtroDocumentoRepository = detOtroDocumentoRepository;
	        this.transmisionControlService = transmisionControlService;
	        this.parametroService = parametroService;
	}

	
	@Override
	@Transactional(transactionManager = "locationTransactionManager", 
			rollbackFor = Exception.class)
	public TransmisionResponseDto recibirTransmisionPc(TransmisionDto transmisionDto, String esquema){
		
		try {
			Integer resultado = 0;
			String mensaje = "";
			
			Optional<CentroComputo> centroComputo = this.centroComputoRepository
					.findOneByCc(transmisionDto.getCentroComputo());
			
			String fechaEjecucion = transmisionDto.getFechaRegistro();
			String fechaTransmision = transmisionDto.getFechaTransmision();
			
			logger.info("Se inicio la puesta en cero con los siguientes parametros para la transmision: {}",
					transmisionDto.getIdTransmision());
			
			logger.info("esquema: {}", esquema);
			
			if(centroComputo.isPresent()){
				logger.info("centro de computo: {}", Integer.valueOf(centroComputo.get().getId().toString()));
			}
			
			logger.info("usuario: {}", transmisionDto.getUsuarioTransmision());
			logger.info("fecha de ejecucion: {}", fechaEjecucion);
			logger.info("fecha de transmision: {}", fechaTransmision);
			
			Map<String, Object> resultadopc = this.actaRepository.puestaCero(esquema,
					Integer.valueOf(centroComputo.get().getId().toString()), fechaEjecucion, fechaTransmision,
					transmisionDto.getUsuarioTransmision() != null ? transmisionDto.getUsuarioTransmision()
							: ConstantesComunes.USUARIO_SYSTEM,
					resultado, mensaje);
			
			if (resultadopc != null) {
				Object resultadoPs = resultadopc.get("po_resultado");
				Integer estado = (resultadoPs != null
						&& resultadoPs.toString().equals(SceConstantes.ACTIVO.toString()))
								? SceConstantes.ACTIVO
								: SceConstantes.INACTIVO;
				logger.info("resultado de la ps de puesto cero: {}", resultadoPs);
				logger.info("mensaje final de la ps de puesta cero: {}", resultadopc.get("po_mensaje"));
				if (!estado.equals(SceConstantes.ACTIVO)) {
					throw new Exception("Ocurrió un error en el procedimiento almacenado de la pc"); // por error puesta cero
				}
			}
			
			return TransmisionResponseDto
					.builder()
					.idTransmision(transmisionDto.getIdTransmision())
					.estado(ConstanteTransmision.ESTADO_TRANSMISION_OK)
					.exitoso(true)
					.mensaje("Transmision exitosa de pc")
					.build();
			
		} catch(Exception e){
			logger.error("Error {}", e);
			return TransmisionResponseDto
					.builder()
					.idTransmision(transmisionDto.getIdTransmision())
					.estado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR)
					.exitoso(false)
					.mensaje(e.getMessage())
					.build();
		}
	}
	

	@Override
	@Transactional(
			transactionManager = "locationTransactionManager", 
			rollbackFor = Exception.class)
	public void recibirTransmision(TransmisionDto transmisionDto, 
			String esquema, 
			String correlationId, 
			String cc,
			Integer orden) throws Exception {

			logger.info("Se inicia la transmision {} con la correlacion {}", transmisionDto.getIdTransmision(), correlationId);
		
			ParametroDto parametroDto = parametroService
	                .obtenerParametro(ConstantesParametros.CAB_PARAM_EJECUCION_PUESTA_CERO_NACION);

	        if (parametroDto != null && ConstantesComunes.TEXT_TRUE.equals(String.valueOf(parametroDto.getValor()))) {
	        	throw new Exception("La puesta cero se encuentra activa");
	        }

			Integer ordenActual = transmisionControlService.getOrden(cc, transmisionDto.getActaTransmitida().getIdActa());
			
			if(orden<ordenActual){
				
				Date fechaRegistroTransmision = DateUtil.getDate(transmisionDto.getFechaRegistro(),
						ConstanteTransmision.FORMATO_FECHA);
				String usuarioRegistroTransmision = transmisionDto.getUsuarioTransmision();
				
				ActaPorTransmitirDto actaTransmitidaDto = transmisionDto.getActaTransmitida();
				
				if (transmisionDto.getAccion().trim().equals(ConstanteTransmision.ACCION_ACTA) 
						|| transmisionDto.getAccion().trim().equals(ConstanteTransmision.ACCION_SOBRE_CELESTE)) {
					
					boolean actaHistorialOp = this.actaHistorialRepository.existsByIdTransmisionAndCentroCc(
							transmisionDto.getIdTransmision(), 
							transmisionDto.getCentroComputo());
					
					if(!actaHistorialOp){
						Acta acta = this.actaRepository.findById(actaTransmitidaDto.getIdActa()).get();
						ActaHistorial actaHistorial = new ActaHistorial();
						actaHistorial.setActa(acta);
						actaHistorial.setMesa(acta.getMesa());
						actaHistorial.setSolucionTecnologica(actaTransmitidaDto.getSolucionTecnologica()!=null 
								? actaTransmitidaDto.getSolucionTecnologica().intValue() : null);	
						actaHistorial.setUbigeoEleccion(acta.getUbigeoEleccion());
						actaHistorial.setNumeroCopia(actaTransmitidaDto.getNumeroCopia());
						actaHistorial.setNumeroLote(actaTransmitidaDto.getNumeroLote());
						actaHistorial.setTipoLote(actaTransmitidaDto.getTipoLote());
						actaHistorial.setElectoresHabiles(actaTransmitidaDto.getElectoresHabiles());
						actaHistorial.setCvas(actaTransmitidaDto.getCvas());
						actaHistorial.setVotosCalculados(actaTransmitidaDto.getVotosCalculados());
						actaHistorial.setTotalVotos(actaTransmitidaDto.getTotalVotos());
						actaHistorial.setEstadoActa(actaTransmitidaDto.getEstadoActa());
						actaHistorial.setEstadoCc(actaTransmitidaDto.getEstadoCc());
						actaHistorial.setEstadoActaResolucion(actaTransmitidaDto.getEstadoActaResolucion());
						actaHistorial.setEstadoDigitalizacion(actaTransmitidaDto.getEstadoDigitalizacion());
						actaHistorial.setEstadoErrorMaterial(actaTransmitidaDto.getEstadoErrorMaterial());
						actaHistorial.setDigitalizacionEscrutinio(actaTransmitidaDto.getDigitalizacionEscrutinio());
						actaHistorial.setDigitalizacionInstalacionSufragio(actaTransmitidaDto.getDigitalizacionInstalacionSufragio());
						actaHistorial.setControlDigEscrutinio(actaTransmitidaDto.getControlDigEscrutinio());
						actaHistorial.setControlDigInstalacionSufragio(actaTransmitidaDto.getControlDigInstalacionSufragio());
						actaHistorial.setObservDigEscrutinio(actaTransmitidaDto.getObservDigEscrutinio());
						actaHistorial.setObservDigInstalacionSufragio(actaTransmitidaDto.getObservDigInstalacionSufragio());
						actaHistorial.setDigitacionHoras(actaTransmitidaDto.getDigitacionHoras());
						actaHistorial.setDigitacionVotos(actaTransmitidaDto.getDigitacionVotos());
						actaHistorial.setDigitacionObserv(actaTransmitidaDto.getDigitacionObserv());
						actaHistorial.setDigitacionFirmasAutomatico(actaTransmitidaDto.getDigitacionFirmasAutomatico());
						actaHistorial.setDigitacionFirmasManual(actaTransmitidaDto.getDigitacionFirmasManual());
						actaHistorial.setControlDigitacion(actaTransmitidaDto.getControlDigitacion());
						actaHistorial.setHoraEscrutinioAutomatico(actaTransmitidaDto.getHoraEscrutinioAutomatico());
						actaHistorial.setHoraEscrutinioManual(actaTransmitidaDto.getHoraEscrutinioManual());
						actaHistorial.setHoraInstalacionAutomatico(actaTransmitidaDto.getHoraInstalacionAutomatico());
						actaHistorial.setHoraInstalacionManual(actaTransmitidaDto.getHoraInstalacionManual());
						actaHistorial.setDescripcionObservAutomatico(actaTransmitidaDto.getDescripcionObservAutomatico());
						actaHistorial.setDescripcionObservManual(actaTransmitidaDto.getDescripcionObservManual());
						actaHistorial.setEscrutinioFirmaMm1Automatico(actaTransmitidaDto.getEscrutinioFirmaMm1Automatico());
						actaHistorial.setEscrutinioFirmaMm2Automatico(actaTransmitidaDto.getEscrutinioFirmaMm2Automatico());
						actaHistorial.setEscrutinioFirmaMm3Automatico(actaTransmitidaDto.getEscrutinioFirmaMm3Automatico());
						actaHistorial.setInstalacionFirmaMm1Automatico(actaTransmitidaDto.getInstalacionFirmaMm1Automatico());
						actaHistorial.setInstalacionFirmaMm2Automatico(actaTransmitidaDto.getInstalacionFirmaMm2Automatico());
						actaHistorial.setInstalacionFirmaMm3Automatico(actaTransmitidaDto.getInstalacionFirmaMm3Automatico());
						actaHistorial.setSufragioFirmaMm1Automatico(actaTransmitidaDto.getSufragioFirmaMm1Automatico());
						actaHistorial.setSufragioFirmaMm2Automatico(actaTransmitidaDto.getSufragioFirmaMm2Automatico());
						actaHistorial.setSufragioFirmaMm3Automatico(actaTransmitidaDto.getSufragioFirmaMm3Automatico());
						actaHistorial.setVerificador(actaTransmitidaDto.getVerificador());
						actaHistorial.setVerificadorv2(actaTransmitidaDto.getVerificador2());
						actaHistorial.setTipoTransmision(actaTransmitidaDto.getTipoTransmision());
						actaHistorial.setFechaCreacion(DateUtil.getDate(actaTransmitidaDto.getAudFechaCreacion(),
								ConstanteTransmision.FORMATO_FECHA));
						actaHistorial.setFechaProcesamiento(fechaRegistroTransmision);
						actaHistorial.setUsuarioProcesamiento(usuarioRegistroTransmision != null 
										? usuarioRegistroTransmision
										: ConstantesComunes.USUARIO_SYSTEM);
						actaHistorial.setUsuarioCreacion(actaTransmitidaDto.getAudUsuarioCreacion());
						actaHistorial.setIdTransmision(transmisionDto.getIdTransmision());
						actaHistorial.setCentroCc(transmisionDto.getCentroComputo());
						this.actaHistorialRepository.save(actaHistorial);
						
						RegistroTramaParam param = new RegistroTramaParam();
						param.setIdTransmision(transmisionDto.getIdTransmision());
						param.setEsquema(esquema);
						param.setIdActa(acta.getId());
						param.setEstadoActa(actaTransmitidaDto.getEstadoActa());
						param.setEstadoActaResolucion(actaTransmitidaDto.getEstadoActaResolucion());
						param.setEstadoComputo(actaTransmitidaDto.getEstadoCc());
						param.setIdDetUbigeoEleccion(SceUtils.toInteger(actaTransmitidaDto.getIdDetUbigeoEleccion()));
						param.setAudUsuarioCreacion(usuarioRegistroTransmision);
						param.setEjecutarPs(true);		
						
						
						param.setResultado(0);
						param.setMensaje("");
						
						if(transmisionDto.getAccion().trim().equals(ConstanteTransmision.ACCION_ACTA)){
							param.setTipoEjecProc(TipoEjecProcEnum.ACTA);
							this.envioTramaSceService.generarRegistrosTransmisionPr(param);
						} else {
							param.setTipoEjecProc(TipoEjecProcEnum.ACTA_CELESTE);
							this.envioTramaSceService.generarRegistrosTransmisionJne(param);
						}
						
					}

				}

				
			} else if(orden>=ordenActual || ordenActual==0){
			
				logger.info("Se inicio el registro de la transmision !");
	
				String directorioImagen = DirectorioUtils.construirRutaConDirectorios(this.ubicacionFile, transmisionDto.getAcronimoProceso(), transmisionDto.getCentroComputo());
				Acta acta = null;
				Optional<DetActa> opDetActa = Optional.empty();
				Optional<DetActaAccion> opDetAccionActa = Optional.empty();
				DetActa detActa = null;
				DetActaAccion detActaAccion = null;
				ActaHistorial actaHistorial = null;
				RegistroTramaParam param = null;
				ActaPorTransmitirDto actaTransmitidaDto = null;
				Optional<UbigeoEleccion> ubigeoEleccion = Optional.empty();
				Archivo archivoResolucion = null;
				Archivo archivoResolucionPdf = null;
				Optional<TabResolucion> tabResolucionOp = Optional.empty();
				TabResolucion tabResolucion = null;
				Optional<DetActaResolucion> detActaResolucionOp = Optional.empty();
				DetActaResolucion detActaResolucion = null;
	
				logger.info("Se da inicio la transmision {} accion {} !", transmisionDto.getIdTransmision(), transmisionDto.getAccion());
	
				if(transmisionDto.getAccion().trim()
						.equals(ConstanteTransmision.ACCION_SOBRE_CELESTE)){
					
					actaTransmitidaDto = transmisionDto.getActaTransmitida().getActaCeleste();
					logger.info("Se realiza la accion de sobre celeste");
					Optional<ActaCeleste> actaCelesteOp = this.actaCelesteRepository.findByIdCc(actaTransmitidaDto.getIdCcCeleste());
					
					Optional<Acta> actaFk = this.actaRepository.findById(
							transmisionDto.getActaTransmitida().getIdActa());
					ActaCeleste actaCeleste = null;
					
					if(actaFk.isPresent()){
						Acta actaPloma = actaFk.get();
						actaPloma.setEstadoActa(transmisionDto.getActaTransmitida().getEstadoActa());
						actaPloma.setUsuarioModificacion(actaTransmitidaDto.getAudUsuarioModificacion());
						actaPloma.setFechaModificacion(new Date());
						actaPloma.setFechaModificacionCc(DateUtil.getDate(actaTransmitidaDto.getAudFechaModificacion(),
								ConstanteTransmision.FORMATO_FECHA));
						actaPloma.setUsuarioProcesamiento(transmisionDto.getUsuarioTransmision());
						actaPloma.setFechaProcesamiento(DateUtil.getDate(transmisionDto.getFechaTransmision(),ConstanteTransmision.FORMATO_FECHA));
						actaPloma.setIdTransmision(transmisionDto.getIdTransmision());
						actaPloma.setCentroCc(transmisionDto.getCentroComputo());
						this.actaRepository.save(actaPloma);
						if(actaCelesteOp.isPresent()){
							logger.info("Se actualizara un sobre celeste");
							actaCeleste = actaCelesteOp.get();
							actaCeleste.setUsuarioModificacion(actaTransmitidaDto.getAudUsuarioModificacion());
							actaCeleste.setFechaModificacion(new Date());
						} else {
							logger.info("Se creara un sobre celeste");
							actaCeleste = new ActaCeleste();
							actaCeleste.setUsuarioCreacion(actaTransmitidaDto.getAudUsuarioCreacion());
							actaCeleste.setFechaCreacion(new Date());
						}
						actaCeleste.setIdCc(actaTransmitidaDto.getIdCcCeleste());
						actaCeleste.setNumeroCopia(actaTransmitidaDto.getNumeroCopia());
						actaCeleste.setDigitoChequeoEscrutinio(actaTransmitidaDto.getDigitoChequeoEscrutinio());
						actaCeleste.setDigitoChequeoInstalacion(actaTransmitidaDto.getDigitoChequeoInstalacion());
						actaCeleste.setDigitoChequeoSufragio(actaTransmitidaDto.getDigitoChequeoSufragio());
						actaCeleste.setEstadoDigitalizacion(actaTransmitidaDto.getEstadoDigitalizacion());
						actaCeleste.setAsignado(actaTransmitidaDto.getAsignado());
						actaCeleste.setDigitalizacionInstalacion(actaTransmitidaDto.getDigitalizacionInstalacion());
						actaCeleste.setDigitalizacionSufragio(actaTransmitidaDto.getDigitalizacionSufragio());
						actaCeleste.setDigitalizacionEscrutinio(actaTransmitidaDto.getDigitalizacionEscrutinio());
						actaCeleste.setDigitalizacionInstalacionSufragio(actaTransmitidaDto.getDigitalizacionInstalacionSufragio());
						actaCeleste.setObservDigEscrutinio(actaTransmitidaDto.getObservDigEscrutinio());
						actaCeleste.setObservDigInstalacionSufragio(actaTransmitidaDto.getObservDigInstalacionSufragio());
						actaCeleste.setFechaModificacionCc(DateUtil.getDate(actaTransmitidaDto.getAudFechaModificacion(),
								ConstanteTransmision.FORMATO_FECHA));
						actaCeleste.setActa(actaPloma);
						String codigoCc = transmisionDto.getCentroComputo();
						this.actaCelesteRepository.save(actaCeleste);
						this.guardarImagenEscrutinioCeleste(actaTransmitidaDto, actaCeleste, codigoCc, directorioImagen, ConstantesTipoDocumentoElectoral.ACTA_DE_ESCRUTINIO);
						this.guardarImagenInstalacionSufragioCeleste(actaTransmitidaDto, actaCeleste, codigoCc, directorioImagen, ConstantesTipoDocumentoElectoral.ACTA_INSTALACION_Y_SUFRAGIO);
						logger.info("se creara el detalle del oficio");
						if(transmisionDto.getActaTransmitida()!=null && 
								transmisionDto.getActaTransmitida().getActaCeleste().getDetallesOficio()!=null){savedDetOficios(
									transmisionDto.getActaTransmitida().getActaCeleste().getDetallesOficio(), 
									transmisionDto.getCentroComputo(),
									directorioImagen
									);
						}
						
						boolean actaHistorialOp = this.actaHistorialRepository.existsByIdTransmisionAndCentroCc(
								transmisionDto.getIdTransmision(), 
								transmisionDto.getCentroComputo());
						
						if(!actaHistorialOp){
							
							actaHistorial = new ActaHistorial();
							actaHistorial.setActa(actaPloma);
							actaHistorial.setMesa(actaPloma.getMesa());
							actaHistorial.setSolucionTecnologica(actaPloma.getSolucionTecnologica());	
							actaHistorial.setUbigeoEleccion(actaPloma.getUbigeoEleccion());
							actaHistorial.setNumeroCopia(actaPloma.getNumeroCopia());
							actaHistorial.setNumeroLote(actaPloma.getNumeroLote());
							actaHistorial.setTipoLote(actaPloma.getTipoLote());
							actaHistorial.setElectoresHabiles(actaPloma.getElectoresHabiles());
							actaHistorial.setCvas(actaPloma.getCvas());
							actaHistorial.setVotosCalculados(actaPloma.getVotosCalculados());
							actaHistorial.setTotalVotos(actaPloma.getTotalVotos());
							actaHistorial.setEstadoActa(actaPloma.getEstadoActa());
							actaHistorial.setEstadoCc(actaPloma.getEstadoCc());
							actaHistorial.setEstadoActaResolucion(actaPloma.getEstadoActaResolucion());
							actaHistorial.setEstadoDigitalizacion(actaPloma.getEstadoDigitalizacion());
							actaHistorial.setEstadoErrorMaterial(actaPloma.getEstadoErrorMaterial());
							actaHistorial.setDigitalizacionEscrutinio(actaPloma.getDigitalizacionEscrutinio());
							actaHistorial.setDigitalizacionInstalacionSufragio(actaPloma.getDigitalizacionInstalacionSufragio());
							actaHistorial.setControlDigEscrutinio(actaPloma.getControlDigEscrutinio());
							actaHistorial.setControlDigInstalacionSufragio(actaPloma.getControlDigInstalacionSufragio());
							actaHistorial.setObservDigEscrutinio(actaPloma.getObservDigEscrutinio());
							actaHistorial.setObservDigInstalacionSufragio(actaPloma.getObservDigInstalacionSufragio());
							actaHistorial.setDigitacionHoras(actaPloma.getDigitacionHoras());
							actaHistorial.setDigitacionVotos(actaPloma.getDigitacionVotos());
							actaHistorial.setDigitacionObserv(actaPloma.getDigitacionObserv());
							actaHistorial.setDigitacionFirmasAutomatico(actaPloma.getDigitacionFirmasAutomatico());
							actaHistorial.setDigitacionFirmasManual(actaPloma.getDigitacionFirmasManual());
							actaHistorial.setControlDigitacion(actaPloma.getControlDigitacion());
							actaHistorial.setHoraEscrutinioAutomatico(actaPloma.getHoraEscrutinioAutomatico());
							actaHistorial.setHoraEscrutinioManual(actaPloma.getHoraEscrutinioManual());
							actaHistorial.setHoraInstalacionAutomatico(actaPloma.getHoraInstalacionAutomatico());
							actaHistorial.setHoraInstalacionManual(actaPloma.getHoraInstalacionManual());
							actaHistorial.setDescripcionObservAutomatico(actaPloma.getDescripcionObservAutomatico());
							actaHistorial.setDescripcionObservManual(actaPloma.getDescripcionObservManual());
							actaHistorial.setEscrutinioFirmaMm1Automatico(actaPloma.getEscrutinioFirmaMm1Automatico());
							actaHistorial.setEscrutinioFirmaMm2Automatico(actaPloma.getEscrutinioFirmaMm2Automatico());
							actaHistorial.setEscrutinioFirmaMm3Automatico(actaPloma.getEscrutinioFirmaMm3Automatico());
							actaHistorial.setInstalacionFirmaMm1Automatico(actaPloma.getInstalacionFirmaMm1Automatico());
							actaHistorial.setInstalacionFirmaMm2Automatico(actaPloma.getInstalacionFirmaMm2Automatico());
							actaHistorial.setInstalacionFirmaMm3Automatico(actaPloma.getInstalacionFirmaMm3Automatico());
							actaHistorial.setSufragioFirmaMm1Automatico(actaPloma.getSufragioFirmaMm1Automatico());
							actaHistorial.setSufragioFirmaMm2Automatico(actaPloma.getSufragioFirmaMm2Automatico());
							actaHistorial.setSufragioFirmaMm3Automatico(actaPloma.getSufragioFirmaMm3Automatico());
							actaHistorial.setVerificador(actaPloma.getVerificador());
							actaHistorial.setVerificadorv2(actaPloma.getVerificador2());
							actaHistorial.setTipoTransmision(actaPloma.getTipoTransmision());
							actaHistorial.setFechaCreacion(actaPloma.getFechaCreacion());
							actaHistorial.setFechaProcesamiento(actaPloma.getFechaProcesamiento());
							actaHistorial.setFechaModificacionCc(actaPloma.getFechaModificacionCc());
							actaHistorial.setUsuarioCreacion(actaPloma.getUsuarioCreacion());
							actaHistorial.setUsuarioProcesamiento(actaPloma.getUsuarioProcesamiento());
							actaHistorial.setUsuarioModificacion(actaPloma.getUsuarioModificacion());
							actaHistorial.setIdTransmision(actaPloma.getIdTransmision());
							actaHistorial.setCentroCc(actaPloma.getCentroCc());
							this.actaHistorialRepository.save(actaHistorial);
						}
						
						param = new RegistroTramaParam();
						param.setIdTransmision(transmisionDto.getIdTransmision());
						param.setEsquema(esquema);
						param.setIdActa(transmisionDto.getActaTransmitida().getIdActa());
						param.setAudUsuarioCreacion(transmisionDto.getUsuarioTransmision());
						param.setResultado(0);
						param.setMensaje("");
						param.setEjecutarPs(true);		
						param.setTipoEjecProc(TipoEjecProcEnum.ACTA_CELESTE);
	
						logger.info("se ejecuto el ps!");
						this.envioTramaSceService.generarRegistrosTransmisionJne(param);
	
						logger.info("Se finaliza la accion de sobre celeste");
					}
					
				}
				else if (transmisionDto.getAccion().trim()
								.equals(ConstanteTransmision.ACCION_MESA_DETALLE)) {
	
					logger.info("Se ejecuta una transmision de mesas detalle con el id {}",
							transmisionDto.getIdTransmision());
	
					actaTransmitidaDto = transmisionDto.getActaTransmitida();
					logger.info("Se realizo la accion de transmision: {}", ConstanteTransmision.ACCION_MESA_DETALLE);
					logger.info("Id transmision: {}", transmisionDto.getIdTransmision());
					logger.info("Id acta (accion acta): {}", actaTransmitidaDto.getIdActa());
	
					if(transmisionDto.getSubAccion()!=null &&
							transmisionDto.getSubAccion().trim().equals(ConstanteTransmision.SUBACCION_DENUNCIAS)){
						this.savedMesa(actaTransmitidaDto.getMesa(), 
								false, 
								false, 
								false, 
								false, 
								false, 
								false,
								true);
					} else {
						this.savedMesa(actaTransmitidaDto.getMesa(), 
								true, 
								true, 
								true, 
								true, 
								true, 
								true,
								false);
					}
					
					logger.info("Se finalizo el registro de la transmision");
					
				} else if (transmisionDto.getAccion().trim().equals(ConstanteTransmision.ACCION_ACTA)) {
	
					logger.info("Se ejecuta una transmision de acta con el id {}", transmisionDto.getIdTransmision());
	
					String codigoCc = transmisionDto.getCentroComputo();
					logger.info("Se realizo la accion de transmision: {}", ConstanteTransmision.ACCION_ACTA);
					Date fechaRegistroTransmision = DateUtil.getDate(transmisionDto.getFechaRegistro(),
							ConstanteTransmision.FORMATO_FECHA);
					String usuarioRegistroTransmision = transmisionDto.getUsuarioTransmision();
					actaTransmitidaDto = transmisionDto.getActaTransmitida();
	
					logger.info("Id transmision: {}", transmisionDto.getIdTransmision());
					logger.info("Id acta (accion acta): {}", actaTransmitidaDto.getIdActa());
	
					ubigeoEleccion = this.detUbigeoRepository.findById(actaTransmitidaDto.getIdDetUbigeoEleccion());
					acta = this.actaRepository.findByIdForUpdate(actaTransmitidaDto.getIdActa()).get();
	
					if (acta != null) {
	
						Long idTransmision = acta.getIdTransmision(); // el id del acta
						String centroCc = acta.getCentroCc();
	
						if (idTransmision == null || (transmisionDto.getCentroComputo().equals(centroCc)
								&& transmisionDto.getIdTransmision() >= idTransmision)) {
	
							logger.info("El acta {} tiene un id {} que es menor al id {} transferido", acta.getId(),
									idTransmision, transmisionDto.getIdTransmision());
	
							param = new RegistroTramaParam();
							param.setIdTransmision(transmisionDto.getIdTransmision());
							param.setEsquema(esquema);
							param.setIdActa(acta.getId());
							param.setEstadoActa(actaTransmitidaDto.getEstadoActa());
							param.setEstadoActaResolucion(actaTransmitidaDto.getEstadoActaResolucion());
							param.setEstadoComputo(actaTransmitidaDto.getEstadoCc());
							param.setIdDetUbigeoEleccion(SceUtils.toInteger(actaTransmitidaDto.getIdDetUbigeoEleccion()));
							param.setAudUsuarioCreacion(usuarioRegistroTransmision);
							param.setEjecutarPs(true);		
							param.setTipoEjecProc(TipoEjecProcEnum.ACTA);
							
							param.setResultado(0);
							param.setMensaje("");
	
	
							// add historial
							
							boolean actaHistorialOp = this.actaHistorialRepository.existsByIdTransmisionAndCentroCc(
									transmisionDto.getIdTransmision(), 
									transmisionDto.getCentroComputo());
							
							if(!actaHistorialOp){
								actaHistorial = new ActaHistorial();			
								actaHistorial.setActa(acta);
								actaHistorial.setMesa(acta.getMesa());
								actaHistorial.setSolucionTecnologica(acta.getSolucionTecnologica());	
								actaHistorial.setUbigeoEleccion(acta.getUbigeoEleccion());
								actaHistorial.setNumeroCopia(acta.getNumeroCopia());
								actaHistorial.setNumeroLote(acta.getNumeroLote());
								actaHistorial.setTipoLote(acta.getTipoLote());
								actaHistorial.setElectoresHabiles(acta.getElectoresHabiles());
								actaHistorial.setCvas(acta.getCvas());
								actaHistorial.setVotosCalculados(acta.getVotosCalculados());
								actaHistorial.setTotalVotos(acta.getTotalVotos());
								actaHistorial.setEstadoActa(acta.getEstadoActa());
								actaHistorial.setEstadoCc(acta.getEstadoCc());
								actaHistorial.setEstadoActaResolucion(acta.getEstadoActaResolucion());
								actaHistorial.setEstadoDigitalizacion(acta.getEstadoDigitalizacion());
								actaHistorial.setEstadoErrorMaterial(acta.getEstadoErrorMaterial());
								actaHistorial.setDigitalizacionEscrutinio(acta.getDigitalizacionEscrutinio());
								actaHistorial.setDigitalizacionInstalacionSufragio(acta.getDigitalizacionInstalacionSufragio());
								actaHistorial.setControlDigEscrutinio(acta.getControlDigEscrutinio());
								actaHistorial.setControlDigInstalacionSufragio(acta.getControlDigInstalacionSufragio());
								actaHistorial.setObservDigEscrutinio(acta.getObservDigEscrutinio());
								actaHistorial.setObservDigInstalacionSufragio(acta.getObservDigInstalacionSufragio());
								actaHistorial.setDigitacionHoras(acta.getDigitacionHoras());
								actaHistorial.setDigitacionVotos(acta.getDigitacionVotos());
								actaHistorial.setDigitacionObserv(acta.getDigitacionObserv());
								actaHistorial.setDigitacionFirmasAutomatico(acta.getDigitacionFirmasAutomatico());
								actaHistorial.setDigitacionFirmasManual(acta.getDigitacionFirmasManual());
								actaHistorial.setControlDigitacion(acta.getControlDigitacion());
								actaHistorial.setHoraEscrutinioAutomatico(acta.getHoraEscrutinioAutomatico());
								actaHistorial.setHoraEscrutinioManual(acta.getHoraEscrutinioManual());
								actaHistorial.setHoraInstalacionAutomatico(acta.getHoraInstalacionAutomatico());
								actaHistorial.setHoraInstalacionManual(acta.getHoraInstalacionManual());
								actaHistorial.setDescripcionObservAutomatico(acta.getDescripcionObservAutomatico());
								actaHistorial.setDescripcionObservManual(acta.getDescripcionObservManual());
								actaHistorial.setEscrutinioFirmaMm1Automatico(acta.getEscrutinioFirmaMm1Automatico());
								actaHistorial.setEscrutinioFirmaMm2Automatico(acta.getEscrutinioFirmaMm2Automatico());
								actaHistorial.setEscrutinioFirmaMm3Automatico(acta.getEscrutinioFirmaMm3Automatico());
								actaHistorial.setInstalacionFirmaMm1Automatico(acta.getInstalacionFirmaMm1Automatico());
								actaHistorial.setInstalacionFirmaMm2Automatico(acta.getInstalacionFirmaMm2Automatico());
								actaHistorial.setInstalacionFirmaMm3Automatico(acta.getInstalacionFirmaMm3Automatico());
								actaHistorial.setSufragioFirmaMm1Automatico(acta.getSufragioFirmaMm1Automatico());
								actaHistorial.setSufragioFirmaMm2Automatico(acta.getSufragioFirmaMm2Automatico());
								actaHistorial.setSufragioFirmaMm3Automatico(acta.getSufragioFirmaMm3Automatico());
								actaHistorial.setVerificador(acta.getVerificador());
								actaHistorial.setVerificadorv2(acta.getVerificador2());
								actaHistorial.setTipoTransmision(acta.getTipoTransmision());
								actaHistorial.setFechaCreacion(acta.getFechaCreacion());
								actaHistorial.setFechaProcesamiento(acta.getFechaProcesamiento());
								actaHistorial.setFechaModificacionCc(acta.getFechaModificacionCc());
								actaHistorial.setUsuarioCreacion(acta.getUsuarioCreacion());
								actaHistorial.setUsuarioProcesamiento(acta.getUsuarioProcesamiento());
								actaHistorial.setUsuarioModificacion(acta.getUsuarioModificacion());
								actaHistorial.setIdTransmision(acta.getIdTransmision());
								actaHistorial.setCentroCc(acta.getCentroCc());
								
								
		
								if (acta.getArchivoEscrutinio() != null) {
									actaHistorial.setArchivoEscrutinio(acta.getArchivoEscrutinio());
								} // end-if
		
								if (acta.getArchivoInstalacionSufragio() != null) {
									actaHistorial.setArchivoInstalacionSufragio(acta.getArchivoInstalacionSufragio());
								}
		
								if (acta.getArchivoEscrutinioFirmado() != null) {
									actaHistorial.setArchivoEscrutinioFirmado(acta.getArchivoEscrutinioFirmado());
								} // end-if
		
								if (acta.getArchivoInstalacionSufragioFirmado() != null) {
									actaHistorial
											.setArchivoInstalacionSufragioFirmado(acta.getArchivoInstalacionSufragioFirmado());
								}
								
								this.actaHistorialRepository.save(actaHistorial);
			
							}
	
							
							this.savedMesa(actaTransmitidaDto.getMesa(), false, false, false, false, false, false, false);
	
							if(ubigeoEleccion.isPresent()){
								acta.setUbigeoEleccion(ubigeoEleccion.get());
							}
							
							acta.setNumeroCopia(actaTransmitidaDto.getNumeroCopia());
							acta.setNumeroLote(actaTransmitidaDto.getNumeroLote());
							acta.setDigitoChequeoEscrutinio(actaTransmitidaDto.getDigitoChequeoEscrutinio());
							acta.setDigitoChequeoInstalacion(actaTransmitidaDto.getDigitoChequeoInstalacion());
							acta.setDigitoChequeoSufragio(actaTransmitidaDto.getDigitoChequeoSufragio());
							acta.setTipoLote(actaTransmitidaDto.getTipoLote());
							acta.setElectoresHabiles(actaTransmitidaDto.getElectoresHabiles());
							acta.setCvas(actaTransmitidaDto.getCvas());
							acta.setCvasAutomatico(actaTransmitidaDto.getCvasAutomatico());
							acta.setCvasv1(actaTransmitidaDto.getCvasv1());
							acta.setCvasv2(actaTransmitidaDto.getCvasv2());
							acta.setIlegibleCvas(actaTransmitidaDto.getIlegibleCvas());
							acta.setIlegibleCvasv1(actaTransmitidaDto.getIlegibleCvasv1());
							acta.setIlegibleCvasv2(actaTransmitidaDto.getIlegibleCvasv2());
							acta.setVotosCalculados(actaTransmitidaDto.getVotosCalculados());
							acta.setTotalVotos(actaTransmitidaDto.getTotalVotos());
							acta.setEstadoActa(actaTransmitidaDto.getEstadoActa());
							acta.setEstadoCc(actaTransmitidaDto.getEstadoCc());
							acta.setEstadoActaResolucion(actaTransmitidaDto.getEstadoActaResolucion());
							acta.setEstadoDigitalizacion(actaTransmitidaDto.getEstadoDigitalizacion());
							acta.setEstadoErrorMaterial(actaTransmitidaDto.getEstadoErrorMaterial());
							acta.setDigitalizacionEscrutinio(actaTransmitidaDto.getDigitalizacionEscrutinio());
							acta.setDigitalizacionInstalacionSufragio(actaTransmitidaDto.getDigitalizacionInstalacionSufragio());
							acta.setControlDigEscrutinio(actaTransmitidaDto.getControlDigEscrutinio());
							acta.setControlDigInstalacionSufragio(actaTransmitidaDto.getControlDigInstalacionSufragio());
							acta.setObservDigEscrutinio(actaTransmitidaDto.getObservDigEscrutinio());
							acta.setObservDigInstalacionSufragio(actaTransmitidaDto.getObservDigInstalacionSufragio());
							acta.setDigitacionHoras(actaTransmitidaDto.getDigitacionHoras());
							acta.setDigitacionVotos(actaTransmitidaDto.getDigitacionVotos());
							acta.setDigitacionObserv(actaTransmitidaDto.getDigitacionObserv());
							acta.setDigitacionFirmasAutomatico(actaTransmitidaDto.getDigitacionFirmasAutomatico());
							acta.setDigitacionFirmasManual(actaTransmitidaDto.getDigitacionFirmasManual());
							acta.setControlDigitacion(actaTransmitidaDto.getControlDigitacion());
							acta.setHoraEscrutinioAutomatico(actaTransmitidaDto.getHoraEscrutinioAutomatico());
							acta.setHoraEscrutinioManual(actaTransmitidaDto.getHoraEscrutinioManual());
							acta.setHoraInstalacionAutomatico(actaTransmitidaDto.getHoraInstalacionAutomatico());
							acta.setHoraInstalacionManual(actaTransmitidaDto.getHoraInstalacionManual());
							acta.setDescripcionObservAutomatico(actaTransmitidaDto.getDescripcionObservAutomatico());
							acta.setDescripcionObservManual(actaTransmitidaDto.getDescripcionObservManual());
							acta.setEscrutinioFirmaMm1Automatico(actaTransmitidaDto.getEscrutinioFirmaMm1Automatico());
							acta.setEscrutinioFirmaMm2Automatico(actaTransmitidaDto.getEscrutinioFirmaMm2Automatico());
							acta.setEscrutinioFirmaMm3Automatico(actaTransmitidaDto.getEscrutinioFirmaMm3Automatico());
							acta.setInstalacionFirmaMm1Automatico(actaTransmitidaDto.getInstalacionFirmaMm1Automatico());
							acta.setInstalacionFirmaMm2Automatico(actaTransmitidaDto.getInstalacionFirmaMm2Automatico());
							acta.setInstalacionFirmaMm3Automatico(actaTransmitidaDto.getInstalacionFirmaMm3Automatico());
							acta.setSufragioFirmaMm1Automatico(actaTransmitidaDto.getSufragioFirmaMm1Automatico());
							acta.setSufragioFirmaMm2Automatico(actaTransmitidaDto.getSufragioFirmaMm2Automatico());
							acta.setSufragioFirmaMm3Automatico(actaTransmitidaDto.getSufragioFirmaMm3Automatico());
							acta.setVerificador(actaTransmitidaDto.getVerificador());
							acta.setVerificador2(actaTransmitidaDto.getVerificador2());
							acta.setTipoTransmision(actaTransmitidaDto.getTipoTransmision());
							acta.setUsuarioModificacion(actaTransmitidaDto.getAudUsuarioModificacion());
							acta.setFechaModificacion(new Date());
							acta.setFechaModificacionCc(DateUtil.getDate(actaTransmitidaDto.getAudFechaModificacion(),
									ConstanteTransmision.FORMATO_FECHA));
							acta.setUsuarioProcesamiento(usuarioRegistroTransmision);
							acta.setFechaProcesamiento(fechaRegistroTransmision);
							acta.setIdTransmision(transmisionDto.getIdTransmision());
							acta.setCentroCc(transmisionDto.getCentroComputo());
							acta.setSolucionTecnologica(actaTransmitidaDto.getSolucionTecnologica().intValue());
	
							this.guardarImagenEscrutinio(actaTransmitidaDto, acta, codigoCc, directorioImagen, ConstantesTipoDocumentoElectoral.ACTA_DE_ESCRUTINIO);
							this.guardarImagenInstalacionSufragio(actaTransmitidaDto, acta, codigoCc, directorioImagen, ConstantesTipoDocumentoElectoral.ACTA_INSTALACION_Y_SUFRAGIO);
	
							
							this.actaRepository.save(acta);
	
							if (actaTransmitidaDto.getAcciones() != null && !actaTransmitidaDto.getAcciones().isEmpty()) {
								for (DetActaAccionPorTransmitirDto detActaAccionDto : actaTransmitidaDto.getAcciones()) {
									opDetAccionActa = this.detActaAccionRepository
											.findByIdCc(detActaAccionDto.getIdCcDetActaAccion());
									if (opDetAccionActa.isPresent()) { // update
										detActaAccion = opDetAccionActa.get();
										detActaAccion.setActa(acta);
										detActaAccion.setFechaModificacion(
												DateUtil.getDate(detActaAccionDto.getAudFechaModificacion(),
														ConstanteTransmision.FORMATO_FECHA));
										detActaAccion.setUsuarioModificacion(detActaAccionDto.getAudUsuarioCreacion());
									} else { // new
										detActaAccion = new DetActaAccion();
										detActaAccion.setActa(acta);
										detActaAccion.setIdCc(detActaAccionDto.getIdCcDetActaAccion());
										detActaAccion
												.setFechaCreacion(DateUtil.getDate(detActaAccionDto.getAudFechaCreacion(),
														ConstanteTransmision.FORMATO_FECHA));
										detActaAccion.setUsuarioCreacion(detActaAccionDto.getAudUsuarioCreacion());
									}
									detActaAccion.setAccion(detActaAccionDto.getAccion());
									detActaAccion.setTiempo(detActaAccionDto.getTiempo());
									detActaAccion.setIteracion(detActaAccionDto.getIteracion());
									detActaAccion.setOrden(detActaAccionDto.getOrden());
									detActaAccion.setUsuarioAccion(detActaAccionDto.getUsuarioAccion());
									detActaAccion.setFechaAccion(DateUtil.getDate(detActaAccionDto.getFechaAccion(),
											ConstanteTransmision.FORMATO_FECHA));
	
									this.detActaAccionRepository.save(detActaAccion);
								}
							}
	
							if (actaTransmitidaDto.getDetalle() != null && !actaTransmitidaDto.getDetalle().isEmpty()) {
								for (DetActaPorTransmitirDto detActaDto : actaTransmitidaDto.getDetalle()) {
									opDetActa = this.detActaRepository.findByIdActaAndIdAgrupacionPolitica(
											detActaDto.getIdActa(), detActaDto.getIdAgrupacionPolitica());
									if (opDetActa.isPresent()) { // update
										detActa = opDetActa.get();
										detActa.setUsuarioModificacion(detActaDto.getAudUsuarioModificacion());
										detActa.setFechaModificacion(DateUtil.getDate(detActaDto.getAudFechaModificacion(),
												ConstanteTransmision.FORMATO_FECHA));
									} else { // new
										detActa = new DetActa();
										detActa.setIdAgrupacionPolitica(detActaDto.getIdAgrupacionPolitica());
										detActa.setActa(acta);
										detActa.setFechaCreacion(DateUtil.getDate(detActaDto.getAudFechaCreacion(),
												ConstanteTransmision.FORMATO_FECHA));
										detActa.setUsuarioCreacion(detActaDto.getAudUsuarioCreacion());
									}
									detActa.setEstado(detActaDto.getEstado());
									detActa.setPosicion(detActaDto.getPosicion());
									detActa.setVotos(detActaDto.getVotos());
									detActa.setVotosAutomatico(detActaDto.getVotosAutomatico());
									detActa.setVotosManual1(detActaDto.getVotosManual1());
									detActa.setVotosManual2(detActaDto.getVotosManual2());
									detActa.setEstadoErrorMaterial(detActaDto.getEstadoErrorMaterial());
									detActa.setIlegible(detActaDto.getIlegible());
									detActa.setIlegiblev1(detActaDto.getIlegiblev1());
									detActa.setIlegiblev2(detActaDto.getIlegiblev2());
	
									this.detActaRepository.save(detActa);
	
									if (detActaDto.getDetActaPreferencial() != null) {
										DetActaPreferencial detActaPreferencial = null;
										DistritoElectoral distritoElectoral = null;
										for (DetActaPreferencialPorTransmitirDto detActaPreferencialDto : detActaDto
												.getDetActaPreferencial()) {
											distritoElectoral = null;
											Optional<DetActaPreferencial> detActaPreferencialOp = this.detActaPreferencialRepository
													.findByIdActaAndIdAgrupacionPoliticaAndList(
															detActaDto.getIdActa(), 
															detActaDto.getIdAgrupacionPolitica(), 
															detActaPreferencialDto.getLista());
											if (detActaPreferencialDto.getIdDistritoElectoral() != null) {
												distritoElectoral = this.distritoElectoralRepository
														.getOne(detActaPreferencialDto.getIdDistritoElectoral());
											}
											if (detActaPreferencialOp.isPresent()) {
												detActaPreferencial = detActaPreferencialOp.get();
												detActaPreferencial.setFechaModificacion(
														DateUtil.getDate(detActaPreferencialDto.getAudFechaModificacion(),
																ConstanteTransmision.FORMATO_FECHA));
												detActaPreferencial.setUsuarioModificacion(
														detActaPreferencialDto.getAudUsuarioModificacion());
											} else {
												detActaPreferencial = new DetActaPreferencial();
												detActaPreferencial.setDetActa(detActa);
												detActaPreferencial.setFechaCreacion(
														DateUtil.getDate(detActaPreferencialDto.getAudFechaCreacion(),
																ConstanteTransmision.FORMATO_FECHA));
												detActaPreferencial
														.setUsuarioCreacion(detActaPreferencialDto.getAudUsuarioCreacion());
											}
											if (distritoElectoral != null) {
												detActaPreferencial.setDistritoElectoral(distritoElectoral);
											}
											detActaPreferencial.setPosicion(detActaPreferencialDto.getPosicion());
											detActaPreferencial.setLista(detActaPreferencialDto.getLista());
											detActaPreferencial.setVotos(detActaPreferencialDto.getVotos());
											detActaPreferencial
													.setVotosAutomatico(detActaPreferencialDto.getVotosAutomatico());
											detActaPreferencial.setVotosManual1(detActaPreferencialDto.getVotosv1());
											detActaPreferencial.setVotosManual2(detActaPreferencialDto.getVotosv2());
											detActaPreferencial.setEstadoErrorMaterial(
													detActaPreferencialDto.getEstadoErrorMaterial());
											detActaPreferencial.setIlegible(detActaPreferencialDto.getIlegible());
											detActaPreferencial.setIlegiblev1(detActaPreferencialDto.getIlegiblev1());
											detActaPreferencial.setIlegiblev2(detActaPreferencialDto.getIlegiblev2());
											detActaPreferencial.setIdOrc(detActaPreferencialDto.getIdOrc());
											this.detActaPreferencialRepository.save(detActaPreferencial);
										}
									}
	
									if (detActaDto.getDetActaOpcion() != null) {
										DetActaOpcion detActaOpcion = null;
										for (DetActaOpcionPorTransmitirDto detActaOpcionDto : detActaDto
												.getDetActaOpcion()) {
											Optional<DetActaOpcion> detOpcionOp = this.detActaOpcionRepository
													.findByIdDetActaOpcionCc(detActaOpcionDto.getIdDetActaOpcionCc());
											if (detOpcionOp.isPresent()) { // update
												detActaOpcion = detOpcionOp.get();
												detActaOpcion.setFechaModificacion(
														DateUtil.getDate(detActaOpcionDto.getAudFechaModificacion(),
																ConstanteTransmision.FORMATO_FECHA));
												detActaOpcion.setUsuarioModificacion(
														detActaOpcionDto.getAudUsuarioModificacion());
											} else {
												detActaOpcion = new DetActaOpcion();
												detActaOpcion.setIdDetActaOpcionCc(detActaOpcionDto.getIdDetActaOpcionCc());
												detActaOpcion.setFechaCreacion(
														DateUtil.getDate(detActaOpcionDto.getAudFechaCreacion(),
																ConstanteTransmision.FORMATO_FECHA));
												detActaOpcion.setUsuarioCreacion(detActaOpcionDto.getAudUsuarioCreacion());
											}
											detActaOpcion.setDetActa(detActa);
											detActaOpcion.setPosicion(detActaOpcionDto.getPosicion());
											detActaOpcion.setVotos(detActaOpcionDto.getVotos());
											detActaOpcion.setVotosAutomatico(detActaOpcionDto.getVotosAutomatico());
											detActaOpcion.setVotosManual1(detActaOpcionDto.getVotosManual1());
											detActaOpcion.setVotosManual2(detActaOpcionDto.getVotosManual2());
											detActaOpcion.setEstadoErrorMaterial(detActaOpcionDto.getEstadoErrorMaterial());
											detActaOpcion.setIlegible(detActaOpcionDto.getIlegible());
											detActaOpcion.setIlegiblev1(detActaOpcionDto.getIlegiblev1());
											detActaOpcion.setIlegiblev2(detActaOpcionDto.getIlegiblev2());
											detActaOpcion.setIlegibleAutomatico(detActaOpcionDto.getIlegibleAutomatico());
											detActaOpcion.setActivo(detActaOpcionDto.getActivo());
											detActaOpcionRepository.save(detActaOpcion);
										}
									}
								}
							}
	
							if (actaTransmitidaDto.getResoluciones() != null && !actaTransmitidaDto.getResoluciones().isEmpty()) {
	
								for (DetActaResolucionPorTransmitirDto detResolucionDto : actaTransmitidaDto.getResoluciones()) {
	
									logger.info("Se inicia el ingreso el detalle de la resolucion {}",
											detResolucionDto.getId());
	
									if (detResolucionDto.getResolucionDto() != null) {
	
										detActaResolucionOp = this.detActaResolucionRepository.getByActaAndResolucion(
												detResolucionDto.getIdActa(),
												detResolucionDto.getResolucionDto().getIdCc()
												);
	
										if (detActaResolucionOp.isPresent()) {
											detActaResolucion = detActaResolucionOp.get();
											detActaResolucion.setFechaModificacion(
													DateUtil.getDate(detResolucionDto.getAudFechaModificacion(),
															ConstanteTransmision.FORMATO_FECHA));
											detActaResolucion
													.setUsuarioModificacion(detResolucionDto.getAudUsuarioModificacion());
										} else {
											detActaResolucion = new DetActaResolucion();
											detActaResolucion.setFechaCreacion(
													DateUtil.getDate(detResolucionDto.getAudFechaCreacion(),
															ConstanteTransmision.FORMATO_FECHA));
											detActaResolucion.setUsuarioCreacion(detResolucionDto.getAudUsuarioCreacion());
										}
	
										detActaResolucion.setEstadoActa(detResolucionDto.getEstadoActa());
										detActaResolucion.setCorrelativo(detResolucionDto.getCorrelativo());
										detActaResolucion.setActivo(detResolucionDto.getActivo());
										detActaResolucion.setActa(acta);
	
										logger.info("Se busca el numero de resolucion {} - {}",
												detResolucionDto.getResolucionDto().getNumeroResolucion(),
												detResolucionDto.getResolucionDto().getTipoResolucion());
	
										tabResolucionOp = this.tabResolucionRepository.findByIdCc(detResolucionDto.getResolucionDto().getIdCc());
										
										if (tabResolucionOp.isPresent()) {
											logger.info("La resolucion {} ya existe ", tabResolucionOp.get().getId());
											tabResolucion = tabResolucionOp.get();
											tabResolucion.setIdCc(detResolucionDto.getResolucionDto().getIdCc());
											tabResolucion.setAudFechaModificacion(DateUtil.getDate(
													detResolucionDto.getResolucionDto().getAudFechaModificacion(),
													ConstanteTransmision.FORMATO_FECHA));
											tabResolucion.setAudUsuarioModificacion(
													detResolucionDto.getResolucionDto().getAudUsuarioModificacion());
											tabResolucion.setNumeroResolucion(
													detResolucionDto.getResolucionDto().getNumeroResolucion());
											tabResolucion.setTipoResolucion(
													detResolucionDto.getResolucionDto().getTipoResolucion());
										} else {
											logger.info("La resolucion no existe se creara uno nuevo");
											tabResolucion = new TabResolucion();
											tabResolucion.setIdCc(detResolucionDto.getResolucionDto().getIdCc());
											tabResolucion.setAudFechaCreacion(DateUtil.getDate(
													detResolucionDto.getResolucionDto().getAudFechaCreacion(),
													ConstanteTransmision.FORMATO_FECHA));
											tabResolucion.setAudUsuarioCreacion(
													detResolucionDto.getResolucionDto().getAudUsuarioCreacion());
											tabResolucion.setNumeroResolucion(
													detResolucionDto.getResolucionDto().getNumeroResolucion());
											tabResolucion.setTipoResolucion(
													detResolucionDto.getResolucionDto().getTipoResolucion());
										} // end-else
	
										try {
	
											if (detResolucionDto.getResolucionDto() != null
													&& detResolucionDto.getResolucionDto().getArchivoResolucion() != null
													&& detResolucionDto.getResolucionDto().getArchivoResolucion()
															.getBase64() != null) {
	
												archivoResolucion = ArchivoUtils.createArchivo(detResolucionDto.getResolucionDto().getArchivoResolucion(), directorioImagen, false);
												archivoResolucion.setActivo(SceConstantes.ACTIVO);
												archivoResolucion.setFechaCreacion(new Date());
												archivoResolucion.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
												archivoResolucion.setCodigoCc(codigoCc);
												archivoResolucion.setDocumentoElectoral(ConstantesTipoDocumentoElectoral.RESOLUCION);
												this.archivoRepository.save(archivoResolucion);
												tabResolucion.setArchivoResolucion(archivoResolucion);
	
												archivoResolucionPdf = ArchivoUtils.createArchivo(detResolucionDto.getResolucionDto().getArchivoResolucion(),directorioImagen, false);
												archivoResolucionPdf.setActivo(SceConstantes.ACTIVO);
												archivoResolucionPdf.setFechaCreacion(new Date());
												archivoResolucionPdf.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
												archivoResolucionPdf.setCodigoCc(codigoCc);
												archivoResolucionPdf.setDocumentoElectoral(ConstantesTipoDocumentoElectoral.RESOLUCION);
												this.archivoRepository.save(archivoResolucionPdf);
												tabResolucion.setArchivoResolucionPdf(archivoResolucionPdf);
	
											}
	
										} catch (Exception e) {
											logger.info("No se creo la imagen para el archivo de instalacion");
										}
	
										tabResolucion.setProcedencia(detResolucionDto.getResolucionDto().getProcedencia());
										tabResolucion.setNumeroExpediente(detResolucionDto.getResolucionDto().getNumeroExpediente());
										tabResolucion.setNumeroPaginas(detResolucionDto.getResolucionDto().getNumeroPaginas());
										tabResolucion.setEstadoResolucion(detResolucionDto.getResolucionDto().getEstadoResolucion());
										tabResolucion.setEstadoDigitalizacion(detResolucionDto.getResolucionDto().getEstadoDigitalizacion());
										tabResolucion.setObservacionDigitalizacion(detResolucionDto.getResolucionDto().getObservacionDigitalizacion());
										tabResolucion.setFechaResolucion(DateUtil.getDate(detResolucionDto.getResolucionDto().getFechaResolucion(),ConstantesComunes.FORMATO_FECHA));
										tabResolucion.setActivo(detResolucionDto.getResolucionDto().getActivo());
	
										this.tabResolucionRepository.save(tabResolucion);
	
										detActaResolucion.setResolucion(tabResolucion);
	
										this.detActaResolucionRepository.save(detActaResolucion);
	
									}
	
									logger.info("Se termina el ingreso del detalle de la resolucion {}",
											detResolucionDto.getId());
								}
	
							}
	
							this.envioTramaSceService.generarRegistrosTransmisionPr(param);
	
						}
	
					} 
				}
			
				transmisionControlService.actualizarOrden(cc, transmisionDto.getActaTransmitida().getIdActa(), orden);
				
			}
			
			
	}

	private Mesa savedMesa(MesaPorTransmitirDto mesaDto, 
			boolean savedOmisosVotantes, 
			boolean savedMiembrosMesaSorteado,
			boolean savedMiembrosMesaEscrutinio, 
			boolean savedMiembrosMesaCola, 
			boolean savedPersoneros,
			boolean savedArchivos,
			boolean savedOtroDocumentos) {
		if (mesaDto == null)
			return null;

	
		Mesa mesaUpd = null;
		if (mesaDto.getIdMesa() != null) {
			Optional<Mesa> mesaOp = this.mesaRepository.findById(mesaDto.getIdMesa());
			if (mesaOp.isPresent()) {
				mesaUpd = mesaOp.get();
				mesaUpd.setEstadoMesa(mesaDto.getEstado());
				mesaUpd.setEstadoDigitalizacionLe(mesaDto.getEstadoDigitalizacionLe());
				mesaUpd.setEstadoDigitalizacionMm(mesaDto.getEstadoDigitalizacionMm());
				mesaUpd.setEstadoDigitalizacionPr(mesaDto.getEstadoDigitalizacionPr());
				mesaUpd.setEstadoDigitalizacionMe(mesaDto.getEstadoDigitalizacionMe());
				mesaUpd.setUsuarioAsignadoLe(mesaDto.getUsuarioAsignadoLe());
				mesaUpd.setUsuarioAsignadoMm(mesaDto.getUsuarioAsignadoMm());
				mesaUpd.setUsuarioAsignadoPr(mesaDto.getUsuarioAsignadoPr());
				mesaUpd.setUsuarioAsignadoMe(mesaDto.getUsuarioAsignadoMe());
				mesaUpd.setFechaAsignadoLe(
						DateUtil.getDate(mesaDto.getFechaAsignadoLe(), ConstantesComunes.FORMATO_FECHA));
				mesaUpd.setFechaAsignadoMm(
						DateUtil.getDate(mesaDto.getFechaAsignadoMm(), ConstantesComunes.FORMATO_FECHA));
				mesaUpd.setFechaAsignadoPr(
						DateUtil.getDate(mesaDto.getFechaAsignadoPr(), ConstantesComunes.FORMATO_FECHA));
				mesaUpd.setFechaAsignadoMe(
						DateUtil.getDate(mesaDto.getFechaAsignadoMe(), ConstantesComunes.FORMATO_FECHA));
				mesaUpd.setUsuarioModificacion(mesaDto.getUsuarioModificacion());
				mesaUpd.setFechaModificacion(
						DateUtil.getDate(mesaDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
				mesaUpd.setActivo(mesaDto.getActivo());
				this.mesaRepository.save(mesaUpd);
				logger.info("Se actualizo el estado de la mesa a instalada");
				if (savedOmisosVotantes && mesaDto.getOmisosVotantes() != null) {
					this.savedOmisosVotante(mesaDto.getOmisosVotantes(), mesaUpd);
				}
				if (savedMiembrosMesaSorteado && mesaDto.getMiembrosMesaSorteado() != null) {
					this.savedMiembrosMesaSorteado(mesaDto.getMiembrosMesaSorteado(), mesaUpd);
				}
				if (savedMiembrosMesaEscrutinio && mesaDto.getMiembrosMesaEscrutinio() != null) {
					this.savedMiembrosMesaEscrutinio(mesaDto.getMiembrosMesaEscrutinio(), mesaUpd);
				}
				if (savedMiembrosMesaCola && mesaDto.getMiembrosMesaCola() != null) {
					this.savedMiembrosMesaCola(mesaDto.getMiembrosMesaCola(), mesaUpd);
				}
				if (savedPersoneros && mesaDto.getPersoneros() != null) {
					this.savedPersoneros(mesaDto.getPersoneros(), mesaUpd);
				}
				if (savedArchivos && mesaDto.getDetalleArchivo() != null) {
					this.savedDocumentos(mesaDto.getDetalleArchivo(), mesaUpd);
				}
				if (savedOtroDocumentos && mesaDto.getDetOtroDocumento()!=null){
					this.savedOtroDocumentos(mesaDto.getDetOtroDocumento(), mesaUpd);				
				}
			} else {
				logger.info("La mesa no existe id={}", mesaDto.getIdMesa());
			}

		}
		return mesaUpd;
	}
	
	private List<DetOtroDocumento> savedOtroDocumentos(List<DetOtroDocumentoPorTransmitirDto> detOtroDocumentosDto, Mesa mesaUpd){
		
		List<DetOtroDocumento> detOtroDocumentos = null;
		DetOtroDocumento detOtroDocumento = null;
		if(detOtroDocumentosDto!=null){
			detOtroDocumentos = new ArrayList<>();
			for(DetOtroDocumentoPorTransmitirDto detOtroDocumentoDto:detOtroDocumentosDto){
				detOtroDocumento = this.savedOtroDocumento(detOtroDocumentoDto, mesaUpd);
				detOtroDocumentos.add(detOtroDocumento);
			} // end-for
		} // end-if
		
		return detOtroDocumentos;
	}
	
	private DetOtroDocumento savedOtroDocumento(DetOtroDocumentoPorTransmitirDto detOtroDocumentoDto, Mesa mesaUpd){
		DetOtroDocumento detOtroDocumentoSaved = null;
		Optional<DetOtroDocumento> detOtroDocumento = this.detOtroDocumentoRepository.findByIdCc(detOtroDocumentoDto.getIdCc());
		if(!detOtroDocumento.isPresent()){
			detOtroDocumentoSaved = new DetOtroDocumento();
			detOtroDocumentoSaved.setIdCc(detOtroDocumentoDto.getIdCc());			
			detOtroDocumentoSaved.setAudUsuarioCreacion(detOtroDocumentoDto.getAudUsuarioCreacion());
			detOtroDocumentoSaved.setAudFechaCreacion(
					DateUtil.getDate(detOtroDocumentoDto.getAudFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		} else {
			detOtroDocumentoSaved = detOtroDocumento.get();
			detOtroDocumentoSaved.setAudUsuarioModificacion(detOtroDocumentoDto.getAudUsuarioCreacion());
			detOtroDocumentoSaved.setAudFechaModificacion(
					DateUtil.getDate(detOtroDocumentoDto.getAudFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		}
		detOtroDocumentoSaved.setMesa(mesaUpd);
		detOtroDocumentoSaved.setCodTipoDocumento(detOtroDocumentoDto.getCodTipoDocumento());
		detOtroDocumentoSaved.setCodTipoPerdida(detOtroDocumentoDto.getCodTipoPerdida());	
		detOtroDocumentoSaved.setActivo(detOtroDocumentoDto.getActivo());
		detOtroDocumentoSaved.setCabOtroDocumento(savedCabOtroDocumento(detOtroDocumentoDto.getCabOtroDocumento()));		
		this.detOtroDocumentoRepository.save(detOtroDocumentoSaved);
		return detOtroDocumentoSaved;
	}
	
	private CabOtroDocumento savedCabOtroDocumento(OtroDocumentoPorTransmitirDto cabOtroDocumentoDto){
		CabOtroDocumento cabOtroDocumentoSaved = null;
		Optional<CabOtroDocumento> detOtroDocumento = this.cabOtroDocumentoRepository.findByIdCc(cabOtroDocumentoDto.getIdCc());
		if(!detOtroDocumento.isPresent()){
			cabOtroDocumentoSaved = new CabOtroDocumento();
			cabOtroDocumentoSaved.setIdCc(cabOtroDocumentoDto.getIdCc());			
			cabOtroDocumentoSaved.setAudUsuarioCreacion(cabOtroDocumentoDto.getAudUsuarioCreacion());
			cabOtroDocumentoSaved.setAudFechaCreacion(
					DateUtil.getDate(cabOtroDocumentoDto.getAudFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		} else {
			cabOtroDocumentoSaved = detOtroDocumento.get();
			cabOtroDocumentoSaved.setAudUsuarioModificacion(cabOtroDocumentoDto.getAudUsuarioCreacion());
			cabOtroDocumentoSaved.setAudFechaModificacion(
					DateUtil.getDate(cabOtroDocumentoDto.getAudFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		}
		
		Optional<CentroComputo> cc = this.centroComputoRepository.findOneByCc(cabOtroDocumentoDto.getCodigoCentroComputo());
		
		if(cc.isPresent()){
			cabOtroDocumentoSaved.setCentroComputo(cc.get());
		}
		
		
		cabOtroDocumentoSaved.setActivo(cabOtroDocumentoDto.getActivo());
		cabOtroDocumentoSaved.setNumeroDocumento(cabOtroDocumentoDto.getNumeroDocumento());
		cabOtroDocumentoSaved.setCodTipoDocumento(cabOtroDocumentoDto.getCodTipoDocumento());
		cabOtroDocumentoSaved.setNumeroPaginas(cabOtroDocumentoDto.getNumeroPaginas());
		cabOtroDocumentoSaved.setEstadoDigitalizacion(cabOtroDocumentoDto.getEstadoDigitalizacion());
		cabOtroDocumentoSaved.setEstadoDocumento(cabOtroDocumentoDto.getEstadoDocumento());	
		cabOtroDocumentoSaved.setUsuarioControl(cabOtroDocumentoDto.getUsuarioControl());
		cabOtroDocumentoSaved.setFechaUsuarioControl(DateUtil.getDate(
				cabOtroDocumentoDto.getFechaUsuarioControl(), 
				ConstantesComunes.FORMATO_FECHA));
		this.cabOtroDocumentoRepository.save(cabOtroDocumentoSaved);
		return cabOtroDocumentoSaved;
	}

	private List<Personero> savedPersoneros(List<PersoneroPorTransmitirDto> personerosDto, Mesa mesaUpd) {
		List<Personero> personeros = null;
		Personero personero = null;
		if (personerosDto != null) {
			personeros = new ArrayList<>();
			for (PersoneroPorTransmitirDto personeroDto : personerosDto) {
				personero = this.savedPersonero(personeroDto, mesaUpd);
				personeros.add(personero);
			}
		}
		return personeros;
	}

	private Personero savedPersonero(PersoneroPorTransmitirDto personeroDto, Mesa mesaUpd) {
		Personero personeroSaved = null;
		Optional<Personero> personero = this.personeroRepository
				.findByDocumentoIdentidadAndIdMesa(personeroDto.getDocumentoIdentidad(), mesaUpd.getId());
		if (!personero.isPresent()) {
			logger.info("Se creara el nuevo personero con la identidad {}", personeroDto.getDocumentoIdentidad());
			personeroSaved = new Personero();
			if (personeroDto.getIdAgrupacionPolitica() != null) {
				Optional<AgrupacionPolitica> agrupacionPolitica = this.agrupacionPoliticaRepository
						.findById(personeroDto.getIdAgrupacionPolitica());
				if(agrupacionPolitica.isPresent()){
					personeroSaved.setAgrupacionPolitica(agrupacionPolitica.get());
				}
			} else {
				personeroSaved.setAgrupacionPolitica(null);
			}
			personeroSaved.setMesa(mesaUpd);
			personeroSaved.setNombres(personeroDto.getNombres());
			personeroSaved.setApellidoMaterno(personeroDto.getApellidoMaterno());
			personeroSaved.setApellidoPaterno(personeroDto.getApellidoPaterno());
			personeroSaved.setDocumentoIdentidad(personeroDto.getDocumentoIdentidad());
			personeroSaved.setActivo(personeroDto.getActivo());
			personeroSaved.setUsuarioCreacion(personeroDto.getAudUsuarioCreacion());
			personeroSaved.setFechaCreacion(
					DateUtil.getDate(personeroDto.getAudFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
			this.personeroRepository.save(personeroSaved);
		} else {
			logger.info("Se actualizará los datos del personero con la identidad {}",
					personeroDto.getDocumentoIdentidad());
			personeroSaved = personero.get();
			if (personeroDto.getIdAgrupacionPolitica() != null) {
				Optional<AgrupacionPolitica> agrupacionPolitica = this.agrupacionPoliticaRepository
						.findById(personeroDto.getIdAgrupacionPolitica());
				if(agrupacionPolitica.isPresent()){
					personeroSaved.setAgrupacionPolitica(agrupacionPolitica.get());
				}
			} else {
				personeroSaved.setAgrupacionPolitica(null);
			}
			personeroSaved.setMesa(mesaUpd);
			personeroSaved.setNombres(personeroDto.getNombres());
			personeroSaved.setApellidoMaterno(personeroDto.getApellidoMaterno());
			personeroSaved.setApellidoPaterno(personeroDto.getApellidoPaterno());
			personeroSaved.setActivo(personeroDto.getActivo());
			personeroSaved.setUsuarioModificacion(personeroDto.getAudUsuarioCreacion());
			personeroSaved.setFechaModificacion(
					DateUtil.getDate(personeroDto.getAudFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.personeroRepository.save(personeroSaved);
		}
		return personeroSaved;
	}

	private List<MesaDocumento> savedDocumentos(List<MesaArchivoPorTransmitirDto> detalleArchivosDto, Mesa mesaUpd) {
		List<MesaDocumento> detallesDocumento = null;
		MesaDocumento detalleDocumento = null;
		if (detalleArchivosDto != null) {
			detallesDocumento = new ArrayList<>();
			for (MesaArchivoPorTransmitirDto detalleArchivoDto : detalleArchivosDto) {
				detalleDocumento = this.savedDocumento(detalleArchivoDto, mesaUpd);
				detallesDocumento.add(detalleDocumento);
			}
		}
		return detallesDocumento;
	}

	private MesaDocumento savedDocumento(MesaArchivoPorTransmitirDto detalleArchivoDto, Mesa mesaUpd) {
		MesaDocumento mesaDocumentoSaved = null;
		Optional<MesaDocumento> mesaDocumento = this.mesaDocumentoRepository
				.findByIdCc(detalleArchivoDto.getIdMesaArchivoCc());
		Optional<OrcDocumentoElectoral> documentoElectoral = this.documentoElectoralRepository
				.findById(detalleArchivoDto.getIdDocumentoElectoral());
		if (!mesaDocumento.isPresent()) {
			logger.info("Se creara el nuevo documento-archivo mesa {}", detalleArchivoDto.getIdMesaArchivoCc());

			mesaDocumentoSaved = new MesaDocumento();

			if (documentoElectoral.isPresent()) {
				mesaDocumentoSaved.setDocumentoElectoral(documentoElectoral.get());
			}

			mesaDocumentoSaved.setDescripcionObservacion(detalleArchivoDto.getDescripcionObservacion());
			mesaDocumentoSaved.setDigitalizacion(detalleArchivoDto.getDigitalizacion());
			mesaDocumentoSaved.setEstadoDigitalizacion(detalleArchivoDto.getEstadoDigitalizacion());
			mesaDocumentoSaved.setPagina(detalleArchivoDto.getPagina());
			mesaDocumentoSaved.setTipoArchivo(detalleArchivoDto.getTipoArchivo());
			mesaDocumentoSaved.setIdCc(detalleArchivoDto.getIdMesaArchivoCc());
			mesaDocumentoSaved.setMesa(mesaUpd);
			mesaDocumentoSaved.setObservacionDigitalizacion(detalleArchivoDto.getObservacionDigitalizacion());
			mesaDocumentoSaved.setActivo(detalleArchivoDto.getActivo());
			mesaDocumentoSaved.setUsuarioCreacion(detalleArchivoDto.getUsuarioCreacion());
			mesaDocumentoSaved.setFechaCreacion(
					DateUtil.getDate(detalleArchivoDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
			this.mesaDocumentoRepository.save(mesaDocumentoSaved);
		} else {
			mesaDocumentoSaved = mesaDocumento.get();

			if (documentoElectoral.isPresent()) {
				mesaDocumentoSaved.setDocumentoElectoral(documentoElectoral.get());
			}

			mesaDocumentoSaved.setDescripcionObservacion(detalleArchivoDto.getDescripcionObservacion());
			mesaDocumentoSaved.setDigitalizacion(detalleArchivoDto.getDigitalizacion());
			mesaDocumentoSaved.setEstadoDigitalizacion(detalleArchivoDto.getEstadoDigitalizacion());
			mesaDocumentoSaved.setPagina(detalleArchivoDto.getPagina());
			mesaDocumentoSaved.setTipoArchivo(detalleArchivoDto.getTipoArchivo());
			mesaDocumentoSaved.setMesa(mesaUpd);
			mesaDocumentoSaved.setObservacionDigitalizacion(detalleArchivoDto.getObservacionDigitalizacion());
			mesaDocumentoSaved.setActivo(detalleArchivoDto.getActivo());
			mesaDocumentoSaved.setUsuarioModificacion(detalleArchivoDto.getUsuarioModificacion());
			mesaDocumentoSaved.setFechaModificacion(
					DateUtil.getDate(detalleArchivoDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.mesaDocumentoRepository.save(mesaDocumentoSaved);
		}
		return mesaDocumentoSaved;
	}

	private List<OmisoVotante> savedOmisosVotante(List<OmisoVotantePorTransmitirDto> omisoVotanteDto, Mesa mesaUpd) {
		List<OmisoVotante> omisosVotantes = null;
		OmisoVotante omisoVotante = null;
		if (omisoVotanteDto != null) {
			omisosVotantes = new ArrayList<>();
			for (OmisoVotantePorTransmitirDto omisoDto : omisoVotanteDto) {
				omisoVotante = this.savedOmisoVotante(omisoDto, mesaUpd);
				omisosVotantes.add(omisoVotante);
			}
		}
		return omisosVotantes;
	}

	private OmisoVotante savedOmisoVotante(OmisoVotantePorTransmitirDto omisoVotanteDto, Mesa mesaUpd) {
		OmisoVotante omisoSaved = null;
		Optional<OmisoVotante> omiso = this.omisoVotanteRepository
				.findByIdMesaAndIdPadronElectoral(omisoVotanteDto.getIdMesa(), omisoVotanteDto.getIdPadronElectoral());
		if (!omiso.isPresent()) {
			omisoSaved = new OmisoVotante();
			Optional<PadronElectoral> padronElectoral = this.padronElectoralRepository
					.findById(omisoVotanteDto.getIdPadronElectoral());
			if(padronElectoral.isPresent()){
				omisoSaved.setPadronElectoral(padronElectoral.get());
			}
			omisoSaved.setMesa(mesaUpd);
			omisoSaved.setActivo(omisoVotanteDto.getActivo());
			omisoSaved.setUsuarioCreacion(omisoVotanteDto.getUsuarioCreacion());
			omisoSaved.setFechaCreacion(
					DateUtil.getDate(omisoVotanteDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
			this.omisoVotanteRepository.save(omisoSaved);
		} else {
			logger.info(
					"El registro para OmisoVotante con el valor de ID de mesa={} y ID padron electoral={} ya se encuentra registrado",
					omisoVotanteDto.getIdMesa(), omisoVotanteDto.getIdPadronElectoral());
			omisoSaved = omiso.get();
			omisoSaved.setActivo(omisoVotanteDto.getActivo());
			omisoSaved.setUsuarioModificacion(omisoVotanteDto.getUsuarioModificacion());
			omisoSaved.setFechaModificacion(
					DateUtil.getDate(omisoVotanteDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.omisoVotanteRepository.save(omisoSaved);
		}
		return omisoSaved;
	}

	private List<OmisoMiembroMesa> savedOmisosMiembroMesa(List<OmisoMiembroMesaPorTransmitirDto> omisosMiembroMesaDto,
			Mesa mesaUpd) {
		List<OmisoMiembroMesa> omisosMiembroMesa = null;
		OmisoMiembroMesa omisoMiembroMesa = null;
		if (omisosMiembroMesaDto != null) {
			omisosMiembroMesa = new ArrayList<>();
			for (OmisoMiembroMesaPorTransmitirDto omisoDto : omisosMiembroMesaDto) {
				omisoMiembroMesa = this.savedOmisoMiembroMesa(omisoDto, mesaUpd);
				omisosMiembroMesa.add(omisoMiembroMesa);
			}
		}
		return omisosMiembroMesa;
	}

	private OmisoMiembroMesa savedOmisoMiembroMesa(OmisoMiembroMesaPorTransmitirDto omisoMiembroDto, Mesa mesaUpd) {
		OmisoMiembroMesa omisoSaved = null;
		Optional<OmisoMiembroMesa> omiso = this.omisoMiembroMesaRepository
				.findByIdCc(omisoMiembroDto.getIdOmisoMiembroMesaCc());
		if (!omiso.isPresent()) {
			logger.info("Se creara un nuevo omiso miembro mesa");
			omisoSaved = new OmisoMiembroMesa();
			Optional<MiembroMesaSorteado> miembroMesaSorteado = this.miembroMesaSorteadoRepository
					.findById(omisoMiembroDto.getIdMiembroMesaSorteado());
			if(miembroMesaSorteado.isPresent()){
				omisoSaved.setMiembroMesaSorteado(miembroMesaSorteado.get());
			}
			omisoSaved.setIdCc(omisoMiembroDto.getIdOmisoMiembroMesaCc());
			omisoSaved.setMesa(mesaUpd);
			omisoSaved.setActivo(omisoMiembroDto.getActivo());
			omisoSaved.setUsuarioCreacion(omisoMiembroDto.getUsuarioCreacion());
			omisoSaved.setFechaCreacion(
					DateUtil.getDate(omisoMiembroDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
			this.omisoMiembroMesaRepository.save(omisoSaved);
			logger.info("Se creo el omiso miembro mesa con ID={}", omisoSaved.getId());
		} else {
			logger.info(
					"El registro para OmisoMiembroMesa con el valor de ID de mesa={} y ID miembro mesa sorteada={} ya se encuentra registrado (ID={})",
					omisoMiembroDto.getIdMesa(), omisoMiembroDto.getIdMiembroMesaSorteado(),
					omisoMiembroDto.getIdOmisoMiembroMesaCc());
			omisoSaved = omiso.get();
			Optional<MiembroMesaSorteado> miembroMesaSorteado = this.miembroMesaSorteadoRepository
					.findById(omisoMiembroDto.getIdMiembroMesaSorteado());
			if(miembroMesaSorteado.isPresent()){
				omisoSaved.setMiembroMesaSorteado(miembroMesaSorteado.get());
			}
			omisoSaved.setIdCc(omisoMiembroDto.getIdOmisoMiembroMesaCc());
			omisoSaved.setMesa(mesaUpd);
			omisoSaved.setActivo(omisoMiembroDto.getActivo());
			omisoSaved.setUsuarioModificacion(omisoMiembroDto.getUsuarioModificacion());
			omisoSaved.setFechaModificacion(
					DateUtil.getDate(omisoMiembroDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.omisoMiembroMesaRepository.save(omisoSaved);
		}
		return omisoSaved;
	}

	private List<MiembroMesaCola> savedMiembrosMesaCola(List<MiembroMesaColaPorTransmitirDto> miembrosMesaColaDto,
			Mesa mesaUpd) {
		List<MiembroMesaCola> miembrosMesaCola = null;
		MiembroMesaCola miembroMesaCola = null;
		if (miembrosMesaColaDto != null) {
			miembrosMesaCola = new ArrayList<>();
			for (MiembroMesaColaPorTransmitirDto omisoDto : miembrosMesaColaDto) {
				miembroMesaCola = this.savedMiembroMesaCola(omisoDto, mesaUpd);
				miembrosMesaCola.add(miembroMesaCola);
			}
		}
		return miembrosMesaCola;
	}

	private MiembroMesaCola savedMiembroMesaCola(MiembroMesaColaPorTransmitirDto miembroMesaColaDto, Mesa mesaUpd) {
		MiembroMesaCola miembroMesaColaSaved = null;
		Optional<MiembroMesaCola> miembroMesaCola = this.miembroMesaColaRepository.findByIdMesaAndIdPadronElectoral(
				miembroMesaColaDto.getIdMesa(), miembroMesaColaDto.getIdPadronElectoral());
		if (!miembroMesaCola.isPresent()) {
			miembroMesaColaSaved = new MiembroMesaCola();
			Optional<PadronElectoral> padronElectoral = this.padronElectoralRepository
					.findById(miembroMesaColaDto.getIdPadronElectoral());
			if(padronElectoral.isPresent()){
				miembroMesaColaSaved.setPadronElectoral(padronElectoral.get());
			}
			miembroMesaColaSaved.setCargo(miembroMesaColaDto.getCargo());
			miembroMesaColaSaved.setMesa(mesaUpd);
			miembroMesaColaSaved.setUsuarioCreacion(miembroMesaColaDto.getUsuarioCreacion());
			miembroMesaColaSaved.setFechaCreacion(
					DateUtil.getDate(miembroMesaColaDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
			this.miembroMesaColaRepository.save(miembroMesaColaSaved);
		} else {
			logger.info(
					"El registro para MiembroMesaCola con el valor de ID de mesa={} y ID padron electoral={} ya se encuentra registrado",
					miembroMesaColaDto.getIdMesa(), miembroMesaColaDto.getIdPadronElectoral());
			miembroMesaColaSaved = miembroMesaCola.get();
			miembroMesaColaSaved.setActivo(miembroMesaColaDto.getActivo());
			miembroMesaColaSaved.setCargo(miembroMesaColaDto.getCargo());
			miembroMesaColaSaved.setUsuarioModificacion(miembroMesaColaDto.getUsuarioModificacion());
			miembroMesaColaSaved.setFechaModificacion(
					DateUtil.getDate(miembroMesaColaDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.miembroMesaColaRepository.save(miembroMesaColaSaved);
		}
		return miembroMesaColaSaved;
	}

	private List<MiembroMesaSorteado> savedMiembrosMesaSorteado(
			List<MiembroMesaSorteadoPorTransmitirDto> miembrosMesaSorteadoDto, Mesa mesaUpd) {
		List<MiembroMesaSorteado> miembrosMesaSorteado = null;
		MiembroMesaSorteado miembroMesaSortado = null;
		if (miembrosMesaSorteadoDto != null) {
			miembrosMesaSorteado = new ArrayList<>();
			for (MiembroMesaSorteadoPorTransmitirDto miembroMesaSorteadoDto : miembrosMesaSorteadoDto) {
				miembroMesaSortado = this.savedMiembroMesaSorteado(miembroMesaSorteadoDto, mesaUpd);
				if (miembroMesaSortado != null) {
					miembrosMesaSorteado.add(miembroMesaSortado);
				}
			}
		}
		return miembrosMesaSorteado;
	}

	private MiembroMesaSorteado savedMiembroMesaSorteado(MiembroMesaSorteadoPorTransmitirDto miembroMesaSorteadoDto,
			Mesa mesaUpd) {
		MiembroMesaSorteado miembroMesaSorteadoSaved = null;
		Optional<MiembroMesaSorteado> miembroMesaSorteado = this.miembroMesaSorteadoRepository
				.findById(miembroMesaSorteadoDto.getIdMiembroMesaSorteado());
		if (miembroMesaSorteado.isPresent()) {
			miembroMesaSorteadoSaved = miembroMesaSorteado.get();
			if (miembroMesaSorteadoDto.getOmisosMiembroMesa() != null) {
				this.savedOmisosMiembroMesa(miembroMesaSorteadoDto.getOmisosMiembroMesa(), mesaUpd);
			} // end-if
			miembroMesaSorteadoSaved.setAsistenciaAutomatico(miembroMesaSorteadoDto.getAsistenciaAutomatico());
			miembroMesaSorteadoSaved.setAsistenciaManual(miembroMesaSorteadoDto.getAsistenciaManual());
			miembroMesaSorteadoSaved.setActivo(miembroMesaSorteadoDto.getActivo());
			miembroMesaSorteadoSaved.setUsuarioModificacion(miembroMesaSorteadoDto.getUsuarioModificacion());
			miembroMesaSorteadoSaved.setFechaModificacion(
					DateUtil.getDate(miembroMesaSorteadoDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.miembroMesaSorteadoRepository.save(miembroMesaSorteadoSaved);
		} else {
			logger.info("El registro para MiembroMesaSorteado con el valor de ID={} no existe",
					miembroMesaSorteadoDto.getIdMiembroMesaSorteado());
		}
		return miembroMesaSorteadoSaved;
	}
	
	private List<DetActaOficio> savedDetOficios(List<DetOficioPorTransmistirDto> detOficiosDto, String cc, String ruta){
		List<DetActaOficio> detOficios = null;
		DetActaOficio detActaOficio = null;
		if(detOficiosDto!=null){
			detOficios = new ArrayList<>();
			for(DetOficioPorTransmistirDto detOficioDto:detOficiosDto){
				detActaOficio = this.savedDetOficio(detOficioDto, cc, ruta);
				detOficios.add(detActaOficio);
			}
		}
		return detOficios;
	}
	
	private DetActaOficio savedDetOficio(DetOficioPorTransmistirDto dto, String cc, String ruta){
		DetActaOficio detActaOficio = null;
		Optional<DetActaOficio> detActaOficioOp = this.detActaOficioRepository.findByIdCc(dto.getIdCc());
		Optional<Acta> actaOp = this.actaRepository.findById(dto.getIdActa());
		Optional<ActaCeleste> actaCelesteOp = this.actaCelesteRepository.findByIdCc(dto.getIdActaCelesteCc());
		Oficio oficio = this.savedOficio(dto.getCabOficio(), cc, ruta);
		CabActaFormato cabActaFormato = this.savedCabActaFormato(dto.getCabActaFormato(), cc, ruta);
		if(detActaOficioOp.isPresent()){
			logger.info("se editara un detalle oficio");
			detActaOficio = detActaOficioOp.get();
			detActaOficio.setUsuarioModificacion(dto.getUsuarioModificacion());
			detActaOficio.setFechaModificacion(DateUtil.getDate(dto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		} else {
			logger.info("se creara un detalle oficio");
			detActaOficio = new DetActaOficio();
			detActaOficio.setUsuarioCreacion(dto.getUsuarioCreacion());
			detActaOficio.setFechaCreacion(
					DateUtil.getDate(dto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
			
		}
		
		detActaOficio.setIdCc(dto.getIdCc());
		detActaOficio.setActivo(dto.getActivo());
		detActaOficio.setOficio(oficio);
		detActaOficio.setCabActaFormato(cabActaFormato);
		if(actaOp.isPresent()){
			detActaOficio.setActa(actaOp.get());
		}
		if(actaCelesteOp.isPresent()){
			detActaOficio.setActaCeleste(actaCelesteOp.get());
		}
		this.detActaOficioRepository.save(detActaOficio);
		return detActaOficio;
	}
	
	private Oficio savedOficio(OficioPorTransmitirDto oficioDto, String cc, String ruta){
		Oficio oficio = null;
		Optional<Oficio> oficioOp = this.oficioRepository.findByIdCc(oficioDto.getIdCc());
		Optional<CentroComputo> centroComputoOp = this.centroComputoRepository.findById(oficioDto.getIdCentroComputo());
		Archivo archivo = this.guardarArchivo(oficioDto.getArchivo(), cc, ruta, "oficio");
		
		if(oficioOp.isPresent()){
			oficio = oficioOp.get();
			oficio.setUsuarioModificacion(oficioDto.getUsuarioModificacion());
			oficio.setFechaModificacion(
					DateUtil.getDate(oficioDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		} else {
			oficio = new Oficio();
			oficio.setUsuarioCreacion(oficioDto.getUsuarioCreacion());
			oficio.setFechaCreacion(
					DateUtil.getDate(oficioDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		}
		
		if(centroComputoOp.isPresent()){
			oficio.setCentroComputo(centroComputoOp.get());
		}
		
		oficio.setIdCc(oficioDto.getIdCc());
		oficio.setEstadoJne(oficioDto.getEstado());		
		oficio.setEstado(oficioDto.getEstadoOficio());
		oficio.setActivo(oficioDto.getActivo());
		oficio.setNombreOficio(oficioDto.getNombreOficio());
		oficio.setArchivo(archivo);
		this.oficioRepository.save(oficio);
		return oficio;
	}
	
	private CabActaFormato savedCabActaFormato(CabActaFormatoPorTransmitirDto cabActaFormatoDto, String cc, String ruta){
		CabActaFormato cabActaFormato = null;
		Optional<CabActaFormato> cabActaFormatoOp = this.cabActaFormatoRepository.findByIdCc(cabActaFormatoDto.getIdCc());
		Formato formato = this.savedFormato(cabActaFormatoDto.getTabFormato(), cc, ruta);
		Archivo archivo = this.guardarArchivo(cabActaFormatoDto.getArchivo(), cc, ruta, "cargo");
		if(cabActaFormatoOp.isPresent()){
			logger.info("se editara un cab acta formato");
			cabActaFormato = cabActaFormatoOp.get();
			cabActaFormato.setUsuarioModificacion(cabActaFormatoDto.getUsuarioModificacion());
			cabActaFormato.setFechaModificacion(
					DateUtil.getDate(cabActaFormatoDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		} else {
			logger.info("se creara un cab acta formato");
			cabActaFormato = new CabActaFormato();
			cabActaFormato.setUsuarioCreacion(cabActaFormatoDto.getUsuarioCreacion());
			cabActaFormato.setFechaCreacion(
					DateUtil.getDate(cabActaFormatoDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		}

		cabActaFormato.setIdCc(cabActaFormatoDto.getIdCc());
		cabActaFormato.setArchivoFormatoPdf(archivo);
		cabActaFormato.setActivo(cabActaFormatoDto.getActivo());
		cabActaFormato.setCorrelativo(cabActaFormatoDto.getCorrelativo());
		cabActaFormato.setFormato(formato);
		this.cabActaFormatoRepository.save(cabActaFormato);
		this.savedDetActaFormatos(cabActaFormatoDto.getDetalle(), cabActaFormato);
		return cabActaFormato;
	}
	
	private List<DetActaFormato> savedDetActaFormatos(List<DetActaFormatoPorTransmitirDto> detActaFormatoDto, CabActaFormato cabActaFormato){
		List<DetActaFormato> detActaFormatos = null;
		DetActaFormato detActaFormato = null;
		if(detActaFormatoDto!=null){
			detActaFormatos = new ArrayList<>();
			for(DetActaFormatoPorTransmitirDto detOficioDto:detActaFormatoDto){
				detActaFormato = this.savedDetActaFormato(detOficioDto, cabActaFormato);
				detActaFormatos.add(detActaFormato);
			}
		}
		return detActaFormatos;
	}
	
	private DetActaFormato savedDetActaFormato(DetActaFormatoPorTransmitirDto detActaFormatoDto, CabActaFormato cabActaFormato){
		DetActaFormato detActaFormato = null;
		Optional<DetActaFormato> detActaFormatoOp = this.detActaFormatoRepository.findByIdCc(detActaFormatoDto.getIdCc());
		Optional<Acta> actaOp = this.actaRepository.findById(detActaFormatoDto.getIdActa());
		if(detActaFormatoOp.isPresent()){
			logger.info("se editara un det acta formato");
			detActaFormato = detActaFormatoOp.get();
			detActaFormato.setUsuarioModificacion(detActaFormatoDto.getUsuarioModificacion());
			detActaFormato.setFechaModificacion(
					DateUtil.getDate(detActaFormatoDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		} else {
			logger.info("se creara un det acta formato");
			detActaFormato = new DetActaFormato();
			detActaFormato.setUsuarioCreacion(detActaFormatoDto.getUsuarioCreacion());
			detActaFormato.setFechaCreacion(
					DateUtil.getDate(detActaFormatoDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		}

		if(actaOp.isPresent()){
			detActaFormato.setActa(actaOp.get());
		}
		detActaFormato.setCabActaFormato(cabActaFormato);
		detActaFormato.setIdCc(detActaFormatoDto.getIdCc());
		detActaFormato.setActivo(detActaFormatoDto.getActivo());
		this.detActaFormatoRepository.save(detActaFormato);
		return detActaFormato;
	}
	
	private Formato savedFormato(TabFormatoPorTransmitirDto formatoDto, String cc, String ruta){
		Formato formato = null;
		Optional<Formato> formatoOp = this.formatoRepository.findByIdCc(formatoDto.getIdCc());
		Archivo archivo = this.guardarArchivo(formatoDto.getArchivo(), cc, ruta, "formato");
		if(formatoOp.isPresent()){
			logger.info("se editara el formato");
			formato = formatoOp.get();
			formato.setUsuarioModificacion(formatoDto.getUsuarioModificacion());
			formato.setFechaModificacion(
					DateUtil.getDate(formatoDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		} else {
			logger.info("se creara el formato");
			formato = new Formato();
			formato.setUsuarioCreacion(formatoDto.getUsuarioCreacion());
			formato.setFechaCreacion(
					DateUtil.getDate(formatoDto.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		}

		formato.setIdCc(formatoDto.getIdCc());
		formato.setActivo(formatoDto.getActivo());
		formato.setCorrelativo(formatoDto.getCorrelativo());
		formato.setTipoFormato(formatoDto.getTipoFormato());
		formato.setArchivo(archivo);
		this.formatoRepository.save(formato);
		return formato;
	}

	private List<MiembroMesaEscrutinio> savedMiembrosMesaEscrutinio(
			List<MiembroMesaEscrutinioPorTransmitirDto> miembrosMesaEscrutinioDto, Mesa mesaUpd) {
		List<MiembroMesaEscrutinio> miembrosMesaEscrutinio = null;
		MiembroMesaEscrutinio miembroMesaEscrutinio = null;
		if (miembrosMesaEscrutinioDto != null) {
			miembrosMesaEscrutinio = new ArrayList<>();
			for (MiembroMesaEscrutinioPorTransmitirDto omisoDto : miembrosMesaEscrutinioDto) {
				miembroMesaEscrutinio = this.savedMiembroMesaEscrutinio(omisoDto, mesaUpd);
				miembrosMesaEscrutinio.add(miembroMesaEscrutinio);
			}
		}
		return miembrosMesaEscrutinio;
	}

	private MiembroMesaEscrutinio savedMiembroMesaEscrutinio(
			MiembroMesaEscrutinioPorTransmitirDto miembroMesaEscrutinioDto, Mesa mesaUpd) {
		MiembroMesaEscrutinio miembroMesaEscrutinioSaved = null;

		logger.info("Consultando escrutinio {}", miembroMesaEscrutinioDto.getIdMiembroMesaEscrutinioCc());
		Optional<MiembroMesaEscrutinio> miembroMesaEscrutinio = this.miembroMesaEscrutinioRepository
				.findByIdCc(miembroMesaEscrutinioDto.getIdMiembroMesaEscrutinioCc());
		if (miembroMesaEscrutinio.isPresent()) {
			logger.info("Se procede a crear un registro de miembro mesa escrutinio");
			miembroMesaEscrutinioSaved = miembroMesaEscrutinio.get();
			miembroMesaEscrutinioSaved.setMesa(mesaUpd);
			miembroMesaEscrutinioSaved
					.setDocumentoIdentidadPresidente(miembroMesaEscrutinioDto.getDocumentoIdentidadPresidente());
			miembroMesaEscrutinioSaved
					.setDocumentoIdentidadSecretario(miembroMesaEscrutinioDto.getDocumentoIdentidadSecretario());
			miembroMesaEscrutinioSaved
					.setDocumentoIdentidadTercerMiembro(miembroMesaEscrutinioDto.getDocumentoIdentidadTercerMiembro());
			miembroMesaEscrutinioSaved.setUsuarioModificacion(miembroMesaEscrutinioDto.getUsuarioModificacion());
			miembroMesaEscrutinioSaved.setFechaModificacion(
					DateUtil.getDate(miembroMesaEscrutinioDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.miembroMesaEscrutinioRepository.save(miembroMesaEscrutinioSaved);
		} else {
			logger.info("El registro para MiembroMesaEscrutinio con el valor de ID={} no existe, se procede a crear",
					miembroMesaEscrutinioDto.getIdMiembroMesaEscrutinioCc());
			miembroMesaEscrutinioSaved = new MiembroMesaEscrutinio();
			miembroMesaEscrutinioSaved.setIdCc(miembroMesaEscrutinioDto.getIdMiembroMesaEscrutinioCc());
			miembroMesaEscrutinioSaved.setMesa(mesaUpd);
			miembroMesaEscrutinioSaved
					.setDocumentoIdentidadPresidente(miembroMesaEscrutinioDto.getDocumentoIdentidadPresidente());
			miembroMesaEscrutinioSaved
					.setDocumentoIdentidadSecretario(miembroMesaEscrutinioDto.getDocumentoIdentidadSecretario());
			miembroMesaEscrutinioSaved
					.setDocumentoIdentidadTercerMiembro(miembroMesaEscrutinioDto.getDocumentoIdentidadTercerMiembro());
			miembroMesaEscrutinioSaved.setUsuarioCreacion(miembroMesaEscrutinioDto.getUsuarioCreacion());
			miembroMesaEscrutinioSaved.setFechaCreacion(
					DateUtil.getDate(miembroMesaEscrutinioDto.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
			this.miembroMesaEscrutinioRepository.save(miembroMesaEscrutinioSaved);
		}
		return miembroMesaEscrutinioSaved;
	}
	

	private Archivo guardarImagenEscrutinio(ActaPorTransmitirDto actaDto, Acta acta, String cc, String ruta, Integer tipoDocumentoElectoral) {
		Archivo archivoEscrutinio = null;
		Archivo archivoEscrutinioPdf = null;
		try {
			if (actaDto.getArchivoEscrutinio() != null && actaDto.getArchivoEscrutinio().getBase64() != null
					&& actaDto.getArchivoEscrutinio().getGuid() != null) {
				Optional<Archivo> existFile = archivoRepository.findByGuid(actaDto.getArchivoEscrutinio().getGuid());
				if (!existFile.isPresent()) {
				
						archivoEscrutinio = ArchivoUtils.createArchivo(actaDto.getArchivoEscrutinio(),ruta, false);
						archivoEscrutinio.setActivo(SceConstantes.ACTIVO);
						archivoEscrutinio.setFechaCreacion(new Date());
						archivoEscrutinio.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoEscrutinio.setCodigoCc(cc);
						archivoEscrutinio.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoEscrutinio);
						acta.setArchivoEscrutinio(archivoEscrutinio);
					
						archivoEscrutinioPdf = ArchivoUtils.createArchivoPdf(actaDto.getArchivoEscrutinio(),ruta);
						archivoEscrutinioPdf.setActivo(SceConstantes.ACTIVO);
						archivoEscrutinioPdf.setFechaCreacion(new Date());
						archivoEscrutinioPdf.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoEscrutinioPdf.setCodigoCc(cc);
						archivoEscrutinioPdf.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoEscrutinioPdf);
						acta.setArchivoEscrutinioPdf(archivoEscrutinioPdf);
					

				} else {
					logger.info("El archivo de escrutinio {} ya existe", actaDto.getArchivoEscrutinio().getGuid());
				}
			} else {
				logger.info("No se guardo el archivo de escrutinio debido a que el acta no contiene el archivo");
			}
		} catch (Exception e) {
			logger.info("No se creo la imagen para el archivo de escrutinio");
		}
		return archivoEscrutinioPdf;
	}

	private Archivo guardarImagenInstalacionSufragio(ActaPorTransmitirDto actaDto, Acta acta, String cc, String ruta, Integer tipoDocumentoElectoral) {
		Archivo archivoSufragio = null;
		Archivo archivoSufragioPdf = null;
		try {
			if (actaDto.getArchivoInstalacionSufragio() != null
					&& actaDto.getArchivoInstalacionSufragio().getBase64() != null
					&& actaDto.getArchivoInstalacionSufragio().getGuid() != null) {
				Optional<Archivo> existFile = archivoRepository
						.findByGuid(actaDto.getArchivoInstalacionSufragio().getGuid());
				if (!existFile.isPresent()) {
					
						archivoSufragio = ArchivoUtils.createArchivo(actaDto.getArchivoInstalacionSufragio(),
								ruta, false);
						archivoSufragio.setActivo(SceConstantes.ACTIVO);
						archivoSufragio.setFechaCreacion(new Date());
						archivoSufragio.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoSufragio.setCodigoCc(cc);
						archivoSufragio.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoSufragio);
						acta.setArchivoInstalacionSufragio(archivoSufragio);
					

				
						archivoSufragioPdf = ArchivoUtils.createArchivoPdf(actaDto.getArchivoInstalacionSufragio(),
								ruta);
						archivoSufragioPdf.setActivo(SceConstantes.ACTIVO);
						archivoSufragioPdf.setFechaCreacion(new Date());
						archivoSufragioPdf.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoSufragioPdf.setCodigoCc(cc);
						archivoSufragioPdf.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoSufragioPdf);
						acta.setArchivoInstalacionSufragioPdf(archivoSufragioPdf);
					

				} else {
					logger.info("El archivo de instalacion de sufragio {} ya existe",
							actaDto.getArchivoInstalacionSufragio().getGuid());
				}
			} else {
				logger.info(
						"No se guardo el archivo de instalacion sufragio debido a que el acta no contiene el archivo");
			}
		} catch (Exception e) {
			logger.info("No se creo la imagen para el archivo de instalacion {}", e.getMessage());
		}
		return archivoSufragioPdf;
	}
	
	private Archivo guardarImagenEscrutinioCeleste(ActaPorTransmitirDto actaDto, ActaCeleste acta, String cc, String ruta, Integer tipoDocumentoElectoral) {
		Archivo archivoEscrutinio = null;
		Archivo archivoEscrutinioPdf = null;
		try {
			if (actaDto.getArchivoEscrutinio() != null && actaDto.getArchivoEscrutinio().getBase64() != null
					&& actaDto.getArchivoEscrutinio().getGuid() != null) {
				Optional<Archivo> existFile = archivoRepository.findByGuid(actaDto.getArchivoEscrutinio().getGuid());
				if (!existFile.isPresent()) {
				
						archivoEscrutinio = ArchivoUtils.createArchivo(actaDto.getArchivoEscrutinio(),ruta, false);
						archivoEscrutinio.setActivo(SceConstantes.ACTIVO);
						archivoEscrutinio.setFechaCreacion(new Date());
						archivoEscrutinio.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoEscrutinio.setCodigoCc(cc);
						archivoEscrutinio.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoEscrutinio);
						acta.setArchivoEscrutinio(archivoEscrutinio);
					
						archivoEscrutinioPdf = ArchivoUtils.createArchivoPdf(actaDto.getArchivoEscrutinio(),ruta);
						archivoEscrutinioPdf.setActivo(SceConstantes.ACTIVO);
						archivoEscrutinioPdf.setFechaCreacion(new Date());
						archivoEscrutinioPdf.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoEscrutinioPdf.setCodigoCc(cc);
						archivoEscrutinioPdf.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoEscrutinioPdf);
						acta.setArchivoEscrutinioPdf(archivoEscrutinioPdf);
					

				} else {
					logger.info("El archivo de escrutinio {} ya existe", actaDto.getArchivoEscrutinio().getGuid());
				}
			} else {
				logger.info("No se guardo el archivo de escrutinio debido a que el acta no contiene el archivo");
			}
		} catch (Exception e) {
			logger.info("No se creo la imagen para el archivo de escrutinio");
		}
		return archivoEscrutinioPdf;
	}

	private Archivo guardarImagenInstalacionSufragioCeleste(ActaPorTransmitirDto actaDto, ActaCeleste acta, String cc, String ruta, Integer tipoDocumentoElectoral) {
		Archivo archivoSufragio = null;
		Archivo archivoSufragioPdf = null;
		try {
			if (actaDto.getArchivoInstalacionSufragio() != null
					&& actaDto.getArchivoInstalacionSufragio().getBase64() != null
					&& actaDto.getArchivoInstalacionSufragio().getGuid() != null) {
				Optional<Archivo> existFile = archivoRepository
						.findByGuid(actaDto.getArchivoInstalacionSufragio().getGuid());
				if (!existFile.isPresent()) {
					
						archivoSufragio = ArchivoUtils.createArchivo(actaDto.getArchivoInstalacionSufragio(),
								ruta, false);
						archivoSufragio.setActivo(SceConstantes.ACTIVO);
						archivoSufragio.setFechaCreacion(new Date());
						archivoSufragio.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoSufragio.setCodigoCc(cc);
						archivoSufragio.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoSufragio);
						acta.setArchivoInstalacionSufragio(archivoSufragio);
					

				
						archivoSufragioPdf = ArchivoUtils.createArchivoPdf(actaDto.getArchivoInstalacionSufragio(),
								ruta);
						archivoSufragioPdf.setActivo(SceConstantes.ACTIVO);
						archivoSufragioPdf.setFechaCreacion(new Date());
						archivoSufragioPdf.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
						archivoSufragioPdf.setCodigoCc(cc);
						archivoSufragioPdf.setDocumentoElectoral(tipoDocumentoElectoral);
						archivoRepository.save(archivoSufragioPdf);
						acta.setArchivoInstalacionSufragioPdf(archivoSufragioPdf);
					

				} else {
					logger.info("El archivo de instalacion de sufragio {} ya existe",
							actaDto.getArchivoInstalacionSufragio().getGuid());
				}
			} else {
				logger.info(
						"No se guardo el archivo de instalacion sufragio debido a que el acta no contiene el archivo");
			}
		} catch (Exception e) {
			logger.info("No se creo la imagen para el archivo de instalacion {}", e.getMessage());
		}
		return archivoSufragioPdf;
	}
	
	private Archivo guardarArchivo(ArchivoTransmisionDto archivo, String cc, String ruta, String identificador) {
		Archivo oficioPdf = null;
		try {
			if (archivo != null
					&& archivo.getBase64() != null
					&& archivo.getGuid() != null) {
				Optional<Archivo> existFile = archivoRepository.findByGuid(archivo.getGuid());
				if (!existFile.isPresent()) {
					
					oficioPdf = ArchivoUtils.createArchivo(archivo,ruta, false);
					oficioPdf.setActivo(SceConstantes.ACTIVO);
					oficioPdf.setFechaCreacion(new Date());
					oficioPdf.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
					oficioPdf.setCodigoCc(cc);
					archivoRepository.save(oficioPdf);

				} else {
					logger.info("El archivo de {} {} ya existe",identificador, archivo.getGuid());
				}
			} else {
				logger.info("No se guardo el archivo de {}", identificador);
			}
		} catch (Exception e) {
			logger.error("No se creo el pdf del {} = {}", identificador, e.getMessage());
		}
		return oficioPdf;
	}



}
