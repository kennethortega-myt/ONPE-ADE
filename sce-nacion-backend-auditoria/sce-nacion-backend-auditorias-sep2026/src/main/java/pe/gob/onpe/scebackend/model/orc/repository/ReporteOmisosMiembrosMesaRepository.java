package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

public interface ReporteOmisosMiembrosMesaRepository extends JpaRepository<Acta, Long>{

	@Query(value = "SELECT * FROM fn_reporte_avance_registro_omiso_miembro_mesa("
												+ ":pi_esquema, "
									    		+ ":pi_eleccion, "
									    		+ ":pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> listarReporteOmisos(@Param("pi_esquema") String piEsquema,
												@Param("pi_eleccion") Integer idEleccion,
												@Param("pi_centro_computo") Integer idCentroComputo);
}
