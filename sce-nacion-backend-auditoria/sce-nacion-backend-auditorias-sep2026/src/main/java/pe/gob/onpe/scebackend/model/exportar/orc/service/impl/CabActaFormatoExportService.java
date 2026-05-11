package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaFormatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ICabActaFormatoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ICabActaFormatoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.CabActaFormato;
import pe.gob.onpe.scebackend.model.orc.repository.CabActaFormatoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class CabActaFormatoExportService extends MigracionService<CabActaFormatoExportDto, CabActaFormato, String> implements ICabActaFormatoExportService {

	@Autowired
	private ICabActaFormatoExportMapper cabActaFormatoMapper;

	@Autowired
	private CabActaFormatoRepository cabActaFormatoRepository;
	
	@Override
	public MigracionRepository<CabActaFormato, String> getRepository() {
		return cabActaFormatoRepository;
	}

	@Override
	public IMigracionMapper<CabActaFormatoExportDto, CabActaFormato> getMapper() {
		return this.cabActaFormatoMapper;
	}

}
