package pe.gob.onpe.scebackend.repository.resultados;

import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;


import java.util.List;
import java.util.Map;

public interface ResultadosActaRepository extends JpaRepository<Acta, Long> {

    @Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_detallado_cpr(:pi_esquema, :pi_n_tipo_eleccion, "
            + ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, :pi_aud_usuario_consulta)", nativeQuery = true)
    List<Map<String, Object>> resultadosActasContabilizadasDetalladoCPR(@Param("pi_esquema") String esquema,
                                                                        @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
                                                                        @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
                                                                        @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
                                                                        @Param("pi_c_ubigeo") TypedParameterValue ubigeo,
                                                                        @Param("pi_aud_usuario_consulta") String usuario
    );

    @Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_resumido_voto_pref(:pi_esquema, :pi_n_tipo_eleccion, "
            + ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
    List<Map<String, Object>> resultadosActasContabilizadasResumidoPreferencial(@Param("pi_esquema") String esquema,
                                                                                @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
                                                                                @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
                                                                                @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
                                                                                @Param("pi_c_ubigeo") TypedParameterValue ubigeo
    );

    @Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_resumido_cpr(:pi_esquema, :pi_n_tipo_eleccion, "
            + ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
    List<Map<String, Object>> resultadosActasContabilizadasResumidoCPR(@Param("pi_esquema") String esquema,
                                                                       @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
                                                                       @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
                                                                       @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
                                                                       @Param("pi_c_ubigeo") TypedParameterValue ubigeo
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

    @Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_detallado(:pi_esquema, :pi_n_tipo_eleccion, "
            + ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, :pi_aud_usuario_consulta)", nativeQuery = true)
    List<Map<String, Object>> resultadosActasContabilizadasDetallado(@Param("pi_esquema") String esquema,
                                                                     @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
                                                                     @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
                                                                     @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
                                                                     @Param("pi_c_ubigeo") TypedParameterValue ubigeo,
                                                                     @Param("pi_aud_usuario_consulta") String usuario
    );

    @Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_resumido(:pi_esquema, :pi_n_tipo_eleccion, "
            + ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
    List<Map<String, Object>> resultadosActasContabilizadasResumido(@Param("pi_esquema") String esquema,
                                                                    @Param("pi_n_tipo_eleccion") TypedParameterValue eleccion,
                                                                    @Param("pi_n_ambito_electoral") TypedParameterValue ambito,
                                                                    @Param("pi_n_centro_computo") TypedParameterValue centroComputo,
                                                                    @Param("pi_c_ubigeo") TypedParameterValue ubigeo
    );

}
