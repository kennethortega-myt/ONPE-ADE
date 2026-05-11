package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

public interface AsistenciaPersonerosRepository extends JpaRepository<Acta, Long> {

	@Query(value = "SELECT * FROM fn_reporte_lista_asistencia_personeros("
									+ ":pi_esquema, "
						    		+ ":pi_eleccion, "
						    		+ ":pi_n_centro_computo, " 
						    		+ ":pi_c_ubigeo, "
						    		+ ":pi_n_mesa)", nativeQuery = true)
	List<Map<String, Object>> listaAsistenciaPersoneros(@Param("pi_esquema") String piEsquema,
														@Param("pi_eleccion") Integer idEleccion,
														@Param("pi_n_centro_computo") Integer idCentroComputo,
														@Param("pi_c_ubigeo") String ubigeo,
														@Param("pi_n_mesa") Integer mesa);
}
