package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.scebackend.model.orc.projections.DetalleCatalogoEstructuraProjection;

import java.util.List;


public interface OrcDetalleCatalogoEstructuraRepository  extends JpaRepository<OrcDetalleCatalogoEstructura, Integer> {

    OrcDetalleCatalogoEstructura findByCatalogoIdAndColumnaAndCodigoI(Integer idCatalogo,String columna, Integer ncodigo);

    @Query("SELECT d.columna as columna, d.nombre as nombre, d.codigoI as codigoI, " +
            "d.codigoS as codigoS, d.orden as orden ,d.activo as activo " +
            "FROM OrcDetalleCatalogoEstructura d " +
            "WHERE d.catalogo.id = :catalogoId AND d.columna = :columna AND d.activo = :activo ORDER BY d.nombre")
    List<DetalleCatalogoEstructuraProjection> findByCatalogoId(@Param("catalogoId") Integer catalogoId,
                                                               @Param("columna") String columna,
                                                               @Param("activo") Integer activo);
}
