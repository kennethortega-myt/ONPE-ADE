package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import java.util.List;
import java.util.Optional;

public interface DetActaOpcionService extends CrudService<DetActaOpcion>  {

  Optional<DetActaOpcion> findById(Integer id);

  Optional<DetActaOpcion> getDetActaOpcionByDetActaAndPosicion(DetActa detActa, Integer posicion);

  List<DetActaOpcion> findByDetActa(DetActa detActa);

  List<DetActaOpcion> findByDetActaOrderByPosicion(DetActa detActa);

  Long obtenerSumaVotosPrimerDetActa(Long actaId);

  void anularDetActaOpcionContabilizada(Long actaId, Long valorPasarNulos, Long posicionNulos);

  void deleteAllInBatch();
}
