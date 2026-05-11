package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaSorteado;

import java.util.List;

public interface MiembroMesaSorteadoService extends CrudService<MiembroMesaSorteado>  {
    List<MiembroMesaSorteado> findByMesa(Mesa tabMesa);

    Long count();
}
