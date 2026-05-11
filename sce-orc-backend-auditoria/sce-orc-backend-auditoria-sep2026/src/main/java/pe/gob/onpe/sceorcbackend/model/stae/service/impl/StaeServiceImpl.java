package pe.gob.onpe.sceorcbackend.model.stae.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import pe.gob.onpe.sceorcbackend.model.dto.queue.NewActaStae;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.*;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;
import pe.gob.onpe.sceorcbackend.model.stae.dto.ActaElectoralRequestDto;
import pe.gob.onpe.sceorcbackend.model.stae.dto.DocumentoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.stae.dto.DocumentoElectoralRequest;
import pe.gob.onpe.sceorcbackend.model.stae.dto.ResultadoPs;
import pe.gob.onpe.sceorcbackend.model.stae.service.StaeService;
import pe.gob.onpe.sceorcbackend.utils.*;

@Service
public class StaeServiceImpl implements StaeService {

	Logger logger = LoggerFactory.getLogger(StaeServiceImpl.class);
	
	private final ActaRepository actaRepository;
	
	private final ArchivoRepository archivoRepository;
	
	private final CentroComputoRepository centroComputoRepository;
	
	private final StaeUtils staeUtils;
	
	private final RabbitMqSender rabbitMqSender;
	
	private final EntityManager entityManager;
	
	private static final String PO_MENSAJE = "po_mensaje";
	
	private static final String PO_ESTADO_ACTA = "po_estado_acta";
    private static final String PO_ESTADO_ACTA_RESOLUCION = "po_estado_acta_resolucion";
    private static final String PO_ESTADO_COMPUTO = "po_estado_computo";
    private static final String PO_ESTADO_ERROR_MATERIAL = "po_estado_error_material";
    private static final String PO_TIPO_TRANSMISION = "po_tipo_transmision";
	
	@Value("${file.upload-dir}")
    private String archivoPath;

	@Value("${file.upload-subcarpeta:}")
    private String uploadSubcarpeta;
	
	public StaeServiceImpl(
			ActaRepository actaRepository,
			ArchivoRepository archivoRepository,
			CentroComputoRepository centroComputoRepository,
			StaeUtils staeUtils,
			RabbitMqSender rabbitMqSender,
			EntityManager entityManager) {
		this.actaRepository = actaRepository;
		this.archivoRepository = archivoRepository;
		this.staeUtils = staeUtils;
		this.centroComputoRepository = centroComputoRepository;
		this.rabbitMqSender = rabbitMqSender;
		this.entityManager = entityManager;
	}
	
	@Override
	@Transactional
	public ResultadoPs insertActaStae(String piEsquema, boolean esDesarrollo, String piActa, String usuario) {
		Integer poResultado = SceConstantes.INACTIVO;
		String poMensaje = "";
		String estadoActa = "";
		String estadoActaResolucion = "";
		String estadoComputo = "";
		String estadoErrorMaterial = "";
		Integer tipoTransmision = 0;
		Map<String, Object> resultadopc = actaRepository.insertActaStae(
				piEsquema, 
				piActa, 
				usuario, 
				esDesarrollo, 
				poResultado, 
				poMensaje,
				estadoActa,
				estadoActaResolucion,
				estadoComputo,
				estadoErrorMaterial,
				tipoTransmision);
		if (resultadopc != null) {
			Object resultadoPs = resultadopc.get("po_resultado");
			poResultado = (resultadoPs != null && resultadoPs.toString().equals(SceConstantes.ACTIVO.toString()))
					? SceConstantes.ACTIVO
					: SceConstantes.INACTIVO;

			poMensaje = resultadopc.get(PO_MENSAJE) != null ? resultadopc.get(PO_MENSAJE).toString() : "";
			
			if(poResultado.equals(SceConstantes.ACTIVO)){
				estadoActa = resultadopc.get(PO_ESTADO_ACTA)!=null ? resultadopc.get(PO_ESTADO_ACTA).toString() : null;
				estadoActaResolucion = resultadopc.get(PO_ESTADO_ACTA_RESOLUCION)!=null ? resultadopc.get(PO_ESTADO_ACTA_RESOLUCION).toString() : null;
				estadoComputo = resultadopc.get(PO_ESTADO_COMPUTO)!=null ? resultadopc.get(PO_ESTADO_COMPUTO).toString() : null;
				estadoErrorMaterial = resultadopc.get(PO_ESTADO_ERROR_MATERIAL)!=null ? resultadopc.get(PO_ESTADO_ERROR_MATERIAL).toString() : null;
				tipoTransmision = resultadopc.get(PO_TIPO_TRANSMISION)!=null ? Integer.parseInt(resultadopc.get(PO_TIPO_TRANSMISION).toString()) : null;
				entityManager.clear();
			}
			
			logger.info("resultado de la ps del registro de actas STAE: {}", resultadoPs);
			logger.info("mensaje final de la ps del registro de actas STAE: {}", resultadopc.get("po_mensaje"));
		}
		
		return ResultadoPs
				.builder()
				.poResultado(poResultado)
				.poMensaje(poMensaje)
				.poEstadoActa(estadoActa)
				.poEstadoComputo(estadoComputo)
				.poEstadoActaResolucion(estadoActaResolucion)
				.poEstadoErrorMaterial(estadoErrorMaterial)
				.poTipoTransmision(tipoTransmision)
				.build();
	}

