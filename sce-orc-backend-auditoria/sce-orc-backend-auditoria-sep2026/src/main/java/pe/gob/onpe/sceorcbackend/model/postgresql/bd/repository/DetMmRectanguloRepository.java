package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetMmRectangulo;
import java.util.List;

public interface DetMmRectanguloRepository extends JpaRepository<DetMmRectangulo, Long> {

    List<DetMmRectangulo> findByMesaId(Long mesaId);

    void deleteByMesaIdAndType(Long idMesa, String type);

    void deleteByMesaId(Long idMesa);


}
