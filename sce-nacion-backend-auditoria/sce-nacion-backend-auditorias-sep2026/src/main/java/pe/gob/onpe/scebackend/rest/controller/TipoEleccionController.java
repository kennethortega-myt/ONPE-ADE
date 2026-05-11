package pe.gob.onpe.scebackend.rest.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ITipoEleccionService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.HashMap;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/tipoEleccion")
public class TipoEleccionController {

    private final ITipoEleccionService tipoEleccionService;

    public TipoEleccionController(ITipoEleccionService tipoEleccionService) {
        this.tipoEleccionService = tipoEleccionService;
    }

    @PostMapping()
    public ResponseEntity<GenericResponse> save(@Valid @RequestBody DatosGeneralesRequestDto paramInputDto)  {
        tipoEleccionService.save(paramInputDto);
        GenericResponse response = new GenericResponse();
        response.setSuccess(Boolean.TRUE);
        response.setMessage("Se registró correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<GenericResponse> listarDocumentoElectoral()  {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.tipoEleccionService.listAll());
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<HashMap<String, Object>> update(@RequestBody DatosGeneralesRequestDto paramInputDto)  {
        tipoEleccionService.save(paramInputDto);

        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("resultado", 1);
        resultado.put("mensaje", "Se actualizó correctamente");

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, Object>> delete(@PathVariable(name = "id") Integer id)  {
        tipoEleccionService.updateStatus(SceConstantes.INACTIVO, id);

        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("resultado", 1);
        resultado.put("mensaje", "Se eliminó correctamente");

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @GetMapping("/hijos/{idPadre}")
    public ResponseEntity<GenericResponse> listarDocumentoElectoralHijos(@PathVariable(name="idPadre") Integer idPadre)  {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.tipoEleccionService.listAllTipoEleccionHijo(idPadre));
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
}
