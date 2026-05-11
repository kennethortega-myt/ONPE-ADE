package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.MiembroMesaSorteadoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMiembroMesaSorteadoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IMiembroMesaSorteadoExportService;
import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaSorteado;
import pe.gob.onpe.scebackend.model.orc.repository.MiembroMesaSorteadoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class MiembroMesaSorteadoExportService extends MigracionService<MiembroMesaSorteadoExportDto, MiembroMesaSorteado, String> implements IMiembroMesaSorteadoExportService {

	@Autowired
	private MiembroMesaSorteadoRepository miembroMesaSorteadoRepository;
	
	@Autowired
	private IMiembroMesaSorteadoExportMapper miembroMesaSorteadoExportMapper;
	
	@Override
	public MigracionRepository<MiembroMesaSorteado, String> getRepository() {
		return miembroMesaSorteadoRepository;
	}

	@Override
	public IMigracionMapper<MiembroMesaSorteadoExportDto, MiembroMesaSorteado> getMapper() {
		return miembroMesaSorteadoExportMapper;
	}

}
