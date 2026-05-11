package pe.gob.onpe.sceorcbackend.rest.controller.reporte;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ConfiguracionProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.mapper.IProcesoElectoralMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ACCESO_TOTAL)
@RestController
@Validated
@CrossOrigin
@RequestMapping({"/configuracionProcesoElectoral", "/comun/proceso-electoral"})
public class ConfiguracionProcesoElectoralController {

    private final MaeProcesoElectoralService maeProcesoElectoralService;
    private final IProcesoElectoralMapper procesoElectoralMapper;

    public ConfiguracionProcesoElectoralController(MaeProcesoElectoralService maeProcesoElectoralService, IProcesoElectoralMapper procesoElectoralMapper) {
        this.maeProcesoElectoralService = maeProcesoElectoralService;
        this.procesoElectoralMapper = procesoElectoralMapper;
    }


    @GetMapping()
    public ResponseEntity<GenericResponse<List<ConfiguracionProcesoElectoralResponseDTO>>> listAll() {
        GenericResponse<List<ConfiguracionProcesoElectoralResponseDTO>> genericResponse = new GenericResponse<>();
        genericResponse.setSuccess(Boolean.TRUE);
        List<ConfiguracionProcesoElectoralResponseDTO> res =
                procesoElectoralMapper.mapToConfiguracionProcesoElectoralResponseDTOList(this.maeProcesoElectoralService.findAll2());
        genericResponse.setData(res);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
