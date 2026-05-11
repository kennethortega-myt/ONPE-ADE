package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.*;
import pe.gob.onpe.sceorcbackend.model.dto.controlcalidad.*;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.AgrupolBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ResolucionActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.VotoPreferencialBean;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.Seccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.SeccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetCcPreferencialResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetCcResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ConstantesSecciones;
import pe.gob.onpe.sceorcbackend.utils.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaResolucionRepository;

@Service
public class ControlCalidadServiceImpl implements ControlCalidadService {

	private final CabActaService actaService;
	private final DetActaResolucionRepository detActaResolucionRepository;
	private final DetActaRectangleService detActaRectangleService;
	private final SeccionService seccionService;
	private final ResolucionService resolucionService;
	private final DetActaService detActaService;
	private final UtilSceService utilSceService;
	private final CabCcResolucionService cabCcResolucionService;
	private final DetCcResolucionRepository detCcResolucionRepository;
	private final DetCcPreferencialResolucionRepository detCcPreferencialResolucionRepository;
    private final ITabLogService logService;
	private final ActaServiceGroup actaServiceGroup;
	private final ActaRepository actaRepository;

    @Value("${sce.nacion.url}")  
    private String urlNacion;    
    private final RestTemplate clientExport;
    
    Logger logger = LoggerFactory.getLogger(ControlCalidadServiceImpl.class);    
	
	public ControlCalidadServiceImpl( CabActaService actaService, 
			DetActaResolucionRepository detActaResolucionRepository,
			DetActaRectangleService detActaRectangleService,
			SeccionService seccionService,
			ResolucionService resolucionService,
			DetActaService detActaService,			
			UtilSceService utilSceService,
			CabCcResolucionService cabCcResolucionService,
			DetCcResolucionRepository detCcResolucionRepository,
			DetCcPreferencialResolucionRepository detCcPreferencialResolucionRepository,
			RestTemplate clientExport,
            ITabLogService logService,
			ActaServiceGroup actaServiceGroup,
			ActaRepository actaRepository
			) {
		this.actaService = actaService;
		this.detActaResolucionRepository = detActaResolucionRepository;
		this.detActaRectangleService = detActaRectangleService;
		this.seccionService = seccionService;
		this.resolucionService = resolucionService;
		this.detActaService = detActaService;
		this.utilSceService = utilSceService;
		this.cabCcResolucionService = cabCcResolucionService;
		this.detCcResolucionRepository = detCcResolucionRepository;
		this.detCcPreferencialResolucionRepository = detCcPreferencialResolucionRepository;
		this.clientExport = clientExport;
		this.logService = logService;
		this.actaServiceGroup = actaServiceGroup;
		this.actaRepository = actaRepository;
	}
	
	@Override
	public ControlCalidadSumaryResponse summaryControlCalidad(String codigoEleccion) {

		Object[] outerResult = this.actaService.summaryControlCalidad(codigoEleccion,
				ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA,
				ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA,
				ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA,
				ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA,
				ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,
				ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION);
		
		if (outerResult == null || outerResult.length == 0 || !(outerResult[0] instanceof Object[] innerResult)) {
		  return ControlCalidadSumaryResponse
				  .builder()
				  .pendiente(0)
				  .validado(0)
				  .build();
		}
		
		return ControlCalidadSumaryResponse
		  .builder()
		  .pendiente(Objects.isNull(innerResult[0]) ? 0 : ((Long) innerResult[0]).intValue())
		  .validado(Objects.isNull(innerResult[1]) ? 0 : ((Long) innerResult[1]).intValue())
		  .build();
	}
  
	@Override
	public List<ControlCalidadActaPendiente> actasPendientesControlCalidad(String codigoEleccion, TokenInfo tokenInfo) {
	  List<Acta> listaActas = actasPendientesCCasignadas(tokenInfo.getNombreUsuario(), codigoEleccion);
	  
	  if(listaActas.isEmpty()) {
		  this.actaService.asignarUsuarioActaControlCalidad(codigoEleccion, tokenInfo, ConstantesComunes.CANTIDAD_ACTAS_ASIGNAR_CONTROL_CALIDAD);
		  return actasPendientesCCasignadas(tokenInfo.getNombreUsuario(), codigoEleccion)
				  .stream()
				  .map( this::getActaPendiente).toList();
	  } else {
		  return listaActas
				  .stream()
				  .map( this::getActaPendiente).toList();
	  }
	  
	}
	
