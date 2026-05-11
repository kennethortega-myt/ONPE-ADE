package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ICandidatoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ICandidatoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Candidato;
import pe.gob.onpe.scebackend.model.orc.repository.CandidatoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class CandidatoExportService extends MigracionService<CandidatoExportDto, Candidato, String> implements ICandidatoExportService {

	@Autowired
	private CandidatoRepository candidatoRepository;
	
	@Autowired
	private ICandidatoExportMapper candidatoMapper;
	
	@Override
	public MigracionRepository<Candidato, String> getRepository() {
		return candidatoRepository;
	}

	@Override
	public IMigracionMapper<CandidatoExportDto, Candidato> getMapper() {
		return candidatoMapper;
	}

}
