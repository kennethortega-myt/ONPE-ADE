package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationRejectMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationSummaryResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.DigitizationListActasItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ActaScanProjection;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

@Service
public class CabActaCelesteServiceImpl implements CabActaCelesteService {

	Logger logger = LoggerFactory.getLogger(CabActaCelesteServiceImpl.class);
	private static final String STATUS_PENDIENTE = "Pendiente";
	private final ActaCelesteRepository actaCelesteRepository;
	private final ArchivoService archivoService;
	private final UsuarioService usuarioService;
	private final ITabLogService logService;

	public CabActaCelesteServiceImpl(
			ActaCelesteRepository actaCelesteRepository, 
			ArchivoService archivoService,
			UsuarioService usuarioService,
			ITabLogService logService) {
		this.actaCelesteRepository = actaCelesteRepository;
		this.archivoService = archivoService;
		this.usuarioService = usuarioService;
		this.logService = logService;
	}

	@Override
	public DigitizationSummaryResponse summaryCeleste(String codigoEleccion) {

		DigitizationSummaryResponse ans = new DigitizationSummaryResponse();
		Object[] outerResult = this.actaCelesteRepository.getDigitalizationSummary(codigoEleccion);

		if (outerResult == null || outerResult.length == 0 || !(outerResult[0] instanceof Object[] innerResult)) {
			return new DigitizationSummaryResponse(0, 0, 0,0,0);
		}

		ans.setPending(Objects.isNull(innerResult[0]) ? 0 : ((Long) innerResult[0]).intValue());
		ans.setApproved(Objects.isNull(innerResult[1]) ? 0 : ((Long) innerResult[1]).intValue());
		ans.setRejected(Objects.isNull(innerResult[2]) ? 0 : ((Long) innerResult[2]).intValue());
		return ans;
	}

	@Override
	@Transactional
	public List<DigitizationListActasItem> listActasCeleste(String codigoEleccion, String usuario, String status,
			int offset, int limit) {
		List<DigitizationListActasItem> listActasResponse = new ArrayList<>();

		List<ActaCeleste> listActasDigitalizadasAsignadasActual = getActasDigitalizadasAAsignar(usuario,
				codigoEleccion);

		for (ActaCeleste cabActa : listActasDigitalizadasAsignadasActual) {
			if (cabActa.getAsignado() == null || cabActa.getAsignado() == 0) {
				Date fecha = new Date();
				guardarAsignacionActaListActas(cabActa, usuario, fecha);
			}
			listActasResponse.add(createDigitizationListActasItem(cabActa));
		}
		return listActasResponse;
	}

	private List<ActaCeleste> getActasDigitalizadasAAsignar(String usuario, String codigoEleccion) {
		List<ActaCeleste> listActasTotalesAsignadas = this.actaCelesteRepository
				.findByEstadoDigitalizacionAndUsuarioAsignado(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA, usuario);
		List<ActaCeleste> listActasTotalesAsignadasPorEleccion = listActasTotalesAsignadas.stream()
				.filter(ac -> ac.getActa().getUbigeoEleccion().getEleccion().getCodigo().equals(codigoEleccion))
				.toList();

		// Si no tiene actas asignadas o son insuficientes, completar hasta el límite
		if (listActasTotalesAsignadasPorEleccion.isEmpty() || listActasTotalesAsignadasPorEleccion.size() < ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION) {
			int nuevaDistribucion = ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION - listActasTotalesAsignadasPorEleccion.size();

			List<ActaCeleste> listActasDigitalizadasLibres = this.actaCelesteRepository
					.findByEstadoDigitalizacionAndUsuarioAsignadoIsNull(
							ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);

			List<ActaCeleste> listActasDigitalizadasLibresPorEleccion = new ArrayList<>(listActasDigitalizadasLibres
					.stream()
					.filter(ac -> ac.getActa().getUbigeoEleccion().getEleccion().getCodigo().equals(codigoEleccion))
					.toList());

			Collections.shuffle(listActasDigitalizadasLibresPorEleccion);

			List<ActaCeleste> actasAdicionales = listActasDigitalizadasLibresPorEleccion.stream()
					.limit(nuevaDistribucion)
					.toList();

			actualizarActasAsignadasListActas(usuario, actasAdicionales);

			// Crear nueva lista combinada
			List<ActaCeleste> listaCombinada = new ArrayList<>(listActasTotalesAsignadasPorEleccion);
			listaCombinada.addAll(actasAdicionales);
			
			return listaCombinada;
		}

		return listActasTotalesAsignadasPorEleccion;
	}

