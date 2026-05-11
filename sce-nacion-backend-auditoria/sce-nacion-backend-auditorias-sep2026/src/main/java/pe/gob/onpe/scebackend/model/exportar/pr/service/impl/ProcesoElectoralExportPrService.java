package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;




import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ProcesoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IProcesoElectoralExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IProcesoElectoralExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.ProcesoElectoralRepository;


@Service
public class ProcesoElectoralExportPrService implements IProcesoElectoralExportPrService {

	@Autowired
	private ProcesoElectoralRepository procesoElectoralRepository;
	
	@Autowired
	private IProcesoElectoralExportPrMapper mapper;
	
	@Override
	public List<ProcesoElectoralExportDto> findAll() {
		List<ProcesoElectoral> entities = this.procesoElectoralRepository.findAll();
		List<ProcesoElectoralExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.mapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
