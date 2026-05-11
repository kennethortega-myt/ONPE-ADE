package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import java.util.List;
import java.util.Optional;

public interface TabAutorizacionRepository extends JpaRepository<TabAutorizacion, Long> {

    List<TabAutorizacion> findByAutorizacionAndUsuarioCreacion(int autorizacion, String usuarioCreacion);

    List<TabAutorizacion> findByAutorizacion(int autorizacion);

    List<TabAutorizacion> findByAutorizacionOrderByFechaModificacionDesc(int autorizacion);

    List<TabAutorizacion> findByAutorizacionAndTipoAutorizacionAndActivo(int autorizacion, String tipo, Integer activo);

    Optional<TabAutorizacion> findFirstByEstadoAprobacionInAndUsuarioCreacionAndTipoAutorizacionAndTipoDocumentoAndIdDocumentoAndActivo(
            List<String> estadosBuscados, String usuarioCreacion, String tipoAutorizacion, String tipoDocumento, Long idDocumento, Integer activo);

    @Query("SELECT MAX(t.numeroAutorizacion) FROM TabAutorizacion t")
    Long findMaxNumeroAutorizacion();

    @Modifying
    @Query("DELETE FROM TabAutorizacion")
    void deleteAllInBatch();


    @Modifying
    @Query("DELETE FROM TabAutorizacion WHERE id <> (SELECT MAX(id) FROM TabAutorizacion)")
    void deleteAllInBatchExceptLast();

}
