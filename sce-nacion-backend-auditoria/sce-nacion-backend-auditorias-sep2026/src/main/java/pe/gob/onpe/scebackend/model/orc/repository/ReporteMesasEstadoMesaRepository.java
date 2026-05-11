package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

public interface ReporteMesasEstadoMesaRepository extends JpaRepository<Acta, Long>{

	@Query(value = "SELECT * FROM fn_reporte_mesas_por_estado(:pi_esquema, "
														+ ":pi_eleccion, "
														+ ":pi_codigo_ambito, "
														+ ":pi_centro_computo, "
														+ ":pi_departamento, "
														+ ":pi_provincia, "
														+ ":pi_distrito, "
														+ ":pi_estado_mesa)", nativeQuery = true)
	List<Map<String, Object>> listarReporteMesasEstado(@Param("pi_esquema") String piEsquema,
														@Param("pi_eleccion") Integer idEleccion,
														@Param("pi_codigo_ambito") String ambito,
														@Param("pi_centro_computo") String centroComputo,
														@Param("pi_departamento") Integer departamento,
														@Param("pi_provincia") Integer provincia,
														@Param("pi_distrito") Integer distrito,
														@Param("pi_estado_mesa") String estado);
	
	@Query(value = "SELECT * FROM fn_reporte_mesas_por_estado_acta(:pi_esquema, "
															+ ":pi_eleccion, "
															+ ":pi_codigo_ambito, "
															+ ":pi_centro_computo, "
															+ ":pi_departamento, "
															+ ":pi_provincia, "
															+ ":pi_distrito, "
															+ ":pi_estado_acta)", nativeQuery = true)
	List<Map<String, Object>> listarReporteMesasEstadoActa(@Param("pi_esquema") String piEsquema,
															@Param("pi_eleccion") Integer idEleccion,
															@Param("pi_codigo_ambito") String ambito,
															@Param("pi_centro_computo") String centroComputo,
															@Param("pi_departamento") Integer departamento,
															@Param("pi_provincia") Integer provincia,
															@Param("pi_distrito") Integer distrito,
															@Param("pi_estado_acta") Integer estado);
	
	@Query(value = "SELECT * FROM fn_reporte_mesas_por_estado_digitacion(:pi_esquema, "
													+ ":pi_eleccion, "
													+ ":pi_codigo_ambito, "
													+ ":pi_centro_computo, "
													+ ":pi_departamento, "
													+ ":pi_provincia, "
													+ ":pi_distrito, "
													+ ":pi_estado_digitacion)", nativeQuery = true)
	List<Map<String, Object>> listarReporteMesasEstadoDigitacion(@Param("pi_esquema") String piEsquema,
																@Param("pi_eleccion") Integer idEleccion,
																@Param("pi_codigo_ambito") String ambito,
																@Param("pi_centro_computo") String centroComputo,
																@Param("pi_departamento") Integer departamento,
																@Param("pi_provincia") Integer provincia,
																@Param("pi_distrito") Integer distrito,
																@Param("pi_estado_digitacion") Integer estado);
	
}
