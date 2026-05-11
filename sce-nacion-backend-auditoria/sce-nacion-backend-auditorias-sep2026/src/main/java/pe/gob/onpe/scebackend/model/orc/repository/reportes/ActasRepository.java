package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import java.util.List;
import java.util.Map;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;

@SuppressWarnings("rawtypes")
public interface ActasRepository extends JpaRepository<Acta, Long>{

	/*
	 * usamos TypedParameterValue para los parámetros que sus valores pueden ser null 
	 */
	@Query(value = "SELECT * FROM fn_reporte_avance_digitalizacion_actas(:pi_esquema, :pi_n_eleccion, "
			+ ":pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
	List<Map<String, Object>> avanceDigitalizacion(@Param("pi_esquema") String esquema,
											 @Param("pi_n_eleccion") TypedParameterValue eleccion,
											 @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
											 @Param("pi_c_ubigeo") TypedParameterValue departamento
											 );

	@Query(value = "SELECT * FROM fn_reporte_avance_digitalizacion_acta_celeste(:pi_esquema, :pi_n_eleccion, "
			+ ":pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
	List<Map<String, Object>> avanceDigitalizacionSobreCeleste(@Param("pi_esquema") String esquema,
												   @Param("pi_n_eleccion") TypedParameterValue eleccion,
												   @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
												   @Param("pi_c_ubigeo") TypedParameterValue departamento
	);

	@Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_resumido_voto_pref(:pi_esquema, :pi_n_tipo_eleccion, "
								+ ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
	List<Map<String, Object>> resultadosActasContabilizadasResumidoPreferencial(@Param("pi_esquema") String esquema,
											 @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
											 @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
											 @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
											 @Param("pi_c_ubigeo") TypedParameterValue ubigeo
											 );
	
	@Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_detallado(:pi_esquema, :pi_n_tipo_eleccion, "
			+ ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, :pi_aud_usuario_consulta)", nativeQuery = true)
	List<Map<String, Object>> resultadosActasContabilizadasDetallado(@Param("pi_esquema") String esquema,
											 @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
											 @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
											 @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
											 @Param("pi_c_ubigeo") TypedParameterValue ubigeo,
											 @Param("pi_aud_usuario_consulta") String usuario
											 );
	
	@Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_detallado_voto_pref(:pi_esquema, :pi_n_tipo_eleccion, "
			+ ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, :pi_aud_usuario_consulta)", nativeQuery = true)
	List<Map<String, Object>> resultadosActasContabilizadasDetalladoPreferencial(@Param("pi_esquema") String esquema,
						 @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
						 @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
						 @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
						 @Param("pi_c_ubigeo") TypedParameterValue ubigeo,
						 @Param("pi_aud_usuario_consulta") String usuario
						 );
	
	@Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_resumido_cpr(:pi_esquema, :pi_n_tipo_eleccion, "
			+ ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
	List<Map<String, Object>> resultadosActasContabilizadasResumidoCPR(@Param("pi_esquema") String esquema,
											 @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
											 @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
											 @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
											 @Param("pi_c_ubigeo") TypedParameterValue ubigeo
											 );
	
	@Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_detallado_cpr(:pi_esquema, :pi_n_tipo_eleccion, "
			+ ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, :pi_aud_usuario_consulta)", nativeQuery = true)
	List<Map<String, Object>> resultadosActasContabilizadasDetalladoCPR(@Param("pi_esquema") String esquema,
											 @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
											 @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
											 @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
											 @Param("pi_c_ubigeo") TypedParameterValue ubigeo,
											 @Param("pi_aud_usuario_consulta") String usuario
											 );
	
	
	@Query(value = "SELECT * FROM fn_reporte_resumen_total_centro_computo(:pi_esquema, "
			+ ":pi_centro_computo, :pi_tipo_eleccion, :pi_estado)", nativeQuery = true)
	List<Map<String, Object>> resumenTotalCentroComputo(@Param("pi_esquema") String esquema,
											 @Param("pi_centro_computo") TypedParameterValue centroComputo,								 
											 @Param("pi_tipo_eleccion") TypedParameterValue eleccion,
											 @Param("pi_estado") TypedParameterValue ambito
											 );
	
	@Query(value = "SELECT * FROM fn_reporte_actas_digitalizadas(:pi_esquema, :pi_eleccion, :pi_ambito_electoral, "
			+ ":pi_centro_computo, :pi_fecha_inicial, :pi_fecha_final)", nativeQuery = true)
	List<Map<String, Object>> actasDigitalizadas(@Param("pi_esquema") String esquema,
											 @Param("pi_eleccion") TypedParameterValue eleccion,
											 @Param("pi_ambito_electoral") TypedParameterValue ambito,
											 @Param("pi_centro_computo") TypedParameterValue centroComputo,
											 @Param("pi_fecha_inicial") TypedParameterValue fechaInicial,
											 @Param("pi_fecha_final") TypedParameterValue fechaFin
											 );

	@Query(value = "SELECT * FROM fn_reporte_estado_acta_por_ambito(:pi_esquema, :pi_eleccion, :pi_ambito_electoral, "
			+ ":pi_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> estadoActasOdpe(@Param("pi_esquema") String esquema,
											 @Param("pi_eleccion") TypedParameterValue eleccion,								 
											 @Param("pi_ambito_electoral") TypedParameterValue ambito,
											 @Param("pi_centro_computo") TypedParameterValue centroComputo											 
											 );

	@Query(value = "SELECT * FROM fn_reporte_estado_acta_por_eleccion(:pi_esquema, :pi_n_centro_computo)", nativeQuery = true)
	List<Map<String, Object>> resumenEstadoActas(@Param("pi_esquema") String esquema,
											  @Param("pi_n_centro_computo") TypedParameterValue centroComputo
	);
	
}
