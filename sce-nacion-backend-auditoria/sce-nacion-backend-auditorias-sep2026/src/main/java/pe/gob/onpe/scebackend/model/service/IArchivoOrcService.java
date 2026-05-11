package pe.gob.onpe.scebackend.model.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;

public interface IArchivoOrcService {

	ArchivoDTO getArchivoById(Long id);
	
	Optional<Archivo> findById(Long id);
	
	Archivo guardarArchivo(MultipartFile file, String usuario, String codigoCentroComputo, Optional<Integer> codigoDocumentoElectoral);
	
}
