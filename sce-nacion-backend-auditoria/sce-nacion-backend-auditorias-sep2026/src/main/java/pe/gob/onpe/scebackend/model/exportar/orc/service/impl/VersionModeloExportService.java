package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.VersionModeloExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IVersionModeloExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IVersionModeloExportService;
import pe.gob.onpe.scebackend.model.orc.entities.VersionModelo;
import pe.gob.onpe.scebackend.model.orc.repository.VersionModeloRepository;

@Service
@AllArgsConstructor
public class VersionModeloExportService implements IVersionModeloExportService {

	private final VersionModeloRepository versionRepository;
	
	private final IVersionModeloExportMapper mapper;
	
	@Override
	public List<VersionModeloExportDto> findAll() {
		List<VersionModelo> entities = this.versionRepository.findAll();
		List<VersionModeloExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
}
