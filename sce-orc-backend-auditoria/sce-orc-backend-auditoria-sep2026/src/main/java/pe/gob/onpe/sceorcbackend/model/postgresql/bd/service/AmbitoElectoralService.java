package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AmbitoElectoral;

import java.util.Optional;

public interface AmbitoElectoralService extends CrudService<AmbitoElectoral> {

   Optional<AmbitoElectoral> findOneAmbitoElectoral();
}
