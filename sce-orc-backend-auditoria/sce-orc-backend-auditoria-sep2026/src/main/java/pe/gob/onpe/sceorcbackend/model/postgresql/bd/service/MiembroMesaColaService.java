package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaCola;

import java.util.List;

public interface MiembroMesaColaService extends CrudService<MiembroMesaCola> {

    void deleteAllInBatch();
    List<MiembroMesaCola> listMiembroMesaColaActivoByMesaId(Long idMesa);
    int inhabilitarOmisoMiembroMesaCola(Long idMesa, String usuario);
}
