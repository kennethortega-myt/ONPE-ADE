package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;


public interface EleccionRepository extends JpaRepository<Eleccion, Long> {

  List<Eleccion> findByProcesoElectoralIdOrderByCodigoAsc(Long idProceso);

  Optional<Eleccion> findByCodigo(String codigo);

  @Query("SELECT e.id FROM Eleccion e WHERE e.codigo IN :codigos")
  List<Integer> findIdsByCodigos(@Param("codigos") List<String> codigos);
  
  @Modifying
  @Query("DELETE FROM Eleccion")
  void deleteAllInBatch();

  Optional<Eleccion> findByProcesoElectoralIdAndPrincipal(Long idProceso, Integer principal);
  
  @Query(value = "SELECT * FROM fn_obtener_datos_eleccion_x_proceso(:c_esquema,:n_proceso_electoral)", nativeQuery = true)
  List<Map<String, Object>> obtenerEleccionPorProcesoElectoralId(@Param("c_esquema") String piEsquema,
																@Param("n_proceso_electoral") Integer idProceso);

}
