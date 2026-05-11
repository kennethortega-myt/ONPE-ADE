package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ProcesoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IProcesoElectoralExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IProcesoElectoralExportService;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.ProcesoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class ProcesoElectoralExportService extends MigracionService<ProcesoElectoralExportDto, ProcesoElectoral, String> implements IProcesoElectoralExportService {

	@Autowired
	private ProcesoElectoralRepository procesoRepository;
	
	@Autowired
	private IProcesoElectoralExportMapper procesoMapper;
	
	@Override
	public MigracionRepository<ProcesoElectoral, String> getRepository() {
		return procesoRepository;
	}

	@Override
	public IMigracionMapper<ProcesoElectoralExportDto, ProcesoElectoral> getMapper() {
		return procesoMapper;
	}

	

}
