package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.importar.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.mapper.IEleccionMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ACCESO_TOTAL)
@RestController
@Validated
@CrossOrigin
@RequestMapping("/monitoreoNacion")
public class MonitoreoOrcController {

    private final EleccionService eleccionService;
    private final IEleccionMapper eleccionMapper;

    public MonitoreoOrcController(EleccionService eleccionService, IEleccionMapper eleccionMapper) {
        this.eleccionService = eleccionService;
        this.eleccionMapper = eleccionMapper;
    }

    @GetMapping("/{procesoId}/elecciones")
    public ResponseEntity<GenericResponse<List<EleccionDto>>> listEleccionesByProceso(
    		@PathVariable("procesoId") Long id) {
        
    	GenericResponse<List<EleccionDto>> genericResponse = new GenericResponse<>();
        List<EleccionDto> elecciones = eleccionMapper.mapToEleccionDtoList(this.eleccionService.findEleccionesByProceso2(id));
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(elecciones);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