	private List<Acta> actasPendientesCCasignadas(String usuarioControlCalidad, String codigoEleccion) {
		return this.actaService.actasPendientesControlCalidadAsignados(usuarioControlCalidad, codigoEleccion)
				  .stream()
				  .filter( acta -> !tieneResolucionesRechazadas(acta))
				  .toList();
	}
	
	private boolean tieneResolucionesRechazadas(Acta acta) {
		Set<DetActaResolucion> resoluciones = acta.getDetResoluciones();
		if(resoluciones != null && !resoluciones.isEmpty()) {
			for(DetActaResolucion actaResol: resoluciones) {
				if(ConstantesEstadoResolucion.RECHAZADA_2DO_CC.equals(actaResol.getResolucion().getEstadoDigitalizacion())) {
					return Boolean.TRUE;
				}
			}
		}					
		return Boolean.FALSE;
	}
	  
	private ControlCalidadActaPendiente getActaPendiente(Acta actaPendiente) {
		  Ubigeo ubigeo = actaPendiente.getUbigeoEleccion().getUbigeo();
		  Eleccion eleccion = actaPendiente.getUbigeoEleccion().getEleccion();
		  Archivo archivoEscrutinio = actaPendiente.getArchivoEscrutinio();
		  Archivo archivoInstSufragio = actaPendiente.getArchivoInstalacionSufragio();
		  
		  return ControlCalidadActaPendiente
			  .builder()
			  .idActa(actaPendiente.getId())
			  .mesa(actaPendiente.getMesa() == null ? "" : actaPendiente.getMesa().getCodigo())
			  .copia(actaPendiente.getNumeroCopia())
			  .digitoChequeo(actaPendiente.getDigitoChequeoEscrutinio())
			  .codigoEleccion(eleccion == null ? "" : eleccion.getCodigo())
			  .nombreEleccion(eleccion == null ? "" : eleccion.getNombre())
			  .cvas(actaPendiente.getCvas())
			  .ubigeoDepa(ubigeo == null ? "" : ubigeo.getDepartamento())
			  .ubigeoProv(ubigeo == null ? "" : ubigeo.getProvincia())
			  .ubigeoNombre(ubigeo == null ? "" : ubigeo.getNombre())
			  .idArchivoActaEscrutinio(archivoEscrutinio == null ? null : archivoEscrutinio.getId())
			  .idArchivoActaInstalacionSufragio(archivoInstSufragio == null ? null : archivoInstSufragio.getId())
			  .build();
	}

	@Override
	public List<ResolucionActaResponse> obtenerResolucionesPorActa(Long idActa) {
		List<DetActaResolucion> resoluciones = this.detActaResolucionRepository.findByActa_Id(idActa);

		if(resoluciones != null && !resoluciones.isEmpty()) {
			return resoluciones
					.stream()
					.map(resol -> {
						TabResolucion tabResol = resol.getResolucion();
						Acta acta = resol.getActa();
						return ResolucionActaResponse
							.builder()
							.idDetActaResol(resol.getId())
							.idActa(resol.getActa().getId())
							.idArchivo(tabResol.getArchivoResolucion() == null ? null : tabResol.getArchivoResolucion().getId())
							.idResolucion(tabResol.getId())
							.nombreResolucion(tabResol.getNumeroResolucion())
							.numeroElectores(acta.getElectoresHabiles())
							.numeroElectoresAusentes(acta.getElectoresHabiles() - acta.getCvas())
							.numeroExpediente(tabResol.getNumeroExpediente())
							.build();
					}).toList();
		}
		return Collections.emptyList();
	}

