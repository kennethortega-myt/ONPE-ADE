package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DistritoElectoral;

import java.util.Optional;

public interface DistritoElectoralService extends CrudService<DistritoElectoral> {
	
    Optional<DistritoElectoral> getById(Integer id);
}
