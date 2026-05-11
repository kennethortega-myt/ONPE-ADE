package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;

import java.util.List;
import java.util.Optional;

public interface DetActaPreferencialRepository extends JpaRepository<DetActaPreferencial, Long> {

  List<DetActaPreferencial> findByDetActa(DetActa detActa);

  List<DetActaPreferencial> findByDetActa_Acta_Id(Long actaId);

  Optional<DetActaPreferencial> findByDetActaAndLista(DetActa detActa, Integer lista);

  @Modifying
  @Query("""
    UPDATE DetActaPreferencial p
    SET p.votos = 0
    WHERE p.detActa.id IN (
        SELECT d.id FROM DetActa d WHERE d.acta.id = :actaId
    )
    """)
  void anularDetActaPreferencialContabilizada(@Param("actaId") Long actaId);



  @Query("SELECT d.estadoErrorMaterial " +
          "FROM DetActaPreferencial d " +
          "WHERE d.detActa.acta.id = :actaId " +
          "AND d.estadoErrorMaterial IS NOT NULL " +
          "AND d.estadoErrorMaterial <> ''")
  List<String> findErroresMaterialesByActaId(@Param("actaId") Long actaId);


  @Modifying
  @Query("DELETE FROM DetActaPreferencial")
  void deleteAllInBatch();
}
