package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Candidato;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@SuppressWarnings("rawtypes")
public interface CandidatoRepository  extends JpaRepository<Candidato, Integer>, MigracionRepository<Candidato, String> {


	@Query(value = "SELECT DISTINCT c.* " +
			"FROM mae_candidato c " +
			"INNER JOIN mae_agrupacion_politica map2 " +
			"ON map2.n_agrupacion_politica_pk = c.n_agrupacion_politica " +
			"INNER JOIN det_ubigeo_eleccion_agrupacion_politica ag " +
			"ON ag.n_agrupacion_politica = map2.n_agrupacion_politica_pk " +
			"INNER JOIN det_ubigeo_eleccion due " +
			"ON due.n_det_ubigeo_eleccion_pk = ag.n_det_ubigeo_eleccion " +
			"INNER JOIN mae_ubigeo mu " +
			"ON mu.n_ubigeo_pk = due.n_ubigeo AND " +
			"CASE WHEN c.n_ubigeo IS NULL THEN 0 " +
			"ELSE CASE WHEN c.n_distrito_electoral IS NOT NULL THEN c.n_distrito_electoral " +
			"ELSE c.n_ubigeo END END = " +
			"CASE WHEN c.n_ubigeo IS NULL THEN 0 " +
			"ELSE CASE WHEN c.n_distrito_electoral IS NOT NULL THEN mu.n_distrito_electoral " +
			"ELSE c.n_ubigeo END END " +
			"INNER JOIN mae_centro_computo mcc " +
			"ON CASE WHEN c.n_ubigeo = 0 THEN 0 ELSE mcc.n_centro_computo_pk END = " +
			"CASE WHEN c.n_ubigeo = 0 THEN 0 ELSE mu.n_centro_computo END " +
			"WHERE mcc.c_codigo = ?1", nativeQuery = true)
	public List<Candidato> findByCc(String codigo);
	

	@Query(value = "SELECT * FROM fn_reporte_listado_autoridades(:pi_esquema, :pi_eleccion, :pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> candidatosPorOrgPol(@Param("pi_esquema") String esquema,
											 @Param("pi_eleccion") TypedParameterValue eleccion,
											 @Param("pi_centro_computo") TypedParameterValue centroComputo);
	
	@Query(value = "SELECT * FROM fn_reporte_listado_autoridades_en_consulta(:pi_esquema, :pi_eleccion, :pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> autoridadesEnConsulta(@Param("pi_esquema") String esquema,
											 @Param("pi_eleccion") TypedParameterValue eleccion,
											 @Param("pi_centro_computo") TypedParameterValue centroComputo);
	
}
