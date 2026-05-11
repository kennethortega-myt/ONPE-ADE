package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaFormato;

public interface CabActaFormatoService extends CrudService<CabActaFormato> {


    void deleteAllInBatch();
}
