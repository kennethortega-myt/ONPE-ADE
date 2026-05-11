package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

public interface ActasNoDevueltasRepository extends JpaRepository<Acta, Long> {

	@Query(value = "SELECT * FROM fn_reporte_actas_no_devueltas("
								+ ":pi_esquema, "
					    		+ ":pi_eleccion, "
					    		+ ":pi_centro_computo, "
					    		+ ":pi_ubigeo, "
					    		+ ":pi_tipo_impresion)", nativeQuery = true)
	List<Map<String, Object>> listarReporteActasNoDevueltas(@Param("pi_esquema") String piEsquema,
															@Param("pi_eleccion") Integer idEleccion,
															@Param("pi_centro_computo") Integer idCentroComputo,
															@Param("pi_ubigeo") String ubigeo,
															@Param("pi_tipo_impresion") Integer tipoImpresion);
	
	@Query(value = "SELECT * FROM fn_reporte_actas_no_devueltas_cpr("
										+ ":pi_esquema, "
							    		+ ":pi_eleccion, "
							    		+ ":pi_centro_computo, "
							    		+ ":pi_ubigeo, "
							    		+ ":pi_tipo_impresion)", nativeQuery = true)
	List<Map<String, Object>> listarReporteActasNoDevueltasCPR(@Param("pi_esquema") String piEsquema,
																@Param("pi_eleccion") Integer idEleccion,
																@Param("pi_centro_computo") Integer idCentroComputo,
																@Param("pi_ubigeo") String ubigeo,
																@Param("pi_tipo_impresion") Integer tipoImpresion);
	
	@Query(value = "SELECT * FROM fn_lista_estado_error_material(:pi_esquema)", nativeQuery = true)
	List<Map<String, Object>> listarErrorMaterial(@Param("pi_esquema") String piEsquema);

}
