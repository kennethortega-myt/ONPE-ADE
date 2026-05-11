package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDocElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IOrcDocumentoElectoralExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IOrcDocumentoElectoralExportService;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDocumentoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.OrcDocumentoElectoralRepository;

@Service
public class OrcDocumentoElectoralExportService implements IOrcDocumentoElectoralExportService {

	@Autowired
	private OrcDocumentoElectoralRepository repository;
	
	@Autowired
	private IOrcDocumentoElectoralExportMapper mapper;
	
	@Override
	public List<OrcDocElectoralExportDto> findAll() {
		List<OrcDocumentoElectoral> entidades = this.repository.listAllByFirstNull();
		List<OrcDocElectoralExportDto> dtos = null;
		if(!Objects.isNull(entidades)) {
			dtos = entidades.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
