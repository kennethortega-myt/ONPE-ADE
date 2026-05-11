package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoEleccionDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IUbigeoEleccionExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IUbigeoEleccionExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.UbigeoEleccion;
import pe.gob.onpe.scebackend.model.orc.repository.UbigeoEleccionRepository;

@Service
public class UbigeoEleccionExportPrService implements IUbigeoEleccionExportPrService {

	@Autowired
	private UbigeoEleccionRepository ubigeoEleccionRepository;
	
	@Autowired
	private IUbigeoEleccionExportPrMapper mapper;
	
	@Override
	public List<UbigeoEleccionDto> findAll() {
		List<UbigeoEleccion> entities = this.ubigeoEleccionRepository.findAll();
		List<UbigeoEleccionDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	


}
