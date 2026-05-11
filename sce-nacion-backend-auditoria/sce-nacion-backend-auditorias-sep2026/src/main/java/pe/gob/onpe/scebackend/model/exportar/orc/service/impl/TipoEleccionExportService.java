package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.entities.TipoEleccion;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmTipoEleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.ITipoEleccionExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ITipoEleccionExportService;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;
import pe.gob.onpe.scebackend.model.repository.TipoEleccionRepository;


@Service
public class TipoEleccionExportService extends MigracionService<AdmTipoEleccionExportDto, TipoEleccion, String> implements ITipoEleccionExportService {

    @Autowired
    private ITipoEleccionExportMapper tipoEleccionMapper;

    @Autowired
    private TipoEleccionRepository tipoEleccionRepository;

	@Override
	public MigracionRepository<TipoEleccion, String> getRepository() {
		return this.tipoEleccionRepository;
	}

	@Override
	public IMigracionMapper<AdmTipoEleccionExportDto, TipoEleccion> getMapper() {
		return this.tipoEleccionMapper;
	}
}