	@Override
	public ImagenesPaso1 obtenerIdsArchivosPaso1(Long idActa, TokenInfo tokenInfo) {
		Acta acta = this.actaService.findById(idActa).orElse(null);		 
		if(acta == null) return null;
		String ubigeo = acta.getUbigeoEleccion().getUbigeo().getCodigo();
		
		String actaInstalacionSufragio = "";
		String actaEscrutinio = "";
		String actaEscrutinioHorizontal = "";
		
		if(ConstantesComunes.SOLUCION_TECNOLOGICA_CONVENCIONAL.compareTo(acta.getSolucionTecnologica()) == 0) {
			if(ubigeo.startsWith("9")) { //EXTRANJEROS
				actaInstalacionSufragio = ConstantesComunes.ABREV_ACTA_INSTALACION_SUGRAFIO_EXTRANJERO;
				actaEscrutinio = ConstantesComunes.ABREV_ACTA_ESCRUTINIO_EXTRANJERO;
				actaEscrutinioHorizontal = ConstantesComunes.ABREV_ACTA_ESCRUTINIO_EXTRANJERO_HORIZONTAL;
			} else { //CONVENCIONALES
				actaInstalacionSufragio = ConstantesComunes.ABREV_ACTA_INSTALACION_SUGRAFIO;
				actaEscrutinio = ConstantesComunes.ABREV_ACTA_ESCRUTINIO;
				actaEscrutinioHorizontal = ConstantesComunes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL;
			}
		} else if(ConstantesComunes.SOLUCION_TECNOLOGICA_STAE.compareTo(acta.getSolucionTecnologica()) == 0) { // STAE
			//CONVENCIONALES
			if(Objects.equals(acta.getTipoTransmision(), ConstantesComunes.TIPO_HOJA_STAE_CONTINGENCIA)) {
				actaInstalacionSufragio = ConstantesComunes.ABREV_ACTA_INSTALACION_SUGRAFIO;
				actaEscrutinio = ConstantesComunes.ABREV_ACTA_ESCRUTINIO;
				actaEscrutinioHorizontal = ConstantesComunes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL;
			}
			if(Objects.equals(acta.getTipoTransmision(), ConstantesComunes.TIPO_HOJA_STAE_TRANSMITIDA)) {
				actaInstalacionSufragio = ConstantesComunes.ABREV_ACTA_SUFRAGIO_STAE;
				actaEscrutinio = ConstantesComunes.ABREV_ACTA_ESCRUTINIO_STAE;
				actaEscrutinioHorizontal = ConstantesComunes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL;
			}
		}
		 
		List<Seccion> secciones = this.seccionService.findIdsByAbreviaturas(List.of(
	               ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_LETTERS,
	               ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_NUMBERS));
		
		ImagenesPaso1 imagenesPaso1 = new ImagenesPaso1();
		
		imagenesPaso1.setIdArchivoCvasLetrasSufragio(
				getIdArchivoSeccionRectangulo(idActa, 
						secciones, 
						ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_LETTERS, 
						actaInstalacionSufragio));
		
		imagenesPaso1.setIdArchivoCvasNumSufragio(
				getIdArchivoSeccionRectangulo(idActa, 
						secciones, 
						ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_NUMBERS, 
						actaInstalacionSufragio));
		
		Long idCvasEsc = getIdArchivoSeccionRectangulo(idActa, 
				secciones, 
				ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_NUMBERS, 
				actaEscrutinio);
		
		if(idCvasEsc == null) {
			idCvasEsc = getIdArchivoSeccionRectangulo(idActa, 
					secciones, 
					ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_NUMBERS, 
					actaEscrutinioHorizontal);
		}
		
		imagenesPaso1.setIdArchivoCvasNumEscrutinio(idCvasEsc);

		logger.info("Acta : " + acta.getMesa().getCodigo() + "-" + acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio() + " - idActa: " + idActa + " - ID de las imagenes del paso 1: " + imagenesPaso1.toString());

		guardarAccionActa(idActa,
				tokenInfo.getCodigoCentroComputo(),
				tokenInfo.getNombreUsuario(),
				ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD, 20);
		
		return imagenesPaso1;
	}
	
	private Long getIdArchivoSeccionRectangulo(Long idActa, List<Seccion> secciones, String seccion, String tipo) {
		List<DetActaRectangleDTO> detActaRectangulos = this.detActaRectangleService.findByActaIdAndSeccion(idActa, 
				getIdSeccion(secciones, seccion))
				.stream()
				.filter(da -> da.getType().equals(tipo))
				.toList();		
		return detActaRectangulos.isEmpty() ? null : detActaRectangulos.getFirst().getArchivo();
	}
	
	private Integer getIdSeccion(List<Seccion> secciones, String abrev) {
		return secciones
				.stream()				
				.filter(seccion -> seccion.getAbreviatura().equals(abrev))
				.map(Seccion::getId)
				.toList()
				.getFirst();
	}

