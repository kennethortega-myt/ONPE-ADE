package pe.gob.onpe.scebackend.model.service;


import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.request.DocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DocumentoElectoralResponseDto;

import java.util.List;

public interface IDocumentoElectoralService {
	
	DocumentoElectoralResponseDto save(DocumentoElectoralRequestDto documentoElectoral) throws GenericException;
	
	List<DocumentoElectoralResponseDto> listAll();
	
	void updateStatus(Integer status,Integer id);

	List<DocumentoElectoralResponseDto> listAllConfiguracionGeneral();

	void saveConfigGeneral(List<DocumentoElectoralRequestDto> listaConfiguracionGeneral);

}
