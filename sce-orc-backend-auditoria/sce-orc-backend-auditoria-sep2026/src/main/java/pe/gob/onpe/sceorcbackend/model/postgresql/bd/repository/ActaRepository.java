package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import jakarta.persistence.LockModeType;
import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.dto.verification.ActaResumenDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ActaProjection;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ActaScanProjection;
import pe.gob.onpe.sceorcbackend.utils.ActaDTO;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ActaRepository extends JpaRepository<Acta, Long> {


  @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.utils.ActaDTO(a.estadoActa, a.estadoCc, a.estadoDigitalizacion, e.codigo) " +
          "FROM Acta a " +
          "JOIN a.ubigeoEleccion ue " +
          "JOIN ue.eleccion e ")
  List<ActaDTO> findActasNative();

  @Query(value = """
    SELECT
        a.id AS idActa,
        e.nombre AS nombreEleccion,
        e.codigo AS codigoEleccion,
        m.codigo AS mesa,
        COALESCE(a.numeroCopia, '') AS copia,
        COALESCE(a.digitoChequeoEscrutinio, '') AS digitoChequeoEscrutinio,
        a.estadoActa AS estadoActa,
        a.estadoCc AS estadoComputo,
        a.estadoDigitalizacion AS estadoDigitalizacion,
        COALESCE(ae.nombre, '') AS archivoEscrutinio,
        COALESCE(ai.nombre, '') AS archivoInstalacion,
        COALESCE(asuf.nombre, '') AS archivoSufragio,
        COALESCE(ais.nombre, '') AS archivoInstalacionSufragio,
        a.activo AS activo,
        a.solucionTecnologica AS solucionTecnologica,
        a.tipoTransmision AS tipoTransmision,
        COALESCE(a.fechaModificacion, a.fechaCreacion) AS fechaModificacion
    FROM Acta a
    JOIN a.mesa m
    JOIN a.ubigeoEleccion ue
    JOIN ue.eleccion e
    LEFT JOIN a.archivoEscrutinio ae
    LEFT JOIN a.archivoInstalacion ai
    LEFT JOIN a.archivoSufragio asuf
    LEFT JOIN a.archivoInstalacionSufragio ais
    WHERE (:codigoEleccion IS NULL OR :codigoEleccion = '' OR e.codigo = :codigoEleccion)
    AND a.estadoDigitalizacion IN :estadosDigitalizacion order by COALESCE(a.fechaModificacion, a.fechaCreacion) desc
    """)
  List<ActaScanProjection> findActasSceScanenr(@Param("codigoEleccion") String codigoEleccion,
                                               @Param("estadosDigitalizacion") List<String> estadosDigitalizacion);


    @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.utils.ActaDTO(a.id, a.estadoActa, ue.eleccion.codigo) " +
          "FROM Acta a " +
          "JOIN a.ubigeoEleccion ue " +
          "WHERE ue.eleccion.codigo = :codigoEleccion ")
  List<ActaDTO> findActasByEleccion(@Param("codigoEleccion") String codigoEleccion);

  List<Acta> findByMesa(Mesa mesa);

  List<Acta> findByMesaOrderById(Mesa mesa);

  @Query("""
    SELECT a FROM Acta a
    WHERE a.mesa.codigo = :codigoMesa
      AND a.ubigeoEleccion.eleccion.codigo = :codigoEleccion
      AND a.activo = 1
""")
  List<Acta> buscarActaPorCodigoMesaaAndCodigoEleccion(
      @Param("codigoMesa") String codigoMesa,
      @Param("codigoEleccion") String codigoEleccion
  );

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
    SELECT a FROM Acta a
    WHERE a.mesa.codigo = :codigoMesa
      AND a.ubigeoEleccion.eleccion.codigo = :codigoEleccion
      AND a.activo = 1
