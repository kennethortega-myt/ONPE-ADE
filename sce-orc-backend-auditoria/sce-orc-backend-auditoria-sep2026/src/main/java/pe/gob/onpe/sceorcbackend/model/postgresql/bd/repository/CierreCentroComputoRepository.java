package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabCierreCentroComputo;

import java.util.Optional;

@Repository
public interface CierreCentroComputoRepository extends JpaRepository<TabCierreCentroComputo, Long> {
    @Query("SELECT COALESCE(MAX(c.correlativo), 0) FROM TabCierreCentroComputo c WHERE c.centroComputo = :centroComputo")
    Integer getUltimoCorrelativoByCentroComputo(@Param("centroComputo") Integer centroComputo);

    @Query("SELECT c FROM TabCierreCentroComputo c WHERE c.centroComputo = :centroComputo " +
            "AND c.reapertura = 0 AND c.activo = 1 ORDER BY c.correlativo DESC")
    Optional<TabCierreCentroComputo> findCierreActivoByCentroComputo(@Param("centroComputo") Integer centroComputo);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM TabCierreCentroComputo c " +
            "WHERE c.centroComputo = :centroComputo AND c.reapertura = 0 AND c.activo = 1")
    boolean existsCierreActivoByCentroComputo(@Param("centroComputo") Integer centroComputo);

    @Query("SELECT c FROM TabCierreCentroComputo c WHERE c.id = :cierreId " +
            "AND c.usuarioCierre = :usuario AND c.reapertura = 0 AND c.activo = 1")
    Optional<TabCierreCentroComputo> findCierreActivoByIdAndUsuario(@Param("cierreId") Long cierreId,
                                                                    @Param("usuario") String usuario);
}
