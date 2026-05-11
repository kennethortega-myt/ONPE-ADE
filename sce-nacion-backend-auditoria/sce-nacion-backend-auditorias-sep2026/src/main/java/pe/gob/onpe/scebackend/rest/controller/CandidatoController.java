package pe.gob.onpe.scebackend.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;
import pe.gob.onpe.scebackend.model.service.ICandidatoService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@RestController
@RequestMapping("/candidatos")
public class CandidatoController {
	
	private final ICandidatoService candidatoService;

    public CandidatoController(ICandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/{cc}")
	@Transactional("locationTransactionManager")
    public ResponseEntity<GenericResponse> listar(@PathVariable(name = "cc") String cc) {
        GenericResponse genericResponse = new GenericResponse();
        List<CandidatoExportDto> candidatoDto = this.candidatoService.listarCandidatosPorCc(cc);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(candidatoDto);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
}
