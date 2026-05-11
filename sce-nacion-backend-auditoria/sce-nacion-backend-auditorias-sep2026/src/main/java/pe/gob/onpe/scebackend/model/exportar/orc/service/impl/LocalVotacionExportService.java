package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.LocalVotacionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ILocalVotacionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ILocalVotacionExportService;
import pe.gob.onpe.scebackend.model.orc.entities.LocalVotacion;
import pe.gob.onpe.scebackend.model.orc.repository.LocalVotacionRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class LocalVotacionExportService extends MigracionService<LocalVotacionExportDto, LocalVotacion, String> implements ILocalVotacionExportService {

	@Autowired
	private LocalVotacionRepository localVotacionRepository;
	
	@Autowired
	private ILocalVotacionExportMapper localVotacionMapper;
	
	
	@Override
	public MigracionRepository<LocalVotacion, String> getRepository() {
		return localVotacionRepository;
	}

	@Override
	public IMigracionMapper<LocalVotacionExportDto, LocalVotacion> getMapper() {
		return localVotacionMapper;
	}

	

}
