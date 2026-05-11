package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcCatalogo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.OrcDetalleCatalogoEstructuraProjection;

import java.util.List;

public interface DetalleCatalogoEstructuraRepository extends JpaRepository<OrcDetalleCatalogoEstructura, Integer> {

    OrcDetalleCatalogoEstructura findByIdAndColumnaAndCodigoI(Integer idTabla, String columna, Integer codigo);

    List<OrcDetalleCatalogoEstructura> findByColumnaAndActivoOrderByCodigoS(String columna, Integer activo);
    @Query("SELECT d.id as id, d.columna as columna, d.nombre as nombre, " +
            "d.codigoI as codigoI, d.codigoS as codigoS, d.tipo as tipo, " +
            "d.orden as orden, d.activo as activo, d.catalogo.id as catalogo " +
            "FROM OrcDetalleCatalogoEstructura d " +
            "WHERE d.catalogo.id = :catalogoId AND d.columna = :columna AND coalesce(d.codigoS, '') not in ('WW') AND d.activo = :activo ORDER BY d.nombre")
    List<OrcDetalleCatalogoEstructuraProjection> findByCatalogo(@Param("catalogoId") Integer  catalogoId,
                                                                @Param("columna") String columna,
                                                                @Param("activo") Integer activo);


}
