package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.onpe.scebackend.exeption.InternalServerErrorException;
import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;
import pe.gob.onpe.scebackend.model.orc.repository.TabArchivoRepository;
import pe.gob.onpe.scebackend.model.service.IArchivoOrcService;
import pe.gob.onpe.scebackend.model.service.StorageService;
import pe.gob.onpe.scebackend.utils.PathUtils;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ArchivoOrcServiceImpl implements IArchivoOrcService {

	private final TabArchivoRepository archivoRepository;
	private final StorageService storageService;
	
	@Override
	@Transactional("locationTransactionManager")
	public ArchivoDTO getArchivoById(Long id) {
		ArchivoDTO archivoDto = new ArchivoDTO();
		Optional<Archivo> archivo = this.archivoRepository.findById(id);
		if (archivo.isPresent()) {
			archivoDto.setId(archivo.get().getId());
			archivoDto.setFormato(archivo.get().getFormato());
			archivoDto.setNombre(archivo.get().getNombre());
			archivoDto.setGuid(archivo.get().getGuid());
		}
		return archivoDto;
	}

	@Override
	@Transactional("locationTransactionManager")
	public Optional<Archivo> findById(Long id) {
		return this.archivoRepository.findById(id);
	}
	
	@Override
	@Transactional("locationTransactionManager")
    public Archivo guardarArchivo(MultipartFile file, String usuario, String codigoCentroComputo, Optional<Integer> codigoDocumentoElectoral) {
        try {
            String detectedType = this.storageService.detectFileType(file);
            Archivo archivo = new Archivo();
            archivo.setNombre(file.getOriginalFilename());
            archivo.setFormato(detectedType);
            archivo.setPeso(String.valueOf(file.getSize()));
            archivo.setActivo(ConstantesComunes.ACTIVO);
            archivo.setGuid(codigoCentroComputo.concat(ConstantesComunes.GUION_MEDIO).concat(DigestUtils.sha256Hex(file.getInputStream())));
            archivo.setUsuarioCreacion(usuario);            
            archivo.setRuta(PathUtils.normalizePath(this.storageService.getPathUpload(), archivo.getGuid()));
            archivo.setDocumentoElectoral(codigoDocumentoElectoral.orElse(null));
            archivo.setFechaCreacion(new Date());
            this.archivoRepository.save(archivo);
            this.storageService.storeFile(file, archivo.getGuid());
            return archivo;
        }catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
