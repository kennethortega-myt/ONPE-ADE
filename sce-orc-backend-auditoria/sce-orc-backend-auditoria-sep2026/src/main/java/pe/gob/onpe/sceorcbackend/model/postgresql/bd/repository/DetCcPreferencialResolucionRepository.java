package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetCcPreferencialResolucion;
import java.util.List;

public interface DetCcPreferencialResolucionRepository extends JpaRepository<DetCcPreferencialResolucion, Long> {

  List<DetCcPreferencialResolucion> findByDetCcResolucion_IdAndActivo(Long idDetCcResolucion, Integer activo);

  @Modifying
  @Query("DELETE FROM DetCcPreferencialResolucion ")
  void deleteAllInBatch();
}
