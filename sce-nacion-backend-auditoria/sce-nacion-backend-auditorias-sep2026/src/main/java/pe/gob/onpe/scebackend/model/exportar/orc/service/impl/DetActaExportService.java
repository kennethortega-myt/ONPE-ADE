package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IDetActaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDetActaExportService;
import pe.gob.onpe.scebackend.model.orc.entities.DetActa;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;


@Service
public class DetActaExportService  extends MigracionService<DetActaExportDto, DetActa, String> implements IDetActaExportService {

	@Autowired
	private DetActaRepository detActaRepository;
	
	@Autowired
	private IDetActaExportMapper detActaMapper;

	@Override
	public MigracionRepository<DetActa, String> getRepository() {
		return detActaRepository;
	}

	@Override
	public IMigracionMapper<DetActaExportDto, DetActa> getMapper() {
		return detActaMapper;
	}
	
	
	

}
