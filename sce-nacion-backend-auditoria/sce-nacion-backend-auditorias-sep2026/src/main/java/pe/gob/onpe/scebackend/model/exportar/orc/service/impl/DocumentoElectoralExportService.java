package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import pe.gob.onpe.scebackend.model.entities.DocumentoElectoral;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDocElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAdmDocumentoElectoralExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IDocumentoElectoralExportService;
import pe.gob.onpe.scebackend.model.repository.DocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class DocumentoElectoralExportService extends MigracionService<AdmDocElectoralExportDto, DocumentoElectoral, String> implements IDocumentoElectoralExportService {
	
	@Autowired
	private IAdmDocumentoElectoralExportMapper documentoElectoralMapper;

	@Autowired
	private DocumentoElectoralRepository documentoElectoralRepository;

	@Override
	public MigracionRepository<DocumentoElectoral, String> getRepository() {
		return this.documentoElectoralRepository;
	}

	@Override
	public IMigracionMapper<AdmDocElectoralExportDto, DocumentoElectoral> getMapper() {
		return this.documentoElectoralMapper;
	}

}
