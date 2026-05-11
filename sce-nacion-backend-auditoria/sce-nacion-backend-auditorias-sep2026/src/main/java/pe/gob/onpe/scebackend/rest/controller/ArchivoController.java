package pe.gob.onpe.scebackend.rest.controller;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.scebackend.exeption.InternalServerErrorException;
import pe.gob.onpe.scebackend.exeption.NotFoundException;
import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.service.IArchivoOrcService;
import pe.gob.onpe.scebackend.model.service.StorageService;
import pe.gob.onpe.scebackend.utils.RoleAutority;


@RestController
@RequestMapping("/archivo-nacion")
public class ArchivoController {

	private final StorageService storageService;

	private final IArchivoOrcService archivoService;
	
	@Value("${fileserver.files}")
	private String ubicacionFile;

    public ArchivoController(StorageService storageService, IArchivoOrcService archivoService) {
        this.storageService = storageService;
        this.archivoService = archivoService;
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable("id") String id) {
        try {
        	ArchivoDTO tabArchivo = this.archivoService.getArchivoById(Long.valueOf(id));
            if (tabArchivo == null) {
                throw new NotFoundException("File not found");
            }

            Resource res= this.storageService.loadFile(id);
            String contentType = tabArchivo.getFormato();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + tabArchivo.getNombre() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(Files.readAllBytes(res.getFile().toPath()));
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to download the file. Error: "+ e.getMessage());
        }
    }
    

}
