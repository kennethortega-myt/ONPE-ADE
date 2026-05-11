package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;

import java.util.List;
import java.util.Map;


public interface AnexosRepository extends JpaRepository<Acta, Long> {

    @Query(value = "SELECT " +
            "c_codigo_eleccion as tipoeleccion, " +
            "c_codigo_ambito_electoral as codigoambito, " +
            "c_codigo_centro_computo as centrocomputo, " +
            "c_numero_lote as numerolote, " +
            "c_codigo_ubigeo as ubigeo, " +
            "c_numero_acta as acta, " +
            "c_numero_copia as numerocopia, " +
            "c_observacion_acta as estadoacta, " +
            "c_estado_error_material as estadoerrormaterial, " +
            "c_detalle_error_material as detalleerrormaterial " +
            "from fn_anexo_listar_actas_observadas(:piEsquema,:piCentroComputo,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaActasObservadas(@Param("piEsquema") String piEsquema,
                                                   @Param("piCentroComputo") Integer piCentroComputo,
                                                   @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_tipo_observacion(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaTipoObservacion(@Param("piEsquema") String piEsquema,
                                                   @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_tipo_error_material(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaTipoErrorMaterial(@Param("piEsquema") String piEsquema,
                                                   @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_votos(:piEsquema,:piCentroComputo,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaVotos(@Param("piEsquema") String piEsquema,
                                        @Param("piCentroComputo") Integer piCentroComputo,
                                                     @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_cifra_repartidora_votos_opcion(:piEsquema,:piCentroComputo,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaVotosCifras(@Param("piEsquema") String piEsquema,
        @Param("piCentroComputo") Integer piCentroComputo,
                                         @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_cifra_repartidora_inicial_votos_opcion(:piEsquema,:piCentroComputo,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaVotosCifrasInicial(@Param("piEsquema") String piEsquema,
        @Param("piCentroComputo") Integer piCentroComputo,
                                         @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_actas_contabilizadas(:piEsquema,:piCentroComputo,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaActasContabilizadas(@Param("piEsquema") String piEsquema,
                                              @Param("piCentroComputo") Integer piCentroComputo,
                                              @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_mesas_no_instaladas(:piEsquema,:piCentroComputo,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaMesasNoinstaladas(@Param("piEsquema") String piEsquema,
                                              @Param("piCentroComputo") Integer piCentroComputo,
                                              @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_agrupacion_politica(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaAgrupacionPolitica(@Param("piEsquema") String piEsquema,
                                                 @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_orden_agrupacion_politica(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaOrdenAgrupacionPolitica(@Param("piEsquema") String piEsquema,
                                                 @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_tipo_eleccion(:piEsquema, :piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaTipoEleccion(@Param("piEsquema") String piEsquema, @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);





    @Query(value = "select * from fn_anexo_listar_ubigeo(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaMaestroUbigeo(@Param("piEsquema") String piEsquema,
                                                     @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);

    @Query(value = "select * from fn_anexo_listar_ambito_electoral(:piEsquema,:piAudUsuarioConsulta)", nativeQuery = true)
    List<Map<String, Object>> listaOdpe(@Param("piEsquema") String piEsquema,
                                                     @Param("piAudUsuarioConsulta") String piAudUsuarioConsulta);


}
