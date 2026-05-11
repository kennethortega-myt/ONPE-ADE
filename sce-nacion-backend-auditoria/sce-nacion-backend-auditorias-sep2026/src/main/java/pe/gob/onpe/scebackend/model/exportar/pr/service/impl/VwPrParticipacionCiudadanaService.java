package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrParticipacionCiudadanaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IVwPrParticipacionCiudadanaExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrParticipacionCiudadanaService;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrParticipacionCiudadana;
import pe.gob.onpe.scebackend.model.orc.repository.VwPrParticipacionCiudadanaRepository;

@Service
public class VwPrParticipacionCiudadanaService implements IVwPrParticipacionCiudadanaService {

	@Autowired
	private VwPrParticipacionCiudadanaRepository repository;
	
	@Autowired
	private IVwPrParticipacionCiudadanaExportPrMapper mapper;
	
	@Override
	public List<VwPrParticipacionCiudadanaExportDto> findAll() {
		List<VwPrParticipacionCiudadana> entities = this.repository.findAll();
		List<VwPrParticipacionCiudadanaExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
