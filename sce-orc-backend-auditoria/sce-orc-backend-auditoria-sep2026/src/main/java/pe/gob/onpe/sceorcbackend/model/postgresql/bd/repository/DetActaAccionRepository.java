package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;

import java.util.List;

public interface DetActaAccionRepository extends JpaRepository<DetActaAccion, Long> {
	
	List<DetActaAccion> findByActa_Id(Long idActa);
	
    List<DetActaAccion> findByActaAndAccionAndIteracion(Acta acta, String accion, Integer iteracion);

    List<DetActaAccion> findByActa_IdAndAccionAndTiempoOrderByIteracion(Long acta, String accion, String tiempo);

    List<DetActaAccion> findByActa_IdAndAccionOrderByIteracion(Long acta, String accion);

    List<DetActaAccion> findByActa_IdAndAccionAndIteracion (Long aLong, String accion, Integer iteracion);

    @Transactional
    @Modifying
    @Query("UPDATE DetActaAccion d SET d.activo = 0 WHERE d.acta.id = :actaId AND d.accion = :accion")
    int inactivarPorActaYAccion(@Param("actaId") Long actaId, @Param("accion") String accion);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DetActaAccion d " +
           "WHERE d.acta.id = :actaId AND d.accion = :accion AND d.tiempo = :tiempo " +
           "AND d.usuarioAccion LIKE :prefixUsuario ESCAPE '\\' AND d.activo = 1")
    boolean existsByActaAccionTiempoAndUsuarioPrefix(@Param("actaId") Long actaId,
                                                     @Param("accion") String accion,
                                                     @Param("tiempo") String tiempo,
                                                     @Param("prefixUsuario") String prefixUsuario);
    
    @Modifying
    @Query("DELETE FROM DetActaAccion")
    void deleteAllInBatch();
}
