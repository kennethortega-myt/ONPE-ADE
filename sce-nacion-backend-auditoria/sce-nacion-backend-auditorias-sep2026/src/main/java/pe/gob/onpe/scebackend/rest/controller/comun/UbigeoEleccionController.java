package pe.gob.onpe.scebackend.rest.controller.comun;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.entities.UbigeoDestructurado;
import pe.gob.onpe.scebackend.model.service.IUbigeoDestructuradoService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/ubigeoEleccion")
public class UbigeoEleccionController {


    private final IUbigeoDestructuradoService ubigeoDestructuradoService;

    public UbigeoEleccionController(IUbigeoDestructuradoService ubigeoDestructuradoService) {
        this.ubigeoDestructuradoService = ubigeoDestructuradoService;
    }

    @GetMapping("/{idEleccion}/departamentos")
    public ResponseEntity<GenericResponse> listDepartamentos(@PathVariable(name = "idEleccion") Long idEleccion){
        GenericResponse genericResponse = new GenericResponse();
    	List<UbigeoDestructurado> departamentos = this.ubigeoDestructuradoService.getUbigeoNivel3(idEleccion);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(departamentos);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/{idEleccion}/departamento/{idDepartamento}/provincias")
    public ResponseEntity<GenericResponse> listProvincias(@PathVariable(name = "idDepartamento") Long idDepartamento, @PathVariable(name = "idEleccion") Long idEleccion){
        GenericResponse genericResponse = new GenericResponse();
        List<UbigeoDestructurado> departamentos = this.ubigeoDestructuradoService.getUbigeoNivel2(idDepartamento, idEleccion);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(departamentos);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/{idEleccion}/provincia/{idProvincia}/distritos")
    public ResponseEntity<GenericResponse> listDistritos(@PathVariable(name = "idProvincia") Long idProvincia, @PathVariable(name = "idEleccion") Long idEleccion){
        GenericResponse genericResponse = new GenericResponse();
        List<UbigeoDestructurado> departamentos = this.ubigeoDestructuradoService.getUbigeoNivel1(idProvincia, idEleccion);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(departamentos);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
