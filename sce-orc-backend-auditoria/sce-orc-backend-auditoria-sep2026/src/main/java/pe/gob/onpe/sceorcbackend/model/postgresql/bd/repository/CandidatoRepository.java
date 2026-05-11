package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Candidato;

import java.util.List;
import java.util.Map;

public interface CandidatoRepository extends JpaRepository<Candidato, Integer> {

	@Modifying
	@Query("DELETE FROM Candidato")
	void deleteAllInBatch();

	/*
	 * usamos TypedParameterValue para los parametros que sus valores pueden ser null
	 */
	@Query(value = "SELECT * FROM fn_reporte_listado_autoridades(:pi_esquema, :pi_eleccion, :pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> candidatosPorOrgPol(@Param("pi_esquema") String esquema,
												  @Param("pi_eleccion") TypedParameterValue eleccion,
												  @Param("pi_centro_computo") TypedParameterValue centroComputo);

	@Query(value = "SELECT * FROM fn_reporte_listado_autoridades_en_consulta(:pi_esquema, :pi_eleccion, :pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> autoridadesEnConsulta(@Param("pi_esquema") String esquema,
													@Param("pi_eleccion") TypedParameterValue eleccion,
													@Param("pi_centro_computo") TypedParameterValue centroComputo);

}
