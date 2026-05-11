package pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.repository.DocumentoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.util.List;

@Service
public class DocumentoElectoralServiceImpl implements DocumentoElectoralService {

	private final DocumentoElectoralRepository documentoElectoralRepository;
	
	public DocumentoElectoralServiceImpl(
			DocumentoElectoralRepository adminDocumentoElectoralRepository) {
		this.documentoElectoralRepository = adminDocumentoElectoralRepository;
	}
	
	@Override
	@Transactional
	public void save(DocumentoElectoral k) {
		this.documentoElectoralRepository.save(k);
	}

	@Override
	@Transactional
	public void saveAll(List<DocumentoElectoral> k) {
		this.documentoElectoralRepository.saveAll(k);
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.documentoElectoralRepository.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentoElectoral> findAll() {
		return this.documentoElectoralRepository.findAll();
	}

	@Override
	public DocumentoElectoral findByAbreviatura(String abreviatura) {
		return this.documentoElectoralRepository.findByAbreviatura(abreviatura);
	}

	@Override
	public List<DocumentoElectoral> buscarDocumentosConfigurados(String abreviaturaProceso) {
		return this.documentoElectoralRepository.buscarDocumentosConfigurados(abreviaturaProceso);
	}

	@Override
	public List<DocumentoElectoral> findByDocumentoElectoralPadre(
			DocumentoElectoral documentoElectoralPadre) {
		return this.documentoElectoralRepository.findByDocumentoElectoralPadre(documentoElectoralPadre);
	}

	@Override
	public List<DocumentoElectoral> findByDocumentoElectoralPadreIsNull() {
		return this.documentoElectoralRepository.findByDocumentoElectoralPadreIsNullAndActivo(ConstantesComunes.ACTIVO);
	}

}
