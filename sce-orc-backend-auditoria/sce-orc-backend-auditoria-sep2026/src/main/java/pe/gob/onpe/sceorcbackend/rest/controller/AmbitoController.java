package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import pe.gob.onpe.sceorcbackend.model.dto.AmbitoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AmbitoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AmbitoElectoralService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@RestController
@RequestMapping("ambitos")
public class AmbitoController {

    Logger logger = LoggerFactory.getLogger(AmbitoController.class);

    private final AmbitoElectoralService maeAmbitoElectoralService;

    public AmbitoController(AmbitoElectoralService maeAmbitoElectoralService) {
        this.maeAmbitoElectoralService = maeAmbitoElectoralService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("/")
    public ResponseEntity<GenericResponse<List<AmbitoDto>>> getAmbitosAll() {
        GenericResponse<List<AmbitoDto>> genericResponse = new GenericResponse<>();
        List<AmbitoElectoral> ambitos = this.maeAmbitoElectoralService.findAll();

        List<AmbitoDto> ambitosDto = null;
        if (ambitos != null && !ambitos.isEmpty()) {
            ambitosDto = ambitos.parallelStream()
                    .map(dto -> {
                        AmbitoDto ambito = new AmbitoDto();
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

}
