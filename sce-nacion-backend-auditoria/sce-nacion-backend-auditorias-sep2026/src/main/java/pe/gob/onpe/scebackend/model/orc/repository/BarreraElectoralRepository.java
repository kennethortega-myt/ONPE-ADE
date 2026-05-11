package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPolitica;

public interface BarreraElectoralRepository extends JpaRepository<AgrupacionPolitica, Long>{

	@Query(value = "SELECT * FROM fn_cifra_reporte_barrera_electoral("
			+ ":pi_esquema, "
    		+ ":pi_tipo_eleccion, "
    		+ ":pi_distrito_electoral, "
    		+ ":pi_agrupacion_politica)", nativeQuery = true)
	List<Map<String, Object>> barreraElectoralParlamentoDiputados(@Param("pi_esquema") String piEsquema,
						@Param("pi_tipo_eleccion") String idEleccion,
						@Param("pi_distrito_electoral") String distritoElectoral,
						@Param("pi_agrupacion_politica") String agrupacion);
	
	@Query(value = "SELECT * FROM fn_cifra_reporte_valla_senador("
			+ ":pi_esquema, "
    		+ ":pi_distrito_electoral, "
    		+ ":pi_agrupacion_politica)", nativeQuery = true)
	List<Map<String, Object>> barreraElectoralSenadores(@Param("pi_esquema") String piEsquema,						
						@Param("pi_distrito_electoral") String distritoElectoral,
						@Param("pi_agrupacion_politica") String agrupacion);
}
