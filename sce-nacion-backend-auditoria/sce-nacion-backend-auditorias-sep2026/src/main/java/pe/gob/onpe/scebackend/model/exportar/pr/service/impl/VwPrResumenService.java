package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrResumenExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IVwPrResumenExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrResumenService;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrResumen;
import pe.gob.onpe.scebackend.model.orc.repository.VwPrResumenRepository;

@Service
public class VwPrResumenService implements IVwPrResumenService {

	@Autowired
	private VwPrResumenRepository vwPrResumenRepository;
	
	@Autowired
	private IVwPrResumenExportPrMapper vwPrResumenMapper;
	
	@Override
	public List<VwPrResumenExportDto> findAll() {
		List<VwPrResumen> entities = this.vwPrResumenRepository.findAll();
		List<VwPrResumenExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.vwPrResumenMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