	@Override
	@Transactional
	public ResultadoPs insertListaElectoresStae(String piEsquema, boolean esDesarrollo, String piLe, String usuario) {
		Integer poResultado = SceConstantes.INACTIVO;
		String poMensaje = "";
		Map<String, Object> resultadopc = actaRepository.insertListaElectoresStae(piEsquema, piLe, usuario, esDesarrollo, poResultado, poMensaje);
		if (resultadopc != null) {
			Object resultadoPs = resultadopc.get("po_resultado");
			poResultado = (resultadoPs != null && resultadoPs.toString().equals(SceConstantes.ACTIVO.toString()))
					? SceConstantes.ACTIVO
					: SceConstantes.INACTIVO;

			poMensaje = resultadopc.get(PO_MENSAJE) != null ? resultadopc.get(PO_MENSAJE).toString() : "";
			logger.info("resultado de la ps del registro de lista electores STAE: {}", resultadoPs);
			logger.info("mensaje final de la ps del registro de lista electores STAE: {}", resultadopc.get("po_mensaje"));
			entityManager.clear();
		}
		
		return ResultadoPs
				.builder()
				.poResultado(poResultado)
				.poMensaje(poMensaje)
				.build();
		
	}
	
	@Override
	@Transactional
	public void guardarDocumentosElectorales(DocumentoElectoralRequest request, String usuario){
		Archivo archivoEscrutinio = null;
		Archivo archivoInstalacion = null;
		Archivo archivoSufragio = null;
	
		Optional<Acta> actaOp = actaRepository.findByNumeroMesaAndEleccion(request.getCodigoMesa(), request.getIdEleccion());
		if(actaOp.isPresent()){
			Acta acta = actaOp.get();
			for(DocumentoElectoralDto documento:request.getDocumentos()){
				if(documento.getTipoDocumentoElectoral()!=null 
						&& documento.getTipoDocumentoElectoral().equals(ConstantesTipoDocumentoElectoral.ACTA_DE_ESCRUTINIO)){
					archivoEscrutinio = this.guardarArchivo(documento, usuario);
					acta.setArchivoEscrutinioFirmado(archivoEscrutinio);
				}else if(documento.getTipoDocumentoElectoral()!=null 
						&& documento.getTipoDocumentoElectoral().equals(ConstantesTipoDocumentoElectoral.ACTA_INSTALACION)){
					archivoInstalacion = this.guardarArchivo(documento, usuario);
					acta.setArchivoInstalacionFirmado(archivoInstalacion);
				}else if(documento.getTipoDocumentoElectoral()!=null 
						&& documento.getTipoDocumentoElectoral().equals(ConstantesTipoDocumentoElectoral.ACTA_SUFRAGIO)){
					archivoSufragio = this.guardarArchivo(documento, usuario);
					acta.setArchivoSufragioFirmado(archivoSufragio);
				}
			}
			acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
			actaRepository.save(actaOp.get());
		}
		
	}
	
