package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabConfirmPc;

public interface TabConfirmPcService extends CrudService<TabConfirmPc> {


    void deleteAllInBatch();

}