	@Override
	@Transactional
	public GenericResponse<Boolean> rechazarControlCalidad(RechazarCcRequest request, TokenInfo tokenInfo) {
		
		for(Long idResol: request.getIdsResoluciones()) {
			this.resolucionService.actualizarEstadoDigitalizacion(tokenInfo, idResol,
					ConstantesEstadoResolucion.RECHAZADA_2DO_CC);
		}

		guardarAccionActa(request.getIdActa(),
				tokenInfo.getCodigoCentroComputo(),
				tokenInfo.getNombreUsuario(),
				ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD_RECHAZAR, 23);

		logService.registrarLog(
				tokenInfo.getNombreUsuario(),
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				"Se rechazó la resolución con autorización nación.",
				tokenInfo.getCodigoCentroComputo(),
				ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
				ConstantesComunes.LOG_TRANSACCIONES_ACCION
		);
		
		return new GenericResponse<>(true, "El acta fue rechazada correctamente.", Boolean.TRUE);
	}
	
	@Override
	@Transactional
	public GenericResponse<Boolean> observarControlCalidad(Long idActa, TokenInfo tokenInfo) {
		this.actaService.observarActaControlCalidad(idActa, tokenInfo);
		guardarAccionActa(idActa,
				tokenInfo.getCodigoCentroComputo(),
				tokenInfo.getNombreUsuario(),
				ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD_OBSERVAR, 25);
		return new GenericResponse<>(true, "El acta fue observada correctamente.", Boolean.TRUE);
	}
	
