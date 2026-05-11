package pe.gob.onpe.scebatchpr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebatchpr.entities.admin.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebatchpr.repository.admin.ConfiguracionProcesoElectoralRepository;
import pe.gob.onpe.scebatchpr.service.ConfiguracionProcesoElectoralService;

@Service
public class ConfiguracionProcesoElectoralServiceImpl implements ConfiguracionProcesoElectoralService {

	private ConfiguracionProcesoElectoralRepository configuracionProcesoElectoralRepository;
	
	public ConfiguracionProcesoElectoralServiceImpl(
			ConfiguracionProcesoElectoralRepository configuracionProcesoElectoralRepository){
		this.configuracionProcesoElectoralRepository = configuracionProcesoElectoralRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ConfiguracionProcesoElectoral> findByVigente() {
		return configuracionProcesoElectoralRepository.findByVigente();
	}
	
	@Override
	@Transactional(readOnly = true)
	public ConfiguracionProcesoElectoral findByEsquema(String esquema) {
		return configuracionProcesoElectoralRepository.findByEsquema(esquema.trim().toUpperCase());
	}

	
}
