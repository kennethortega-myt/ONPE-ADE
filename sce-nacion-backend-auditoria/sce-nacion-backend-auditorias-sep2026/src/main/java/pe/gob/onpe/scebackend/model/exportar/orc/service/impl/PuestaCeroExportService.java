package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.PuestaCeroExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IPuestaCeroExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IPuestaCeroExportService;
import pe.gob.onpe.scebackend.model.orc.entities.PuestaCero;
import pe.gob.onpe.scebackend.model.orc.repository.PuestaCeroRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class PuestaCeroExportService extends MigracionService<PuestaCeroExportDto, PuestaCero, String> implements IPuestaCeroExportService {

	@Autowired
	private PuestaCeroRepository puestaCeroRepository;
	
	@Autowired
	private IPuestaCeroExportMapper puestaCeroMapper;

	@Override
	public MigracionRepository<PuestaCero, String> getRepository() {
		return puestaCeroRepository;
	}

	@Override
	public IMigracionMapper<PuestaCeroExportDto, PuestaCero> getMapper() {
		return puestaCeroMapper;
	}

}
