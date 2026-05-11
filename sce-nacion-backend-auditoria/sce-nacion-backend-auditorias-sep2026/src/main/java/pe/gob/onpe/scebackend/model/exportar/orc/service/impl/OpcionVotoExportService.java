package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OpcionVotoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IOpcionVotoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IOpcionVotoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.OpcionVoto;
import pe.gob.onpe.scebackend.model.orc.repository.OpcionVotoRepository;

@Service
public class OpcionVotoExportService implements IOpcionVotoExportService {

	@Autowired
	private OpcionVotoRepository repository;
	
	@Autowired
	private IOpcionVotoExportMapper mapper;
	
	@Override
	public List<OpcionVotoExportDto> findAll() {
		List<OpcionVoto> entidades = (List<OpcionVoto>) this.repository.findAll();
		List<OpcionVotoExportDto> dtos = null;
		if(!Objects.isNull(entidades)) {
			dtos = entidades.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
}
