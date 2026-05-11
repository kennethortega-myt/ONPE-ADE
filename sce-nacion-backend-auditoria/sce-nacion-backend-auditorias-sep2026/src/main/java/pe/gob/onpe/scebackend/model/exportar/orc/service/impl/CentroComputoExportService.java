package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CentroComputoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ICentroComputoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ICentroComputoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class CentroComputoExportService extends MigracionService<CentroComputoExportDto, CentroComputo, String> implements ICentroComputoExportService {

	@Autowired
	private CentroComputoRepository centroComputoRepository;
	
	@Autowired
	private ICentroComputoExportMapper centroComputoMapper;

	@Override
	public MigracionRepository<CentroComputo, String> getRepository() {
		return centroComputoRepository;
	}

	@Override
	public IMigracionMapper<CentroComputoExportDto, CentroComputo> getMapper() {
		return centroComputoMapper;
	}
	
	
	

}
