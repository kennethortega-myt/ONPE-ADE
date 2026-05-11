package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.exception.NotFoundException;
import org.springframework.core.io.Resource;

import pe.gob.onpe.sceorcbackend.model.dto.MonitoreoNacionBusquedaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.ReturnMonitoreoActas;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/monitoreo")
public class MonitoreoController {

  private final CabActaService cabActaService;

  private final ArchivoService tabArchivoService;

  private final StorageService storageService;
  
  @Value("${file.upload-dir}")
  private String imagePath;

  public MonitoreoController(CabActaService cabActaService, ArchivoService tabArchivoService, StorageService storageService) {
    this.cabActaService = cabActaService;
    this.tabArchivoService = tabArchivoService;
    this.storageService = storageService;
  }

  @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
  @PostMapping("/listActas")
  public ResponseEntity<ReturnMonitoreoActas> listActas(@RequestBody MonitoreoNacionBusquedaDto request) {
    return ResponseEntity.status(HttpStatus.OK).body(this.cabActaService.listActasMonitoreo(request));
  }

  @PreAuthorize(RoleAutority.ACCESO_TOTAL)
  @GetMapping("/file/{id}")
  public ResponseEntity<byte[]> getFile(@PathVariable("id") Long id) {
      try {
    	  Archivo tabArchivo = this.tabArchivoService.getArchivoById(id);
          if (tabArchivo == null) {
              throw new NotFoundException("File not found");
          }

          Resource res = this.storageService.loadFile(tabArchivo.getGuid(), false);
          String contentType = tabArchivo.getFormato();

          // Return the file
          return ResponseEntity.ok()
                  .header("Content-Disposition", "attachment; filename=\"" + tabArchivo.getNombre() + "\"")
                  .contentType(MediaType.parseMediaType(contentType))
                  .body(Files.readAllBytes(res.getFile().toPath()));
      } catch (IOException e) {
          throw new InternalServerErrorException("Failed to download the file. Error: " + e.getMessage());
      }
  }

}
