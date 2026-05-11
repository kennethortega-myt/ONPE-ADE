package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import java.util.List;
import java.util.Optional;

public interface DetActaRepository extends JpaRepository<DetActa, Long> {

    List<DetActa> findByActa(Acta acta);

    List<DetActa> findByActa_Id(Long idActa);

    List<DetActa> findByActa_IdOrderByPosicion(Long idActa);

    Optional<DetActa> findByActaIdAndPosicion(Long actaId, Long posicion);


    @Query("SELECT d.estadoErrorMaterial FROM DetActa d WHERE  d.acta.id = :actaId AND d.estadoErrorMaterial IS NOT NULL AND d.estadoErrorMaterial <> ''")
    List<String> findAllEstadoErrorMaterialRaw(Long actaId);


    @Modifying
    @Query("""
    UPDATE DetActa d
    SET d.votos = CASE
        WHEN d.posicion = :posicionNulos THEN :valorPasarNulos
        ELSE 0
    END
    WHERE d.acta.id = :actaId
""")
    void anularDetActaContabilizada(
        @Param("actaId") Long actaId,
        @Param("valorPasarNulos") Long valorPasarNulos,
        @Param("posicionNulos") Long posicionNulos
    );


    @Modifying
    @Query("DELETE FROM DetActa")
    void deleteAllInBatch();

}
