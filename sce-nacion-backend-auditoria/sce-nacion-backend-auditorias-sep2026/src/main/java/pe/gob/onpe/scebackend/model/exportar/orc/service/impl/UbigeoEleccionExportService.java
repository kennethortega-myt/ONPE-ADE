package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoEleccionDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IUbigeoEleccionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IUbigeoEleccionExportService;
import pe.gob.onpe.scebackend.model.orc.entities.UbigeoEleccion;
import pe.gob.onpe.scebackend.model.orc.repository.UbigeoEleccionRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;


@Service
public class UbigeoEleccionExportService extends MigracionService<UbigeoEleccionDto, UbigeoEleccion, String> implements IUbigeoEleccionExportService {

	@Autowired
	private UbigeoEleccionRepository ueRepository;
	
	@Autowired
	private IUbigeoEleccionExportMapper ueMapper;
	
	@Override
	public MigracionRepository<UbigeoEleccion, String> getRepository() {
		return ueRepository;
	}

	@Override
	public IMigracionMapper<UbigeoEleccionDto, UbigeoEleccion> getMapper() {
		return ueMapper;
	}

	
	
}
