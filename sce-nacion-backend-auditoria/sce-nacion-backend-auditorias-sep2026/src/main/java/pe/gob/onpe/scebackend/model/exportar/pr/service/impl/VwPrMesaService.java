package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrMesaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IVwPrMesaExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrMesaService;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrMesa;
import pe.gob.onpe.scebackend.model.orc.repository.VwPrMesaRepository;

@Service
public class VwPrMesaService implements IVwPrMesaService {

	@Autowired
	private VwPrMesaRepository vwPrMesaRepository;
	
	@Autowired
	private IVwPrMesaExportPrMapper vwPrMesaMapper;

	@Override
	public List<VwPrMesaExportDto> findAll() {
		List<VwPrMesa> entities = this.vwPrMesaRepository.findAll();
		List<VwPrMesaExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.vwPrMesaMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
}
