package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.entities.Catalogo;
import pe.gob.onpe.scebackend.model.service.ICatalogoService;
import pe.gob.onpe.scebackend.utils.RoleAutority;


@RestController
@Validated
@RequestMapping("/catalogo")
public class CatalogoController {

    private final ICatalogoService catalogoService;

    public CatalogoController(ICatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("getCatalogo")
    public ResponseEntity<GenericResponse> listCatalogo(@RequestParam(value="tabla") String tabla) {
        GenericResponse genericResponse = new GenericResponse();
        Catalogo catalogo = this.catalogoService.getCatalogoByTabla(tabla);

        if (catalogo != null) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(catalogo);
        }
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
