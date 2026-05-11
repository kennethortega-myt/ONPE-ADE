package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

public interface ReporteRelacionPuestaCeroRepository extends JpaRepository<Acta, Long> {

	@Query(value = "SELECT * FROM fn_reporte_estado_puesta_cero("
									+ ":pi_esquema, "
						    		+ ":pi_centro_computo, "
						    		+ ":pi_estado)", nativeQuery = true)
	List<Map<String, Object>> listarReporteRelacionPuestaCero(@Param("pi_esquema") String piEsquema,
														@Param("pi_centro_computo") String centroComputo,
														@Param("pi_estado") Integer estado);
}