	@Override
	@Transactional
	public void guardarDocumentosElectorales(Long idActa, List<DocumentoElectoralDto> documentos, String usuario) {
		if (documentos != null) {
			Archivo archivoEscrutinio = null;
			Archivo archivoInstalacion = null;
			Archivo archivoSufragio = null;
			for (DocumentoElectoralDto documento : documentos) {
				if (documento.getTipoDocumentoElectoral() != null && documento.getTipoDocumentoElectoral()
						.equals(ConstantesTipoDocumentoElectoral.ACTA_DE_ESCRUTINIO)) {
					archivoEscrutinio = this.guardarArchivo(documento, usuario);
				} else if (documento.getTipoDocumentoElectoral() != null && documento.getTipoDocumentoElectoral()
						.equals(ConstantesTipoDocumentoElectoral.ACTA_INSTALACION)) {
					archivoInstalacion = this.guardarArchivo(documento, usuario);
				} else if (documento.getTipoDocumentoElectoral() != null && documento.getTipoDocumentoElectoral()
						.equals(ConstantesTipoDocumentoElectoral.ACTA_SUFRAGIO)) {
					archivoSufragio = this.guardarArchivo(documento, usuario);
				}
			}
			actaRepository.actualizarArchivosActa(
					idActa, 
					(archivoEscrutinio!=null && archivoEscrutinio.getId()!=null) ? archivoEscrutinio.getId() : null, 
					(archivoInstalacion!=null && archivoInstalacion.getId()!=null) ? archivoInstalacion.getId() : null,
					(archivoSufragio!=null && archivoSufragio.getId()!=null) ? archivoSufragio.getId() : null, 
					ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
		}

	}
	
	private Archivo guardarArchivo(DocumentoElectoralDto documento, String usuario){
		Archivo archivo = this.generarArchivo(documento, usuario);
		if(archivo!=null){
			this.archivoRepository.save(archivo);
		}
		return archivo;
	}
	
	private Archivo generarArchivo(
			DocumentoElectoralDto documentoElectoralDto,  
			String usuario) {
		Archivo documentoElectoral = null;
		try {
			if (documentoElectoralDto.getBase64() != null
					&& documentoElectoralDto.getGuid() != null) {
				Optional<Archivo> existFile = archivoRepository.findByGuid(documentoElectoralDto.getGuid());
				if (!existFile.isPresent()) {
					documentoElectoral = this.staeUtils.createArchivo(documentoElectoralDto, this.getFullUploadPath(), false);
					documentoElectoral.setActivo(SceConstantes.ACTIVO);
					documentoElectoral.setFechaCreacion(new Date());
					documentoElectoral.setUsuarioCreacion(usuario);
					documentoElectoral.setCodigoDocumentoElectoral(documentoElectoralDto.getTipoDocumentoElectoral());
				} else {
					logger.info("El archivo de escrutinio {} ya existe", documentoElectoralDto.getGuid());
				}
			} else {
				logger.info("No se guardo el archivo de escrutinio debido a que no se encontro el archivo en base 64");
			}
		} catch (Exception e) {
			logger.error("Se genero un error al guardar el archivo", e);
		}
		return documentoElectoral;
	}

	@Override
	public boolean validarTokenStae(String tokenBearer, String cc) {
		boolean exitoso = false;
		if (tokenBearer != null && tokenBearer.startsWith("Bearer ")) {
            String tokenIn = tokenBearer.substring(7); // Elimina el prefijo "Bearer "
            Optional<CentroComputo> cep = centroComputoRepository.findByCodigo(cc);
            if(cep.isPresent()){
            	String tokenSaved = cep.get().getApiTokenBackedCc();
            	return tokenIn.equals(tokenSaved);
            }
		}
		return exitoso;
	}
	
	
	@Override
	public void sendProcessActaStae(
			Acta acta,
			String codUsuario,
			String codCentroComput
			){

		try{
			
			logger.info("Se inicia el envio a la cola sce-queue-process-acta-stae, el acta {}", acta.getId());
			
			this.rabbitMqSender.sendProcessActaStae(
					NewActaStae
					.builder()
					.actaId(acta.getId())
					.codUsuario(codUsuario)
					.codCentroComputo(codCentroComput)
					.build());
			
			logger.info("Se finaliza el envio a la cola sce-queue-process-acta-stae, el acta {}", acta.getId());
			
		} catch (Exception e) {
			logger.error("Error al enviar a cola sce-queue-process-acta-stae", e);
		} // end send sce-queue-process-acta-stae
		
		
	}

	private String getFullUploadPath() {
		if (uploadSubcarpeta == null || uploadSubcarpeta.isEmpty()) {
			return archivoPath;
		}
		Path fullPath = Paths.get(archivoPath).resolve(uploadSubcarpeta);
		if (!Files.exists(fullPath)) {
			try {
				Files.createDirectories(fullPath);
				logger.info("Subcarpeta creada: {}", fullPath);
			} catch (IOException e) {
				logger.error("Error creando subcarpeta {}: {}", fullPath, e.getMessage());
			}
		}
		return fullPath.toString() + File.separator;
	}
	
	@Override
	@Transactional
	public Optional<Acta> getActa(ActaElectoralRequestDto actaDto){
		return actaRepository.findByNumeroMesaAndEleccion(actaDto.getNumeroActa(), actaDto.getEleccion());
	}

}
