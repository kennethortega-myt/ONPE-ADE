package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetDistritoElectoralEleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IDetDistritoElectoralEleccionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDetDistritoElectoralEleccionExportService;
import pe.gob.onpe.scebackend.model.orc.entities.DetDistritoElectoralEleccion;
import pe.gob.onpe.scebackend.model.orc.repository.DetDistritoElectoralEleccionRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class DetDistritoElectoralEleccionExportService extends MigracionService<DetDistritoElectoralEleccionExportDto, DetDistritoElectoralEleccion, String> implements IDetDistritoElectoralEleccionExportService {

	private final IDetDistritoElectoralEleccionExportMapper mapper;

	private final DetDistritoElectoralEleccionRepository repository;

    public DetDistritoElectoralEleccionExportService(IDetDistritoElectoralEleccionExportMapper actaMapper, DetDistritoElectoralEleccionRepository actaRepository) {
        this.mapper = actaMapper;
        this.repository = actaRepository;
    }

    @Override
	public MigracionRepository<DetDistritoElectoralEleccion, String> getRepository() {
		return repository;
	}

	@Override
	public IMigracionMapper<DetDistritoElectoralEleccionExportDto, DetDistritoElectoralEleccion> getMapper() {
		return this.mapper;
	}
	
}
