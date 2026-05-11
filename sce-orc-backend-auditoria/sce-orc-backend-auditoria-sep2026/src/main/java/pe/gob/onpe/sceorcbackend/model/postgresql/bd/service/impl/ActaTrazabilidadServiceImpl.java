package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.trazabilidad.ActaTransmisionNacionTrazabilidadDto;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.TransmisionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaTransmisionNacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTrazabilidadService;

@Service
public class ActaTrazabilidadServiceImpl implements ActaTrazabilidadService {
	
	@Autowired
	private ActaTransmisionNacionRepository actaTransmisionNacionRepository;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	@Transactional(readOnly = true)
	public List<ActaTransmisionNacion> traza(Long idActa) {
		return this.actaTransmisionNacionRepository.listarTrazabilidadPorActaId(idActa);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActaTransmisionNacion> trazaActaExcluidos(Long idActa) {

		List<String> tiposExcluidos = Arrays.asList(TransmisionNacionEnum.REGISTRO_PERSONEROS.name(),
				TransmisionNacionEnum.REGISTRO_MIEMBRO_MESA_ESCRUTINIO.name(),
				TransmisionNacionEnum.OMISOS_MIEMBRO_MESA.name(),
				TransmisionNacionEnum.OMISOS_LISTA_ELECTORES_VOTANTES.name(),
				TransmisionNacionEnum.CC_RECHAZAR_RES_TRANSMISION.name());
		return this.actaTransmisionNacionRepository.listarTrazabilidadPorActaId(idActa, tiposExcluidos);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ActaTransmisionNacionTrazabilidadDto> trazaActaConInicioFin(Long idActa) {

		List<String> tiposExcluidos = Arrays.asList(
				TransmisionNacionEnum.REGISTRO_PERSONEROS.name(),
				TransmisionNacionEnum.REGISTRO_MIEMBRO_MESA_ESCRUTINIO.name(),
				TransmisionNacionEnum.OMISOS_MIEMBRO_MESA.name(),
				TransmisionNacionEnum.OMISOS_LISTA_ELECTORES_VOTANTES.name()
		);

		List<Object[]> rawResults = this.actaTransmisionNacionRepository.listarTrazabilidadConFechasInicioFin(idActa, tiposExcluidos);
		List<ActaTransmisionNacionTrazabilidadDto> resultado = new ArrayList<>();

		for (Object[] row : rawResults) {
			try {
				ActaTransmisionNacionTrazabilidadDto dto = new ActaTransmisionNacionTrazabilidadDto();
				dto.setId(((Number) row[0]).longValue());
				dto.setIdActa(((Number) row[1]).longValue());
				dto.setEstadoTransmitidoNacion((Integer) row[2]);
				dto.setTipoTransmision((String) row[3]);
				dto.setTransmite((Integer) row[4]);
				dto.setFechaTransmision((Date) row[5]);
				dto.setFechaRegistro((Date) row[6]);
				dto.setUsuarioRegistro((String) row[7]);
				dto.setAccion((String) row[8]);
				dto.setUsuarioTransmision((String) row[9]);
				dto.setIntento((Integer) row[10]);
				dto.setFechaInicio((Date) row[11]);
				dto.setFechaFin((Date) row[12]);

				if (row[13] instanceof String json) {
					dto.setRequestActaTransmision(objectMapper.readValue(json, TransmisionDto.class));
				} else if (row[13] instanceof Map<?, ?>  map) {
					String json = objectMapper.writeValueAsString(map);
					dto.setRequestActaTransmision(objectMapper.readValue(json, TransmisionDto.class));
				}

				resultado.add(dto);
			} catch (Exception e) {
				throw new InternalServerErrorException(String.format("Error parseando requestActaTransmision %s", e.getMessage()));
			}
		}

		return resultado;

	}

}