	@Override
	@Transactional
	public void approveMesa(DigitizationApproveMesaRequest request, String usuario, String proceso, String cc) {

		try {
			Optional<ActaCeleste> optionalActa = this.actaCelesteRepository.findById(request.getActaId());

			if (optionalActa.isEmpty()) {
				throw new BadRequestException("Acta no encontrada");
			}

			ActaCeleste acta = optionalActa.get();

			validaRequestApproveMesa(acta, request);

			Date fecha = new Date();

			actualizarActaApproveMesa(acta, usuario, fecha);

			actualizarActasUsuario(usuario);
			
			this.logService.registrarLog(
					usuario,
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					String.format("El acta celeste %s fue aprobada en control de digitalización por el usuario %s.", SceUtils.getNumMesaAndCopia(acta), usuario),
					cc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			throw e;
		}
	}

	@Override
	@Transactional
	public void rejectActa(String codigoEleccion, DigitizationRejectMesaRequest request, TokenInfo tokenInfo) {

		Optional<ActaCeleste> optionalCabActa = this.actaCelesteRepository.findById(request.getActaId());
		if (optionalCabActa.isEmpty()) {
		  logger.error(ConstantesComunes.MENSAJE_LOG_ERROR_ACTA_NO_EXISTE_PARA_ELECION, request.getActaId());
		  return;
		}
		ActaCeleste acta = optionalCabActa.get();

		Archivo archivoAE = acta.getArchivoEscrutinio();
		Archivo archivoAIS = acta.getArchivoInstalacionSufragio();
		if(archivoAE!=null){
		  archivoAE.setActivo(ConstantesComunes.INACTIVO);
		  this.archivoService.save(archivoAIS);
		}
		if(archivoAIS!=null){
		  archivoAIS.setActivo(ConstantesComunes.INACTIVO);
		  this.archivoService.save(archivoAIS);
		}

		acta.setDigitalizacionEscrutinio(0L);
		acta.setDigitalizacionInstalacion(0L);
		acta.setDigitalizacionSufragio(0L);
		acta.setDigitalizacionInstalacionSufragio(0L);
		acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA);
		acta.setFechaModificacion(new Date());
		acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
		acta.setAsignado(null);
		acta.setUsuarioAsignado(null);
		acta.setArchivoEscrutinio(null);
		acta.setArchivoInstalacionSufragio(null);
		this.actaCelesteRepository.save(acta);
		Usuario tabUsuario = this.usuarioService.findByUsername(tokenInfo.getNombreUsuario());
		tabUsuario.setActasAsignadas((tabUsuario.getActasAsignadas() == null ? 0 : tabUsuario.getActasAsignadas()) - 1);
		tabUsuario.setActasAtendidas((tabUsuario.getActasAtendidas() == null ? 0 : tabUsuario.getActasAtendidas()) + 1);
		this.usuarioService.save(tabUsuario);

		this.logService.registrarLog(
				tokenInfo.getNombreUsuario(),
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				String.format("Se rechazó el acta celeste %s, en control de digitalización de actas por el usuario %s.", SceUtils.getNumMesaAndCopia(acta), tokenInfo.getNombreUsuario()),
				tokenInfo.getCodigoCentroComputo(),
				ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
				1
		);

	}

