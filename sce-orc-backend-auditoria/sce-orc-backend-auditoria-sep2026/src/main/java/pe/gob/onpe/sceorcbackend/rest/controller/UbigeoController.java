package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.dto.LocalVotacionDTO;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.NivelUbigeoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.LocalVotacionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UbigeoService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@RestController
@RequestMapping("/ubigeo")
public class UbigeoController {

    private final UbigeoService ubigeoService;
    private final LocalVotacionService localVotacionService;

    public UbigeoController(UbigeoService ubigeoService, LocalVotacionService localVotacionService) {
        this.ubigeoService = ubigeoService;
        this.localVotacionService = localVotacionService;
    }


    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/departamentos")
    public ResponseEntity<GenericResponse<List<NivelUbigeoDto>>> listarDepartamentos(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseHelperException.createSuccessResponse("Se obtuvo la lista de departamentos correctamente.", this.ubigeoService.buscarDepartamentos());
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/provincias/{idDepartamento}")
    public ResponseEntity<GenericResponse<List<NivelUbigeoDto>>> listarProvincia(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
        @PathVariable("idDepartamento") Long idDepartamento) {

        return ResponseHelperException.createSuccessResponse("Se obtuvo la lista de provincias correctamente.", this.ubigeoService.buscarProvincias(idDepartamento));
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/distritos/{idProvincia}")
    public ResponseEntity<GenericResponse<List<NivelUbigeoDto>>> listarDistritos(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
        @PathVariable("idProvincia") Long idProvincia) {

        return ResponseEntity.status(HttpStatus.OK).body(
                new GenericResponse<>(Boolean.TRUE, "Se obtuvo la lista de distritos correctamente.", this.ubigeoService.buscaDistritos(idProvincia)));

    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/locales-votacion/{idUbigeo}")
    public ResponseEntity<GenericResponse<List<LocalVotacionDTO>>> listarLocalesVotacionPorUbigeo(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
        @PathVariable("idUbigeo") Long idUbigeo) {

        return ResponseEntity.status(HttpStatus.OK).body(
                new GenericResponse<>(Boolean.TRUE, "Se obtuvo la lista de locales de votación correctamente.", this.localVotacionService.listarLocalesPorUbigeo(idUbigeo)));

    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
    @GetMapping("/elecciones/{idUbigeo}")
    public ResponseEntity<GenericResponse<List<EleccionDto>>> listarEleccionesPorUbigeo(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
        @PathVariable("idUbigeo") Long idUbigeo) {

        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(Boolean.TRUE, "Se obtuvo la lista de elecciones correctamente.", this.ubigeoService.buscaEleccionesPorUbigeo(idUbigeo)));

    }

}
