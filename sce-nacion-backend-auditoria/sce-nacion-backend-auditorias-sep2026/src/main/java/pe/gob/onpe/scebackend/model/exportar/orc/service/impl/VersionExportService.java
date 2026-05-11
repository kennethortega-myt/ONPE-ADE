package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.VersionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IVersionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IVersionExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Version;
import pe.gob.onpe.scebackend.model.orc.repository.VersionRepository;

@Service
public class VersionExportService implements IVersionExportService {

	@Autowired
	private VersionRepository versionRepository;
	
	@Autowired
	private IVersionExportMapper mapper;
	
	@Override
	public List<VersionExportDto> findAll() {
		List<Version> entities = this.versionRepository.findAll();
		List<VersionExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