	private void validaRequestApproveMesa(ActaCeleste acta, DigitizationApproveMesaRequest request) {
		if (request.getEstado() != null) {
			if (acta.getArchivoEscrutinio() == null || acta.getArchivoInstalacionSufragio() == null) {
				throw new BadRequestException("Acta incompleta");
			}

			if (!acta.getArchivoEscrutinio().getId().equals(request.getFileId1())
					|| !acta.getArchivoInstalacionSufragio().getId().equals(request.getFileId2())) {
				throw new BadRequestException("El estado de la acta ha cambiado, por favor recargue la página");
			}

			if (!acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA)) {
				throw new BadRequestException("Acta no está pendiente de aprobacion");
			}
		} else {
			if (!acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA)) {
				throw new BadRequestException("Acta no está pendiente de aprobacion");
			}
		}
	}

	private void actualizarActasAsignadasListActas(String usuario,
			List<ActaCeleste> listActasDigitalizadasAsignadasNuevas) {
		Usuario tabUsuario = this.usuarioService.findByUsername(usuario);
		if(tabUsuario!=null){
			int asignadasActual = tabUsuario.getActasAsignadas() == null ? 0 : tabUsuario.getActasAsignadas();
			tabUsuario.setActasAsignadas(asignadasActual + listActasDigitalizadasAsignadasNuevas.size());
			this.usuarioService.save(tabUsuario);
		}
	}
	
	private void actualizarActasUsuario(String usuario) {
	    Usuario tabUsuario = this.usuarioService.findByUsername(usuario);
		if(tabUsuario!=null){
			tabUsuario.setActasAsignadas((tabUsuario.getActasAsignadas() == null ? 0 : tabUsuario.getActasAsignadas()) - 1);
			tabUsuario.setActasAtendidas((tabUsuario.getActasAtendidas() == null ? 0 : tabUsuario.getActasAtendidas()) + 1);
			this.usuarioService.save(tabUsuario);
		}
	  }

	private void guardarAsignacionActaListActas(ActaCeleste cabActa, String usuario, Date fecha) {
		cabActa.setAsignado(1);
		cabActa.setUsuarioAsignado(usuario);
		cabActa.setUsuarioModificacion(usuario);
		cabActa.setFechaModificacion(fecha);
		this.actaCelesteRepository.save(cabActa);
	}

	private void actualizarActaApproveMesa(ActaCeleste acta, String usuario, Date fecha) {
		acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
		acta.setUsuarioModificacion(usuario);
		acta.setFechaModificacion(fecha);
		this.actaCelesteRepository.save(acta);

	}

	DigitizationListActasItem createDigitizationListActasItem(ActaCeleste cabActa) {
		DigitizationListActasItem item = new DigitizationListActasItem();
		item.setActaId(cabActa.getId());
		item.setMesa(cabActa.getActa().getMesa().getCodigo());
		item.setEstado(cabActa.getEstadoDigitalizacion());
		item.setFecha(cabActa.getFechaCreacion());
		item.setDigitalizacionEscrutinio(cabActa.getDigitalizacionEscrutinio());
		item.setObservacionDigtalAe(cabActa.getObservDigEscrutinio());
		item.setDigitalizacionInstalacionSufragio(cabActa.getDigitalizacionInstalacionSufragio());
		item.setObservacionDigtalAis(cabActa.getObservDigInstalacionSufragio());

		applyArchivo(item::setActa1FileId, item::setActa1Status,
				cabActa.getArchivoEscrutinio(),
				cabActa.getDigitalizacionEscrutinio());

		applyArchivo(item::setActa2FileId, item::setActa2Status,
				cabActa.getArchivoInstalacionSufragio(),
				cabActa.getDigitalizacionInstalacionSufragio()) ;

		String mesa = item.getMesa() + ConstantesComunes.GUION_MEDIO;
		String copia = cabActa.getNumeroCopia() != null ? cabActa.getNumeroCopia() : "";
		item.setNroActaEscrutinio(mesa + copia + (cabActa.getDigitoChequeoEscrutinio() != null ? cabActa.getDigitoChequeoEscrutinio() : ""));
		item.setNroActaInstalacionSufragio(mesa + copia + (cabActa.getDigitoChequeoInstalacion() != null ? cabActa.getDigitoChequeoInstalacion() : ""));

		return item;
	}

	private void applyArchivo(LongConsumer setFileId, Consumer<String> setStatus,
														Archivo archivo, Long digitalizacion) {
		if (archivo != null) {
			setFileId.accept(archivo.getId());
			setStatus.accept(this.mapNDigitalizacion(digitalizacion));
		} else {
			setStatus.accept(STATUS_PENDIENTE);
		}
	}

	private String mapNDigitalizacion(Long n) {
		if (n == null) {
			return ConstantesComunes.TEXT_DIGITALIZACION_REDIGITALIZAR;
		}

		if (n.equals(ConstantesComunes.N_DIGITALIZACION_VALIDADO_AUTOMATICAMENTE)) {
			return ConstantesComunes.TEXT_DIGITALIZACION_VALIDADO_AUTOMATICAMENTE;
		}

		if (n.equals(ConstantesComunes.N_DIGITALIZACION_PROCESAMIENTO_MANUAL)) {
			return ConstantesComunes.TEXT_DIGITALIZACION_PROCESAMIENTO_MANUAL;
		}

		if (n.equals(ConstantesComunes.N_DIGITALIZACION_REDIGITALIZAR)
				|| n.equals(ConstantesComunes.N_DIGITALIZACION_REDIGITALIZAR_2)
				|| n.equals(ConstantesComunes.N_DIGITALIZACION_REDIGITALIZAR_4)) {
			return ConstantesComunes.TEXT_DIGITALIZACION_REDIGITALIZAR;
		}

		return n.toString();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<ActaCeleste> findById(Long id) {
	  return this.actaCelesteRepository.findById(id);
	}


	/**
	 * Parametro estadoDigitalizacion sea
	 * D -> c_estado_digitalizacion = 'D'
	 * O -> c_estado_digitalizacion in ('O','X')
	 * C -> c_estado_digitalizacion in ('K','C','B')
	 * P -> c_estado_digitalizacion = 'P'
	 * */
	@Override
	public List<ActaScanProjection> listActasCelesteSceScanner(String codigoEleccion, String estadoDigitalizacion) {
		logger.info("Iniciando listActasCeslesteSceScanner - codigoEleccion: {}, estadoDigitalizacion: {}", codigoEleccion, estadoDigitalizacion);

		List<String> estados = new ArrayList<>();

		if (estadoDigitalizacion == null || estadoDigitalizacion.isEmpty()) {
			logger.info("Estado digitalizacion vacío, agregando todos los estados");
			// Si está vacío, agregar todos los estados
			estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
			estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);
			estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO);
			estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
			estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA);
			estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA);
			estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA);
		} else {
			switch (estadoDigitalizacion) {
				case ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA:
					logger.info("Filtrando por estado DIGITALIZADA");
					estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);
					break;
				case ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA:
					logger.info("Filtrando por estado 1ER_CONTROL_RECHAZADA y 1ERA_DIGITACION_RECHAZADA");
					estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA);
					estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA);
					break;
				case ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA:
					logger.info("Filtrando por estado 1ER_CONTROL_ACEPTADA, REVISADA_1ER_CC y 2DO_CONTROL_ACEPTADA");
					estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
					estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO);
					estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA);
					break;
				case ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION:
					logger.info("Filtrando por estado PENDIENTE_DIGITALIZACION");
					estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
					break;
				default:
					logger.info("Estado no reconocido, usando valor directo: {}", estadoDigitalizacion);
					estados.add(estadoDigitalizacion);
			}
		}

		logger.info("Estados a filtrar: {}", estados);

		List<ActaScanProjection> resultado = this.actaCelesteRepository.findActasCelesteSceScanenr(codigoEleccion, estados);

		logger.info("Total de actas encontradas: {}", resultado.size());


		return resultado;
	}

	@Override
	public void save(ActaCeleste acta) {
		this.actaCelesteRepository.save(acta);

	}

	@Override
	public void saveAll(List<ActaCeleste> k) {
		this.actaCelesteRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.actaCelesteRepository.deleteAll();
	}

	@Override
	public List<ActaCeleste> findAll() {
		return this.actaCelesteRepository.findAll();
	}
}
