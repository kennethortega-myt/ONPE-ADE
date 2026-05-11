package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetParametroExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IDetParametroExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDetParametroExportService;
import pe.gob.onpe.scebackend.model.orc.entities.DetParametro;
import pe.gob.onpe.scebackend.model.orc.repository.DetParametroRepository;

@Service
public class DetParametroExportService implements IDetParametroExportService {

	@Autowired
	private DetParametroRepository detParametroRepository;
	
	@Autowired
	private IDetParametroExportMapper detParametroMapper;

	@Override
	public List<DetParametroExportDto> findAll() {
		List<DetParametro> entidades = this.detParametroRepository.findAll();
		List<DetParametroExportDto> dtos = null;
		if(!Objects.isNull(entidades)) {
			dtos = entidades.stream().map(
					entity ->  this.detParametroMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
}
