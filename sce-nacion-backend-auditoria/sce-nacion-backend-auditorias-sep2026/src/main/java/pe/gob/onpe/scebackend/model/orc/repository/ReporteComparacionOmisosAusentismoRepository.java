package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

public interface ReporteComparacionOmisosAusentismoRepository extends JpaRepository<Acta, Long> {

	@Query(value = "SELECT * FROM fn_reporte_comparacion_omisos_ausentismo("
						+ ":pi_esquema, "
			    		+ ":pi_n_ambito_electoral, "
			    		+ ":pi_n_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> listarReporteComparacionOmisosAusentismo(@Param("pi_esquema") String piEsquema,
															@Param("pi_n_ambito_electoral") Integer idAmbito,
															@Param("pi_n_centro_computo") Integer idCentroComputo);
}
