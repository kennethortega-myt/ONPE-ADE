package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetCcOpcionResolucion;

public interface DetCcOpcionResolucionRepository extends JpaRepository<DetCcOpcionResolucion, Long> {

  @Modifying
  @Query("DELETE FROM DetCcOpcionResolucion ")
  void deleteAllInBatch();
}
