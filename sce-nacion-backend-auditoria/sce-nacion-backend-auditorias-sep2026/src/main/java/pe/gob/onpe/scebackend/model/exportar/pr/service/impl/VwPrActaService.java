package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IVwPrActaExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrActaService;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrActa;
import pe.gob.onpe.scebackend.model.orc.repository.VwPrActaRepository;

@Service
public class VwPrActaService implements IVwPrActaService{

	@Autowired
	private VwPrActaRepository vwPrActaRepository;
	
	@Autowired
	private IVwPrActaExportPrMapper vwPrActaMapper;
	
	@Override
	public List<VwPrActaExportDto> findAll() {
		List<VwPrActa> entities = this.vwPrActaRepository.findAll();
		List<VwPrActaExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.vwPrActaMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
