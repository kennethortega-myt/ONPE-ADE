package pe.gob.onpe.scebackend.rest.controller;


import java.util.Date;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.request.VerificacionHabilitacionDiaEleccionRequest;
import pe.gob.onpe.scebackend.model.dto.response.VerificacionHabilitacionDiaEleccionResponse;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.service.impl.ProcesoElectoralService;
import pe.gob.onpe.scebackend.utils.DateUtil;

@RestController
@RequestMapping("/proceso-electoral")
public class ProcesoElectoralController {

	private final ProcesoElectoralService procesoElectoralService;

    public ProcesoElectoralController(ProcesoElectoralService procesoElectoralService) {
        this.procesoElectoralService = procesoElectoralService;
    }

    @GetMapping("/acronimo/")
    public ResponseEntity<VerificacionHabilitacionDiaEleccionResponse> save(@RequestBody VerificacionHabilitacionDiaEleccionRequest procesoElectoralDto) {
        Optional<ProcesoElectoral> procesoElectoral = this.procesoElectoralService.findByAcronimo(procesoElectoralDto.getAcronimo());
        String formatoFecha = procesoElectoralDto.getFormatoFechaConvocatoria();
        Date fechaConvocatoria = procesoElectoral.map(ProcesoElectoral::getFechaConvocatoria).orElse(null);
        String sfechaConvocatoria = DateUtil.getDateString(fechaConvocatoria, formatoFecha);
        VerificacionHabilitacionDiaEleccionResponse response = new VerificacionHabilitacionDiaEleccionResponse();
        response.setAcronimo(procesoElectoral.map(ProcesoElectoral::getAcronimo).orElse(null));
        response.setFormatoFechaConvocatoria(formatoFecha);
        response.setFechaConvocatoria(sfechaConvocatoria);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
}
