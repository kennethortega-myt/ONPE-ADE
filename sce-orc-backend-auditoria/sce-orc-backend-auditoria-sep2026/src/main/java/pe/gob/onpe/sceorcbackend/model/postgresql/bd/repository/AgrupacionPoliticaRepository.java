package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;

import java.util.List;
import java.util.Map;

public interface AgrupacionPoliticaRepository extends JpaRepository<AgrupacionPolitica, Long> {

	@Modifying
    @Query("DELETE FROM AgrupacionPolitica")
    void deleteAllInBatch();

    /*
     * usamos TypedParameterValue para los parametros que sus valores pueden ser null
     */
    @Query(value = "SELECT * FROM fn_reporte_listado_organizaciones_politicas(:pi_esquema, :pi_eleccion, :pi_centro_computo)", nativeQuery = true)
    List<Map<String, Object>> organizacionesPoliticas(@Param("pi_esquema") String esquema,
                                                      @Param("pi_eleccion") TypedParameterValue eleccion,
                                                      @Param("pi_centro_computo") TypedParameterValue centroComputo);

    
    @Query(value = "CALL sp_cargar_datos_candidato_agrupacion(:pi_esquema,:po_resultado,:po_mensaje)", nativeQuery = true)
	Map<String, Object>  cargarCandidatos(
			@Param("pi_esquema") String esquema, //pi_esquema
			@Param("po_resultado") Integer resultado,
			@Param("po_mensaje") String mensaje
	);

    List<AgrupacionPolitica> findByTipoAgrupacionPoliticaOrderByDescripcionAsc(Long tipo);
    
}
