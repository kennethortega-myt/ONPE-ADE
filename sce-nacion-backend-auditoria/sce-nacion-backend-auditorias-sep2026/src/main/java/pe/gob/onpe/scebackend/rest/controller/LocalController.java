package pe.gob.onpe.scebackend.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.LocalVotacionDto;
import pe.gob.onpe.scebackend.model.service.impl.LocalVotacionService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/local")
public class LocalController {

	private final LocalVotacionService localVotacionService;

    public LocalController(LocalVotacionService localVotacionService) {
        this.localVotacionService = localVotacionService;
    }

    @GetMapping("/ubigeo/{idUbigeo}")
    public ResponseEntity<List<LocalVotacionDto>> listDepartamentos(@PathVariable(name = "idUbigeo") Long idUbigeo){
    	List<LocalVotacionDto> locales = this.localVotacionService.findByUbigeo(idUbigeo);
        return new ResponseEntity<>(locales, HttpStatus.OK);
    }
	
}
