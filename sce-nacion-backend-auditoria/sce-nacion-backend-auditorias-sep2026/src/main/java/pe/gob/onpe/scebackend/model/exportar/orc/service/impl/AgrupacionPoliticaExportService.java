package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAgrupacionPoliticaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IAgrupacionPoliticaExportService;
import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPolitica;
import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;



@Service
public class AgrupacionPoliticaExportService extends MigracionService<AgrupacionPoliticaExportDto, AgrupacionPolitica, String> implements IAgrupacionPoliticaExportService {

	@Autowired
	public AgrupacionPoliticaRepository repository;
	
	@Autowired
	public IAgrupacionPoliticaExportMapper mapper;

	@Override
	public MigracionRepository<AgrupacionPolitica, String> getRepository() {
		return repository;
	}

	@Override
	public IMigracionMapper<AgrupacionPoliticaExportDto, AgrupacionPolitica> getMapper() {
		return mapper;
	}

}
