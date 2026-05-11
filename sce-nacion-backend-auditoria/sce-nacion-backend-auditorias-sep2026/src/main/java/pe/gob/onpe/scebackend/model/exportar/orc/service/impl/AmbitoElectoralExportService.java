package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.dto.AmbitoElectoralDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAmbitoElectoralExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IAmbitoElectoralExportService;
import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.AmbitoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class AmbitoElectoralExportService extends MigracionService<AmbitoElectoralDto, AmbitoElectoral, String> implements IAmbitoElectoralExportService {

	@Autowired
	private AmbitoElectoralRepository ambitoElectoralRepository;
	
	@Autowired
	private IAmbitoElectoralExportMapper ambitoElectoralMapper;

	@Override
	public MigracionRepository<AmbitoElectoral, String> getRepository() {
		return ambitoElectoralRepository;
	}

	@Override
	public IMigracionMapper<AmbitoElectoralDto, AmbitoElectoral> getMapper() {
		return ambitoElectoralMapper;
	}
	
	

}
