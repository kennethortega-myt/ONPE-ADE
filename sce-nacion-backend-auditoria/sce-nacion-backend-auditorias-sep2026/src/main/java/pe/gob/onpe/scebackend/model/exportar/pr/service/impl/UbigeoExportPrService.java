package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IUbigeoExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IUbigeoExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.Ubigeo;
import pe.gob.onpe.scebackend.model.orc.repository.UbigeoRepository;

@Service
public class UbigeoExportPrService implements IUbigeoExportPrService {

	@Autowired
	private UbigeoRepository ubigeoRepository;
	
	@Autowired
	private IUbigeoExportPrMapper mapper;
	
	@Override
	public List<UbigeoExportDto> findAll() {
		List<Ubigeo> entities = this.ubigeoRepository.findAll();
		List<UbigeoExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
}
