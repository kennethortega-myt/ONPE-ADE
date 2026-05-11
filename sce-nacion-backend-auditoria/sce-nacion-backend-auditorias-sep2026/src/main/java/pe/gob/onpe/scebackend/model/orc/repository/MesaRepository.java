package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Mesa;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@SuppressWarnings("rawtypes")
public interface MesaRepository extends JpaRepository<Mesa, Long>, MigracionRepository<Mesa, String>{

	@Query("SELECT m FROM Mesa m "
			+ "JOIN m.localVotacion l "
			+ "JOIN l.ubigeo u "
			+ "JOIN u.centroComputo c "
			+ "WHERE c.codigo = ?1")
	public List<Mesa> findByCc(String codigo);


	Mesa findByCodigo(String numeroMesa);

	List<Mesa> findByCodigoIn(List<String> lstCodigos);

	@Query(
			value = "SELECT DISTINCT * FROM fn_reporte_mesas_por_ubigeo(:pi_esquema, :pi_c_centro_computo, " +
					"(SELECT NULLIF (:pi_departamento,'0')), " +
					"(SELECT NULLIF (:pi_provincia,'0')), " +
					"(SELECT NULLIF (:pi_distrito,'0'))) " +
					"ORDER BY c_ubigeo, c_codigo_local, c_numero_mesa",
			nativeQuery = true)
	public List<Map<String, Object>> getReporteMesaPorUbigeo(@Param("pi_esquema") String piEsquema,
															 @Param("pi_c_centro_computo") TypedParameterValue piCentroComputo,
															 @Param("pi_departamento") String piDepartamento,
															 @Param("pi_provincia") String piProvincia,
															 @Param("pi_distrito") String piDistrito);
	
	@Query(value = """
    		SELECT 
    		tm.* FROM tab_mesa tm 
			INNER JOIN mae_local_votacion lv ON lv.n_local_votacion_pk = tm.n_local_votacion
			INNER JOIN mae_ubigeo ub ON ub.n_ubigeo_pk = lv.n_ubigeo
			INNER JOIN mae_centro_computo cc ON cc.n_centro_computo_pk = ub.n_centro_computo
			where cc.c_codigo = :cc
    		AND (:lastId IS NULL OR tm.n_mesa_pk > :lastId)
    		ORDER BY tm.n_mesa_pk
    		""",
    		nativeQuery = true)
    List<Mesa> importar(
    		@Param("cc") String cc,
    		@Param("lastId") Long lastId,
    		Pageable pageable);
	
	
	@Query(value = """
    		SELECT 
    		COUNT(1)
    		FROM tab_mesa tm 
			INNER JOIN mae_local_votacion lv ON lv.n_local_votacion_pk = tm.n_local_votacion
			INNER JOIN mae_ubigeo ub ON ub.n_ubigeo_pk = lv.n_ubigeo
			INNER JOIN mae_centro_computo cc ON cc.n_centro_computo_pk = ub.n_centro_computo
			where cc.c_codigo = :cc
    		""",
    		nativeQuery = true)
    int contar(
    		@Param("cc") String cc);



}
