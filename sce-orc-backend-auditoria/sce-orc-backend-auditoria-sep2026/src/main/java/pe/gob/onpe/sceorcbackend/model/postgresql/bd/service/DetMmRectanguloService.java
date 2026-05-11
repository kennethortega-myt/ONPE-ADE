package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;



import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetMmRectangulo;

import java.util.List;

public interface DetMmRectanguloService extends CrudService<DetMmRectangulo> {

    List<DetMmRectangulo> findByMesaId(Long mesaId);

    void deleteByMesaIdAndType(Long idMesa, String type);
    void deleteByMesaId(Long idMesa);

    void deleteAllInBatch();

}
