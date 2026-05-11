package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaRealExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IAgrupacionPoliticaRealExportService;
import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPoliticaReal;
import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaRealRepository;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;


@Service
public class AgrupacionPoliticaRealExportService implements IAgrupacionPoliticaRealExportService {

	@Autowired
	private AgrupacionPoliticaRealRepository agrupacionPoliticaRealRepository;
	
	@Override
	public List<AgrupacionPoliticaRealExportDto> findAll() {
		return agrupacionPoliticaRealRepository.findAll()
	            .stream()
	            .map(this::convertToDto)
				.toList();
	}

	@Override
	public List<AgrupacionPoliticaRealExportDto> findByCc(String cc) {
		return agrupacionPoliticaRealRepository.findByCc(cc)
	            .stream()
	            .map(this::convertToDto)
				.toList();
	}

	private AgrupacionPoliticaRealExportDto convertToDto(AgrupacionPoliticaReal entidad) {
		AgrupacionPoliticaRealExportDto dto = new AgrupacionPoliticaRealExportDto();
		dto.setId(entidad.getId());
		dto.setCodigo(entidad.getCodigo());
		dto.setDescripcion(entidad.getDescripcion());
		dto.setTipoAgrupacionPolitica(entidad.getTipoAgrupacionPolitica());
		dto.setUbigeoMaximo(entidad.getUbigeoMaximo());
		dto.setActivo(entidad.getActivo());
		dto.setEstado(entidad.getEstado());
		dto.setAudUsuarioCreacion(entidad.getUsuarioCreacion());
		dto.setAudFechaCreacion(DateUtil.getDateString(entidad.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		dto.setAudUsuarioModificacion(entidad.getUsuarioModificacion());
		dto.setAudFechaModificacion(DateUtil.getDateString(entidad.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		return dto;
	}

}
