package pe.gob.onpe.scebackend.multitenant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.CabActaDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.orc.entities.TransmisionEnvio;
import pe.gob.onpe.scebackend.model.orc.repository.EleccionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.TransmisionEnvioRepository;
import pe.gob.onpe.scebackend.model.repository.ConfiguracionProcesoElectoralRepository;

@RestController
@Validated
@RequestMapping("/dummy")
public class DummyController {

	private final ConfiguracionProcesoElectoralRepository adminRepo;

	private final TransmisionEnvioRepository transmisionRepo;

	private final EleccionRepository eleccionRepo;

    public DummyController(ConfiguracionProcesoElectoralRepository adminRepo, TransmisionEnvioRepository transmisionRepo, EleccionRepository eleccionRepo) {
        this.adminRepo = adminRepo;
        this.transmisionRepo = transmisionRepo;
        this.eleccionRepo = eleccionRepo;
    }

    @GetMapping("/")
    public ResponseEntity<GenericResponse> save(@RequestBody CabActaDto acta) {
        GenericResponse genericResponse = new GenericResponse();

        List<TransmisionEnvio> transmisiones = transmisionRepo.findAll();
        List<TransmisionEnvio> transmisionesDto = null;
        transmisionesDto = new ArrayList<>();
        TransmisionEnvio transmisionDto = null;
        for(TransmisionEnvio t:transmisiones) {
        	transmisionDto = new TransmisionEnvio();
        	transmisionDto.setTramaDato(t.getTramaDato());
        	transmisionesDto.add(transmisionDto);
        }
        
        List<ConfiguracionProcesoElectoral> configuraciones = adminRepo.findAll();
        List<ConfiguracionProcesoElectoral> configuracionesDto = null;
        ConfiguracionProcesoElectoral configuracionDto = null;
        configuracionesDto = new ArrayList<>();
        for(ConfiguracionProcesoElectoral c:configuraciones) {
        	configuracionDto = new ConfiguracionProcesoElectoral();
        	configuracionDto.setAcronimo(c.getAcronimo());
        	configuracionDto.setNombreEsquemaPrincipal(c.getNombreEsquemaPrincipal());
        	configuracionesDto.add(configuracionDto);
        }
        
        List<Eleccion> elecciones = eleccionRepo.findAll();
        List<Eleccion> eleccionesDto = null;
        Eleccion eleccionDto = null;
        eleccionesDto = new ArrayList<>();
        for(Eleccion c:elecciones) {
        	eleccionDto = new Eleccion();
        	eleccionDto.setCodigo(c.getCodigo());
        	eleccionesDto.add(eleccionDto);
        }
        
        DummyResponse dummyResponse = new DummyResponse();
        dummyResponse.setConfiguraciones(configuracionesDto);
        dummyResponse.setTransmisiones(transmisionesDto);
        dummyResponse.setElecciones(eleccionesDto);
     
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(dummyResponse);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
	
}
