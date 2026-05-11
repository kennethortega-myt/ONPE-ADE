package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.MesaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMesaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IMesaExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Mesa;
import pe.gob.onpe.scebackend.model.orc.repository.MesaRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;


@Service
public class MesaExportService extends MigracionService<MesaExportDto, Mesa, String> implements IMesaExportService {

	@Autowired
	private MesaRepository mesaRepository;
	
	@Autowired
	private IMesaExportMapper mesaMapper;
	
	@Override
	public MigracionRepository<Mesa, String> getRepository() {
		return mesaRepository;
	}

	@Override
	public IMigracionMapper<MesaExportDto, Mesa> getMapper() {
		return mesaMapper;
	}

	

}
