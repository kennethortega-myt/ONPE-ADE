package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IAgrupacionPoliticaExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IAgrupacionPoliticaExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPolitica;
import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaRepository;

@Service
public class AgrupacionPoliticaExportPrService implements IAgrupacionPoliticaExportPrService {

	@Autowired
	private AgrupacionPoliticaRepository agrupacionPoliticaRepository;
	
	@Autowired
	private IAgrupacionPoliticaExportPrMapper mapper;
	
	@Override
	public List<AgrupacionPoliticaExportDto> findAll() {
		List<AgrupacionPolitica> entities = this.agrupacionPoliticaRepository.findAll();
		List<AgrupacionPoliticaExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
