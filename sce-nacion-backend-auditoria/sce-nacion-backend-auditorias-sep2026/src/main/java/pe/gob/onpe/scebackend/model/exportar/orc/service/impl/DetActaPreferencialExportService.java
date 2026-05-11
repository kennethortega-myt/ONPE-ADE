package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaPreferencialExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IDetActaPreferencialExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDetActaPreferencialExportService;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaPreferencial;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaPreferencialRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class DetActaPreferencialExportService extends MigracionService<DetActaPreferencialExportDto, DetActaPreferencial, String> implements IDetActaPreferencialExportService {

	@Autowired
	private DetActaPreferencialRepository detActaPreferencialRepository;
	
	@Autowired
	private IDetActaPreferencialExportMapper detActaPreferencialMapper;
	
	@Override
	public MigracionRepository<DetActaPreferencial, String> getRepository() {
		return detActaPreferencialRepository;
	}

	@Override
	public IMigracionMapper<DetActaPreferencialExportDto, DetActaPreferencial> getMapper() {
		return detActaPreferencialMapper;
	}

}
