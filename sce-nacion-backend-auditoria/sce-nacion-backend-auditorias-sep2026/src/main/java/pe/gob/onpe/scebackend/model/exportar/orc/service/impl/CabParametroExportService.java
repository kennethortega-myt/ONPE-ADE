package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabParametroExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ICabParametroExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ICabParametroExportService;
import pe.gob.onpe.scebackend.model.orc.entities.CabParametro;
import pe.gob.onpe.scebackend.model.orc.repository.CabParametroRepository;

@Service
public class CabParametroExportService implements ICabParametroExportService{

	@Autowired
	private CabParametroRepository parametroRepository;
	
	@Autowired
	private ICabParametroExportMapper parametroMapper;

	@Override
	public List<CabParametroExportDto> findAll() {
		List<CabParametro> entidades = this.parametroRepository.findAll();
		List<CabParametroExportDto> dtos = null;
		if(!Objects.isNull(entidades)) {
			dtos = entidades.stream().map(
					entity ->  this.parametroMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
}
