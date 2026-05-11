package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import java.util.List;
import java.util.Optional;

public interface DetActaService extends CrudService<DetActa> {

    List<DetActa> findByCabActa(Acta acta);

    Optional<DetActa> getDetActa(Long actaAlearotia, long posicion);

    Optional<DetActa> findById(Long id);

    void deleteAllInBatch();
    
    List<DetActa> findByIdActaOrderByPosicion(Long idActa);

}
