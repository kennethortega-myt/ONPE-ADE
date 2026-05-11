package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.PadronElectoral;

public interface PadronElectoralRepository extends JpaRepository<PadronElectoral, Long> {

	@Query("SELECT p FROM PadronElectoral p JOIN p.mesa m JOIN m.localVotacion lv JOIN lv.ubigeo u JOIN u.centroComputo cc WHERE cc.codigo = :cc ORDER BY p.id")
	Page<PadronElectoral> importarOrc(@Param("cc") String cc, Pageable pageable);
	
	@Query("SELECT p FROM PadronElectoral p ORDER BY p.id")
	Page<PadronElectoral> findAll(Pageable pageable);

    PadronElectoral findByDocumentoIdentidad(String documentoIdentidad);
    
    @Query(value = """
    		SELECT COALESCE(SUM(tm.n_cantidad_electores_habiles + tm.n_cantidad_electores_habiles_extranjeros), 0)
    	    FROM tab_mesa tm
    	    INNER JOIN mae_local_votacion mlv ON tm.n_local_votacion = mlv.n_local_votacion_pk
    	    INNER JOIN mae_ubigeo mu ON mu.n_ubigeo_pk = mlv.n_ubigeo
    	    INNER JOIN mae_centro_computo mcc ON mcc.n_centro_computo_pk = mu.n_centro_computo
    	    WHERE mcc.c_codigo = :ccCodigo
    	    """, nativeQuery = true)
    int cantidadPorCc(@Param("ccCodigo") String ccCodigo);
    
    @Query(value = """
    		SELECT p.*
    		FROM tab_padron_electoral p
    		INNER JOIN tab_mesa m              ON p.n_mesa = m.n_mesa_pk
    		INNER JOIN mae_local_votacion lv   ON m.n_local_votacion = lv.n_local_votacion_pk
    		INNER JOIN mae_ubigeo u            ON lv.n_ubigeo = u.n_ubigeo_pk
    		INNER JOIN mae_centro_computo cc       ON u.n_centro_computo = cc.n_centro_computo_pk
    		WHERE cc.c_codigo = :cc
    		  AND (:lastId IS NULL OR p.n_padron_electoral_pk > :lastId)
    		ORDER BY p.n_padron_electoral_pk
    		""",
    		nativeQuery = true)
    List<PadronElectoral> importarOptOrc(
    		@Param("cc") String cc,
    		@Param("lastId") Long lastId,
    		Pageable pageable);
	
}
