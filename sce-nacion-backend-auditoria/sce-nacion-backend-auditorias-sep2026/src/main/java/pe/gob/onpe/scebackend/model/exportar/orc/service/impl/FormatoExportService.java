package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.FormatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IFormatoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IFormatoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Formato;
import pe.gob.onpe.scebackend.model.orc.repository.FormatoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class FormatoExportService extends MigracionService<FormatoExportDto, Formato, String> implements IFormatoExportService {

	@Autowired
	private IFormatoExportMapper formatoMapper;

	@Autowired
	private FormatoRepository formatoRepository;
	
	@Override
	public MigracionRepository<Formato, String> getRepository() {
		return formatoRepository;
	}

	@Override
	public IMigracionMapper<FormatoExportDto, Formato> getMapper() {
		return this.formatoMapper;
	}


}