package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ActaTransmisionProjection;


public interface ActaTransmisionNacionRepository extends JpaRepository<ActaTransmisionNacion, Long> {
 
	@Query("SELECT a " +
            "FROM ActaTransmisionNacion a " + 
			"WHERE a.idActa=:idActa "+
			"AND a.transmite = 1 " +
			"AND a.estadoTransmitidoNacion = 0 " +
			"AND a.requestActaTransmision is not null " +
            "ORDER BY a.id asc, a.orden asc ")
	List<ActaTransmisionNacion> findByIdActaConTransmisionesOrdenadas(Long idActa);
	
	@Query("SELECT a " +
            "FROM ActaTransmisionNacion a " + 
			"WHERE a.idActa=:idActa "+
			"AND a.transmite = 1 " +
			"AND a.requestActaTransmision is not null " +
            "ORDER BY a.id asc ")
	List<ActaTransmisionNacion> findByIdActaConTodasTransmisionesOrdenadas(Long idActa);
	
	@Query("SELECT a " +
            "FROM ActaTransmisionNacion a " + 
			"WHERE a.estadoTransmitidoNacion = :estadoTransmitidoNacion " + 
            "AND (a.accion = :accionActa OR a.accion = :accionMesa) " +
			"AND a.idActa = :idActa " +
			"AND a.transmite = 1 " +
			"AND (a.intento is null or a.intento=0) " +
            "AND a.requestActaTransmision is not null ")
	List<ActaTransmisionNacion> listarFaltantesTransmitir(
			Long idActa,
			Integer estadoTransmitidoNacion,
			String accionActa,
			String accionMesa);
	
	@Query("SELECT a " +
            "FROM ActaTransmisionNacion a " + 
			"WHERE a.estadoTransmitidoNacion = :estadoTransmitidoNacion " + 
            //"AND a.accion = :accion " +
			"AND a.transmite = 1 " +
			"AND a.intento>1 " + // debe ser 1
            "AND a.requestActaTransmision is not null ")
	List<ActaTransmisionNacion> listarFaltantesTransmitirPorFallo(
			Integer estadoTransmitidoNacion);

	@Query("SELECT a " +
			"FROM ActaTransmisionNacion a " +
			"WHERE a.idActa = :idActa order by a.fechaCreacion asc"
	)
	List<ActaTransmisionNacion> listarTrazabilidadPorActaId(Long idActa);


	@Query("SELECT a " +
			"FROM ActaTransmisionNacion a " +
			"WHERE a.idActa = :idActa " +
			"AND a.tipoTransmision NOT IN :tiposExcluidos "+
			"ORDER BY a.fechaCreacion ASC")
	List<ActaTransmisionNacion> listarTrazabilidadPorActaId(
			@Param("idActa") Long idActa,
			@Param("tiposExcluidos") List<String> tiposExcluidos);




	@Query("""
        SELECT a.id AS id,
               a.tipoTransmision AS tipoTransmision,
               a.idActa AS idActa,
               a.accion AS accion
        FROM ActaTransmisionNacion a
        WHERE (:accion IS NULL OR a.accion = :accion)
          AND (:tipoTransmision IS NULL OR a.tipoTransmision = :tipoTransmision)
          AND (:idActa IS NULL OR a.idActa = :idActa)
        ORDER BY a.fechaCreacion ASC
    """)
	List<ActaTransmisionProjection> buscarTramasParaLiberar(
			@Param("accion") String accion,
			@Param("tipoTransmision") String tipoTransmision,
			@Param("idActa") Long idActa
	);



	@Modifying
	@Transactional
	@Query("UPDATE ActaTransmisionNacion a SET a.activo = 0 WHERE a.id = :id")
	int inactivarPorId(@Param("id") Long id);




	@Query(value = """
    SELECT 
        n_transmision_pk AS id,
        n_acta AS idActa,
        n_estado_transmitido_nacion AS estadoTransmitidoNacion,
        c_tipo_transmision AS tipoTransmision,
        n_transmite AS transmite,
        d_fecha_transmision AS fechaTransmision,
        d_fecha_registro AS fechaRegistro,
        c_aud_usuario_creacion AS usuarioRegistro,
        c_accion AS accion,
        c_usuario_transmision AS usuarioTransmision,
        n_intento AS intento,
        d_fecha_registro AS fechaInicio,
        LEAD(d_fecha_registro) OVER (ORDER BY d_fecha_registro) AS fechaFin,
        c_peticion_acta_transmision AS requestActaTransmision
    FROM tab_acta_transmision 
    WHERE n_acta = :idActa 
      AND c_accion = 'ACTA'
      AND c_tipo_transmision NOT IN (:tiposExcluidos)
    ORDER BY d_fecha_registro
	""", nativeQuery = true)
	List<Object[]> listarTrazabilidadConFechasInicioFin(
			@Param("idActa") Long idActa,
			@Param("tiposExcluidos") List<String> tiposExcluidos
	);



	@Modifying
	@Query("DELETE FROM ActaTransmisionNacion")
	void deleteAllInBatch();
	
	@Query("SELECT r FROM ActaTransmisionNacion r WHERE r.id = (SELECT MIN(r2.id) FROM ActaTransmisionNacion r2 where r2.accion=:accion)")
	Optional<ActaTransmisionNacion> listRegistroMin(String accion);


