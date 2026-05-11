package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@RestController
@RequestMapping("/catalogo")
public class CatalogoController {

    private final OrcDetalleCatalogoEstructuraService orcDetalleCatalogoEstructuraService;

    public CatalogoController(OrcDetalleCatalogoEstructuraService orcDetalleCatalogoEstructuraService) {
        this.orcDetalleCatalogoEstructuraService = orcDetalleCatalogoEstructuraService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("/det-catalogo-estructura")
    public ResponseEntity<GenericResponse<List<DetCatalogoEstructuraDTO>>> obtenerCatalogoEstructura(
            @RequestParam(value = "c_maestro") String cMaestro,
            @RequestParam(value = "c_columna") String cColumna) {

        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(
                true,
                "Lista de Catálogos " + cColumna,
                this.orcDetalleCatalogoEstructuraService.findByMaestroAndColumna(cMaestro, cColumna)));
    }


}
