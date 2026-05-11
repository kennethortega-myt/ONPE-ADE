package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.TabPrTransmision;


public interface TabPrTransmisionRepository extends JpaRepository<TabPrTransmision, Long> {

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
	
	@Query(value = "CALL sp_jne_registrar_trama(:pi_esquema,:pi_n_acta,:pi_aud_usuario_creacion,:po_resultado,:po_mensaje);", nativeQuery = true)
	public Map<String, Object> registrarTramaJne(
            @Param("pi_esquema") String esquema, //pi_esquema
            @Param("pi_n_acta") Long idActa,
            @Param("pi_aud_usuario_creacion") String audUsuarioCreacion,
            @Param("po_resultado") Integer resultado,
            @Param("po_mensaje") String mensaje
    );
	
	@Query("SELECT t FROM TabPrTransmision t WHERE t.estado = 0 and t.activo=1")
	public List<TabPrTransmision> listarPendientes();
	
	@Query("SELECT t FROM TabPrTransmision t WHERE t.estado = 0 and t.activo=1 and t.idActa=:idActa")
	public List<TabPrTransmision> listarPendientes(@Param("idActa") Long idActa);
	
	@Modifying
	@Query("UPDATE TabPrTransmision t SET t.estado = :newState WHERE t.id IN :idList")
	int actualizarEstados(@Param("idList") List<Long> idList, @Param("newState") Integer newState);
	
	@Modifying
	@Query("UPDATE TabPrTransmision t SET t.estado = :newState WHERE t.id = :idFila")
	int actualizarEstado(@Param("idFila") Integer idFila, @Param("newState") Integer newState);
	
	
}
