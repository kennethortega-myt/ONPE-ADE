package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmConfigProcesoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAdmConfiguracionProcesoExportElectoralMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IConfiguracionProcesoElectoralExportService;
import pe.gob.onpe.scebackend.model.repository.ConfiguracionProcesoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class ConfiguracionProcesoElectoralExportService extends MigracionService<AdmConfigProcesoElectoralExportDto, ConfiguracionProcesoElectoral, String> implements IConfiguracionProcesoElectoralExportService {

    @Autowired
    private IAdmConfiguracionProcesoExportElectoralMapper configuracionProcesoElectoralMapper;


    @Autowired
    private ConfiguracionProcesoElectoralRepository configuracionProcesoElectoralRepository;

    @Override
    public MigracionRepository<ConfiguracionProcesoElectoral, String> getRepository() {
        return this.configuracionProcesoElectoralRepository;
    }

    @Override
    public IMigracionMapper<AdmConfigProcesoElectoralExportDto, ConfiguracionProcesoElectoral> getMapper() {
        return this.configuracionProcesoElectoralMapper;
    }

}
