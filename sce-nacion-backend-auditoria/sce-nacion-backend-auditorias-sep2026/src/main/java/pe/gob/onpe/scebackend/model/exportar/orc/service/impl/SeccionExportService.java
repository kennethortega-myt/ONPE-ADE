package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.entities.Seccion;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmSeccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ISeccionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ISeccionExportService;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;
import pe.gob.onpe.scebackend.model.repository.SeccionRepository;


@Service
public class SeccionExportService extends MigracionService<AdmSeccionExportDto, Seccion, String>  implements ISeccionExportService {

    @Autowired
    private ISeccionExportMapper seccionMapper;

    @Autowired
    private SeccionRepository seccionRepository;
    
	@Override
	public MigracionRepository<Seccion, String> getRepository() {
		return this.seccionRepository;
	}

	@Override
	public IMigracionMapper<AdmSeccionExportDto, Seccion> getMapper() {
		return this.seccionMapper;
	}
}
