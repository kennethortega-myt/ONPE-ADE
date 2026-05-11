package pe.gob.onpe.scebackend.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.gob.onpe.scebackend.model.dto.*;
import pe.gob.onpe.scebackend.model.dto.request.FiltrosActaNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponseAlternative;
import pe.gob.onpe.scebackend.model.dto.response.MonitoreoGetFilesResponse;
import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.repository.ArchivoOrcRepository;
import pe.gob.onpe.scebackend.model.service.*;
import pe.gob.onpe.scebackend.model.service.comun.IAmbitoElectoralService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;


@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@Slf4j
@RestController
@Validated
@RequestMapping("/monitoreoNacion")
public class MonitoreoNacionController {

    private final IActaService actaService;

    private final IEleccionService eleccionService;

    private final IAmbitoElectoralService ambitoElectoralService;

    private final IProcesoElectoralService procesoElectoralService;

    private final ICentroComputoService centroComputoService;

    private final ArchivoOrcRepository archivoOrcRepository;

    private final StorageService storageService;
    
    private final IArchivoOrcService archivoService;

    public MonitoreoNacionController(IActaService actaService, IEleccionService eleccionService, IAmbitoElectoralService ambitoElectoralService,
                                     IProcesoElectoralService procesoElectoralService, ICentroComputoService centroComputoService,
                                     ArchivoOrcRepository archivoOrcRepository, StorageService storageService, IArchivoOrcService archivoService) {
        this.actaService = actaService;
        this.eleccionService = eleccionService;
        this.ambitoElectoralService = ambitoElectoralService;
        this.procesoElectoralService = procesoElectoralService;
        this.centroComputoService = centroComputoService;
        this.archivoOrcRepository = archivoOrcRepository;
        this.storageService = storageService;
        this.archivoService = archivoService;
    }

    @GetMapping("")
    public ResponseEntity<GenericResponse> listAll() {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.actaService.listActa(new FiltrosActaNacionRequestDto()));
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PostMapping("/listActas")
    public ResponseEntity<ReturnMonitoreoActas> listActas(

    		@RequestBody MonitoreoNacionBusquedaDto request) {
    	
    	log.info("--------Monitoreo Nacion------");
        log.info("idProceso: {}",request.getIdProceso());
        log.info("electionId: {}",request.getIdEleccion());
        log.info("idDepartamento: {}"+request.getIdDepartamento());
        log.info("idProvincia: {}",request.getIdProvincia());
        log.info("idUbigeo: {}",request.getIdUbigeo());
        log.info("idLocal: {}",request.getIdLocal());
        log.info("mesa: {}",request.getMesa());
        log.info("-----------------------------");

        return new ResponseEntity<>(this.actaService.listActasMonitoreo(request), HttpStatus.OK);
    }
    