""")
  List<Acta> buscarActaPorCodigoMesaaAndCodigoEleccionForUpdate(
      @Param("codigoMesa") String codigoMesa,
      @Param("codigoEleccion") String codigoEleccion
  );


  List<Acta> findByMesa_Id(Long idMesa);

  List<Acta> findByMesa_IdOrderByIdAsc(Long idMesa);

  List<Acta> findByEstadoActa(String cEstadoActa);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Acta a WHERE a.id = :id")
  Optional<Acta> findByIdForUpdate(@Param("id") Long id);


  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Acta> findByEstadoDigitalizacionAndUsuarioAsignadoIsNull(String cEstadoDigitalizacion);

  List<Acta> findByEstadoDigitalizacionAndUsuarioAsignado(String cEstadoDigitalizacion, String usuario);


  List<Acta> findByEstadoActaAndUsuarioCorreccion(String estadoActa, String usuario);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Acta> findByEstadoActaAndUsuarioCorreccionIsNull(String estadoActa);


    @Query("""
    SELECT a
    FROM Acta a
    WHERE (a.digitalizacionEscrutinio = 3 OR a.digitalizacionInstalacionSufragio = 3)
    AND a.estadoActa = :estadoActa
    AND a.usuarioProcesamientoManual = :usuario
    """)
    List<Acta> findActasProcesamientoManualPorUsuario(
            @Param("estadoActa") String estadoActa,
            @Param("usuario") String usuario
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT a
    FROM Acta a
    WHERE (a.digitalizacionEscrutinio = 3 OR a.digitalizacionInstalacionSufragio = 3)
    AND a.estadoActa = :estadoActa
    AND a.usuarioProcesamientoManual IS NULL
    """)
    List<Acta> findActasProcesamientoManualSinUsuario(@Param("estadoActa") String estadoActa);


    @Query("""
    SELECT a FROM Acta a
    JOIN a.mesa m
    JOIN a.ubigeoEleccion ue
    JOIN ue.eleccion e
    JOIN e.procesoElectoral pe
    WHERE
    e.principal = 1 AND
    m.codigo = :nroMesa AND
    pe.activo = 1
    """)
    List<Acta> findActaPrincipalByMesa(
            @Param("nroMesa") String nroMesa);


  @Query("""
    SELECT a.id FROM Acta a
    JOIN a.ubigeoEleccion ue
    JOIN ue.eleccion e
    WHERE (
      (a.estadoActa = :estadoC AND a.verificador = :usuarioVerificador) OR (a.estadoActa = :estadoW AND a.verificador2 = :usuarioVerificador))
    AND (a.digitalizacionEscrutinio <> 3 AND a.digitalizacionInstalacionSufragio <> 3)
    AND e.codigo = :codigoEleccion
    """)
  List<Long> findByEstadoActaAndVerificadorAndCodigoEleccion(
      @Param("estadoC") String estadoC,
      @Param("estadoW") String estadoW,
      @Param("usuarioVerificador") String usuarioVerificador,
      @Param("codigoEleccion") String codigoEleccion);


  @Query("""
    SELECT a.id
    FROM Acta a
    JOIN a.ubigeoEleccion ue
    JOIN ue.eleccion e
    WHERE (
        (a.estadoActa = :estadoC AND a.verificador = :usuarioVerificador) 
        OR 
        (a.estadoActa = :estadoW AND a.verificador2 = :usuarioVerificador)
    )
    AND (a.digitalizacionEscrutinio = :digitalizacion OR a.digitalizacionInstalacionSufragio = :digitalizacion)
    AND e.codigo = :codigoEleccion
    """)
    List<Long> findByEstadoActaAndVerificadorAndCodigoEleccionAndDigitalizacion(
            @Param("estadoC") String estadoC,
            @Param("estadoW") String estadoW,
            @Param("usuarioVerificador") String usuarioVerificador,
            @Param("codigoEleccion") String codigoEleccion,
            @Param("digitalizacion") Long digitalizacion
    );


  @Query(value = """
    SELECT a.n_acta_pk
     FROM cab_acta a
     JOIN det_ubigeo_eleccion ue ON ue.n_det_ubigeo_eleccion_pk = a.n_det_ubigeo_eleccion
     JOIN mae_eleccion e ON e.n_eleccion_pk = ue.n_eleccion
     WHERE e.c_codigo = :codigoEleccion
     AND (a.c_estado_acta_resolucion IS NULL OR a.c_estado_acta_resolucion NOT IN :estadosResolucionExcluidos)
     AND ((a.c_estado_acta = :estadoDigitalizada AND a.c_verificador IS NULL)
          OR (a.c_estado_acta = :estadoSegundaVerificacion
              AND a.c_verificador_v2 IS NULL
              AND a.c_verificador IS NOT NULL
              AND a.c_verificador <> :usuario))
     AND (a.n_digitalizacion_escrutinio <> 3 AND a.n_digitalizacion_instalacion_sufragio <> 3)
     AND EXISTS (
         SELECT 1
         FROM det_acta_accion daa
         WHERE daa.n_acta = a.n_acta_pk
         AND daa.c_accion = 'MODELO_PROCESAR'
         AND daa.c_tiempo = 'FIN'
         AND daa.n_activo = 1
         AND daa.n_iteracion = (
             SELECT MAX(daa2.n_iteracion)
             FROM det_acta_accion daa2
             WHERE daa2.n_acta = a.n_acta_pk
             AND daa2.c_accion = 'MODELO_PROCESAR'
             AND daa2.n_activo = 1
         )
     )
     AND EXISTS (
           SELECT 1 FROM det_acta_rectangulo dar
           WHERE dar.n_acta = a.n_acta_pk
     )  
    ORDER BY RANDOM()
    LIMIT 1
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
  Optional<Long> findActaDisponibleConModeloProcesado(
          @Param("codigoEleccion") String codigoEleccion,
          @Param("estadosResolucionExcluidos") List<String> estadosResolucionExcluidos,
          @Param("estadoDigitalizada") String estadoDigitalizada,
          @Param("estadoSegundaVerificacion") String estadoSegundaVerificacion,
          @Param("usuario") String usuario
  );


  @Query("""
    SELECT a.id FROM Acta a
    JOIN a.ubigeoEleccion ue
    JOIN ue.eleccion e
    WHERE e.codigo = :codigoEleccion
    AND (a.estadoActaResolucion IS NULL OR a.estadoActaResolucion NOT IN :estadosResolucionExcluidos)
    AND (
        (a.estadoActa = :estadoDigitalizada AND a.verificador IS NULL )
        OR
        (a.estadoActa = :estadoSegundaVerificacion AND a.verificador2 IS NULL AND a.verificador IS NOT NULL AND a.verificador <> :usuario)
    )
    AND (a.digitalizacionEscrutinio = 3 OR a.digitalizacionInstalacionSufragio = 3)
    """)
    List<Long> findActasParaProcesamientoManualDisponibles(
            @Param("codigoEleccion") String codigoEleccion,
            @Param("estadosResolucionExcluidos") List<String> estadosResolucionExcluidos,
            @Param("estadoDigitalizada") String estadoDigitalizada,
            @Param("estadoSegundaVerificacion") String estadoSegundaVerificacion,
            @Param("usuario") String usuario);




  List<Acta> findByUsuarioAsignado(String cUsuarioAsignado);


  List<Acta> findByAutorizacionIdAndReprocesar(Long idAutorizacion, String procesar);


  Long countByEstadoDigitalizacionNot(String estadoDigitalizacion);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Acta e
       SET e.digitoChequeoEscrutinio = null,
           e.digitoChequeoInstalacion = null,
           e.digitoChequeoSufragio = null,
           e.archivoEscrutinio = null,
           e.archivoEscrutinioFirmado = null,
           e.archivoInstalacionSufragio = null,
           e.archivoInstalacionSufragioFirmado = null,
           e.archivoEscrutinioPdf = null,
           e.archivoInstalacion = null,
           e.archivoInstalacionPdf = null,
           e.archivoInstalacionFirmado = null,
           e.archivoSufragio = null,
           e.archivoSufragioFirmado = null,
           e.archivoSufragioPdf = null,
           e.archivoInstalacionSufragioPdf = null,
           e.numeroCopia = null,
           e.numeroLote = null,
           e.cvas = null,
           e.cvasV1 = null,
           e.cvasV2 = null,
           e.cvasAutomatico = null,
           e.ilegibleCvas = null,
           e.ilegibleCvasV1 = null,
           e.ilegibleCvasV2 = null,
           e.votosCalculados = null,
           e.totalVotos = null,
           e.estadoActaResolucion = null,
           e.estadoErrorMaterial = null,
           e.horaInstalacionManual = null,
           e.horaEscrutinioManual = null,
           e.horaEscrutinioAutomatico = null,
           e.horaInstalacionAutomatico = null,
           e.controlDigitacion = null,
           e.controlDigEscrutinio = null,
           e.controlDigInstalacionSufragio = null,
           e.descripcionObservManual = null,
           e.descripcionObservAutomatico = null,
           e.observDigEscrutinio = null,
           e.observDigInstalacionSufragio = null,
           e.digitacionSinDatosManual = null,
           e.digitacionSinDatosManualV1 = null,
           e.digitacionSinDatosManualV2 = null,
           e.digitacionSolicitudNulidadManual = null,
           e.digitacionSolicitudNulidadManualV1 = null,
           e.digitacionSolicitudNulidadManualV2 = null,
           e.escrutinioFirmaMm1Automatico = null,
           e.escrutinioFirmaMm2Automatico = null,
           e.escrutinioFirmaMm3Automatico = null,
           e.instalacionFirmaMm1Automatico = null,
           e.instalacionFirmaMm2Automatico = null,
           e.instalacionFirmaMm3Automatico = null,
           e.sufragioFirmaMm1Automatico = null,
           e.sufragioFirmaMm2Automatico = null,
           e.sufragioFirmaMm3Automatico = null,
           e.reprocesar = null,
           e.usuarioProcesamiento = null,
           e.fechaProcesamiento = null,
           e.autorizacionId = null,
           e.digitacionFirmasManual = null,
           e.digitacionFirmasManualV1 = null,
           e.digitacionFirmasManualV2 = null,
           e.usuarioAsignado = null,
           e.verificador = null,
           e.verificador2 = null,
           e.tipoTransmision = null,
           e.estadoActa = :estadoActa,
           e.estadoCc = :estadoComputo,
           e.estadoDigitalizacion = :estadoDigitalizacion,
           e.digitalizacionEscrutinio = 0,
           e.digitalizacionInstalacionSufragio = 0,
           e.digitacionHoras = 0,
           e.digitacionVotos = 0,
           e.digitacionObserv = 0,
           e.activo = 1,
           e.usuarioModificacion = :usuario,
           e.asignado = null,
           e.fechaModificacion = :fechaModificacion,
           e.ipServer = null,
           e.hostName = null,
           e.usuarioControlCalidad = NULL,
           e.fechaControlCalidad = NULL,
           e.usuarioCorreccion = NULL,
           e.fechaUsuarioCorreccion = NULL,
           e.usuarioProcesamientoManual = NULL,
           e.fechaUsuarioProcesamientoManual = NULL
    """)
    int reseteaValores(
            @Param("estadoActa") String estadoActa,
            @Param("estadoComputo") String estadoComputo,
            @Param("estadoDigitalizacion") String estadoDigitalizacion,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Date fechaModificacion
    );



    @Modifying
  @Transactional
  @Query("UPDATE Acta e " +
          "SET e.digitoChequeoEscrutinio = null, " +
          "    e.digitoChequeoInstalacion = null, " +
          "    e.archivoEscrutinio = null, " +
          "    e.archivoEscrutinioFirmado = null, " +
          "    e.archivoInstalacionSufragio = null, " +
          "    e.archivoInstalacionSufragioFirmado = null, " +
          "    e.numeroCopia = null, " +
          "    e.numeroLote = null, " +
          "    e.cvas = null, " +
          "    e.cvasV1 = null, " +
          "    e.cvasV2 = null, " +
          "    e.cvasAutomatico = null, " +
          "    e.ilegibleCvas = null, " +
          "    e.ilegibleCvasV1 = null, " +
          "    e.ilegibleCvasV2 = null, " +
          "    e.votosCalculados = null, " +
          "    e.totalVotos = null, " +
          "    e.estadoActaResolucion = null, " +
          "    e.estadoErrorMaterial = null, " +
          "    e.horaInstalacionManual = null, " +
          "    e.horaEscrutinioManual = null, " +
          "    e.horaEscrutinioAutomatico = null, " +
          "    e.horaInstalacionAutomatico = null, " +
          "    e.controlDigitacion = null, " +
          "    e.controlDigEscrutinio = null, " +
          "    e.controlDigInstalacionSufragio = null, " +
          "    e.descripcionObservManual = null, " +
          "    e.descripcionObservAutomatico = null, " +
          "    e.observDigEscrutinio = null, " +
          "    e.observDigInstalacionSufragio = null, " +
          "    e.usuarioAsignado = null, " +
          "    e.verificador = null, " +
          "    e.verificador2 = null, " +
          "    e.tipoTransmision = null, " +
          "    e.estadoActa = :estadoActa, " +
          "    e.estadoCc = :estadoComputo, " +
          "    e.estadoDigitalizacion = :estadoDigitalizacion, " +
          "    e.digitalizacionEscrutinio = 0, " +
          "    e.digitalizacionInstalacionSufragio = 0, " +
          "    e.digitacionHoras = 0, " +
          "    e.digitacionVotos = 0, " +
          "    e.digitacionObserv = 0, " +
          "    e.activo = 1, " +
          "    e.usuarioModificacion = :usuario, " +
          "    e.asignado = NULL, " +
          "    e.fechaModificacion = :fechaModificacion where e.id = :pIdActa")
  int reseteaValoresPorActa(@Param("estadoActa") String estadoActa,
                     @Param("estadoComputo") String estadoComputo,
                     @Param("estadoDigitalizacion") String estadoDigitalizacion,
                     @Param("usuario") String usuario,
                     @Param("fechaModificacion") Date fechaModificacion,
                      @Param("pIdActa") Long idActa);

  @Query(value = "SELECT a FROM Acta a " +
      "WHERE (a.estadoActa = :estadoActaProcesada AND a.estadoCc = :estadoComputoContabilizada) " +
      "   OR (a.estadoActa = :estadoActaProcesadaPorResolucion AND a.estadoCc = :estadoComputoContabilizada) " +
      "   OR a.estadoActa = :estadoActaEnviadaJEE " +
      "   OR a.estadoActa = :estadoActaParaEnvioJurado " +
      "   OR a.estadoActa = :estadoActaExtraviado  " +
      "   OR a.estadoActa = :estadoActaSiniestrado " +
      "   OR a.estadoActa = :estadoMesaNoInstalado " +
      "   OR a.estadoActa = :estadoReprocesadaNormal " +
      "   OR a.estadoActa = :estadoReprocesadaAnulada " +
      "   OR (a.estadoActa = :estadoActaAnulada AND a.estadoCc = :estadoComputoContabilizada) " +
      "ORDER BY a.id ")
  List<Acta> filtrarActasParaMonitoreoTransmision(
      @Param("estadoActaProcesada") String estadoActaProcesada,
      @Param("estadoComputoContabilizada") String estadoComputoContabilizada,
      @Param("estadoActaProcesadaPorResolucion") String estadoActaProcesadaPorResolucion,
      @Param("estadoActaEnviadaJEE") String estadoActaEnviadaJEE,
      @Param("estadoActaParaEnvioJurado") String estadoActaParaEnvioJurado,
      @Param("estadoActaExtraviado") String estadoActaExtraviado,
      @Param("estadoActaSiniestrado") String estadoActaSiniestrado,
      @Param("estadoMesaNoInstalado") String estadoMesaNoInstalado,
      @Param("estadoReprocesadaNormal") String estadoReprocesadaNormal,
      @Param("estadoReprocesadaAnulada") String estadoReprocesadaAnulada,
      @Param("estadoActaAnulada") String estadoActaAnulada);

  @Query("""
  SELECT
    SUM(CASE 
      WHEN a.estadoDigitalizacion = :estadoDigtalPendiente 
        AND a.estadoActa NOT IN :estadosActaExcluidos
        AND a.estadoCc <> :estadoComputoContabilizada
      THEN 1 ELSE 0 END) AS soloPendientes,
    SUM(CASE WHEN a.estadoDigitalizacion = :estadoDigtalDigitalizado THEN 1 ELSE 0 END) AS digtal,
    SUM(CASE WHEN a.estadoDigitalizacion IN :estadoDigtalAprobada THEN 1 ELSE 0 END) AS approved,
    SUM(CASE WHEN a.estadoDigitalizacion IN :estadosDigtalRechazados THEN 1 ELSE 0 END) AS rejected,
    SUM(CASE 
      WHEN (a.estadoDigitalizacion = :estadoDigtalPendiente AND a.estadoActa = :estadoActaSiniestrada)
        OR (a.estadoDigitalizacion = :estadoDigtalPendiente AND a.estadoActa = :estadoActaExtraviada)
        OR (a.estadoDigitalizacion = :estadoDigtalPendiente AND a.estadoActa = :estadoActaNoInstalada AND a.estadoCc = :estadoComputoContabilizada)
      THEN 1 ELSE 0 END) AS noInstalados
  FROM Acta a
  WHERE a.ubigeoEleccion.eleccion.codigo = :codigoEleccion
  """)
  Object[] getDigitalizationSummary(
          @Param("codigoEleccion") String codigoEleccion,
          @Param("estadoDigtalDigitalizado") String estadoDigtalDigitalizado,
          @Param("estadoDigtalAprobada") List<String> estadoDigtalAprobada,
          @Param("estadosDigtalRechazados") List<String> estadosDigtalRechazados,
          @Param("estadoDigtalPendiente") String estadoDigtalPendiente,
          @Param("estadoActaSiniestrada") String estadoActaSiniestrada,
          @Param("estadoActaExtraviada") String estadoActaExtraviada,
          @Param("estadoActaNoInstalada") String estadoActaNoInstalada,
          @Param("estadoComputoContabilizada") String estadoComputoContabilizada,
          @Param("estadosActaExcluidos") List<String> estadosActaExcluidos
  );

  /*
   * usamos TypedParameterValue para los parámetros que sus valores pueden ser null
   */
  @Query(value = "SELECT * FROM fn_reporte_avance_digitalizacion_actas(:pi_esquema, :pi_eleccion, "
          + ":pi_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
  List<Map<String, Object>> avanceDigitalizacion(@Param("pi_esquema") String esquema,
                                                 @Param("pi_eleccion") TypedParameterValue eleccion,
                                                 @Param("pi_centro_computo") TypedParameterValue centroComputo,
                                                 @Param("pi_c_ubigeo") TypedParameterValue departamento
  );
  @Query(value = "SELECT * FROM fn_reporte_avance_digitalizacion_acta_celeste(:pi_esquema, :pi_n_eleccion, "
          + ":pi_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
  List<Map<String, Object>> avanceDigitalizacionSobreCeleste(@Param("pi_esquema") String esquema,
                                                             @Param("pi_n_eleccion") TypedParameterValue eleccion,
                                                             @Param("pi_centro_computo") TypedParameterValue centroComputo,
                                                             @Param("pi_c_ubigeo") TypedParameterValue departamento
  );

  @Query(value = "SELECT * FROM fn_reporte_avance_estado_acta(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_centro_computo, :pi_n_estado, :pi_aud_usuario_consulta)", nativeQuery = true)
  List<Map<String, Object>> avanceEstadoActa(@Param("pi_esquema") String esquema,
                                             @Param("pi_n_tipo_eleccion") Integer tipoEleccion,
                                             @Param("pi_n_centro_computo") Integer centro,
                                             @Param("pi_n_estado") Integer estado,
                                             @Param("pi_aud_usuario_consulta") String usuarioConsulta);

  @Query(value = "SELECT * FROM fn_reporte_estado_acta_por_ambito(:pi_esquema, :pi_eleccion, :pi_ambito_electoral, "
          + ":pi_centro_computo)", nativeQuery = true)
  List<Map<String, Object>> estadoActasOdpe(@Param("pi_esquema") String esquema,
                                            @Param("pi_eleccion") TypedParameterValue eleccion,
                                            @Param("pi_ambito_electoral") TypedParameterValue ambito,
                                            @Param("pi_centro_computo") TypedParameterValue centroComputo
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

  @Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_resumido(:pi_esquema, :pi_n_tipo_eleccion, "
          + ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, :pi_aud_usuario_consulta)", nativeQuery = true)
  List<Map<String, Object>> resultadosActasContabilizadasResumido(@Param("pi_esquema") String esquema,
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

  @Query(value = "SELECT * FROM fn_reporte_avance_acta_contabilizada_resumido_voto_pref(:pi_esquema, :pi_n_tipo_eleccion, "
          + ":pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo)", nativeQuery = true)
  List<Map<String, Object>> resultadosActasContabilizadasResumidoPreferencial(@Param("pi_esquema") String esquema,
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

  @Query(value = "SELECT * FROM fn_reporte_auditoria_digitacion_cpr(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)))", nativeQuery = true)
  List<Map<String, Object>> auditoriaDigitacionCPR(@Param("pi_esquema") String esquema,
                                                   @Param("pi_n_tipo_eleccion") Integer tipoEleccion,
                                                   @Param("pi_n_ambito_electoral") Integer ambitoElectoral,
                                                   @Param("pi_n_centro_computo") Integer centroComputo,
                                                   @Param("pi_c_ubigeo") String ubigeo,
                                                   @Param("pi_c_mesa") String mesa);

  @Query(value = "SELECT * FROM fn_reporte_auditoria_digitacion(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)))", nativeQuery = true)
  List<Map<String, Object>> auditoriaDigitacion(@Param("pi_esquema") String esquema,
                                                @Param("pi_n_tipo_eleccion") Integer tipoEleccion,
                                                @Param("pi_n_ambito_electoral") Integer ambitoElectoral,
                                                @Param("pi_n_centro_computo") Integer centroComputo,
                                                @Param("pi_c_ubigeo") String ubigeo,
                                                @Param("pi_c_mesa") String mesa);

  @Query(value = "SELECT * FROM fn_reporte_auditoria_digitacion_voto_preferencial(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)))", nativeQuery = true)
  List<Map<String, Object>> auditoriaDigitacionPreferencial(@Param("pi_esquema") String esquema,
                                                            @Param("pi_n_tipo_eleccion") Integer tipoEleccion,
                                                            @Param("pi_n_ambito_electoral") Integer ambitoElectoral,
                                                            @Param("pi_n_centro_computo") Integer centroComputo,
                                                            @Param("pi_c_ubigeo") String ubigeo,
                                                            @Param("pi_c_mesa") String mesa);
  
  @Query(value = "SELECT * FROM fn_reporte_avance_mesa_por_mesa(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo,(CAST(:pi_c_mesa AS VARCHAR)), :pi_aud_usuario_consulta)", nativeQuery = true)
  List<Map<String, Object>> avanceMesaMesa(@Param("pi_esquema") String esquema,
											 @Param("pi_n_tipo_eleccion") Integer tipoEleccion,
											 @Param("pi_n_ambito_electoral") Integer ambitoElectoral,
											 @Param("pi_n_centro_computo") Integer centroComputo,
											 @Param("pi_c_ubigeo") String ubigeo,
											 @Param("pi_c_mesa") String mesa,
											 @Param("pi_aud_usuario_consulta") String usuarioConsulta);
  
  @Query(value = "SELECT * FROM fn_reporte_avance_mesa_por_mesa_voto_preferencial(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, (CAST(:pi_c_mesa AS VARCHAR)), :pi_c_usuario)", nativeQuery = true)
  List<Map<String, Object>> avanceMesaMesaPreferencial(@Param("pi_esquema") String esquema,
											 @Param("pi_n_tipo_eleccion") Integer tipoEleccion,
											 @Param("pi_n_ambito_electoral") Integer ambitoElectoral,
											 @Param("pi_n_centro_computo") Integer centroComputo,
											 @Param("pi_c_ubigeo") String ubigeo,
											 @Param("pi_c_mesa") String mesa,
											 @Param("pi_c_usuario") String usuario);
  
  @Query(value = "SELECT * FROM fn_reporte_avance_mesa_por_mesa_cpr(:pi_esquema, :pi_n_tipo_eleccion, :pi_n_ambito_electoral, :pi_n_centro_computo, :pi_c_ubigeo, (CAST(:pi_c_mesa AS VARCHAR)), :pi_aud_usuario_consulta)", nativeQuery = true)
	List<Map<String, Object>> avanceMesaMesaCPR(@Param("pi_esquema") String esquema,
											 @Param("pi_n_tipo_eleccion") Integer tipoEleccion,
											 @Param("pi_n_ambito_electoral") Integer ambitoElectoral,
											 @Param("pi_n_centro_computo") Integer centroComputo,
											 @Param("pi_c_ubigeo") String ubigeo,
											 @Param("pi_c_mesa") String mesa,
											 @Param("pi_aud_usuario_consulta") String usuarioConsulta);
  
  @Query(""" 
            SELECT 
	      SUM(CASE WHEN a.estadoCc = :estadoComputoPen
               AND ( ( a.estadoDigitalizacion = :estadoDigitalizacion AND a.estadoActa NOT IN :estadosActaExcluidos) OR
                     (a.estadoDigitalizacion = :estadoDigPendiente AND a.estadoActa = :estadoActaProcesada ) ) THEN 1 ELSE 0 END) AS pendiente, 
	      SUM(CASE WHEN a.estadoDigitalizacion = :estadoDigitalizadaVal AND a.estadoCc = :estadoComputoVal THEN 1 ELSE 0 END) AS validado 
	      FROM Acta a 
	      WHERE a.ubigeoEleccion.eleccion.codigo = :codigoEleccion 
  	     """)
  Object[] getControlCalidadSummary(@Param("codigoEleccion") String codigoEleccion,
		  @Param("estadoDigitalizacion") String estadoDigitalizacion,
		  @Param("estadoComputoPen") String estadoComputoPen,
		  @Param("estadoDigitalizadaVal") String estadoDigitalizadaVal,
		  @Param("estadoComputoVal") String estadoComputoVal,
		  @Param("estadosActaExcluidos") List<String> estadosActaExcluidos,
          @Param("estadoDigPendiente") String estadoDigPendiente,
          @Param("estadoActaProcesada") String estadoActaProcesada);
  
  @Query("""
		    SELECT a
		    FROM Acta a
		    WHERE a.usuarioControlCalidad = :usuarioControlCalidad
		      AND a.ubigeoEleccion.eleccion.codigo = :codigoEleccion
			  AND a.estadoCc = :estadocc
		      AND ( ( a.estadoDigitalizacion = :estadoDigitalizacion AND a.estadoActa NOT IN :estadosActaExcluidos) OR 
				     (a.estadoDigitalizacion = :estadoDigPendiente AND a.estadoActa = :estadoActaProcesada )
	               )
		""")
  List<Acta> listarActasPendientesCcAsignados(
		  @Param("estadocc") String estadocc,
	      @Param("estadoDigitalizacion") String estadoDigitalizacion,
	      @Param("usuarioControlCalidad") String usuarioControlCalidad,
	      @Param("codigoEleccion") String codigoEleccion,
	      @Param("estadosActaExcluidos") List<String> estadosActaExcluidos,
          @Param("estadoDigPendiente") String estadoDigPendiente,
          @Param("estadoActaProcesada") String estadoActaProcesada);
  
  @Query("""
		    SELECT a
		    FROM Acta a
		    WHERE a.usuarioControlCalidad IS NULL
		      AND a.ubigeoEleccion.eleccion.codigo = :codigoEleccion
		      AND a.estadoCc = :estadocc 
		      AND ( ( a.estadoDigitalizacion = :estadoDigitalizacion AND a.estadoActa NOT IN :estadosActaExcluidos) OR 
				     (a.estadoDigitalizacion = :estadoDigPendiente AND a.estadoActa = :estadoActaProcesada )
	               )		      
		""")
List<Acta> listarActasPendientesCcNoAsignados(
		  @Param("estadocc") String estadocc,
	      @Param("estadoDigitalizacion") String estadoDigitalizacion,	      
	      @Param("codigoEleccion") String codigoEleccion,
	      @Param("estadosActaExcluidos") List<String> estadosActaExcluidos,
          @Param("estadoDigPendiente") String estadoDigPendiente,
          @Param("estadoActaProcesada") String estadoActaProcesada);

  @Query("""
    SELECT new pe.gob.onpe.sceorcbackend.model.dto.verification.ActaResumenDto(
        a.id,
        m.codigo,
        a.numeroCopia,
        a.digitoChequeoEscrutinio,
        e.nombre,
        a.estadoActa
    )
    FROM Acta a
    JOIN a.mesa m
    JOIN m.localVotacion lv
    JOIN a.ubigeoEleccion ue
    JOIN ue.eleccion e
    WHERE a.estadoActa IN :estadosActa
      AND ue.ubigeo.id = :idUbigeo
      AND (:idLocalVotacion =0 OR lv.id = :idLocalVotacion)
      AND (:idEleccion =0 OR e.id = :idEleccion) order by a.id
""")
  List<ActaResumenDto> listarActasPorEstadoUbigeoLocalVotacion(
      @Param("estadosActa") List<String> estadosActa,
      @Param("idUbigeo") Long idUbigeo,
      @Param("idLocalVotacion") Long idLocalVotacion,
      @Param("idEleccion") Long idEleccion
  );


  @Query("""
    SELECT SUM(CASE WHEN a.estadoActa IN :estadosActa THEN 1 ELSE 0 END), COUNT(a)
    FROM Acta a
    JOIN a.mesa m
    JOIN m.localVotacion lv
    JOIN a.ubigeoEleccion ue
    JOIN ue.eleccion e
    WHERE ue.ubigeo.id = :idUbigeo
      AND (:idLocalVotacion =0 OR lv.id = :idLocalVotacion)
      AND (:idEleccion =0 OR e.id = :idEleccion)
""")
  List<Object[]> contarCoincidenciasYTotal(
      @Param("idUbigeo") Long idUbigeo,
      @Param("idLocalVotacion") Long idLocalVotacion,
      @Param("idEleccion") Long idEleccion,
      @Param("estadosActa") List<String> estadosActa
  );
  
  	@Query(value = "CALL sp_registrar_transmision_acta(:pi_esquema,:pi_c_acta_text,:pi_aud_usuario_creacion,:pi_desarrollo,:po_resultado,:po_mensaje,:po_estado_acta,:po_estado_acta_resolucion,:po_estado_computo,:po_estado_error_material,:po_tipo_transmision)", nativeQuery = true)
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
			@Param("po_estado_error_material") String poErrorMaterial,
			@Param("po_tipo_transmision") Integer poTipoTransmision
			
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

	@Query("SELECT a FROM Acta a " + 
			"JOIN a.mesa m " + 
			"WHERE m.codigo = ?1")
	Optional<Acta> findByNumeroMesa(String numeroMesa);

    List<ActaProjection> findByEstadoActaAndVerificadorIsNotNullAndVerificador2IsNull(String estadoActa);

    List<ActaProjection> findByEstadoActaAndVerificadorIsNotNullAndVerificador2IsNotNull(String estadoActa);


    @Transactional
    @Modifying
    @Query("""
       UPDATE Acta a
          SET a.estadoActa   = :estado,
              a.verificador  = :usuario
        WHERE a.id          = :idActa
       """)
    void updateEstadoActaAndVerificador(@Param("idActa") Long idActa,
                                        @Param("estado") String estado,
                                        @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("""
       UPDATE Acta a
          SET a.estadoActa   = :estado,
              a.verificador2 = :usuario
        WHERE a.id          = :idActa
       """)
    void updateEstadoActaAndVerificador2(@Param("idActa") Long idActa,
                                         @Param("estado") String estado,
                                         @Param("usuario") String usuario);
    
    
    @Query(value = "SELECT ca.* "
			+ "FROM cab_acta ca "
			+ "inner join tab_mesa tm on ca.n_mesa = tm.n_mesa_pk "
			+ "INNER JOIN mae_local_votacion mlv ON tm.n_local_votacion=mlv.n_local_votacion_pk "
			+ "INNER JOIN mae_ubigeo mu ON mlv.n_ubigeo=mu.n_ubigeo_pk "
			+ "INNER JOIN det_ubigeo_eleccion due on due.n_ubigeo = mu.n_ubigeo_pk "
			+ "and due.n_det_ubigeo_eleccion_pk = ca.n_det_ubigeo_eleccion "
			+ "where tm.c_mesa = ?1 and due.n_eleccion = ?2", nativeQuery = true)
	Optional<Acta> findByNumeroMesaAndEleccion(String numeroMesa, Integer idEleccion);



    @Query("""
		    SELECT a
		    FROM Acta a
		    WHERE a.mesa.codigo = :mesa
		      AND a.ubigeoEleccion.eleccion.codigo = :codigoEleccion
		""")
    List<Acta> listarActasPorMesaAndCodigoEleccion(
            @Param("mesa") String mesa,
            @Param("codigoEleccion") String codigoEleccion
    );




    /**
     * metodos para liberar actas en control de digitalización
     */
    @Query(""" 
        SELECT a.id AS id,
               a.usuarioAsignado AS usuarioAsignado
        FROM Acta a
        WHERE a.estadoDigitalizacion = :estadoDigitalizacion
          AND a.usuarioAsignado IS NOT NULL
    """)
    List<ActaProjection> buscarActaControlDigitalizacionTomadas(@Param("estadoDigitalizacion") String estadoDigitalizacion);


    @Transactional
    @Modifying
    @Query("""
       UPDATE Acta a
       SET
         a.usuarioAsignado  = null,
         a.asignado = 0
       WHERE a.id = :idActa
       """)
    void liberarActasControlDigitalizacion(@Param("idActa") Long idActa);



    /**
     * metodos para liberar actas para procesamiento manual tomadas
     */
    @Query(""" 
        SELECT a.id AS id,
               a.usuarioProcesamientoManual AS usuarioProcesamientoManual
        FROM Acta a
        WHERE (a.digitalizacionEscrutinio = 3 OR a.digitalizacionInstalacionSufragio = 3)
          AND a.estadoActa = :estadoActa
          AND a.usuarioProcesamientoManual IS NOT NULL
    """)
    List<ActaProjection> buscarActaProcesamientoManualTomadas(@Param("estadoActa") String estadoActa);


    @Transactional
    @Modifying
    @Query("""
       UPDATE Acta a
       SET
         a.usuarioProcesamientoManual  = null,
         a.fechaUsuarioProcesamientoManual = null
       WHERE a.id = :idActa
       """)
    void liberarActasProcesamientoManual(@Param("idActa") Long idActa);





    /**
     * métodos para liberar actas por corregir tomadas
     */
    @Query(""" 
        SELECT a.id AS id,
               a.usuarioCorreccion AS usuarioCorreccion
        FROM Acta a
        WHERE
          a.estadoActa = :estadoActa
          AND a.usuarioCorreccion IS NOT NULL
    """)
    List<ActaProjection> buscarActasPorCorregirTomadas(@Param("estadoActa") String estadoActa);

    @Transactional
    @Modifying
    @Query("""
       UPDATE Acta a
       SET
        a.usuarioCorreccion  = null,
        a.fechaUsuarioCorreccion = null
       WHERE a.id = :idActa
       """)
    void liberarActasPorCorregir(@Param("idActa") Long idActa);



    /**
     * Métodos para liberar actas de control de calidad
     */
    @Query(""" 
        SELECT a.id AS id,
               a.usuarioControlCalidad AS usuarioControlCalidad
        FROM Acta a
        WHERE
          a.estadoDigitalizacion = :estadoDigitalizacion AND
          a.estadoCc = :estadoComputo
          AND a.usuarioControlCalidad IS NOT NULL
    """)
    List<ActaProjection> buscarActasControlCalidadTomadas (
            @Param("estadoDigitalizacion") String estadoDigitalizacion,
            @Param("estadoComputo") String estadoComputo
    );

    @Transactional
    @Modifying
    @Query("""
       UPDATE Acta a
       SET
        a.usuarioControlCalidad  = null,
        a.fechaControlCalidad = null
       WHERE a.id = :idActa
       """)
    void liberarActasControlCalidad(@Param("idActa") Long idActa);
    
    @Query("SELECT a FROM Acta a WHERE a.id = :id AND a.activo = 1")
    Optional<Acta> buscarActaActivaPorId(@Param("id") Long id);
    
    
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
			AND a.c_estado_computo = 'S'
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
			AND a.c_estado_acta = 'I' AND a.c_estado_computo = 'O' 
			""", 
			nativeQuery = true)
	Long getTotalEnviadasJne(
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
			AND a.c_estado_acta IN ('J','K') 
			AND a.c_estado_computo = 'O' 
			""", 
			nativeQuery = true)
	Long getTotalDevueltasJne(
			@Param("idProceso") Long idProceso,
			@Param("idEleccion") Long idEleccion,
			@Param("idDepartamento") Long idDepartamento, 
			@Param("idProvincia") Long idProvincia,
			@Param("idUbigeo") Long idUbigeo, 
			@Param("idLocalVotacion") Long idLocalVotacion,
			@Param("mesa") String mesa);

  @Query(value = "SELECT * FROM fn_reporte_estado_acta_por_eleccion(:pi_esquema, :int4)", nativeQuery = true)
  List<Map<String, Object>> resumenEstadoActas(@Param("pi_esquema") String esquema,
                                            @Param("int4") TypedParameterValue centroComputo
  );
  
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE cab_acta SET " +
                 "n_archivo_escrutinio_pdf_firmado = COALESCE(:archivoEscrutinioId, n_archivo_escrutinio_pdf_firmado), " +
                 "n_archivo_instalacion_pdf_firmado = COALESCE(:archivoInstalacionId, n_archivo_instalacion_pdf_firmado), " +
                 "n_archivo_sufragio_pdf_firmado = COALESCE(:archivoSufragioId, n_archivo_sufragio_pdf_firmado), " +
                 "c_estado_digitalizacion = :estado " +
                 "WHERE n_acta_pk = :idActa", 
         nativeQuery = true)
  int actualizarArchivosActa(
      @Param("idActa") Long idActa,
      @Param("archivoEscrutinioId") Long archivoEscrutinioId,
      @Param("archivoInstalacionId") Long archivoInstalacionId,
      @Param("archivoSufragioId") Long archivoSufragioId,
      @Param("estado") String estado
  );

}
