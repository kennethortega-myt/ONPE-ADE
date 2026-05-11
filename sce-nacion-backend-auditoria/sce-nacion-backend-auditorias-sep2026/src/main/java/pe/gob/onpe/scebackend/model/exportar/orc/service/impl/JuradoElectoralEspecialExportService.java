package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.TabJuradoElectoralEspecialExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ITabJuradoElectoralEspecialExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IJuradoElectoralEspecialExportService;
import pe.gob.onpe.scebackend.model.orc.entities.JuradoElectoralEspecial;
import pe.gob.onpe.scebackend.model.orc.repository.JuradoElectoralEspecialRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class JuradoElectoralEspecialExportService extends 
	MigracionService<TabJuradoElectoralEspecialExportDto, JuradoElectoralEspecial, String> implements IJuradoElectoralEspecialExportService {

	@Autowired
	public JuradoElectoralEspecialRepository repository;
	
	@Autowired
	public ITabJuradoElectoralEspecialExportMapper mapper;

	@Override
	public MigracionRepository<JuradoElectoralEspecial, String> getRepository() {
		return repository;
	}

	@Override
	public IMigracionMapper<TabJuradoElectoralEspecialExportDto, JuradoElectoralEspecial> getMapper() {
		return mapper;
	}

}
