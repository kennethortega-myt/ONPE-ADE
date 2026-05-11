package pe.gob.onpe.sceorcbackend.model.postgresql.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.Seccion;
import java.util.List;
import java.util.Optional;

public interface SeccionRepository extends JpaRepository<Seccion, Integer> {

  Optional<Seccion> findByAbreviatura(String abreviatura);

  Optional<Seccion> findByNombre(String nombre);


  @Query("SELECT s FROM Seccion s WHERE s.abreviatura IN :abreviaturas")
  List<Seccion> findIdsByAbreviaturas(@Param("abreviaturas") List<String> abreviaturas);

  @Modifying
  @Query("DELETE FROM Seccion")
  void deleteAllInBatch();

}
