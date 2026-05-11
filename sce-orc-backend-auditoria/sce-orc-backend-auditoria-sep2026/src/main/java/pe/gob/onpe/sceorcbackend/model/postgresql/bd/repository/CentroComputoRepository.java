package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;

import java.util.Map;
import java.util.Optional;


public interface CentroComputoRepository extends JpaRepository<CentroComputo, Long> {

    Optional<CentroComputo> findByCodigo(String codigo);

    @Modifying
    @Query("DELETE FROM CentroComputo")
    void deleteAllInBatch();
    
    @Query(value = "select distinct mcc.* from cab_acta ca "
  		  + " inner join tab_mesa tm on ca.n_mesa = tm.n_mesa_pk "
  		  + " inner join mae_local_votacion mlv on mlv.n_local_votacion_pk = tm.n_local_votacion "
  		  + " inner join mae_ubigeo mu on mu.n_ubigeo_pk = mlv.n_ubigeo "
  		  + " inner join mae_centro_computo mcc on mcc.n_centro_computo_pk = mu.n_centro_computo "
  		  + " where tm.c_mesa = ?1", nativeQuery = true)
    public Optional<CentroComputo> findByCodigoMesa(String codigo);



    @Query(value = "CALL sp_registrar_puesta_cero_cc(:pi_esquema,:pi_n_centro_computo, :pi_c_fecha_ejecucion, :pi_c_fecha_transmision, :pi_aud_usuario_creacion, :po_resultado,:po_mensaje)", nativeQuery = true)
    Map<String, Object> registrarPuestaCeroCc(
            @Param("pi_esquema") String esquema,
            @Param("pi_n_centro_computo") Integer idCentroComputo,
            @Param("pi_c_fecha_ejecucion") String fechaEjecucion,
            @Param("pi_c_fecha_transmision") String fechaTransmision,
            @Param("pi_aud_usuario_creacion") String usuarioCreacion,
            @Param("po_resultado") Integer resultado,
            @Param("po_mensaje") String mensaje
    );
	
}
