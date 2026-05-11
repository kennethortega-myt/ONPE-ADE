package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetCcResolucion;

import java.util.List;

public interface DetCcResolucionRepository extends JpaRepository<DetCcResolucion, Long> {

  List<DetCcResolucion> findByCabCcResolucion_IdAndActivo(Long idCabCcResolucion, Integer activo);

  @Modifying
  @Query("DELETE FROM DetCcResolucion ")
  void deleteAllInBatch();
}
