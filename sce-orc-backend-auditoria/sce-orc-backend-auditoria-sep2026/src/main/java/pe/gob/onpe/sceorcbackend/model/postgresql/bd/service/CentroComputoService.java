package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;

import java.util.Optional;

public interface CentroComputoService extends CrudService<CentroComputo> {

  Optional<CentroComputo> findByCodigo(String codigo);
  Optional<CentroComputo> getCentroComputoActual();

}
