package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import java.util.List;
import java.util.Optional;


public interface DetActaOpcionRepository extends JpaRepository<DetActaOpcion, Integer>{


	List<DetActaOpcion> findByDetActa(DetActa detActa);

	List<DetActaOpcion> findByDetActaOrderByPosicion(DetActa detActa);

  Optional<DetActaOpcion> findByDetActaAndPosicion(DetActa detActa, Integer posiscion);


  @Query(value = """
    SELECT SUM(o.n_votos)
    FROM det_acta_opcion o
    WHERE o.n_det_acta = (
        SELECT d.n_det_acta_pk 
        FROM det_acta d 
        WHERE d.n_acta = :actaId 
        ORDER BY d.n_det_acta_pk ASC 
        LIMIT 1
    )
""", nativeQuery = true)
  Long obtenerSumaVotosPrimerDetActa(@Param("actaId") Long actaId);


    @Modifying
    @Query("""
      UPDATE DetActaOpcion o
      SET o.votos = CASE
          WHEN o.posicion = :posicionNulos THEN :valorPasarNulos
          ELSE 0
      END
      WHERE o.detActa.id IN (
          SELECT d.id FROM DetActa d WHERE d.acta.id = :actaId
      )
    """)
    void anularDetActaOpcionContabilizada(
            @Param("actaId") Long actaId,
            @Param("valorPasarNulos") Long valorPasarNulos,
            @Param("posicionNulos") Long posicionNulos
    );

	@Modifying
    @Query("DELETE FROM DetActaOpcion")
    void deleteAllInBatch();
	
}
