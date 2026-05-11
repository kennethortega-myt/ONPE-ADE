package pe.gob.onpe.scebatchpr.service;

import java.util.List;

import pe.gob.onpe.scebatchpr.entities.admin.ConfiguracionProcesoElectoral;

public interface ConfiguracionProcesoElectoralService {

	List<ConfiguracionProcesoElectoral> findByVigente();
	
	ConfiguracionProcesoElectoral findByEsquema(String esquema);
	
}
