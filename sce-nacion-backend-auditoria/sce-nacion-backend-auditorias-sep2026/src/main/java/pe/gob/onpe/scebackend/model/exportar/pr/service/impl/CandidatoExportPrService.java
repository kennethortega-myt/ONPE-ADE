package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.ICandidatoExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.ICandidatoExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.Candidato;
import pe.gob.onpe.scebackend.model.orc.repository.CandidatoRepository;

@Service
public class CandidatoExportPrService implements ICandidatoExportPrService{

	@Autowired
	private CandidatoRepository candidatoRepository;
	
	@Autowired
	private ICandidatoExportPrMapper mapper;
	
	@Override
	public List<CandidatoExportDto> findAll() {
		List<Candidato> entities = this.candidatoRepository.findAll();
		List<CandidatoExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
