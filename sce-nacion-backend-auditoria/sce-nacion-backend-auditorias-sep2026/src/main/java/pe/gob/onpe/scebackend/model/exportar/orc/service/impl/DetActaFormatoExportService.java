package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaFormatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IDetActaFormatoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDetActaFormatoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaFormato;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaFormatoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class DetActaFormatoExportService extends MigracionService<DetActaFormatoExportDto, DetActaFormato, String> implements IDetActaFormatoExportService {

	@Autowired
	private IDetActaFormatoExportMapper detActaFormatoMapper;

	@Autowired
	private DetActaFormatoRepository detActaFormatoRepository;
	
	@Override
	public MigracionRepository<DetActaFormato, String> getRepository() {
		return detActaFormatoRepository;
	}

	@Override
	public IMigracionMapper<DetActaFormatoExportDto, DetActaFormato> getMapper() {
		return this.detActaFormatoMapper;
	}


}