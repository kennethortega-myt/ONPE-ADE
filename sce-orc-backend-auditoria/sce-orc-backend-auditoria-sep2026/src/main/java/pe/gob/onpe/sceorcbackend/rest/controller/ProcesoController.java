package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pe.gob.onpe.sceorcbackend.model.dto.FiltroProcesoAmbitoDto;
import pe.gob.onpe.sceorcbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.ProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/proceso")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProcesoController {

    private final MaeProcesoElectoralService maeProcesoElectoralService;
    private final EleccionService maeEleccionService;

    public ProcesoController(MaeProcesoElectoralService maeProcesoElectoralService,
        EleccionService maeEleccionService) {
        this.maeProcesoElectoralService = maeProcesoElectoralService;
        this.maeEleccionService = maeEleccionService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("/")
    public ResponseEntity<GenericResponse<List<ProcesoElectoralResponseDTO>>> listProcesos() {
        GenericResponse<List<ProcesoElectoralResponseDTO>> genericResponse = new GenericResponse<>();

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.maeProcesoElectoralService.findAll2());

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("/{id}/elecciones")
    public ResponseEntity<GenericResponse<List<EleccionResponseDto>>> listEleccionesByProceso(@PathVariable("id") Long id) {
        GenericResponse<List<EleccionResponseDto>> genericResponse = new GenericResponse<>();
        List<EleccionResponseDto> elecciones = this.maeEleccionService.findEleccionesByProceso2(id);
        List<EleccionResponseDto> eleccionesOrdenadas = elecciones.stream().sorted(Comparator.comparing(EleccionResponseDto::getId)).toList();

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(eleccionesOrdenadas);//elecciones

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @PostMapping("/tipo-ambito-por-acronimo/")
    public ResponseEntity<GenericResponse<ProcesoAmbitoDto>> getTipoAmbito(@RequestBody FiltroProcesoAmbitoDto filtroProcesoAmbitoDto) {
        GenericResponse<ProcesoAmbitoDto> genericResponse = new GenericResponse<>();
        String acronimo = filtroProcesoAmbitoDto.getAcronimo();
        ProcesoAmbitoDto procesoAmb = this.maeProcesoElectoralService.getTipoAmbito(acronimo);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(procesoAmb);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("/{id-proceso}/tipo-ambito/")
    public ResponseEntity<GenericResponse<ProcesoAmbitoDto>> getTipoAmbitoPorIdProceso(@PathVariable("id-proceso") Long id) {
        GenericResponse<ProcesoAmbitoDto> genericResponse = new GenericResponse<>();
        ProcesoAmbitoDto procesoAmb = this.maeProcesoElectoralService.getTipoAmbitoPorIdProceso(id);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(procesoAmb);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/elecciones")
    public ResponseEntity<GenericResponse<List<EleccionDto>>> getElecciones() {
        GenericResponse<List<EleccionDto>> genericResponse = new GenericResponse<>();
        List<EleccionDto> procesoAmb = this.maeProcesoElectoralService.getElecciones();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(procesoAmb);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
