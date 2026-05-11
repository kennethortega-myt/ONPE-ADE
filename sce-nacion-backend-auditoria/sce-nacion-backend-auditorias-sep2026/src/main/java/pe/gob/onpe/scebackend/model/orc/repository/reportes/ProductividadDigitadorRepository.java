package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.DetActaAccion;


public interface ProductividadDigitadorRepository extends JpaRepository<DetActaAccion, Long> {

	@Query(value = "SELECT * FROM fn_reporte_productividad_digitador("
			+ ":pi_c_esquema, "
			+ ":pi_n_eleccion, "
    		+ ":pi_n_centro_computo, "
    		+ ":pi_c_usuario)", nativeQuery = true)
	List<Map<String, Object>> listarReporteProductividadDigitador(@Param("pi_c_esquema") String piEsquema,
																  @Param("pi_n_eleccion") Integer idEleccion,
										@Param("pi_n_centro_computo") Integer idCentroComputo,
										@Param("pi_c_usuario") String usuario);
	
	@Query(value = "SELECT * FROM fn_obtener_usuarios_digitador("
			+ ":pi_esquema, "
    		+ ":pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> listarUsuariosDigitador(@Param("pi_esquema") String piEsquema,
													  @Param("pi_centro_computo") Integer idCentroComputo);
}
