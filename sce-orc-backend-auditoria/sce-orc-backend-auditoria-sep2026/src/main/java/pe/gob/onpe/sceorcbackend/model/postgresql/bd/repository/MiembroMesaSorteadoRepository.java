package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaSorteado;

import java.util.List;

public interface MiembroMesaSorteadoRepository extends JpaRepository<MiembroMesaSorteado, Long> {

    List<MiembroMesaSorteado> findByMesa(Mesa tabMesa);
    
    @Modifying
    @Query("DELETE FROM MiembroMesaSorteado")
    void deleteAllInBatch();
    
    @Query("SELECT mms FROM MiembroMesaSorteado mms "
    		+ "JOIN mms.mesa m "
    		+ "WHERE m.id = ?1 ")
    List<MiembroMesaSorteado> findByMesaId(Long idMesa);


  Long countByEstado(Integer estado);


}
