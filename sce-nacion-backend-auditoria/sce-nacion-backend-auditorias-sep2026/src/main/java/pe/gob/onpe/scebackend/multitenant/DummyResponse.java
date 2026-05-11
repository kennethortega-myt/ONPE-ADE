package pe.gob.onpe.scebackend.multitenant;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.orc.entities.TransmisionEnvio;

@Getter
@Setter
public class DummyResponse {

	private List<TransmisionEnvio> transmisiones;
	private List<ConfiguracionProcesoElectoral> configuraciones;
	private List<Eleccion> elecciones;
	
}
