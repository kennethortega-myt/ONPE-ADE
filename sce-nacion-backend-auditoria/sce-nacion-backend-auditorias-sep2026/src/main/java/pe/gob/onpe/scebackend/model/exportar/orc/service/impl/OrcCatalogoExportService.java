package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcCatalogoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IOrcCatalogoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IOrcCatalogoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.OrcCatalogo;
import pe.gob.onpe.scebackend.model.orc.repository.OrcCatalogoRepository;

@Service
public class OrcCatalogoExportService implements IOrcCatalogoExportService {

	@Autowired
	private OrcCatalogoRepository catalogoRepository;
	
	@Autowired
	private IOrcCatalogoExportMapper catalogoMapper;
	
	@Override
	public List<OrcCatalogoExportDto> findAll() {
		List<OrcCatalogo> catalogos = (List<OrcCatalogo>) this.catalogoRepository.findAll();
		List<OrcCatalogoExportDto> dtos = null;
		if(!Objects.isNull(catalogos)) {
			dtos = catalogos.stream().map(
					entity ->  this.catalogoMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}


}
