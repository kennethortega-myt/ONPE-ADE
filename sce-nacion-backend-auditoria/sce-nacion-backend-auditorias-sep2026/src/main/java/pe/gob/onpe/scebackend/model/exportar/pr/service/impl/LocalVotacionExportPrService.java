package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import pe.gob.onpe.scebackend.model.exportar.pr.dto.LocalVotacionExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.ILocalVotacionExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.ILocalVotacionExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.LocalVotacion;
import pe.gob.onpe.scebackend.model.orc.repository.LocalVotacionRepository;

@Service
public class LocalVotacionExportPrService implements ILocalVotacionExportPrService {

	@Autowired
	private LocalVotacionRepository localVotacionRepository;
	
	@Autowired
	private ILocalVotacionExportPrMapper mapper;
	
	@Override
	public List<LocalVotacionExportDto> findAll() {
		List<LocalVotacion> entities = this.localVotacionRepository.findAll();
		List<LocalVotacionExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
}
