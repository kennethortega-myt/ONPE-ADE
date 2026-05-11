package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pe.gob.onpe.scebackend.model.dto.ActaInfoDTO;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ActaRepository extends JpaRepository<Acta, Long>, MigracionRepository<Acta, String>{
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Acta a WHERE a.id=:id")
	Optional<Acta> findByIdForUpdate(@Param("id") Long id);
	
	@Query("SELECT a FROM Acta a " + 
			"JOIN a.mesa m " + 
			"WHERE m.codigo = ?1")
	Optional<Acta> findByNumeroMesa(String numeroMesa);

	@Query("SELECT a FROM Acta a " + 
			"JOIN a.mesa m " + 
			"JOIN m.localVotacion l "+ 
			"JOIN l.ubigeo u " + 
			"JOIN u.centroComputo c " + 
			"WHERE c.codigo = ?1")
	List<Acta> findByCc(String codigo);

	@Query("""
    SELECT new pe.gob.onpe.scebackend.model.dto.ActaInfoDTO(a.mesa.codigo, a.numeroCopia, a.digitoChequeoEscrutinio) 
    FROM Acta a 
    WHERE a.id = :id
""")
	Optional<ActaInfoDTO> findActaInfoDTOById(@Param("id") Long id);

	@Query("""
    SELECT DISTINCT a FROM Acta a
    JOIN a.ubigeoEleccion ue
    JOIN ue.ubigeo u
    JOIN u.ubigeoPadre prov
    JOIN prov.ubigeoPadre dep
    JOIN ue.eleccion e
    JOIN e.procesoElectoral pe
    JOIN a.mesa tm
    JOIN tm.localVotacion mlv
    WHERE 1=1
    AND (:idProceso IS NULL OR pe.id = :idProceso)
    AND (:idEleccion IS NULL OR e.id = :idEleccion)
    AND (:idDepartamento IS NULL OR dep.id = :idDepartamento)
    AND (:idProvincia IS NULL OR prov.id = :idProvincia)
    AND (:idUbigeo IS NULL OR u.id = :idUbigeo)
    AND (:idLocalVotacion IS NULL OR mlv.id = :idLocalVotacion)
    AND (:mesa IS NULL OR tm.codigo = :mesa)
    AND NOT (
        (a.estadoActa = 'O' AND a.estadoCc = 'O') OR
        (a.estadoActa = 'S' AND a.estadoCc = 'O') OR
        (a.estadoActa = 'K' AND a.estadoCc = 'O')
    )
    ORDER BY a.id
""")
	List<Acta> buscarMonitoreoNacion(
			@Param("idProceso") Long idProceso, 
			@Param("idEleccion") Long idEleccion, 
			@Param("idDepartamento") Long idDepartamento, 
			@Param("idProvincia") Long idProvincia, 
			@Param("idUbigeo") Long idUbigeo, 
			@Param("idLocalVotacion") Long idLocalVotacion,
			@Param("mesa") String mesa,
			Pageable pageable);

	@Query(value = "SELECT * FROM fn_reporte_avance_mesa_por_mesa(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)), :pi_aud_usuario_consulta)", nativeQuery = true)
	List<Map<String, Object>> avanceMesaMesa(@Param("pi_esquema") String piEsquema,
											 @Param("pi_n_tipo_eleccion") Integer piTipoEleccion,
											 @Param("pi_n_ambito_electoral") Integer piAmbitoElectoral,
											 @Param("pi_n_centro_computo") Integer piCentroComputo,
											 @Param("pi_c_ubigeo") String piUbigeo,
											 @Param("pi_c_mesa") String piMesa,
											 @Param("pi_aud_usuario_consulta") String piAudUsuarioConsulta);

	@Query(value = "SELECT * FROM fn_reporte_auditoria_digitacion(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)))", nativeQuery = true)
	List<Map<String, Object>> auditoriaDigitacion(@Param("pi_esquema") String piEsquema,
												 @Param("pi_n_tipo_eleccion") Integer piTipoEleccion,
												 @Param("pi_n_ambito_electoral") Integer piAmbitoElectoral,
												 @Param("pi_n_centro_computo") Integer piCentroComputo,
												 @Param("pi_c_ubigeo") String piUbigeo,
												 @Param("pi_c_mesa") String piMesa);

	@Query(value = "SELECT * FROM fn_reporte_auditoria_digitacion_cpr(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)))", nativeQuery = true)
	List<Map<String, Object>> auditoriaDigitacionCPR(@Param("pi_esquema") String piEsquema,
													 @Param("pi_n_tipo_eleccion") Integer piTipoEleccion,
													 @Param("pi_n_ambito_electoral") Integer piAmbitoElectoral,
													 @Param("pi_n_centro_computo") Integer piCentroComputo,
													 @Param("pi_c_ubigeo") String piUbigeo,
													 @Param("pi_c_mesa") String piMesa);

	@Query(value = "SELECT * FROM fn_reporte_auditoria_digitacion_voto_preferencial(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)))", nativeQuery = true)
	List<Map<String, Object>> auditoriaDigitacionPreferencial(@Param("pi_esquema") String piEsquema,
												  @Param("pi_n_tipo_eleccion") Integer piTipoEleccion,
												  @Param("pi_n_ambito_electoral") Integer piAmbitoElectoral,
												  @Param("pi_n_centro_computo") Integer piCentroComputo,
												  @Param("pi_c_ubigeo") String piUbigeo,
												  @Param("pi_c_mesa") String piMesa);

	@Query(value = "SELECT * FROM fn_reporte_avance_mesa_por_mesa_voto_preferencial(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, (CAST(:pi_c_mesa AS VARCHAR)), :pi_c_usuario)", nativeQuery = true)
	List<Map<String, Object>> avanceMesaMesaPreferencial(@Param("pi_esquema") String piEsquema,
											 @Param("pi_n_tipo_eleccion") Integer piTipoEleccion,
											 @Param("pi_n_ambito_electoral") Integer piAmbitoElectoral,
											 @Param("pi_n_centro_computo") Integer piCentroComputo,
											 @Param("pi_c_ubigeo") String piUbigeo,
											 @Param("pi_c_mesa") String piMesa,
											 @Param("pi_c_usuario") String piUsuario);
	
	@Query(value = "SELECT * FROM fn_reporte_avance_mesa_por_mesa_cpr(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, (CAST(:pi_c_mesa AS VARCHAR)), :pi_aud_usuario_consulta)", nativeQuery = true)
	List<Map<String, Object>> avanceMesaMesaCPR(@Param("pi_esquema") String piEsquema,
											 @Param("pi_n_tipo_eleccion") Integer piTipoEleccion,
											 @Param("pi_n_ambito_electoral") Integer piAmbitoElectoral,
											 @Param("pi_n_centro_computo") Integer piCentroComputo,
											 @Param("pi_c_ubigeo") String piUbigeo,
											 @Param("pi_c_mesa") String piMesa,
											 @Param("pi_aud_usuario_consulta") String piUsuario);

	@Query(value = "SELECT * FROM fn_reporte_avance_estado_acta(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_centro_computo, :pi_n_estado, :pi_aud_usuario_consulta)", nativeQuery = true)
	List<Map<String, Object>> avanceEstadoActa(@Param("pi_esquema") String piEsquema,
											   @Param("pi_n_tipo_eleccion") Integer piTipoEleccion,
											   @Param("pi_n_centro_computo") Integer piCentroComputo,
											   @Param("pi_n_estado") Integer piEstado,
											   @Param("pi_aud_usuario_consulta") String piAudUsuarioConsulta);

	
	@Query(value = "CALL sp_registrar_puesta_cero(:pi_esquema,:pi_n_centro_computo,:pi_c_fecha_ejecucion,:pi_c_fecha_transmision,:pi_aud_usuario,:po_resultado,:po_mensaje)", nativeQuery = true)
	Map<String, Object>  puestaCero(
			@Param("pi_esquema") String piEsquema,
			@Param("pi_n_centro_computo") Integer piCentroComputo,
			@Param("pi_c_fecha_ejecucion") String piFechaEjecucion,
	        @Param("pi_c_fecha_transmision") String piFechaTransmision,
			@Param("pi_aud_usuario") String piAudUsuario,
			@Param("po_resultado") Integer poResultado,
			@Param("po_mensaje") String poMensaje
	);
	
	@Query("SELECT count(DISTINCT a) FROM Acta a JOIN a.ubigeoEleccion ue "
			+ "JOIN ue.ubigeo u "
			+ "JOIN u.ubigeoPadre prov "
			+ "JOIN prov.ubigeoPadre dep "
			+ "JOIN ue.eleccion e "
			+ "JOIN e.procesoElectoral pe "
			+ "JOIN a.mesa tm "
			+ "JOIN tm.localVotacion mlv "
			+ "WHERE 1=1 "
			+ "AND (:idProceso IS NULL OR pe.id= :idProceso ) "
			+ "AND (:idEleccion IS NULL OR e.id = :idEleccion ) "
			+ "AND (:idDepartamento IS NULL OR dep.id = :idDepartamento ) "
			+ "AND (:idProvincia IS NULL OR prov.id = :idProvincia ) "
			+ "AND (:idUbigeo IS NULL OR u.id = :idUbigeo ) "
			+ "AND (:idLocalVotacion IS NULL OR mlv.id=:idLocalVotacion ) "
			+ "AND (:mesa IS NULL OR tm.codigo = :mesa) ")
	Integer getCantidadRegistros(
			@Param("idProceso") Long idProceso,
			@Param("idEleccion") Long idEleccion,
			@Param("idDepartamento") Long idDepartamento,
			@Param("idProvincia") Long idProvincia,
			@Param("idUbigeo") Long idUbigeo,
			@Param("idLocalVotacion") Long idLocalVotacion,
			@Param("mesa") String mesa,
			@Param("grupoActa") String grupoActa);

	/*
	@Query("""
    SELECT count(DISTINCT a) as cantidad, a.estadoActa as estado 
    FROM Acta a 
    JOIN a.ubigeoEleccion ue 
    JOIN ue.ubigeo u 
    JOIN u.ubigeoPadre prov 
    JOIN prov.ubigeoPadre dep 
    JOIN ue.eleccion e 
    JOIN e.procesoElectoral pe 
    JOIN a.mesa tm 
    JOIN tm.localVotacion mlv 
    WHERE 1=1 
    AND (:idProceso IS NULL OR pe.id = :idProceso) 
    AND (:idEleccion IS NULL OR e.id = :idEleccion) 
    AND (:idDepartamento IS NULL OR dep.id = :idDepartamento) 
    AND (:idProvincia IS NULL OR prov.id = :idProvincia) 
    AND (:idUbigeo IS NULL OR u.id = :idUbigeo) 
    AND (:idLocalVotacion IS NULL OR mlv.id = :idLocalVotacion) 
    AND (:mesa IS NULL OR tm.codigo = :mesa)
    AND NOT (
        (a.estadoActa = 'O' AND a.estadoCc = 'O') OR 
        (a.estadoActa = 'S' AND a.estadoCc = 'O') OR
        (a.estadoActa = 'K' AND a.estadoCc = 'O')
    ) 
    GROUP BY a.estadoActa
""")
	List<Map<String,Long>> getCabeceras(
			@Param("idProceso") Long idProceso,
			@Param("idEleccion") Long idEleccion,
			@Param("idDepartamento") Long idDepartamento,
			@Param("idProvincia") Long idProvincia,
			@Param("idUbigeo") Long idUbigeo,
			@Param("idLocalVotacion") Long idLocalVotacion,
			@Param("mesa") String mesa);*/
	
	
	@Query(value = "CALL sp_registrar_transmision_acta(:pi_esquema,:pi_c_acta_text,:pi_aud_usuario_creacion,:pi_desarrollo,:po_resultado,:po_mensaje,:po_estado_acta,:po_estado_acta_resolucion,:po_estado_computo,:po_estado_error_material)", nativeQuery = true)
	Map<String, Object>  insertActaStae(
			@Param("pi_esquema") String piEsquema,
			@Param("pi_c_acta_text") String piActa,
			@Param("pi_aud_usuario_creacion") String usuario,
			@Param("pi_desarrollo") boolean piEsDesarrollo,
			@Param("po_resultado") Integer poResultado,
			@Param("po_mensaje") String poMensaje,
			@Param("po_estado_acta") String poEstadoActa,
			@Param("po_estado_acta_resolucion") String poEstadoActaResolucion,
			@Param("po_estado_computo") String poEstadoComputo,
			@Param("po_estado_error_material") String poErrorMaterial
	);
	
	@Query(value = "CALL sp_registrar_transmision_lista_electores(:pi_esquema,:pi_c_lista_text,:pi_aud_usuario_creacion,:pi_desarrollo,:po_resultado,:po_mensaje)", nativeQuery = true)
	Map<String, Object>  insertListaElectoresStae(
			@Param("pi_esquema") String piEsquema,
			@Param("pi_c_lista_text") String piLe,
			@Param("pi_aud_usuario_creacion") String usuario,
			@Param("pi_desarrollo") boolean piEsDesarrollo,
			@Param("po_resultado") Integer poResultado,
			@Param("po_mensaje") String poMensaje
	);
	
	@Query(value = "SELECT ca.* "
			+ "FROM cab_acta ca "
			+ "inner join tab_mesa tm on ca.n_mesa = tm.n_mesa_pk "
			+ "INNER JOIN mae_local_votacion mlv ON tm.n_local_votacion=mlv.n_local_votacion_pk "
			+ "INNER JOIN mae_ubigeo mu ON mlv.n_ubigeo=mu.n_ubigeo_pk "
			+ "INNER JOIN det_ubigeo_eleccion due on due.n_ubigeo = mu.n_ubigeo_pk "
			+ "and due.n_det_ubigeo_eleccion_pk = ca.n_det_ubigeo_eleccion "
			+ "where tm.c_mesa = ?1 and due.n_eleccion = ?2", nativeQuery = true)
	Optional<Acta> findByNumeroMesaAndEleccion(String numeroMesa, Integer idEleccion);
	
	
	@Query(value = """
			SELECT COUNT(DISTINCT a.n_acta_pk) 
			FROM cab_acta a
			JOIN det_ubigeo_eleccion ue ON ue.n_det_ubigeo_eleccion_pk = a.n_det_ubigeo_eleccion 
			JOIN mae_ubigeo mu ON mu.n_ubigeo_pk = ue.n_ubigeo 
			JOIN mae_ubigeo prov ON prov.n_ubigeo_pk = mu.n_ubigeo_padre
			JOIN mae_ubigeo dep ON dep.n_ubigeo_pk = prov.n_ubigeo_padre
			JOIN mae_eleccion e ON e.n_eleccion_pk = ue.n_eleccion 
			JOIN mae_proceso_electoral mpe ON mpe.n_proceso_electoral_pk = e.n_proceso_electoral 
			JOIN tab_mesa tm ON tm.n_mesa_pk = a.n_mesa 
			JOIN mae_local_votacion mlv ON mlv.n_local_votacion_pk = tm.n_local_votacion 
			WHERE 1=1
			AND (:idProceso IS NULL OR mpe.n_proceso_electoral_pk = :idProceso) 
			AND (:idEleccion IS NULL OR e.n_eleccion_pk = :idEleccion) 
			AND (:idDepartamento IS NULL OR dep.n_ubigeo_pk = :idDepartamento) 
			AND (:idProvincia IS NULL OR prov.n_ubigeo_pk = :idProvincia) 
			AND (:idUbigeo IS NULL OR mu.n_ubigeo_pk = :idUbigeo) 
			AND (:idLocalVotacion IS NULL OR  mlv.n_local_votacion_pk = :idLocalVotacion) 
			AND (:mesa IS NULL OR tm.c_mesa = :mesa) 
			AND (a.c_estado_computo = 'S')
			""", 
			nativeQuery = true)
	Long getTotalNormales(
			        @Param("idProceso") Long idProceso,
			        @Param("idEleccion") Long idEleccion,
			        @Param("idDepartamento") Long idDepartamento,
			        @Param("idProvincia") Long idProvincia,
			        @Param("idUbigeo") Long idUbigeo,
			        @Param("idLocalVotacion") Long idLocalVotacion,
			        @Param("mesa") String mesa);
	
	@Query(value = """
			SELECT COUNT(DISTINCT a.n_acta_pk) 
			FROM cab_acta a
			JOIN det_ubigeo_eleccion ue ON ue.n_det_ubigeo_eleccion_pk = a.n_det_ubigeo_eleccion 
			JOIN mae_ubigeo mu ON mu.n_ubigeo_pk = ue.n_ubigeo 
			JOIN mae_ubigeo prov ON prov.n_ubigeo_pk = mu.n_ubigeo_padre
			JOIN mae_ubigeo dep ON dep.n_ubigeo_pk = prov.n_ubigeo_padre
			JOIN mae_eleccion e ON e.n_eleccion_pk = ue.n_eleccion 
			JOIN mae_proceso_electoral mpe ON mpe.n_proceso_electoral_pk = e.n_proceso_electoral 
			JOIN tab_mesa tm ON tm.n_mesa_pk = a.n_mesa 
			JOIN mae_local_votacion mlv ON mlv.n_local_votacion_pk = tm.n_local_votacion 
			WHERE 1=1
			AND (:idProceso IS NULL OR mpe.n_proceso_electoral_pk = :idProceso) 
			AND (:idEleccion IS NULL OR e.n_eleccion_pk = :idEleccion) 
			AND (:idDepartamento IS NULL OR dep.n_ubigeo_pk = :idDepartamento) 
			AND (:idProvincia IS NULL OR prov.n_ubigeo_pk = :idProvincia) 
			AND (:idUbigeo IS NULL OR mu.n_ubigeo_pk = :idUbigeo) 
			AND (:idLocalVotacion IS NULL OR  mlv.n_local_votacion_pk = :idLocalVotacion) 
			AND (:mesa IS NULL OR tm.c_mesa = :mesa) 
			AND ((a.c_estado_acta IN ('O','H','S') AND a.c_estado_computo = 'O') OR (a.c_estado_acta_resolucion='R'))
			""", 
			nativeQuery = true)
	Long getTotalObservadas(
			        @Param("idProceso") Long idProceso,
			        @Param("idEleccion") Long idEleccion,
			        @Param("idDepartamento") Long idDepartamento,
			        @Param("idProvincia") Long idProvincia,
			        @Param("idUbigeo") Long idUbigeo,
			        @Param("idLocalVotacion") Long idLocalVotacion,
			        @Param("mesa") String mesa);
	
	
	@Query(value = """
			SELECT COUNT(DISTINCT a.n_acta_pk)
			FROM cab_acta a
			JOIN det_ubigeo_eleccion ue ON ue.n_det_ubigeo_eleccion_pk = a.n_det_ubigeo_eleccion 
			JOIN mae_ubigeo mu ON mu.n_ubigeo_pk = ue.n_ubigeo 
			JOIN mae_ubigeo prov ON prov.n_ubigeo_pk = mu.n_ubigeo_padre
			JOIN mae_ubigeo dep ON dep.n_ubigeo_pk = prov.n_ubigeo_padre
			JOIN mae_eleccion e ON e.n_eleccion_pk = ue.n_eleccion 
			JOIN mae_proceso_electoral mpe ON mpe.n_proceso_electoral_pk = e.n_proceso_electoral 
			JOIN tab_mesa tm ON tm.n_mesa_pk = a.n_mesa 
			JOIN mae_local_votacion mlv ON mlv.n_local_votacion_pk = tm.n_local_votacion 
			WHERE 1=1
			AND (:idProceso IS NULL OR mpe.n_proceso_electoral_pk = :idProceso) 
			AND (:idEleccion IS NULL OR e.n_eleccion_pk = :idEleccion) 
			AND (:idDepartamento IS NULL OR dep.n_ubigeo_pk = :idDepartamento) 
			AND (:idProvincia IS NULL OR prov.n_ubigeo_pk = :idProvincia) 
			AND (:idUbigeo IS NULL OR mu.n_ubigeo_pk = :idUbigeo) 
			AND (:idLocalVotacion IS NULL OR  mlv.n_local_votacion_pk = :idLocalVotacion) 
			AND (:mesa IS NULL OR tm.c_mesa = :mesa) 
			AND (a.c_estado_acta = 'I' AND a.c_estado_computo = 'O') 
			""", 
			nativeQuery = true)
	Long getTotalEnviadasJne(@Param("idProceso") Long idProceso, @Param("idEleccion") Long idEleccion,
			@Param("idDepartamento") Long idDepartamento, @Param("idProvincia") Long idProvincia,
			@Param("idUbigeo") Long idUbigeo, @Param("idLocalVotacion") Long idLocalVotacion,
			@Param("mesa") String mesa);
	
	
	@Query(value = """
			SELECT COUNT(DISTINCT a.n_acta_pk)
			FROM cab_acta a
			JOIN det_ubigeo_eleccion ue ON ue.n_det_ubigeo_eleccion_pk = a.n_det_ubigeo_eleccion 
			JOIN mae_ubigeo mu ON mu.n_ubigeo_pk = ue.n_ubigeo 
			JOIN mae_ubigeo prov ON prov.n_ubigeo_pk = mu.n_ubigeo_padre
			JOIN mae_ubigeo dep ON dep.n_ubigeo_pk = prov.n_ubigeo_padre
			JOIN mae_eleccion e ON e.n_eleccion_pk = ue.n_eleccion 
			JOIN mae_proceso_electoral mpe ON mpe.n_proceso_electoral_pk = e.n_proceso_electoral 
			JOIN tab_mesa tm ON tm.n_mesa_pk = a.n_mesa 
			JOIN mae_local_votacion mlv ON mlv.n_local_votacion_pk = tm.n_local_votacion 
			WHERE 1=1
			AND (:idProceso IS NULL OR mpe.n_proceso_electoral_pk = :idProceso) 
			AND (:idEleccion IS NULL OR e.n_eleccion_pk = :idEleccion) 
			AND (:idDepartamento IS NULL OR dep.n_ubigeo_pk = :idDepartamento) 
			AND (:idProvincia IS NULL OR prov.n_ubigeo_pk = :idProvincia) 
			AND (:idUbigeo IS NULL OR mu.n_ubigeo_pk = :idUbigeo) 
			AND (:idLocalVotacion IS NULL OR  mlv.n_local_votacion_pk = :idLocalVotacion) 
			AND (:mesa IS NULL OR tm.c_mesa = :mesa) 
			AND ( a.c_estado_acta IN ('J','K') AND a.c_estado_computo = 'O') 
			""", 
			nativeQuery = true)
	Long getTotalDevueltasJne(@Param("idProceso") Long idProceso, @Param("idEleccion") Long idEleccion,
			@Param("idDepartamento") Long idDepartamento, @Param("idProvincia") Long idProvincia,
			@Param("idUbigeo") Long idUbigeo, @Param("idLocalVotacion") Long idLocalVotacion,
			@Param("mesa") String mesa);
	
	@Query("SELECT a FROM Acta a WHERE a.mesa.codigo = :codigoMesa "
	        + "AND a.ubigeoEleccion.eleccion.codigo = :codigoEleccion "
	        + "AND a.activo = 1")
	List<Acta> buscarActaPorCodigoMesaAndCodigoEleccion(
	        @Param("codigoMesa") String codigoMesa,
	        @Param("codigoEleccion") String codigoEleccion
	    );
	
	
	@Query(value = """
    		SELECT 
    		ca.* FROM 
    		cab_acta ca 
			INNER JOIN tab_mesa tm ON ca.n_mesa = tm.n_mesa_pk
			INNER JOIN mae_local_votacion lv ON lv.n_local_votacion_pk = tm.n_local_votacion
			INNER JOIN mae_ubigeo ub ON ub.n_ubigeo_pk = lv.n_ubigeo
			INNER JOIN mae_centro_computo cc ON cc.n_centro_computo_pk = ub.n_centro_computo
			where cc.c_codigo = :cc
    		AND (:lastId IS NULL OR ca.n_acta_pk > :lastId)
    		ORDER BY ca.n_acta_pk
    		""",
    		nativeQuery = true)
    List<Acta> importar(
    		@Param("cc") String cc,
    		@Param("lastId") Long lastId,
    		Pageable pageable);
	
	@Query(value = """
    		SELECT 
    		COUNT(1) 
    		FROM cab_acta ca 
			INNER JOIN tab_mesa tm ON ca.n_mesa = tm.n_mesa_pk
			INNER JOIN mae_local_votacion lv ON lv.n_local_votacion_pk = tm.n_local_votacion
			INNER JOIN mae_ubigeo ub ON ub.n_ubigeo_pk = lv.n_ubigeo
			INNER JOIN mae_centro_computo cc ON cc.n_centro_computo_pk = ub.n_centro_computo
			where cc.c_codigo = :cc
    		""",
    		nativeQuery = true)
    int contar(
    		@Param("cc") String cc);

	
	

}
