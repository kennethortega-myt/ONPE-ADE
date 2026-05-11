package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetOtroDocumento;

public interface DetOtroDocumentoService extends CrudService<DetOtroDocumento>{

    void deleteAllInBatch();

}