	@Override
	public DataPaso2Response obtenerDataPaso2(Long idActa) {
		 Acta acta = this.actaService.findById(idActa).orElse(null);
		 
		 if(acta == null) return null;
		 
		 List<DetActaRectangleDTO> detActaRectangles = this.detActaRectangleService.findByActaId(acta.getId());
		 
		 String estadoResol = acta.getEstadoActaResolucion();

		 DataPaso2Response datosPaso2 = DataPaso2Response
				 .builder()
				 .idImgFirmaPresidenteEsc(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_PRESIDENT))
				 .idImgFirmaSecretarioEsc(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_SECRETARY))
				 .idImgFirmaTercerMiembroEsc(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_THIRD_MEMBER))
				 .idImgFirmaPresidenteIns(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_PRESIDENT))
				 .idImgFirmaSecretarioIns(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_SECRETARY))
				 .idImgFirmaTercerMiembroIns(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_THIRD_MEMBER))
				 .idImgFirmaPresidenteSuf(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_PRESIDENT))
				 .idImgFirmaSecretarioSuf(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_SECRETARY))
				 .idImgFirmaTercerMiembroSuf(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_THIRD_MEMBER))
				 .idImgObsEscrutinio(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_OBSERVATION_COUNT))
				 .idImgObsInstalacion(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_OBSERVATION_INSTALL))
				 .idImgObsSufragio(getIdImagen(detActaRectangles, ConstantesSecciones.SECTION_ABREV_OBSERVATION_VOTE))
				 .isActaConFirma( estadoResol == null || !estadoResol.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA))
				 .build();

		logger.info("Acta : " + acta.getMesa().getCodigo() + "-" + acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio() + " - idActa: " + idActa + " - ID de las imagenes del paso 2: " + datosPaso2.toString());

		 return datosPaso2;
			 
	}
	
	@Override
	@Transactional
	public GenericResponse<Boolean> aceptarControlCalidad(RechazarCcRequest request, TokenInfo tokenInfo) {
		this.actaService.aceptarActaControlCalidad(request.getIdActa(), tokenInfo);
		
		for(Long idResol: request.getIdsResoluciones()) {
			this.resolucionService.actualizarEstadoDigitalizacion(tokenInfo, idResol,
					ConstantesEstadoResolucion.SEGUNDO_CC_ACEPTADA);
		}

		guardarAccionActa(request.getIdActa(),
				tokenInfo.getCodigoCentroComputo(),
				tokenInfo.getNombreUsuario(),
				ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD, 21);
		
		return new GenericResponse<>(true, "El acta fue aceptada en el control de calidad correctamente.", Boolean.TRUE);
	}
	
	@Override
	public GenericResponse<DataPaso3Response> obtenerDataPaso3(Long idActa, String schema) {
		DetActaRectangleDTO archivoRectAgrupol = null;
		DetActaRectangleDTO archivoRectPref = null;
		Integer numeroColumnasPref = null;
		Acta acta = this.actaService.findById(idActa).orElse(null);			
		
		if(acta == null) {
			return new GenericResponse<>(false, "El acta no fue encontrada.", null);
		}
		
		String tipoEleccion = acta.getUbigeoEleccion().getEleccion().getCodigo();		
		String estadoResol = acta.getEstadoActaResolucion();
		List<DetActaRectangleDTO> detActaRectangles = this.detActaRectangleService.findByActaId(idActa);

		if(!ConstantesComunes.COD_ELEC_PRE.equals(tipoEleccion)) {
			numeroColumnasPref = this.utilSceService.obtenerCantidadCandidatos(schema, idActa);
		}

		if(!detActaRectangles.isEmpty()) {
			archivoRectAgrupol = findByAbreviatura(detActaRectangles, ConstantesSecciones.SECTION_ABREV_AGRUPOL_VOTOS);

			if(!ConstantesComunes.COD_ELEC_PRE.equals(tipoEleccion)) {
				archivoRectPref = findByAbreviatura(detActaRectangles, ConstantesSecciones.SECTION_ABREV_VOTO_PREF_COLUMNAS);
			}
		}
		
		List<DetActa> detActaList = detActaService.findByIdActaOrderByPosicion(idActa);
		
		List<DetActaCcResponse> detActaAgrupolList = getDetalleActa(detActaList);

		if(detActaAgrupolList.isEmpty()) {
			return new GenericResponse<>(false, "No se pudo obtener el detalle del acta.", null);
		}
		
		DataPaso3Response response = DataPaso3Response
									  .builder()
									  .imagenesAgrupol(archivoRectAgrupol == null ? null : archivoRectAgrupol.getValues().getBody().getFirst())
									  .imagenesPreferencial(archivoRectPref == null ? null : archivoRectPref.getValues().getBody().getFirst())
									  .numeroColumnasPref(numeroColumnasPref)
									  .cvas(acta.getCvas())
									  .sinFirmas(estadoResol != null && estadoResol.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA))
									  .solicitudNulidad(estadoResol != null && estadoResol.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD))
									  .detActaAgrupol(detActaAgrupolList)
									  .detActaPreferenciales(detalleVotosPreferenciales(detActaList, numeroColumnasPref))
									  .build();

		logger.info("Acta : " + acta.getMesa().getCodigo() + "-" + acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio() + " - idActa: " + idActa + " - ID de las imagenes del paso 3: " + response.toString());
		
		return new GenericResponse<>(true, "Se obtuvo la información del paso 3 correctamente.", response);		
	}

	@Override
	public ResolucionActaBean getHistorialResolucionAntesDespues(Long idActa, Long idResolucion) {

		Optional<Acta> optionalActa = this.actaService.findById(idActa);
		if (optionalActa.isEmpty()) {
			throw new BadRequestException(String.format("No existe el acta con id: %d.", idActa));
		}

		Optional<TabResolucion> optionalResolucion = this.resolucionService.findById(idResolucion);
		if (optionalResolucion.isEmpty()) {
			throw new BadRequestException(String.format("No existe la resolución con id: %d.", idResolucion));
		}

		CabCcResolucion resolucionAntes = this.cabCcResolucionService
				.findByActaAndResolucionAndEstadoCambioAndActivo(
						idActa, idResolucion, ConstantesComunes.ESTADO_CAMBIO_RESOL_ANTES, ConstantesComunes.ACTIVO)
				.orElse(null);

		CabCcResolucion resolucionDespues = this.cabCcResolucionService
				.findByActaAndResolucionAndEstadoCambioAndActivo(
						idActa, idResolucion, ConstantesComunes.ESTADO_CAMBIO_RESOL_DESPUES, ConstantesComunes.ACTIVO)
				.orElse(null);

		ResolucionActaBean resolucionActaBean = new ResolucionActaBean();
		
		if(resolucionAntes != null ) {
			resolucionActaBean.setActaAntes(construirActaBean(resolucionAntes));
		}
		
		if(resolucionDespues != null) {
			resolucionActaBean.setActaDespues(construirActaBean(resolucionDespues));
		}		

		return resolucionActaBean;
	}

	private ActaBean construirActaBean(CabCcResolucion cabCcResolucion) {

		ActaBean actaBean = new ActaBean();

		actaBean.setActaId(cabCcResolucion.getActa());
		actaBean.setEstadoActa(cabCcResolucion.getEstadoActa());
		actaBean.setEstadoComputo(cabCcResolucion.getEstadoCc());

		String cvasValue = cabCcResolucion.getCvas() != null ? cabCcResolucion.getCvas().toString() : null;
		if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(cabCcResolucion.getIlegibleCvas())) {
			cvasValue = ConstantesComunes.C_VALUE_ILEGIBLE;
		}
		actaBean.setCvas(cvasValue);

		String estadoResol = cabCcResolucion.getEstadoActaResolucion();
		actaBean.setEstadoResolucion(estadoResol);
		actaBean.setAgrupacionesPoliticas(construirListAgrupolBean(cabCcResolucion.getId()));
		actaBean.setSolicitudNulidad(estadoResol != null && estadoResol.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD));
		actaBean.setSinFirma(estadoResol != null && estadoResol.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA));

		return actaBean;
	}

	private List<AgrupolBean> construirListAgrupolBean(Long idCabCcResolucion) {
		return detCcResolucionRepository
				.findByCabCcResolucion_IdAndActivo(idCabCcResolucion, ConstantesComunes.ACTIVO)
				.stream()
				.sorted(Comparator.comparing(det ->
						det.getPosicion() != null ? det.getPosicion() : Integer.MAX_VALUE
				))
				.map(det -> {
					var bean = new AgrupolBean();
					bean.setIdAgrupol(det.getIdAgrupacionPolitica() != null ? det.getIdAgrupacionPolitica().longValue() : null);
					bean.setPosicion(det.getPosicion() != null ? det.getPosicion().longValue() : null);
					bean.setActivo(Objects.toString(det.getActivo(), null));
					bean.setVotos(Objects.toString(det.getVotos(), null));
					bean.setIlegible(det.getIlegible());
					bean.setEstado(det.getEstado());
					bean.setVotosPreferenciales(construirListVotoPreferencialBean(det.getId()));
					return bean;
				})
				.toList();
	}


	private List<VotoPreferencialBean> construirListVotoPreferencialBean(Long idDetCcResolucion) {
		return detCcPreferencialResolucionRepository
				.findByDetCcResolucion_IdAndActivo(idDetCcResolucion, ConstantesComunes.ACTIVO)
				.stream()
				.sorted(Comparator.comparing(item ->
						item.getLista() != null ? item.getLista() : Integer.MAX_VALUE
				))
				.map(item -> {
					var bean = new VotoPreferencialBean();
					bean.setLista(item.getLista());
					bean.setActivo(Objects.toString(item.getActivo(), null));
					bean.setVotos(Objects.toString(item.getVotos(), null));
					bean.setIlegible(item.getIlegible());
					bean.setEstado(item.getEstado());
					return bean;
				})
				.toList();
	}

	private List<List<DetActaCcResponse>> detalleVotosPreferenciales(List<DetActa> detActaList, Integer numeroColumnasPref) {
		List<List<DetActaCcResponse>> detActaPreferenciales = new ArrayList<>();
		List<DetActaCcResponse> detActaColumna;
		DetActaCcResponse votoColumna;
		if(numeroColumnasPref != null) {
			for(int i = 1; i <= numeroColumnasPref; i++) {
				detActaColumna = new ArrayList<>();
				for(DetActa detActa: detActaList) {
						votoColumna = DetActaCcResponse
								.builder()
								.id(detActa.getId())
								.idAgrupacion(detActa.getAgrupacionPolitica().getId())
								.estado(detActa.getEstado())
								.votos(votoPreferencialColumna(detActa.getPreferenciales(), i))
								.build();
						detActaColumna.add(votoColumna);
				}
				detActaPreferenciales.add(detActaColumna);
			}
		}
		
		return detActaPreferenciales;
	}
	
	private Long votoPreferencialColumna(Set<DetActaPreferencial> votospreferenciales, int columna) {
		for(DetActaPreferencial detActa: votospreferenciales) {
			if(detActa.getLista() == columna) {
				return detActa.getVotos();
			}
		}
		
		return null;		
	}
	
	private List<DetActaCcResponse> getDetalleActa(List<DetActa> detActaList) {
		if(detActaList == null) return Collections.emptyList();
		
		return 	detActaList
					.stream()
					.map(det -> DetActaCcResponse
							.builder()
							.id(det.getId())
							.idAgrupacion(det.getAgrupacionPolitica().getId())
							.posicion(det.getPosicion())
							.votos(det.getVotos())
							.estado(det.getEstado())
							.build()
					).toList();
	}
	
	private DetActaRectangleDTO findByAbreviatura(List<DetActaRectangleDTO> detActaRectangleDTOS, String abreviatura) {
	    if (detActaRectangleDTOS == null || abreviatura == null) {
	      return null;
	    }

	    return detActaRectangleDTOS.stream()
	        .filter(dto -> abreviatura.equals(dto.getAbreviatura()))
	        .findFirst()
	        .orElse(null);
	  }
	
	private Long getIdImagen(List<DetActaRectangleDTO> detActaRectangles, String abreviatura) {
		DetActaRectangleDTO detActa = findByAbreviatura(detActaRectangles, abreviatura);
		
		return detActa == null ? null : detActa.getArchivo();
	}
	
	@Override
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, String proceso, Long idDocumento, String tipoDocumento) {

        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        
        String tipoAutorizacion = tipoDocumento.equals(ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA) ? 
        		ConstantesAutorizacion.TIPO_AUTORIZACION_CONTROL_CALIDAD_ACTA : ConstantesAutorizacion.TIPO_AUTORIZACION_CONTROL_CALIDAD_RESOLUCION;
        
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(tipoAutorizacion);
        request.setTipoDocumento(tipoDocumento);
        request.setIdDocumento(idDocumento);
        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

        ResponseEntity<AutorizacionNacionResponseDto> response = this.clientExport.exchange(
                urlNacion + ConstantesComunes.URL_NACION_RECIBIR_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                AutorizacionNacionResponseDto.class);
        return response.getBody();
    }

    @Override
    public Boolean solicitaAutorizacion(String usuario, String cc, String proceso, Long idDocumento, String tipoDocumento, Long idActa, TokenInfo tokenInfo) {
        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        
        String tipoAutorizacion = tipoDocumento.equals(ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA) ? 
        		ConstantesAutorizacion.TIPO_AUTORIZACION_CONTROL_CALIDAD_ACTA : ConstantesAutorizacion.TIPO_AUTORIZACION_CONTROL_CALIDAD_RESOLUCION;
        
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(tipoAutorizacion);
        request.setTipoDocumento(tipoDocumento);
        request.setIdDocumento(idDocumento);
        
        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

        @SuppressWarnings("rawtypes")
		ResponseEntity<GenericResponse> response = this.clientExport.exchange(
                urlNacion + ConstantesComunes.URL_NACION_RECIBIR_SOLICITUD_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                GenericResponse.class);

        GenericResponse<?> body = response.getBody();
        boolean isSuccessful = body != null && body.isSuccess();
        if(isSuccessful){
            registrarLogControlCalidad(usuario, cc, tipoAutorizacion, tipoDocumento, idDocumento);
			if(tipoDocumento.equals(ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA)) {
				guardarAccionActa(idActa,
						tokenInfo.getCodigoCentroComputo(),
						tokenInfo.getNombreUsuario(),
						ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD_OBSERVAR, 24);
			} else {
				guardarAccionActa(idActa,
						tokenInfo.getCodigoCentroComputo(),
						tokenInfo.getNombreUsuario(),
						ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_CALIDAD_RECHAZAR, 22);
			}
        }
        return isSuccessful;
    }

    private void registrarLogControlCalidad(String usuario, String cc, String tipoAutorizacion, String tipoDocumento, Long idDocumento) {
        String mensaje = buildMensajeLog(usuario, tipoAutorizacion, tipoDocumento, idDocumento, cc);

        logService.registrarLog(
                usuario,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                mensaje,
                cc,
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                ConstantesComunes.LOG_TRANSACCIONES_ACCION
        );
    }

    private String buildMensajeLog(String usuario, String tipoAutorizacion, String tipoDocumento, Long idDocumento, String cc) {
        if(tipoAutorizacion.equals(ConstantesAutorizacion.TIPO_AUTORIZACION_CONTROL_CALIDAD_ACTA)){
            if (tipoDocumento.equals(ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA)) {
                return buildMensajeLogActa(usuario, idDocumento);
            }

            return buildMensajeLogResolucion(usuario, idDocumento);
        }else{
            return String.format("Solicitud del usuario %s para rechazar una resolución del centro de cómputo %s.",
                    usuario, cc);

        }

    }

    private String buildMensajeLogActa(String usuario, Long idDocumento) {
        Optional<Acta> optActa = this.actaService.findById(idDocumento);

        if (optActa.isEmpty()) {
            return "";
        }

        Acta acta = optActa.get();
        boolean tieneCopiaYDigito = acta.getNumeroCopia() != null && acta.getDigitoChequeoEscrutinio() != null;

        if (tieneCopiaYDigito) {
            return String.format("Solicitud del usuario %s para observar el acta %s-%s%s en el control de calidad.",
                    usuario,
                    acta.getMesa().getCodigo(),
                    acta.getNumeroCopia(),
                    acta.getDigitoChequeoEscrutinio());
        }

        return String.format("Solicitud del usuario %s para observar el acta %s en el control de calidad.",
                usuario,
                acta.getMesa().getCodigo());
    }

    private String buildMensajeLogResolucion(String usuario, Long idDocumento) {
        Optional<TabResolucion> optionalResolucion = this.resolucionService.findById(idDocumento);
        if (optionalResolucion.isEmpty()) {
            return "";
        }
        TabResolucion resolucion = optionalResolucion.get();
        return String.format("Solicitud del usuario %s para observar la resolución %s en el control de calidad.",
                usuario, resolucion.getNumeroResolucion());
    }


    private HttpHeaders getHeaderAutorizacion(String proceso){
        HttpHeaders headers = new HttpHeaders();
        headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
        headers.set(SceConstantes.TENANT_HEADER, proceso);
        headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
    
    @Override
	@Transactional
	public GenericResponse<Boolean> cancelarControlCalidad(List<Long> idsActas, TokenInfo tokenInfo) {
		this.actaService.desasignarUsuarioActaControlCalidad(idsActas, tokenInfo);
		
		return new GenericResponse<>(true, "Se canceló el control de calidad del acta correctamente.", Boolean.TRUE);
	}

	private void guardarAccionActa(Long idActa, String codigoCentroComputo ,String usuario, String tiempo, String accion, int orden) {
		Optional<Acta> optionalCabActa = this.actaServiceGroup.getCabActaService().findById(idActa);
		if (optionalCabActa.isPresent()) {
			DetActaAccion detActaAccion = DetActaAccion.builder()
					.fechaAccion(optionalCabActa.get().getFechaModificacion())
					.usuarioAccion(usuario)
					.codigoCentroComputo(codigoCentroComputo)
					.tiempo(tiempo)
					.activo(1)
					.accion(accion)
					.usuarioCreacion(usuario)
					.acta(optionalCabActa.get())
					.orden(orden)
					.build();
			this.actaServiceGroup.getDetActaAccionService().save(detActaAccion);
		}
	}

	public GenericResponse<List<AgrupacionPoliticaResponse>> agrupacionesPoliticas(Long idActa, String schema) {
		Optional<Acta> actaOptional = this.actaService.findById(idActa);
		if(actaOptional.isPresent()) {
			Acta acta = actaOptional.get();
			UbigeoEleccion ubiElec = acta.getUbigeoEleccion();
			List<Map<String, Object>> resultados = actaRepository.avanceMesaMesa(
					schema,
					ubiElec.getEleccion().getId().intValue(),
					ubiElec.getUbigeo().getAmbitoElectoral().getId().intValue(),
					ubiElec.getUbigeo().getCentroComputo().getId().intValue(),
					null,
					acta.getMesa().getCodigo(),
					"ControlCalidad"
			);

			return new GenericResponse<>(true,
					"Se obtuvo las agrupaciones políticas correctamente.",
					resultados.parallelStream()
							.map(this::mapearResponse)
							.sorted(Comparator
									.comparing(AgrupacionPoliticaResponse::getIdActa)
									.thenComparing(AgrupacionPoliticaResponse::getPosicion))
							.toList());
		}

		return null;

	}

	private AgrupacionPoliticaResponse mapearResponse(Map<String, Object> reporte) {
		AgrupacionPoliticaResponse mesaReporte = new AgrupacionPoliticaResponse();
		mesaReporte.setIdActa((Long) reporte.get("n_acta_pk"));
		mesaReporte.setId( Integer.parseInt(reporte.get("n_agrupacion_politica").toString()) );
		Short posi = (Short) reporte.get("n_posicion");
		mesaReporte.setPosicion(Integer.parseInt(posi.toString()));
		mesaReporte.setCodigo((String) reporte.get("c_codigo_organizacion_politica"));
		mesaReporte.setDescripcion((String) reporte.get("c_descripcion"));

		return mesaReporte;
	}
}
