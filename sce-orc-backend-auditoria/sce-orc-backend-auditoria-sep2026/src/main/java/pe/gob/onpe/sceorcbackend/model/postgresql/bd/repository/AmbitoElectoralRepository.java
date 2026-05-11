package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AmbitoElectoral;

@Repository
public interface AmbitoElectoralRepository extends JpaRepository<AmbitoElectoral, Long> {

	@Modifying
    @Query("DELETE FROM AmbitoElectoral")
    void deleteAllInBatch();
	
	@Query(value = "SELECT * FROM fn_obtener_datos_ambito_electoral_x_centro_computo(:c_esquema, :n_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> listarAmbitoElectoralPorCentroComputo(@Param("c_esquema") String piEsquema,
																	@Param("n_centro_computo") Integer idCentroComputo);
    
	@Query(value = "SELECT * FROM fn_obtener_datos_ambito_electoral_x_eleccion(:c_esquema,:n_eleccion)", nativeQuery = true)
	List<Map<String, Object>> listarAmbitoElectoralPorEleccion(@Param("c_esquema") String piEsquema,
															@Param("n_eleccion") Integer idEleccion);
	
}
