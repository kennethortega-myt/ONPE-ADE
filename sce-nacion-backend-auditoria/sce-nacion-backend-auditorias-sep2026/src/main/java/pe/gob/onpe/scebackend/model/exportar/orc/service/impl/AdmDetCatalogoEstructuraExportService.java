package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.entities.DetalleCatalogoEstructura;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAdmDetCatalogoEstructuraExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IAdmDetCatalogoEstructuraExportService;
import pe.gob.onpe.scebackend.model.repository.DetalleCatalogoEstructuraRepository;

@Service
public class AdmDetCatalogoEstructuraExportService implements IAdmDetCatalogoEstructuraExportService {

	@Autowired
	private DetalleCatalogoEstructuraRepository catalogoEstructuraRepository;
	
	@Autowired
	private IAdmDetCatalogoEstructuraExportMapper catalogoEstructuraMapper;
	
	@Override
	public List<AdmDetCatalogoEstructuraExportDto> findAll() {
		List<DetalleCatalogoEstructura> estructuras = (List<DetalleCatalogoEstructura>) this.catalogoEstructuraRepository.findAll();
		List<AdmDetCatalogoEstructuraExportDto> dtos = null;
		if(!Objects.isNull(estructuras)) {
			dtos = estructuras.stream().map(
					entity ->  this.catalogoEstructuraMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
