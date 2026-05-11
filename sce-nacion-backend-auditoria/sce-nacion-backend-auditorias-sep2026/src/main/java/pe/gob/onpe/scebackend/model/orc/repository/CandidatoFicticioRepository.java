package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.CandidatoFicticio;

public interface CandidatoFicticioRepository extends JpaRepository<CandidatoFicticio, Integer> {

	@Query(value = "SELECT DISTINCT c.* " +
			"FROM mae_candidato_ficticio c " +
			"INNER JOIN mae_agrupacion_politica_ficticia map2 " +
			"ON map2.n_agrupacion_politica_ficticia_pk = c.n_agrupacion_politica " +
			"INNER JOIN det_ubigeo_eleccion_agrupacion_politica ag " +
			"ON ag.n_agrupacion_politica = map2.n_agrupacion_politica_ficticia_pk " +
			"INNER JOIN det_ubigeo_eleccion due " +
			"ON due.n_det_ubigeo_eleccion_pk = ag.n_det_ubigeo_eleccion " +
			"INNER JOIN mae_ubigeo mu " +
			"ON mu.n_ubigeo_pk = due.n_ubigeo AND " +
			"CASE WHEN c.n_ubigeo IS NULL THEN 0 " +
			"ELSE CASE WHEN c.n_distrito_electoral IS NOT NULL THEN c.n_distrito_electoral " +
			"ELSE c.n_ubigeo END " +
			"END = CASE WHEN c.n_ubigeo IS NULL THEN 0 " +
			"ELSE CASE WHEN c.n_distrito_electoral IS NOT NULL THEN mu.n_distrito_electoral " +
			"ELSE c.n_ubigeo END " +
			"END " +
			"INNER JOIN mae_centro_computo mcc " +
			"ON mcc.n_centro_computo_pk = mu.n_centro_computo " +
			"WHERE mcc.c_codigo = ?1", nativeQuery = true)
	public List<CandidatoFicticio> findByCc(String codigo);
	
	
}