    @GetMapping("/archivo/{id}")
    public ResponseEntity<GenericResponse> obtenerArchivo(
            @PathVariable("id") Long id) {
    	GenericResponse genericResponse = new GenericResponse();
    	log.info("Se va a devolver el archivo con id {}", id);
    	String base64 = null;
    	try{
    		Optional<Archivo> archivoOp = this.archivoService.findById(id);
        	if(archivoOp.isPresent()){
        		File file = this.storageService.obtenerArchivoRuta(archivoOp.get());
        		byte[] fileContent = Files.readAllBytes(file.toPath());
        		base64 = Base64.getEncoder().encodeToString(fileContent);
        	}
        	genericResponse.setData(base64);
        	genericResponse.setSuccess(true);
        	genericResponse.setMessage("Archivo generado correctamente");
    	}catch (Exception e) {
    		log.error("No se genero correctamente el archivo", e);
    		genericResponse.setSuccess(false);
    		genericResponse.setMessage("Error al generar el archivo");
		}
    	
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PostMapping("/paginacion")
    public ResponseEntity<PaginacionDetalleDto> listActas(
            @RequestBody MonitoreoNacionBusquedaDto request, @RequestParam("cantidad") Integer cantidad) {
        return new ResponseEntity<>(this.actaService.informacionPaginacion(request, cantidad), HttpStatus.OK);
    }

    @GetMapping("/{procesoId}/elecciones")
    public ResponseEntity<GenericResponse> listEleccionesByProceso(
            @PathVariable("procesoId") Long id) {

        GenericResponse genericResponse = new GenericResponse();
        List<EleccionDto> elecciones = this.eleccionService.listByProcesoId(id);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(elecciones);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/{procesoId}/eleccionesPreferencial")
    public ResponseEntity<GenericResponse> listEleccionesPreferencialByProceso(
            @PathVariable("procesoId") Long id) {

        GenericResponse genericResponse = new GenericResponse();
        List<EleccionDto> elecciones = this.eleccionService.listEleccPreferencialByProcesoId(id);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(elecciones);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ambitos")
    public ResponseEntity<GenericResponse> getAmbitosAll() {
        GenericResponse genericResponse = new GenericResponse();
        List<AmbitoElectoral> ambitos = this.ambitoElectoralService.findAll();

        List<AmbitoElectoralDto> ambitosDto = null;
        if (ambitos != null && !ambitos.isEmpty()) {
            ambitosDto = ambitos.parallelStream()
                    .map(dto -> {
                        AmbitoElectoralDto ambito = new AmbitoElectoralDto();
                        ambito.setId(dto.getId());
                        ambito.setCodigo(dto.getCodigo());
                        ambito.setNombre(dto.getNombre());
                        return ambito;
                    })
                    .toList();
        }

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(ambitosDto);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/{id-proceso}/tipoAmbito")
    public ResponseEntity<GenericResponse> getTipoAmbitoPorIdProceso(@PathVariable("id-proceso") Long id) {
        GenericResponse genericResponse = new GenericResponse();
        Long idProceso = id;
        ProcesoAmbitoDto procesoAmb = this.procesoElectoralService.getTipoAmbitoPorIdProceso(idProceso);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(procesoAmb);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/centroComputo")
    public ResponseEntity<GenericResponse> getCentroComputoAll() {
        GenericResponse genericResponse = new GenericResponse();
        List<CentroComputo> centrosComputo = this.centroComputoService.findAll();

        List<CentroComputoDto> ccDto = null;
        if (centrosComputo != null && !centrosComputo.isEmpty()) {
            ccDto = centrosComputo.parallelStream()
                    .map(dto -> {
                        CentroComputoDto ambito = new CentroComputoDto();
                        ambito.setId(dto.getId());
                        ambito.setCodigo(dto.getCodigo());
                        ambito.setNombre(dto.getNombre());
                        return ambito;
                    })
                    .toList();
        }


        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(ccDto);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }


    @PostMapping("/filesPng")
    public ResponseEntity<GenericResponseAlternative<MonitoreoGetFilesResponse>> getFilesPng(
            @RequestParam(value = "acta1FileId") Long acta1FileId,
            @RequestParam(value = "acta2FileId") Long acta2FileId
    ) {
        GenericResponseAlternative<MonitoreoGetFilesResponse> response = new GenericResponseAlternative<>();
        MonitoreoGetFilesResponse dataBase64 = new MonitoreoGetFilesResponse();
        try {

            Archivo tabArchivoFile1 = validateAndGetFile(acta1FileId, response);
            Archivo tabArchivoFile2 = validateAndGetFile(acta2FileId, response);

            if (tabArchivoFile1 == null || tabArchivoFile2 == null) {
                handleSingleFileCase(tabArchivoFile1, tabArchivoFile2, dataBase64, response);
            } else {
                handleBothFilesCase(tabArchivoFile1, tabArchivoFile2, response);
            }

            return createResponseEntity(response);

        } catch (IOException | ExecutionException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Archivo validateAndGetFile(Long fileId, GenericResponseAlternative<MonitoreoGetFilesResponse> response) {
        Optional<Archivo> optionalArchivo = this.archivoOrcRepository.findById(fileId);
        if (optionalArchivo.isEmpty()) {
            response.setMessage(fileId + " no encontrado en la base de datos.");
            return null;
        }
        return optionalArchivo.get();
    }

    private void handleSingleFileCase(Archivo file1, Archivo file2, MonitoreoGetFilesResponse dataBase64,
                                      GenericResponseAlternative<MonitoreoGetFilesResponse> response) throws IOException, ExecutionException, InterruptedException {
        if (file1 != null) {
            processFile(file1, dataBase64::setActa1File, response);
        }
        if (file2 != null) {
            processFile(file2, dataBase64::setActa2File, response);
        }
    }

    private void handleBothFilesCase(Archivo acta1FileId, Archivo acta2FileId,
                                     GenericResponseAlternative<MonitoreoGetFilesResponse> response)
            throws IOException, ExecutionException, InterruptedException {

        MonitoreoGetFilesResponse dataBase64 = this.storageService.loadFilesToBase64(acta1FileId, acta2FileId);
        if (dataBase64.getActa1File() == null && dataBase64.getActa2File() == null) {
            response.setMessage("Archivos no encontrados: " + acta1FileId + " " + acta2FileId);
            response.setSuccess(false);
        } else {
            setResponseMessage(dataBase64, acta1FileId.getId(), acta2FileId.getId(), response);
            response.setSuccess(true);
        }
    }

    private void processFile(Archivo fileId, Consumer<String> fileSetter, GenericResponseAlternative<MonitoreoGetFilesResponse> response)
            throws IOException, ExecutionException, InterruptedException {

        this.storageService.loadOnlyFileToBase64(fileId).thenAccept(dataOnlyFileBase64 -> {
            if (dataOnlyFileBase64 == null) {
                response.setMessage(ConstantesComunes.MENSAJE_ARCHIVO_NO_ENCONTRADO + fileId);
                response.setSuccess(false);
            } else {
                fileSetter.accept(dataOnlyFileBase64);
                response.setSuccess(true);
            }
        });
    }

    private ResponseEntity<GenericResponseAlternative<MonitoreoGetFilesResponse>> createResponseEntity(
            GenericResponseAlternative<MonitoreoGetFilesResponse> response) {
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    private void setResponseMessage(MonitoreoGetFilesResponse dataBase64, Long acta1FileId, Long acta2FileId,
                                    GenericResponseAlternative<MonitoreoGetFilesResponse> response) {
        if (dataBase64.getActa1File() == null && dataBase64.getActa2File() != null) {
            response.setMessage(ConstantesComunes.MENSAJE_ARCHIVO_NO_ENCONTRADO + acta1FileId);
        } else if (dataBase64.getActa1File() != null && dataBase64.getActa2File() == null) {
            response.setMessage(ConstantesComunes.MENSAJE_ARCHIVO_NO_ENCONTRADO + acta2FileId);
        }
        response.setData(dataBase64);
    }
}
