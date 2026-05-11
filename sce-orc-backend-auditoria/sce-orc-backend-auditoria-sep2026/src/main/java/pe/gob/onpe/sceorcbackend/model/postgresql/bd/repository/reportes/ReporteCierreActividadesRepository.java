package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;

public interface ReporteCierreActividadesRepository extends JpaRepository<Acta, Long> {

	@Query(value = "SELECT * FROM fn_reporte_historico_cierre_reapertura_centro_computo("
			+ ":pi_esquema, "    		
    		+ ":pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> listarReporteHistoricoCierreReapertura(@Param("pi_esquema") String piEsquema,
															@Param("pi_centro_computo") Integer idCentroComputo);
	
	@Query(value = "SELECT * FROM fn_reporte_cierre_reapertura_centro_computo("
			+ ":pi_esquema, "    		
    		+ ":pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> listarReporteCierreActividades(@Param("pi_esquema") String piEsquema,
															@Param("pi_centro_computo") Integer idCentroComputo);
	
	@Query(value = "SELECT * FROM fn_reporte_cierre_reapertura_centro_computo("
			+ ":pi_esquema, "    		
    		+ ":pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> listarReporteReaperturaActividades(@Param("pi_esquema") String piEsquema,
															@Param("pi_centro_computo") Integer idCentroComputo);
	
}