	@Query(value = "CALL sp_pr_registrar_trama(:pi_esquema,:pi_n_acta,:pi_n_det_ubigeo_eleccion,:pi_c_estado_acta,:pi_c_estado_computo,:pi_c_estado_acta_resolucion,:pi_aud_usuario_creacion,:po_resultado,:po_mensaje);", nativeQuery = true)
	public Map<String, Object> registrarTramaPr(
            @Param("pi_esquema") String esquema, //pi_esquema
            @Param("pi_n_acta") Long idActa,
            @Param("pi_n_det_ubigeo_eleccion") Integer idDetUbigeoEleccion,
            @Param("pi_c_estado_acta") String estadoActa,
            @Param("pi_c_estado_computo") String estadoComputo,
            @Param("pi_c_estado_acta_resolucion") String estadoActaResolucion,
            @Param("pi_aud_usuario_creacion") String audUsuarioCreacion,
            @Param("po_resultado") Integer resultado,
            @Param("po_mensaje") String mensaje
    );
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "CALL sp_resetear_lista_tablas(:pi_esquema)", nativeQuery = true)
	public void  resetearSecuencias(
			@Param("pi_esquema") String piEsquema //pi_esquema
	);
	

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "CALL sp_elimina_constraint_tab_miembro_mesa_sorteado(:pi_esquema)", nativeQuery = true)
	public int  eliminarConstraintMiembroMesaSorteado(
			@Param("pi_esquema") String piEsquema
	);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "CALL sp_crea_constraint_tab_miembro_mesa_sorteado(:pi_esquema)", nativeQuery = true)
	public void  crearConstraintMiembroMesaSorteado(
			@Param("pi_esquema") String piEsquema
	);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "CALL sp_elimina_constraint_tab_miembro_mesa_cola(:pi_esquema)", nativeQuery = true)
	public int  eliminarConstraintMiembroMesaCola(
			@Param("pi_esquema") String piEsquema
	);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "CALL sp_crea_constraint_tab_miembro_mesa_cola(:pi_esquema)", nativeQuery = true)
	public void  crearConstraintMiembroMesaCola(
			@Param("pi_esquema") String piEsquema
	);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "CALL sp_elimina_constraint_tab_omiso_votante(:pi_esquema)", nativeQuery = true)
	public int  eliminarConstraintOmisoVotante(
			@Param("pi_esquema") String piEsquema
	);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "CALL sp_crea_constraint_tab_omiso_votante(:pi_esquema)", nativeQuery = true)
	public void  crearConstraintOmisoVotante(
			@Param("pi_esquema") String piEsquema
	);
	
	@Query("SELECT a " +
			"FROM ActaTransmisionNacion a " +
			"WHERE a.idActa = :idActa "
			+ "AND (a.estadoTransmitidoNacion=:estadoFaltante or a.estadoTransmitidoNacion=:estadoBloqueante) "
			+ "AND a.transmite=1 "
	)
	List<ActaTransmisionNacion> listarPendientes(
			@Param("idActa") Long idActa, 
			@Param("estadoFaltante") Integer estadoFaltante, 
			@Param("estadoBloqueante") Integer estadoBloqueante);
	
	@Query("SELECT DISTINCT a.idActa " +
		"FROM ActaTransmisionNacion a " +
		"WHERE a.estadoTransmitidoNacion <> :estadoBloqueante "
	)
	List<Long> listarActasNoBloqueadas(@Param("estadoBloqueante") Integer estadoBloqueante);
	
	
	@Query("SELECT a " +
			"FROM ActaTransmisionNacion a " +
			"WHERE a.idActa = :idActa and a.estadoTransmitidoNacion=:estadoBloqueante "
	)
	List<ActaTransmisionNacion> listarEjecutandosePorIdActa(@Param("idActa") Long idActa, @Param("estadoBloqueante") Integer estadoBloqueante);
	
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(a.orden) FROM ActaTransmisionNacion a WHERE a.idActa = :idActa")
    Optional<Integer> findMaxOrdenByIdActaWithLock(@Param("idActa") Long idActa);
	
	@Query(value = """
		    SELECT COUNT(1)
		    FROM tab_acta_transmision t
		    WHERE t.n_acta =  :idActa AND t.n_estado_transmitido_nacion=2
		    """, nativeQuery = true)
	int contarEnEjecucion(@Param("idActa") Long idActa);
	
	
	@Modifying
	@Transactional
	@Query("UPDATE ActaTransmisionNacion a SET a.estadoTransmitidoNacion = 0, a.mensajeError= :mensajeError WHERE a.idActa = :idActa")
	int actualizarFallidoReseteo(@Param("idActa") Long idActa, @Param("mensajeError") String mensajeError);
	
	@Modifying
	@Transactional
	@Query("UPDATE ActaTransmisionNacion a SET a.estadoTransmitidoNacion = 1, a.mensajeError= :mensaje WHERE a.idActa = :idActa")
	int actualizarOkReseteo(@Param("idActa") Long idActa, @Param("mensaje") String mensaje);
	
	@Modifying
	@Transactional
	@Query("UPDATE ActaTransmisionNacion a SET a.estadoTransmitidoNacion = :estado, a.mensajeError= :mensaje WHERE a.idActa = :idActa")
	int actualizarEstadoReseteo(@Param("idActa") Long idActa, @Param("estado") Integer estado, @Param("mensaje") String mensaje);
	
	
}
