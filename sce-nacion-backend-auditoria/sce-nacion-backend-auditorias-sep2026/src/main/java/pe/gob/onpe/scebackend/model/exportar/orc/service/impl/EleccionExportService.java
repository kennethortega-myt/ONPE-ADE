package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.EleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IEleccionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IEleccionExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.orc.repository.EleccionRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;


@Service
public class EleccionExportService extends MigracionService<EleccionExportDto, Eleccion, String> implements IEleccionExportService {

	@Autowired
	private EleccionRepository eleccionRepository;
	
	@Autowired
	private IEleccionExportMapper eleccionMapper;
	
	@Override
	public MigracionRepository<Eleccion, String> getRepository() {
		return eleccionRepository;
	}

	@Override
	public IMigracionMapper<EleccionExportDto, Eleccion> getMapper() {
		return eleccionMapper;
	}

	

}
