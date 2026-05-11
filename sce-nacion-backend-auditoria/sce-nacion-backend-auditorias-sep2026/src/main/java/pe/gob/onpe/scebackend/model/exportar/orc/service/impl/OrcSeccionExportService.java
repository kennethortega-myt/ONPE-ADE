package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcSeccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IOrcSeccionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IOrcSeccionExportService;
import pe.gob.onpe.scebackend.model.orc.entities.OrcSeccion;
import pe.gob.onpe.scebackend.model.orc.repository.OrcSeccionRepository;

@Service
public class OrcSeccionExportService implements IOrcSeccionExportService{

	@Autowired
	private OrcSeccionRepository repository;
	
	@Autowired
	private IOrcSeccionExportMapper mapper;
	
	@Override
	public List<OrcSeccionExportDto> findAll() {
		List<OrcSeccion> entidades = (List<OrcSeccion>) this.repository.findAll();
		List<OrcSeccionExportDto> dtos = null;
		if(!Objects.isNull(entidades)) {
			dtos = entidades.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
