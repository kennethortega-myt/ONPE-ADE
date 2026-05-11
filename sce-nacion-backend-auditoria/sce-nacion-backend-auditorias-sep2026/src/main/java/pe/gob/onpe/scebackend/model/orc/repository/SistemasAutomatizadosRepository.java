package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

public interface SistemasAutomatizadosRepository extends JpaRepository<Acta, Long>{

	@Query(value = "SELECT * FROM fn_reporte_sistemas_automatizados("
			+ ":pi_c_esquema, "
    		+ ":pi_n_eleccion, "
    		+ ":pi_n_ambito_electoral, "
    		+ ":pi_n_centro_computo, "
    		+ ":pi_c_ubigeo,"
    		+ ":pi_c_estado_stae)", nativeQuery = true)
	List<Map<String, Object>> listarReporteSistemasAutomatizados(@Param("pi_c_esquema") String piEsquema,
										@Param("pi_n_eleccion") Integer idEleccion,
										@Param("pi_n_ambito_electoral") Integer idAmbito,
										@Param("pi_n_centro_computo") Integer idCentroComputo,
										@Param("pi_c_ubigeo") String ubigeo,
										@Param("pi_c_estado_stae") String estado);
}
