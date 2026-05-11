package pe.gob.onpe.scebackend.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.exeption.BadRequestException;
import pe.gob.onpe.scebackend.model.stae.dto.ActaElectoralRequestDto;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaDto;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaPreferencialDto;
import pe.gob.onpe.scebackend.model.stae.dto.files.DocumentoElectoralDto;
import pe.gob.onpe.scebackend.model.stae.utils.ConsultaErroresMateriales;
import pe.gob.onpe.scebackend.model.stae.wrapper.ErroresActaWrapper;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesEstadoActa;

@Component
public class StaeUtils {
	
	Logger logger = LoggerFactory.getLogger(StaeUtils.class);
	
	private static final String PDF_EXTENSION = "pdf";
	private static final String MIME_TYPE = "application/pdf";

	public DocumentoElectoralDto getDocumentoElectoralDto(String rutaAbsoluta, String guid, Integer tipoDocumentoElectoral) {
		DocumentoElectoralDto dto = null;
				try {
					String base64 = convertToBase64(rutaAbsoluta);
					dto = DocumentoElectoralDto
							.builder()
							.base64(base64)
							.mimeType(MIME_TYPE)
							.guid(guid)
							.extension(PDF_EXTENSION)
							.tipoDocumentoElectoral(tipoDocumentoElectoral)
							.build();
				} catch (Exception e) {
					logger.error("error", e);
					return null;
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


	public void completarEstadoActaComputoResolucionErrorAritmetico(ActaElectoralRequestDto actaElectoralRequestDto, Integer cantidadElectoresHabiles) {

		try{
			actaElectoralRequestDto.setEstadoActa(null);
			actaElectoralRequestDto.setEstadoErrorAritmetico(null);
			actaElectoralRequestDto.setEstadoCompu(null);
			actaElectoralRequestDto.setEstadoActaResolucion(null);

			List<DetalleActaDto> detActaListToErrores = new ArrayList<>();
			List<DetalleActaPreferencialDto> detActaPreferencialListToErrores = new ArrayList<>();
			String codigoEleccion = actaElectoralRequestDto.getEleccion().toString();
			Integer totalVotosCalculados = getTotalVotosAgrupacionesPoliticas(actaElectoralRequestDto.getDetalleActa());
			actaElectoralRequestDto.setVotosCalculados(totalVotosCalculados);
			ErroresActaWrapper erroresWrapper = new ErroresActaWrapper(detActaListToErrores, detActaPreferencialListToErrores);
			registrarActaAgrupacionespoliticas(codigoEleccion, actaElectoralRequestDto, erroresWrapper, cantidadElectoresHabiles);
			registrarActaObservaciones(actaElectoralRequestDto);

			if (!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
				actaElectoralRequestDto.setEstadoErrorAritmetico(ConsultaErroresMateriales.getErrMatANivelDeActa(actaElectoralRequestDto, totalVotosCalculados, cantidadElectoresHabiles));
				if(actaElectoralRequestDto.getEstadoErrorAritmetico()!=null){
					SceUtils.agregarEstadoResolucion(actaElectoralRequestDto, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT);
				}
				SceUtils.guardarVeriConvencionalEstadoResolucionErrorMaterialAgrupol(actaElectoralRequestDto, erroresWrapper.getDetActaListToErrores());
				if(ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion))
					SceUtils.guardarVeriConvencionalEstadoResolucionErrorMaterialPreferencial(actaElectoralRequestDto, erroresWrapper.getDetActaPreferencialListToErrores());
			}

			registrarActaPorCorregirEstadoActaCompu(actaElectoralRequestDto);
		} catch (Exception e) {
			logger.error("Error:",e);
			throw new BadRequestException("Error en el procedimiento de seteo de actas.");
		}

	}

	private void registrarActaPorCorregirEstadoActaCompu(ActaElectoralRequestDto actaElectoralRequestDto) {
		if (actaElectoralRequestDto.getEstadoActaResolucion() == null || actaElectoralRequestDto.getEstadoActaResolucion().isEmpty()) {
			actaElectoralRequestDto.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA);
			actaElectoralRequestDto.setEstadoCompu(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
		} else {
			actaElectoralRequestDto.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO);
			actaElectoralRequestDto.setEstadoCompu(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
		}
	}

	private void registrarActaObservaciones(ActaElectoralRequestDto actaElectoralRequestDto) {
		if(actaElectoralRequestDto.getFlagNulidad().equals(1)){
			SceUtils.agregarEstadoResolucion(actaElectoralRequestDto, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
		}else{
			SceUtils.removerEstadoResolucion(actaElectoralRequestDto, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
		}


	}

	private void registrarActaAgrupacionespoliticas(String codigoEleccion,
													ActaElectoralRequestDto actaElectoralRequestDto,
													ErroresActaWrapper erroresWrapper,
													Integer cantidadElectoresHabiles) {

		for (DetalleActaDto detalleActaDto : actaElectoralRequestDto.getDetalleActa()) {
			if (detalleActaDto.getVotos() == null)
				detalleActaDto.setVotos(0);
			if (!detalleActaDto.getEstadoAgrupacionPolitica().equals(ConstantesComunes.N_ACHURADO)) { // No considera achurados
				procesarAgrupacion(codigoEleccion, detalleActaDto, actaElectoralRequestDto, erroresWrapper, cantidadElectoresHabiles);
			}
		}

	}

	private void procesarAgrupacion(String codigoEleccion,
									DetalleActaDto detalleActaDto,
									ActaElectoralRequestDto actaElectoralRequestDto,
									ErroresActaWrapper erroresWrapper,
									Integer cantidadElectoresHabiles) {


		if (!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
			//paso Ilegible, no existe para stae
			verificarVotosImpugnados(detalleActaDto, actaElectoralRequestDto);
			Integer totalVotosPreferenciales = procesarVotosPreferenciales(codigoEleccion, actaElectoralRequestDto, detalleActaDto, erroresWrapper.getDetActaPreferencialListToErrores(), cantidadElectoresHabiles);
			detalleActaDto.setEstadoErrorAritmetico(ConsultaErroresMateriales.getDetErrorMaterialAgrupol(actaElectoralRequestDto, detalleActaDto, totalVotosPreferenciales, codigoEleccion, cantidadElectoresHabiles));

			if(detalleActaDto.getEstadoErrorAritmetico()!=null && !detalleActaDto.getEstadoErrorAritmetico().isEmpty()){
				SceUtils.agregarEstadoResolucion(actaElectoralRequestDto, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
			}
		}

		erroresWrapper.getDetActaListToErrores().add(detalleActaDto);

	}

	private Integer procesarVotosPreferenciales(String codigoEleccion,
											 ActaElectoralRequestDto actaElectoralRequestDto,
											 DetalleActaDto detalleActaDto,
											 List<DetalleActaPreferencialDto> detActaPreferencialListToErrores,
											 Integer cantidadElectoresHabiles) {

		Integer totalVotosPreferencialesPorAgrupacion = 0;
		if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
			for (DetalleActaPreferencialDto votoPreferencial : detalleActaDto.getDetalleActaPreferencial()) {
				procesarVotoPreferencial(votoPreferencial, actaElectoralRequestDto, detalleActaDto, detActaPreferencialListToErrores, cantidadElectoresHabiles);
				totalVotosPreferencialesPorAgrupacion += obtenerTotalVotosPreferenciales(votoPreferencial);
			}
		}
		return totalVotosPreferencialesPorAgrupacion;

	}

	private Integer obtenerTotalVotosPreferenciales (DetalleActaPreferencialDto votoPreferencial) {
		return votoPreferencial.getVotos()==null ? 0
				: votoPreferencial.getVotos();
	}


	private void procesarVotoPreferencial(DetalleActaPreferencialDto detalleActaPreferencialDto,
										  ActaElectoralRequestDto acta,
										  DetalleActaDto detalleActaDto,
										 	List<DetalleActaPreferencialDto> detActaPreferencialListToErrores,
										  Integer cantidadElectoresHabiles) {
		if (detalleActaPreferencialDto.getVotos() == null ) {
			detalleActaPreferencialDto.setVotos(0);
		}
		detalleActaPreferencialDto.setEstadoErrorAritmetico(ConsultaErroresMateriales.getDetErrorMaterialPreferencial(acta, detalleActaDto, detalleActaPreferencialDto, cantidadElectoresHabiles));
		detActaPreferencialListToErrores.add(detalleActaPreferencialDto);
	}


	private void verificarVotosImpugnados(DetalleActaDto detalleActaDto, ActaElectoralRequestDto actaDto) {
		if (Objects.equals(detalleActaDto.getCodigoAgrupacionPolitica(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.toString()) &&
				detalleActaDto.getVotos() != null && detalleActaDto.getVotos() > 0) {
			SceUtils.agregarEstadoResolucion(actaDto, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
		}
	}

	private Integer getTotalVotosAgrupacionesPoliticas(List<DetalleActaDto> detalleActa) {
		return detalleActa.stream()
				.filter(detActa -> detActa.getEstadoAgrupacionPolitica().equals(ConstantesComunes.N_PARTICIPA))
				.mapToInt(detActa -> detActa.getVotos() != null ? detActa.getVotos() : 0)
				.sum();

	}

}
