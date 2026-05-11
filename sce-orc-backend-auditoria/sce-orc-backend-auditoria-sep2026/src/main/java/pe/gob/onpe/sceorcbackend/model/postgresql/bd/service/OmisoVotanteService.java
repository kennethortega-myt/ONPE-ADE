package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoVotante;

import java.util.List;

public interface OmisoVotanteService extends CrudService<OmisoVotante> {

    void deleteAllInBatch();
    List<OmisoVotante> buscarPorIdMesa(Long idMesa);
    List<OmisoVotante> buscarPorIdMesaActivo(Long idMesa);
    int inhabilitarOmisosVotantes(Long idMesa, String usuario);

    Long contarPorActivo(Integer activo);
}
