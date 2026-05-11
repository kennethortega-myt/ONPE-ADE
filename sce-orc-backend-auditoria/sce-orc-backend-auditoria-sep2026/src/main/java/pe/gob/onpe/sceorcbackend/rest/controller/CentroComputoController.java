package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@RestController
@RequestMapping("centros-computo")
public class CentroComputoController {

    Logger logger = LoggerFactory.getLogger(CentroComputoController.class);

    private final CentroComputoService maeCentroComputoService;

    public CentroComputoController(CentroComputoService maeCentroComputoService) {
        this.maeCentroComputoService = maeCentroComputoService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("/")
    public ResponseEntity<GenericResponse<List<CentroComputoDto>>> getAmbitosAll() {
        GenericResponse<List<CentroComputoDto>> genericResponse = new GenericResponse<>();
        List<CentroComputo> centrosComputo = this.maeCentroComputoService.findAll();

        List<CentroComputoDto> ccDto = null;
        if (centrosComputo != null && !centrosComputo.isEmpty()) {
            ccDto = centrosComputo.parallelStream()
                    .map(dto -> {
                        CentroComputoDto ambito = new CentroComputoDto();
                        ambito.setId(dto.getId());
                        ambito.setCodigo(dto.getCodigo());
                        ambito.setNombre(dto.getNombre());
                        return ambito;
                    })
                    .toList();
        }


        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(ccDto);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
