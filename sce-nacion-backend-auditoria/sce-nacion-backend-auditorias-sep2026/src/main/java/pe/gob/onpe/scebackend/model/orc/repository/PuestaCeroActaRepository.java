package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.orc.projections.PuestaCeraActaProjection;

public interface PuestaCeroActaRepository extends JpaRepository<Acta, Long> {

    @Query(value = """
            SELECT
                ca.n_acta_pk,
                ca.n_mesa,
                due.n_eleccion
            FROM cab_acta ca
            INNER JOIN det_ubigeo_eleccion due
                ON due.n_det_ubigeo_eleccion_pk = ca.n_det_ubigeo_eleccion
            WHERE ca.n_mesa IN (:mesas)
            """, nativeQuery = true)
    List<PuestaCeraActaProjection> buscarActasPorMesas(@Param("mesas") List<Long> mesas);

    @Query(value = "CALL sp_registrar_puesta_cero_acta(:pi_esquema, :pi_c_mesa, :pi_n_eleccion, :pi_aud_usuario, null, null)", nativeQuery = true)
    Map<String, Object> puestaCeroActa(
            @Param("pi_esquema") String piEsquema,
            @Param("pi_c_mesa") String piMesa,
            @Param("pi_n_eleccion") Integer piEleccion,
            @Param("pi_aud_usuario") String piAudUsuario);

}
