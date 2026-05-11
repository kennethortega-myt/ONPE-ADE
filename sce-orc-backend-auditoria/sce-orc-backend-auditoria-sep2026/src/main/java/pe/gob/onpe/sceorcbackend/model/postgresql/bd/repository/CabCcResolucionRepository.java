package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabCcResolucion;
import java.util.Map;
import java.util.Optional;

public interface CabCcResolucionRepository extends JpaRepository<CabCcResolucion, Long> {

  Optional<CabCcResolucion> findByActaAndResolucionAndEstadoCambioAndActivo(Long idActa, Long idResolucion, String estadoCambio, Integer activo);

  @Modifying
  @Query("DELETE FROM CabCcResolucion ")
  void deleteAllInBatch();

  @Query(value = "CALL sp_registrar_cc_resolucion(:pi_esquema,:pi_n_acta,:pi_c_estado_cambio,:pi_n_resolucion,:pi_aud_usuario_creacion,:po_resultado,:po_mensaje)", nativeQuery = true)
  Map<String, Object> spRegistrarCcResolucion(
      @Param("pi_esquema") String esquema,
      @Param("pi_n_acta") Long idActa,
      @Param("pi_c_estado_cambio") String estadoCambio,
      @Param("pi_n_resolucion") Long idResolucion,
      @Param("pi_aud_usuario_creacion") String usuario,
      @Param("po_resultado") Integer resultado,
      @Param("po_mensaje") String mensaje
  );


}
