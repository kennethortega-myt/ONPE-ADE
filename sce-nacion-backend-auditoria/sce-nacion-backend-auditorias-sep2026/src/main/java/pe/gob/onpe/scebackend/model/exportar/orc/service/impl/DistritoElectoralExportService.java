package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DistritoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IDistritoElectoralExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDistritoElectoralExportService;
import pe.gob.onpe.scebackend.model.orc.entities.DistritoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.DistritoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class DistritoElectoralExportService extends MigracionService<DistritoElectoralExportDto, DistritoElectoral, String> implements IDistritoElectoralExportService {

	@Autowired
	private DistritoElectoralRepository distritoElectoralRepository;
	
	@Autowired
	private IDistritoElectoralExportMapper distritoElectoralMapper;
	
	@Override
	public MigracionRepository<DistritoElectoral, String> getRepository() {
		return distritoElectoralRepository;
	}

	@Override
	public IMigracionMapper<DistritoElectoralExportDto, DistritoElectoral> getMapper() {
		return distritoElectoralMapper;
	}
	
}
