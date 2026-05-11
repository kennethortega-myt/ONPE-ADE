package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaOpcionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IDetActaOpcionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDetActaOpcionExportService;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaOpcion;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaOpcionRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class DetActaOpcionExportService extends MigracionService<DetActaOpcionExportDto, DetActaOpcion, String> implements IDetActaOpcionExportService {

	@Autowired
	private DetActaOpcionRepository detActaOpcionrepository;
	
	@Autowired
	private IDetActaOpcionExportMapper detActaOpcionExportMapper;
	
	@Override
	public MigracionRepository<DetActaOpcion, String> getRepository() {
		return detActaOpcionrepository;
	}

	@Override
	public IMigracionMapper<DetActaOpcionExportDto, DetActaOpcion> getMapper() {
		return detActaOpcionExportMapper;
	}

}
