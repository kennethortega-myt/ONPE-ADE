package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaCola;

public interface MiembroMesaColaRepository extends JpaRepository<MiembroMesaCola, Long> {

    @Modifying
    @Query("DELETE FROM MiembroMesaCola")
    void deleteAllInBatch();
    
    @Query("SELECT mmc FROM MiembroMesaCola mmc "
    		+ "JOIN mmc.mesa m "
    		+ "WHERE m.id = ?1 ")
    List<MiembroMesaCola> findByMesaId(Long idMesa);

    @Query("SELECT mmc FROM MiembroMesaCola mmc "
            + "JOIN mmc.mesa m "
            + "WHERE mmc.activo = 1 and m.id = ?1 ")
    List<MiembroMesaCola> findByMesaIdActivo(Long idMesa);

    @Modifying
    @Transactional
    @Query("UPDATE MiembroMesaCola o " +
            "SET o.fechaModificacion = CURRENT_TIMESTAMP, o.usuarioModificacion = :usuario, o.activo= 0 where o.mesa.id = :idMesa and o.activo = 1")
    int inhabilitarOmisoMiembroMesaCola(@Param("usuario") String usuario,
                                    @Param("idMesa") Long idMesa);
}
