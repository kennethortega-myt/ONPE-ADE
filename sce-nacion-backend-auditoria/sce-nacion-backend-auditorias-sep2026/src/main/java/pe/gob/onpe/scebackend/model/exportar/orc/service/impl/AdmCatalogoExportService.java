package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.entities.Catalogo;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmCatalogoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAdmCatalogoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IAdmCatalogoExportService;
import pe.gob.onpe.scebackend.model.repository.CatalogoRepository;

@Service
public class AdmCatalogoExportService implements IAdmCatalogoExportService {

	@Autowired
	private CatalogoRepository catalogoRepository;
	
	@Autowired
	private IAdmCatalogoExportMapper catalogoMapper;
	
	@Override
	public List<AdmCatalogoExportDto> findAll() {
		List<Catalogo> catalogos = (List<Catalogo>) this.catalogoRepository.findAll();
		List<AdmCatalogoExportDto> dtos = null;
		if(!Objects.isNull(catalogos)) {
			dtos = catalogos.stream().map(
					entity ->  this.catalogoMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}


}
