package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import java.util.List;
import java.util.Optional;

public interface DetActaPreferencialService extends CrudService<DetActaPreferencial> {

    List<DetActaPreferencial> findByDetActa(DetActa detActa);

    Optional<DetActaPreferencial> getDetActaPreferencialByDetActaAndLista(DetActa detActa, Integer lista);

    void delete(DetActaPreferencial detActaPreferencial);

    void deleteAllInBatch();

    Optional<DetActaPreferencial> findById(Long id);
}
