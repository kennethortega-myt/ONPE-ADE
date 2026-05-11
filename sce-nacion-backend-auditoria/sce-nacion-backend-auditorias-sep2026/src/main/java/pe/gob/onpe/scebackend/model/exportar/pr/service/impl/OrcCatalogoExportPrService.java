package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.CatalogoExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IOrcCatalogoExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IOrcCatalogoExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.OrcCatalogo;
import pe.gob.onpe.scebackend.model.orc.repository.OrcCatalogoRepository;

@Service
public class OrcCatalogoExportPrService implements IOrcCatalogoExportPrService {

	@Autowired
	private OrcCatalogoRepository catalogoRepository;
	
	@Autowired
	private IOrcCatalogoExportPrMapper catalogoMapper;
	
	@Override
	public List<CatalogoExportDto> findAll() {
		List<OrcCatalogo> catalogos = (List<OrcCatalogo>) this.catalogoRepository.findAll();
		List<CatalogoExportDto> dtos = null;
		if(!Objects.isNull(catalogos)) {
			dtos = catalogos.stream().map(
					entity ->  this.catalogoMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
