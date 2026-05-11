package pe.gob.onpe.scebackend.model.service;


import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.entities.Archivo;

public interface IArchivoService {
    
    Archivo guardarArchivo(Archivo archivo);

    Archivo guardarArchivoDTO(ArchivoDTO archivoDTO);
    
    ArchivoDTO getArchivoByGuid(String guid);

    Archivo getArchivoByGuuid(String guid);
    
    ArchivoDTO getArchivoById(Long id);
}
