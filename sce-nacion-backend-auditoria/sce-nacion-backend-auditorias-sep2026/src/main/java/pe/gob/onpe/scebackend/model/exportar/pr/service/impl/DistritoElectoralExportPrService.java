package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IDistritoElectoralExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DistritoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IDistritoElectoralExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.DistritoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.DistritoElectoralRepository;

@Service
public class DistritoElectoralExportPrService implements IDistritoElectoralExportPrService {

	@Autowired
	private DistritoElectoralRepository distritoElectoralRepository;
	
	@Autowired
	private IDistritoElectoralExportPrMapper mapper; 
	
	@Override
	public List<DistritoElectoralExportDto> findAll() {
		List<DistritoElectoral> entities = this.distritoElectoralRepository.findAll();
		List<DistritoElectoralExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
