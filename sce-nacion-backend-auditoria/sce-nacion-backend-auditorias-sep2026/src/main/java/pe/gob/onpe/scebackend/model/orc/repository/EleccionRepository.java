package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface EleccionRepository extends JpaRepository<Eleccion, Long>, MigracionRepository<Eleccion, String> {

	@Query("SELECT distinct e FROM Eleccion e JOIN e.ubigeosElecciones ue JOIN ue.ubigeo u JOIN u.centroComputo c WHERE c.codigo = ?1")
	public List<Eleccion> findByCc(String codigo);
	
	// para monitoreo
	@Query("SELECT distinct e FROM Eleccion e JOIN e.procesoElectoral p WHERE p.id = ?1 order by e.id asc")
	List<Eleccion> findByProcesoElectoralId(Long id);

	@Query("SELECT distinct e FROM Eleccion e JOIN e.procesoElectoral p WHERE p.id = ?1 and e.preferencial = 1 order by e.id asc")
	List<Eleccion> findEleccPreferencialByProcesoElectoralId(Long id);
	
	@Query(value = "SELECT * FROM fn_obtener_datos_eleccion_x_proceso(:c_esquema,:n_proceso_electoral)", nativeQuery = true)
	  List<Map<String, Object>> obtenerEleccionPorProcesoElectoralId(@Param("c_esquema") String piEsquema,
																	@Param("n_proceso_electoral") Integer idProceso);

}
