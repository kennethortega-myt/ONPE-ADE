package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoMiembroMesa;

import java.util.List;

public interface OmisoMiembroMesaService extends CrudService<OmisoMiembroMesa> {

    void deleteAllInBatch();
    List<OmisoMiembroMesa> buscarPorMesa(Long idMesa);
    List<OmisoMiembroMesa> buscarOmisoActivoPorMesa(Long idMesa);
    int inhabilitarOmisoMiembroMesa(Long idMesa, String usuario);

    Long contarPorActivo(Integer activo);
}
