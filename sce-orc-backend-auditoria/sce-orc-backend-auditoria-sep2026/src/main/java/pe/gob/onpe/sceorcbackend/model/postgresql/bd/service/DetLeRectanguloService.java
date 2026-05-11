package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetLeRectangulo;

import java.util.List;

public interface DetLeRectanguloService extends CrudService<DetLeRectangulo> {

    List<DetLeRectangulo> findByMesaId(Long mesaId);
    void deleteByMesaIdAndType(Long idMesa, String type);

    void deleteByMesaId(Long idMesa);
    void deleteAllInBatch();
}
