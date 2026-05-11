package pe.gob.onpe.scebackend.model.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.request.DocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DocumentoElectoralResponseDto;
import pe.gob.onpe.scebackend.model.entities.DocumentoElectoral;
import pe.gob.onpe.scebackend.model.mapper.IDocumentoElectoralMapper;
import pe.gob.onpe.scebackend.model.repository.DocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.IDocumentoElectoralService;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentoElectoralServiceImpl implements IDocumentoElectoralService {
	
	@Autowired
	private IDocumentoElectoralMapper documentoElectoralMapper;

	@Autowired
	private DocumentoElectoralRepository documentoElectoralRepository;
	
	@Override
	@Transactional("tenantTransactionManager")
	public DocumentoElectoralResponseDto save(DocumentoElectoralRequestDto documentoElectoral) throws GenericException {
		DocumentoElectoral docu = this.documentoElectoralMapper.dtoToDocumento(documentoElectoral);
		List<DocumentoElectoral> documentos = this.documentoElectoralRepository.findAll();
		validarNombreAbreviaturaExistente(documentos, docu);
		if(docu.getId() == 0){
			docu = documentoElectoralRepository.save(docu);
		}else{
			documentoElectoralRepository.updateDatos(docu.getId(),docu.getNombre(),docu.getAbreviatura(),docu.getTamanioHoja(),
					docu.getMultipagina(),docu.getCodigoBarraOrientacion());
		}

		return this.documentoElectoralMapper.documentoToDTO(docu);
	}

	private static void validarNombreAbreviaturaExistente(List<DocumentoElectoral> documentos, DocumentoElectoral docu) throws GenericException {
		boolean nombreExistente = documentos.stream()
				.anyMatch(d -> d.getNombre().equalsIgnoreCase(docu.getNombre())
						&& !Objects.equals(d.getId(), docu.getId()));
		boolean abreviaturaExistente = documentos.stream()
				.anyMatch(d -> d.getAbreviatura().equalsIgnoreCase(docu.getAbreviatura())
						&& !Objects.equals(d.getId(), docu.getId()));
		if (nombreExistente) {
			log.info("Ya existe un documento con el mismo nombre: {}", docu.getNombre());
			throw new GenericException("Ya existe un documento con el mismo nombre: " + docu.getNombre());
		}
		if (abreviaturaExistente) {
			log.info("Ya existe un documento con el mismo acronimo: {}", docu.getAbreviatura());
			throw new GenericException("Ya existe un documento con el mismo acronimo: " + docu.getAbreviatura());
		}
	}

	@Override
	@Transactional("tenantTransactionManager")
	public List<DocumentoElectoralResponseDto> listAll() {
		return this.documentoElectoralRepository.findByActivo(SceConstantes.ACTIVO).stream().map(documento ->
				this.documentoElectoralMapper.documentoToDTO(documento)).collect(Collectors.toList());
	}

	@Override
	 @Transactional("tenantTransactionManager")
	public void updateStatus(Integer status, Integer id) {
		this.documentoElectoralRepository.updateEstado(status,id);
	}

	@Override
	@Transactional("tenantTransactionManager")
	public List<DocumentoElectoralResponseDto> listAllConfiguracionGeneral() {
		return this.documentoElectoralRepository.findByActivoAndConfiguracionGeneral(SceConstantes.ACTIVO, SceConstantes.IS_CONFIG_GENERAL).stream().map(documento ->
				this.documentoElectoralMapper.documentoToDTO(documento)).collect(Collectors.toList());
	}

	@Override
	@Transactional("tenantTransactionManager")
	public void saveConfigGeneral(List<DocumentoElectoralRequestDto> listaConfiguracionGeneral) {
	for(DocumentoElectoralRequestDto documento: listaConfiguracionGeneral){
		this.documentoElectoralRepository.updateEstado(documento.getActivo(),documento.getId());
	}
	}


}
