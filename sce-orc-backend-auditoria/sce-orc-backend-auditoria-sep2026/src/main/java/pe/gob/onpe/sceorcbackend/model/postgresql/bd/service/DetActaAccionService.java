package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;

import java.util.List;

public interface DetActaAccionService extends CrudService<DetActaAccion> {
    List<DetActaAccion> findByActaAndAccionAndIteracion(Acta acta, String accion, Integer iteracion);

    List<DetActaAccion> findByActa_IdAndAccionAndIteracion(Long acta, String accion, Integer iteracion);

    int inactivarPorActaYAccion(Long actaId, String accion);

    boolean existsByActaAccionTiempoAndUsuarioPrefix(Long actaId, String accion, String tiempo, String prefixUsuario);

    void deleteAllInBatch();
}
