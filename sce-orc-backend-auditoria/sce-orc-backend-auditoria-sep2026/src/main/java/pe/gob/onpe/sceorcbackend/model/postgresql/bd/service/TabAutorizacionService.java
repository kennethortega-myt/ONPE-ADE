package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import java.util.List;
import java.util.Optional;

public interface TabAutorizacionService extends CrudService<TabAutorizacion> {

    List<TabAutorizacion> findByAutorizacionAndUsuarioCreacion(int autorizacion, String usuarioCreacion);

    List<TabAutorizacion> findByAutorizacion(int autorizacion);

    List<TabAutorizacion> findByAutorizacionOrderByFechaModificacionDesc(int autorizacion);

    List<TabAutorizacion> findByAutorizacionAndTipoAutorizacionAndActivo(int autorizacion, String tipo, Integer activo);

    Optional<TabAutorizacion> findById(Long id);

    void deleteAllInBatch();

    void deleteAllInBatchExceptLast();

    Long count();

    Long findMaxNumeroAutorizacion();
}
