package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IActaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IActaExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class ActaExportService extends MigracionService<CabActaExportDto, Acta, String> implements IActaExportService {

	private final IActaExportMapper actaMapper;

	private final ActaRepository actaRepository;

    public ActaExportService(IActaExportMapper actaMapper, ActaRepository actaRepository) {
        this.actaMapper = actaMapper;
        this.actaRepository = actaRepository;
    }

    @Override
	public MigracionRepository<Acta, String> getRepository() {
		return actaRepository;
	}

	@Override
	public IMigracionMapper<CabActaExportDto, Acta> getMapper() {
		return this.actaMapper;
	}


}
