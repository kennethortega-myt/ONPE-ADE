package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetLeRectangulo;

import java.util.List;

public interface DetLeRectanguloRepository extends JpaRepository<DetLeRectangulo, Long> {

    List<DetLeRectangulo> findByMesaId(Long mesaId);

    void deleteByMesaIdAndType(Long idMesa, String type);

    void deleteByMesaId(Long idMesa);

    @Modifying
    @Query("DELETE FROM DetLeRectangulo")
    void deleteAllInBatch();
}
