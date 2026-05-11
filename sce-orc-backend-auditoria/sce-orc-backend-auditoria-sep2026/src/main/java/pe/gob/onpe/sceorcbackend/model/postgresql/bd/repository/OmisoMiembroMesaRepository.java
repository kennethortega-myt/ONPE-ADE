package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoMiembroMesa;

public interface OmisoMiembroMesaRepository extends JpaRepository<OmisoMiembroMesa, Long> {

    @Modifying
    @Query("DELETE FROM OmisoMiembroMesa")
    void deleteAllInBatch();
    
    @Query("SELECT omm FROM OmisoMiembroMesa omm "
    		+ "JOIN omm.miembroMesaSorteado mms "
    		+ "JOIN mms.mesa m "
    		+ "WHERE m.id = ?1 ")
    List<OmisoMiembroMesa> findByMesaId(Long idMesa);
    
    @Query("SELECT omm FROM OmisoMiembroMesa omm "
    		+ "JOIN omm.miembroMesaSorteado mms "
    		+ "WHERE mms.id = ?1 ")
    List<OmisoMiembroMesa> findByMiembroMesaSorteadoId(Long idMiembroMesaSorteado);

    @Query("SELECT omm FROM OmisoMiembroMesa omm "
            + "JOIN omm.miembroMesaSorteado mms "
            + "JOIN mms.mesa m "
            + "WHERE omm.activo = 1 and m.id = ?1 ")
    List<OmisoMiembroMesa> findByMesaIdActivo(Long idMesa);

    @Modifying
    @Transactional
    @Query("UPDATE OmisoMiembroMesa o " +
            "SET o.fechaModificacion = CURRENT_TIMESTAMP, o.usuarioModificacion = :usuario, o.activo= 0 where o.mesa.id = :idMesa and o.activo = 1")
    int inhabilitarOmisoMiembroMesa(@Param("usuario") String usuario,
                                @Param("idMesa") Long idMesa);


    @Query("SELECT COUNT(p) FROM OmisoMiembroMesa p WHERE p.activo = :activo")
    Long contarPorActivo(@Param("activo") Integer activo);
}
