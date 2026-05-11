package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Candidato;

public interface ProbablesCandidatosElectosRepository extends JpaRepository<Candidato, Integer>{

	@Query(value = "SELECT * FROM fn_cifra_obtiene_candidato_electo("
								+ ":pi_esquema, "
					    		+ ":pi_tipo_eleccion, "
					    		+ ":pi_distrito_electoral, "
					    		+ ":pi_agrupacion_politica, "
					    		+ ":pi_codigo_cargo)", nativeQuery = true)
	List<Map<String, Object>> listarReporteProbablesCandidatosElectos(@Param("pi_esquema") String piEsquema,
										@Param("pi_tipo_eleccion") String idEleccion,
										@Param("pi_distrito_electoral") String distritoElectoral,
										@Param("pi_agrupacion_politica") String agrupacion,
										@Param("pi_codigo_cargo") Integer cargo);

}
