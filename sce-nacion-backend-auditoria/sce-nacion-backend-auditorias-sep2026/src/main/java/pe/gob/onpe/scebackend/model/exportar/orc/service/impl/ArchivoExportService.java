package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmArchivoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAdmArchivoExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IArchivoExportService;
import pe.gob.onpe.scebackend.model.entities.Archivo;
import pe.gob.onpe.scebackend.model.repository.ArchivoRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;


@Service
public class ArchivoExportService extends MigracionService<AdmArchivoExportDto, Archivo, String> implements IArchivoExportService {
    
    @Autowired
    private IAdmArchivoExportMapper archivoMapper;
    
    @Autowired
    private ArchivoRepository archivoRepository;

	@Override
	public MigracionRepository<Archivo, String> getRepository() {
		return this.archivoRepository;
	}

	@Override
	public IMigracionMapper<AdmArchivoExportDto, Archivo> getMapper() {
		return this.archivoMapper;
	}
}
