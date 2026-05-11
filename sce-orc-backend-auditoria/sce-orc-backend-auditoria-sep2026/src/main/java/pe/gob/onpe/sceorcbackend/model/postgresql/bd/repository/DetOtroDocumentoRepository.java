package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabOtroDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetOtroDocumento;

import java.util.List;

public interface DetOtroDocumentoRepository extends JpaRepository<DetOtroDocumento, Long> {


    List<DetOtroDocumento> findByCabOtroDocumento(CabOtroDocumento cabOtroDocumento);

    @Query("""
    SELECT d FROM DetOtroDocumento d
    JOIN d.mesa m
    WHERE
    m.codigo = :nroMesa AND
    d.codTipoDocumento = :tipoDocumento AND
    d.activo = 1
    """)
    List<DetOtroDocumento> findByNroMesaAndTipoDocumento(
            @Param("nroMesa") String nroMesa,
            @Param("tipoDocumento") String tipoDocumento);

    @Modifying
    @Query("DELETE FROM DetOtroDocumento")
    void deleteAllInBatch();
    
    @Query("""
    SELECT d FROM DetOtroDocumento d
    JOIN d.mesa m
    WHERE
    m.id = :idMesa
    """)
    List<DetOtroDocumento> findByIdMesa(Long idMesa);
}
