package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IUbigeoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IUbigeoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Ubigeo;
import pe.gob.onpe.scebackend.model.orc.repository.UbigeoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class UbigeoExportService extends MigracionService<UbigeoExportDto, Ubigeo, String> implements IUbigeoExportService {

	@Autowired
	private UbigeoRepository ubigeoRepository;
	
	@Autowired
	private IUbigeoExportMapper ubigeoMapper;

	@Override
	public MigracionRepository<Ubigeo, String> getRepository() {
		return ubigeoRepository;
	}

	@Override
	public IMigracionMapper<UbigeoExportDto, Ubigeo> getMapper() {
		return ubigeoMapper;
	}
	
	

	

}
