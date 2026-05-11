package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabCcResolucion;
import java.util.Optional;

public interface CabCcResolucionService extends CrudService<CabCcResolucion> {

    void deleteAllInBatch();

    Optional<CabCcResolucion> findByActaAndResolucionAndEstadoCambioAndActivo(Long idActa, Long idResolucion, String estadoCambio, Integer activo);

    void spRegistrarCcResolucion(String esquema,Long idActa,String estadoCambio, Long idResolucion, String usuario);


}
