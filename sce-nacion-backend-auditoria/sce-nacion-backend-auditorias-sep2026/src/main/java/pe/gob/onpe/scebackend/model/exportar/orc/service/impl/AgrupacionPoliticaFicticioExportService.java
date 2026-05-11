package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;


import java.util.List;

import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaFicticioExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IAgrupacionPoliticaFicticioExportService;
import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPoliticaFicticia;
import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaFicticiaRepository;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;


@Service
public class AgrupacionPoliticaFicticioExportService implements IAgrupacionPoliticaFicticioExportService {

	private AgrupacionPoliticaFicticiaRepository agrupacionPoliticaFicticiaRepository;
	
	public AgrupacionPoliticaFicticioExportService(
			AgrupacionPoliticaFicticiaRepository agrupacionPoliticaFicticiaRepository){
		this.agrupacionPoliticaFicticiaRepository = agrupacionPoliticaFicticiaRepository;
	}
	
	@Override
	public List<AgrupacionPoliticaFicticioExportDto> findAll() {
		return agrupacionPoliticaFicticiaRepository.findAll()
	            .stream()
				.map(this::getAgrupolFicticioExport).toList();
	}

	@Override
	public List<AgrupacionPoliticaFicticioExportDto> findByCc(String cc) {
		return agrupacionPoliticaFicticiaRepository.findByCc(cc)
	            .stream()
	            .map(this::getAgrupolFicticioExport).toList();
	}

	private AgrupacionPoliticaFicticioExportDto getAgrupolFicticioExport(AgrupacionPoliticaFicticia ficticio) {
		AgrupacionPoliticaFicticioExportDto dto = new AgrupacionPoliticaFicticioExportDto();
		dto.setId(ficticio.getId());
		dto.setCodigo(ficticio.getCodigo());
		dto.setDescripcion(ficticio.getDescripcion());
		dto.setTipoAgrupacionPolitica(ficticio.getTipoAgrupacionPolitica());
		dto.setUbigeoMaximo(ficticio.getUbigeoMaximo());
		dto.setActivo(ficticio.getActivo());
		dto.setEstado(ficticio.getEstado());
		dto.setAudUsuarioCreacion(ficticio.getUsuarioCreacion());
		dto.setAudFechaCreacion(DateUtil.getDateString(ficticio.getFechaCreacion(),ConstantesComunes.FORMATO_FECHA));
		dto.setAudUsuarioModificacion(ficticio.getUsuarioModificacion());
		dto.setAudFechaModificacion(DateUtil.getDateString(ficticio.getFechaModificacion(),ConstantesComunes.FORMATO_FECHA));
		return dto;
	}

}
