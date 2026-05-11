package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPolitica;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@SuppressWarnings("rawtypes")
public interface AgrupacionPoliticaRepository extends JpaRepository<AgrupacionPolitica, Long>, MigracionRepository<AgrupacionPolitica, String>  {

	@Query("SELECT distinct a FROM AgrupacionPolitica a " + 
			"JOIN a.detalle d " + 
			"JOIN d.ubigeoEleccion ue " +
			"JOIN ue.ubigeo u "+
			"JOIN u.centroComputo c WHERE c.codigo = ?1")
	List<AgrupacionPolitica> findByCc(String codigo);
	
	Optional<AgrupacionPolitica> findById(Long id);
	
	/*
	 * usamos TypedParameterValue para los parámetros que sus valores pueden ser null 
	 */
	@Query(value = "SELECT * FROM fn_reporte_listado_organizaciones_politicas(:pi_esquema, :pi_eleccion, :pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> organizacionesPoliticas(@Param("pi_esquema") String esquema,
											 @Param("pi_eleccion") TypedParameterValue eleccion,
											 @Param("pi_centro_computo") TypedParameterValue centroComputo);


	@Query(value = "CALL sp_cargar_datos_candidato_agrupacion(:pi_esquema,:po_resultado,:po_mensaje)", nativeQuery = true)
	Map<String, Object>  cargarCandidatos(
			@Param("pi_esquema") String piEsquema,
			@Param("po_resultado") Integer poResultado,
			@Param("po_mensaje") String poMensaje
	);
	
	@Query(value = "SELECT * FROM fn_obtener_datos_agrupacion_politica_x_distrito_electoral(:pi_esquema, "
																				    		+ ":pi_tipo_eleccion, "
																				    		+ ":pi_distrito_electoral)", nativeQuery = true)
	List<Map<String, Object>> listarAgrupolPorDistritoElectoral(@Param("pi_esquema") String piEsquema,
															@Param("pi_tipo_eleccion") String idEleccion,
															@Param("pi_distrito_electoral") String distritoElectoral);
}
