package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IOrcDetCatalogoEstructuraExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IOrcDetCatalogoEstructuraExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.scebackend.model.orc.repository.OrcDetalleCatalogoEstructuraRepository;

@Service
public class OrcDetCatalogoEstructuraExportPrService implements IOrcDetCatalogoEstructuraExportPrService {

	@Autowired
	private OrcDetalleCatalogoEstructuraRepository catalogoEstructuraRepository;
	
	@Autowired
	private IOrcDetCatalogoEstructuraExportPrMapper catalogoEstructuraMapper;
	
	@Override
	public List<AdmDetCatalogoEstructuraExportDto> findAll() {
		List<OrcDetalleCatalogoEstructura> estructuras = (List<OrcDetalleCatalogoEstructura>) this.catalogoEstructuraRepository.findAll();
		List<AdmDetCatalogoEstructuraExportDto> dtos = null;
		if(!Objects.isNull(estructuras)) {
			dtos = estructuras.stream().map(
					entity ->  this.catalogoEstructuraMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
