package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import jakarta.persistence.LockModeType;
import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.MesaProjection;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

  Mesa findByCodigo(String mesa);

  List<Mesa> findByCodigoIn(List<String> lstCodigos);

  List<Mesa> findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMm(String estadoPendiente, String estadoMesa, String usuario);


  @Query("""
    SELECT m.id
    FROM Mesa m
    WHERE m.usuarioAsignadoLe IS NULL
      AND m.activo = 1
      AND m.estadoDigitalizacionLe IN :estadosFiltro
      AND (
            m.estadoDigitalizacionLe <> :estadoP
            OR m.estadoMesa = :estadoMesaNoInstalada
          )

      AND (
            m.estadoDigitalizacionLe NOT IN :estadosSC
            OR EXISTS (
                  SELECT 1
                  FROM DetLeRectangulo d
                  WHERE d.mesaId = m.id
            )
          )
      AND EXISTS (
            SELECT 1
            FROM Acta a
            JOIN a.ubigeoEleccion ue
            JOIN ue.eleccion e
            WHERE a.mesa = m
              AND e.principal = 1
              AND a.estadoActa NOT IN :estadosActaNoProcesadas
      )
""")
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Long> findMesaIdsLeRandomLibres(
          @Param("estadosFiltro") List<String> estadosFiltro,
          @Param("estadoP") String estadoP,
          @Param("estadoMesaNoInstalada") String estadoMesaNoInstalada,
          @Param("estadosSC") List<String> estadosSC,
          @Param("estadosActaNoProcesadas") List<String> estadosActaNoProcesadas
  );


  @Query("""
    SELECT m.id
    FROM Mesa m
    WHERE m.usuarioAsignadoLe = :usuario
      AND m.activo = 1
      AND m.estadoDigitalizacionLe IN :estadosFiltro
      AND (
            (m.estadoDigitalizacionLe = :estadoDigitalPendiente
             AND m.estadoMesa = :estadoMesaNoInstalada)
            OR (m.estadoDigitalizacionLe <> :estadoDigitalPendiente)
          )
    """)
  List<Long> findMesasAsignadasConFiltro(
          @Param("usuario") String usuario,
          @Param("estadosFiltro") List<String> estadosFiltro,
          @Param("estadoDigitalPendiente") String estadoDigitalPendiente,
          @Param("estadoMesaNoInstalada") String estadoMesaNoInstalada
  );


  @Query("""
        SELECT m
        FROM Mesa m
        WHERE m.estadoDigitalizacionMm IN (:estadoDigitalizacionMm)
          AND (
            m.estadoDigitalizacionMm = :estadoDigtalPerdidaTotal
            OR EXISTS (
            SELECT 1 FROM DetMmRectangulo d
            WHERE d.mesaId = m.id
          )
        )
        AND m.usuarioAsignadoMm IS NULL
        """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Mesa> findMesasByEstadoMmAndWithRectangulos(@Param("estadoDigitalizacionMm") String estadoDigitalizacionMm,
                                                   @Param("estadoDigtalPerdidaTotal") String estadoDigtalPerdidaTotal);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Mesa> findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMmIsNull(String estadoPendienteDigtal, String estadoMesa);

  List<Mesa> findByEstadoDigitalizacionMmAndUsuarioAsignadoMm(String estadoDigitalizacion, String usuario);

  List<Mesa> findByEstadoDigitalizacionMmAndUsuarioControlMm(String estadoDigitalizacion, String usuarioControl);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Mesa> findByEstadoDigitalizacionMmAndUsuarioControlMmIsNull(String estadoDigitalizacion);

  List<Mesa> findByEstadoDigitalizacionLeInAndUsuarioControlLe(List<String> estadosDigtal, String usuarioControl);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Mesa> findByEstadoDigitalizacionLeInAndUsuarioControlLeIsNull(List<String> estadosDigtal);

  @Modifying
  @Query("DELETE FROM Mesa")
  void deleteAllInBatch();


  @Modifying
  @Transactional
  @Query("UPDATE Mesa e " +
      "SET e.estadoDigitalizacionMm = :estadoDigitalizacion, " +
      "    e.estadoDigitalizacionLe = :estadoDigitalizacion, " +
      "    e.estadoDigitalizacionPr = :estadoDigitalizacion, " +
      "    e.estadoDigitalizacionMe = :estadoDigitalizacion, " +
      "    e.usuarioAsignadoLe = null, " +
      "    e.usuarioAsignadoMm = null, " +
      "    e.usuarioAsignadoPr = null, " +
      "    e.usuarioAsignadoMe = null, " +
      "    e.fechaAsignadoMm = null, " +
      "    e.fechaAsignadoLe = null, " +
      "    e.fechaAsignadoPr = null, " +
      "    e.fechaAsignadoMe = null, " +
      "    e.usuarioControlMm = null, " +
      "    e.fechaUsuarioControlMm = null, " +
      "    e.usuarioControlLe = null, " +
      "    e.fechaUsuarioControlLe = null, " +
      "    e.estadoMesa = :estadoMesa, " +
      "    e.usuarioModificacion = :usuario, " +
      "    e.fechaModificacion = :fechaModificacion")
  int reseteaValores(@Param("estadoDigitalizacion") String estadoDigitalizacion,
      @Param("estadoMesa") String estadoMesa,
      @Param("usuario") String usuario,
      @Param("fechaModificacion") Date fechaModificacion);

  @Query(
      value = "SELECT DISTINCT * FROM fn_reporte_mesas_por_ubigeo(:pi_esquema, :pi_c_centro_computo, " +
          "(SELECT NULLIF (:pi_departamento,'0')), " +
          "(SELECT NULLIF (:pi_provincia,'0')), " +
          "(SELECT NULLIF (:pi_distrito,'0'))) " +
          "ORDER BY c_ubigeo, c_codigo_local, c_numero_mesa",
      nativeQuery = true)
  public List<Map<String, Object>> getReporteMesaPorUbigeo(@Param("pi_esquema") String piEsquema,
      @Param("pi_c_centro_computo") TypedParameterValue piCentroComputo,
      @Param("pi_departamento") String piDepartamento,
      @Param("pi_provincia") String piProvincia,
      @Param("pi_distrito") String piDistrito);

  @Modifying
  @Transactional
  @Query("UPDATE Mesa e " +
      "SET e.estadoDigitalizacionPr = :estadoDigitalizacion, e.fechaAsignadoPr =:fechaModificacion, "
      + "e.usuarioAsignadoPr =:usuario  WHERE e.id =:idMesa ")
  int actualizarEstadoPR(@Param("estadoDigitalizacion") String estadoDigitalizacion,
      @Param("fechaModificacion") Date fechaModificacion,
      @Param("usuario") String usuario,
      @Param("idMesa") Long idMesa);

  @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto(a, m) FROM Acta a " +
      "JOIN a.mesa m " +
      "JOIN a.ubigeoEleccion ue " +
      "WHERE a.estadoDigitalizacion IN (:estadosActa) AND a.numeroCopia IS NOT NULL " +
      "AND m.estadoDigitalizacionPr IN (:estado) " +
      "AND ue.eleccion.codigo =:idEleccion " +
      "ORDER BY FUNCTION('RANDOM')")
  List<MesaActaDto> findRandomMesa(@Param("idEleccion") String idEleccion, @Param("estadosActa") List<String> estadosActa, @Param("estado") String estado);

  @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto(a, m) FROM Acta a " +
      "JOIN a.mesa m " +
      "JOIN a.ubigeoEleccion ue " +
      "WHERE (a.estadoActa IN (:estadosActa) OR (a.estadoActa = 'L' AND a.numeroCopia IS NULL)) " +
      "AND m.estadoDigitalizacionPr IN (:estado) " +
      "AND ue.eleccion.codigo =:idEleccion " +
      "ORDER BY FUNCTION('RANDOM')")
  List<MesaActaDto> findRandomMesaExSi(@Param("idEleccion") String idEleccion, @Param("estadosActa") List<String> estadosActa, @Param("estado") String estado);

    @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto(a, m) FROM Acta a " +
            "JOIN a.mesa m " +
            "JOIN a.ubigeoEleccion ue " +
            "WHERE a.estadoActa IN (:estadosActa) " +
            "AND m.estadoDigitalizacionPr IN (:estado) " +
            "AND ue.eleccion.codigo =:idEleccion " +
            "ORDER BY FUNCTION('RANDOM')")
    List<MesaActaDto> findRandomMesaNoInstaladas(@Param("idEleccion") String idEleccion, @Param("estadosActa") List<String> estadosActa, @Param("estado") String estado);


    @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto(a, m) FROM Acta a " +
      "JOIN a.mesa m " +
      "JOIN a.ubigeoEleccion ue " +
      "WHERE a.estadoDigitalizacion IN (:estadosActa) and a.numeroCopia IS NOT NULL " +
      "AND m.estadoDigitalizacionMe IN (:estado) " +
      "AND ue.eleccion.codigo =:idEleccion " +
      "ORDER BY FUNCTION('RANDOM')")
  List<MesaActaDto> findRandomMesaME(@Param("idEleccion") String idEleccion, @Param("estadosActa") List<String> estadosActa, @Param("estado") String estado);

  @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto(a, m) FROM Acta a " +
      "JOIN a.mesa m " +
      "JOIN a.ubigeoEleccion ue " +
      "WHERE (a.estadoActa IN (:estadosActa) OR (a.estadoActa = 'L' AND a.numeroCopia IS NULL))" +
      "AND m.estadoDigitalizacionMe IN (:estado) " +
      "AND ue.eleccion.codigo =:idEleccion " +
      "ORDER BY FUNCTION('RANDOM')")
  List<MesaActaDto> findRandomMesaMeExSi(@Param("idEleccion") String idEleccion, @Param("estadosActa") List<String> estadosActa, @Param("estado") String estado);

    @Query(value = "SELECT new pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto(a, m) FROM Acta a " +
            "JOIN a.mesa m " +
            "JOIN a.ubigeoEleccion ue " +
            "WHERE a.estadoActa IN (:estadosActa) " +
            "AND m.estadoDigitalizacionMe IN (:estado) " +
            "AND ue.eleccion.codigo =:idEleccion " +
            "ORDER BY FUNCTION('RANDOM')")
    List<MesaActaDto> findRandomMesaMeNoInstalada(@Param("idEleccion") String idEleccion, @Param("estadosActa") List<String> estadosActa, @Param("estado") String estado);


    @Modifying
  @Transactional
  @Query("UPDATE Mesa e " +
      "SET e.estadoDigitalizacionMe = :estadoDigitalizacion, e.fechaAsignadoMe = :fechaModificacion, "
      + "e.usuarioAsignadoMe = :usuario  WHERE e.id = :idMesa ")
  int actualizarEstadoME(@Param("estadoDigitalizacion") String estadoDigitalizacion,
      @Param("fechaModificacion") Date fechaModificacion,
      @Param("usuario") String usuario,
      @Param("idMesa") Long idMesa);

  @Query(value = "SELECT * FROM tab_mesa WHERE c_estado_digitalizacion_me = 'E' AND d_aud_fecha_usuario_asignado_me < now() - interval '30 minutes'", nativeQuery = true)
  List<Mesa> findMesasAsignadasMEHaceMasDe15Min();

  @Query(value = "SELECT * FROM tab_mesa WHERE c_estado_digitalizacion_pr = 'E' AND d_aud_fecha_usuario_asignado_pr < now() - interval '30 minutes'", nativeQuery = true)
  List<Mesa> findMesasAsignadasPRHaceMasDe15Min();

  @Modifying
  @Transactional
  @Query("UPDATE Mesa e " +
          "SET e.estadoDigitalizacionMm = :estadoDigitalizacion, e.fechaAsignadoMm =:fechaModificacion, "
          + "e.usuarioAsignadoMm =:usuario  WHERE e.id =:idMesa ")
  int actualizarEstadoMM(@Param("estadoDigitalizacion") String estadoDigitalizacion,
                         @Param("fechaModificacion") Date fechaModificacion,
                         @Param("usuario") String usuario,
                         @Param("idMesa") Long idMesa);
  @Modifying
  @Transactional
  @Query("UPDATE Mesa e " +
          "SET e.estadoDigitalizacionLe = :estadoDigitalizacion, e.fechaAsignadoLe =:fechaModificacion, "
          + "e.usuarioAsignadoLe =:usuario  WHERE e.id =:idMesa ")
  int actualizarEstadoLE(@Param("estadoDigitalizacion") String estadoDigitalizacion,
                         @Param("fechaModificacion") Date fechaModificacion,
                         @Param("usuario") String usuario,
                         @Param("idMesa") Long idMesa);




  /**
   * Métodos para liberar mesa (LE) en control de digitalizacion
   */
  @Query(""" 
        SELECT m.id AS id,
               m.usuarioControlLe AS usuarioControlLe
        FROM Mesa m
        WHERE
          m.estadoDigitalizacionLe in :estadosDigitalizacion AND
          m.usuarioControlLe IS NOT NULL
    """)
  List<MesaProjection> buscarMesaLeControlDigtalTomadas(@Param("estadosDigitalizacion")List<String> estadosDigitalizacion);


  @Transactional
  @Modifying
  @Query("""
       UPDATE Mesa m
       SET
        m.usuarioControlLe  = null,
        m.fechaUsuarioControlLe = null
       WHERE m.id = :idMesa
       """)
  void liberarMesaLeControlDigtalTomadas(@Param("idMesa") Long idMesa);



  /**
   * Métodos para liberar mesa (MM) en control de digitalizacion
   */
  @Query(""" 
        SELECT m.id AS id,
               m.usuarioControlMm AS usuarioControlMm
        FROM Mesa m
        WHERE
          m.estadoDigitalizacionMm in :estadosDigitalizacion AND
          m.usuarioControlMm IS NOT NULL
    """)
  List<MesaProjection> buscarMesaMmControlDigtalTomadas(@Param("estadosDigitalizacion")List<String> estadosDigitalizacion);


  @Transactional
  @Modifying
  @Query("""
       UPDATE Mesa m
       SET
        m.usuarioControlMm  = null,
        m.fechaUsuarioControlMm = null
       WHERE m.id = :idMesa
       """)
  void liberarMesaMmControlDigtalTomadas(@Param("idMesa") Long idMesa);




  /**
   * Métodos para liberar mesa (LE) en verificacion o registro de omisos de LE
   */
  @Query("""
    SELECT m.id AS id,
           m.usuarioAsignadoLe AS usuarioAsignadoLe
    FROM Mesa m
    WHERE
    (
      (
        m.estadoDigitalizacionLe IN (:estadosDigitalizacion)
         AND
          EXISTS (
             SELECT 1 FROM DetLeRectangulo d
             WHERE d.mesaId = m.id
         )
      )
      OR
      m.estadoDigitalizacionLe = :estadoDigtalPerdidaTotal
      OR
      (m.estadoDigitalizacionLe = :estadoPendienteDigital AND m.estadoMesa = :estadoMesa)
    )
    AND m.usuarioAsignadoLe IS NOT NULL
    """)
  List<MesaProjection> buscarMesaLeVerificacionTomadas(
          @Param("estadosDigitalizacion") List<String> estadosDigitalizacion,
          @Param("estadoPendienteDigital") String estadoPendienteDigital,
          @Param("estadoDigtalPerdidaTotal") String estadoDigtalPerdidaTotal,
          @Param("estadoMesa") String estadoMesa);


  @Transactional
  @Modifying
  @Query("""
       UPDATE Mesa m
       SET
        m.usuarioAsignadoLe  = null,
        m.fechaAsignadoLe = null
       WHERE m.id = :idMesa
       """)
  void liberarMesaLeVerificacionTomadas(@Param("idMesa") Long idMesa);



  /**
   * Métodos para liberar mesa (MM) en verificacion o registro de omisos de MM
   */
  @Query("""
    SELECT m.id AS id,
           m.usuarioAsignadoMm AS usuarioAsignadoMm
    FROM Mesa m
    WHERE
        (
          (m.estadoDigitalizacionMm IN (:estadosDigitalizacion)
           AND EXISTS (
               SELECT 1 FROM DetMmRectangulo d
               WHERE d.mesaId = m.id
           ))
          OR
          m.estadoDigitalizacionMm = :estadoDigtalPerdidaTotal
          OR
          (m.estadoDigitalizacionMm = :estadoPendienteDigital
           AND m.estadoMesa = :estadoMesa)
        )
        AND m.usuarioAsignadoMm IS NOT NULL
    """)
  List<MesaProjection> buscarMesaMmVerificacionTomadas(
          @Param("estadosDigitalizacion") List<String> estadosDigitalizacion,
          @Param("estadoPendienteDigital") String estadoPendienteDigital,
          @Param("estadoDigtalPerdidaTotal") String estadoDigtalPerdidaTotal,
          @Param("estadoMesa") String estadoMesa);


  @Transactional
  @Modifying
  @Query("""
       UPDATE Mesa m
       SET
        m.usuarioAsignadoMm  = null,
        m.fechaAsignadoMm = null
       WHERE m.id = :idMesa
       """)
  void liberarMesaMmVerificacionTomadas(@Param("idMesa") Long idMesa);


  @Query("SELECT COUNT(p) FROM Mesa p WHERE p.estadoDigitalizacionLe= :estadoDigitalizacionLe AND p.activo = :activo")
  Long contarLePorActivo(@Param("estadoDigitalizacionLe") String estadoDigitalizacionLe, @Param("activo") Integer activo);


  @Query("SELECT COUNT(p) FROM Mesa p WHERE p.estadoDigitalizacionMm= :estadoDigitalizacionMm AND p.activo = :activo")
  Long contarMmPorActivo(@Param("estadoDigitalizacionMm") String estadoDigitalizacionMm, @Param("activo") Integer activo);


  @Query("SELECT COUNT(p) FROM Mesa p WHERE p.estadoDigitalizacionMe= :estadoDigitalizacionMe AND p.activo = :activo")
  Long contarMePorActivo(@Param("estadoDigitalizacionMe") String estadoDigitalizacionMe, @Param("activo") Integer activo);


  @Query("SELECT COUNT(p) FROM Mesa p WHERE p.estadoDigitalizacionPr= :estadoDigitalizacionPr AND p.activo = :activo")
  Long contarPrPorActivo(@Param("estadoDigitalizacionPr") String estadoDigitalizacionPr, @Param("activo") Integer activo);

  /**
   * Obtener todas las mesas ordenadas por código ascendente
   */
  List<Mesa> findAllByOrderByCodigoAsc();

    @Modifying
    @Transactional
    @Query("UPDATE Mesa e " +
            "SET e.usuarioAsignadoMm =:usuario, e.fechaAsignadoMm =:fechaModificacion "
            + " WHERE e.id =:idMesa ")
    void actualizarUsuarioAsignadoMM(@Param("fechaModificacion") Date fechaModificacion,
            @Param("usuario") String usuario,
            @Param("idMesa") Long idMesa);
    @Modifying
    @Transactional
    @Query("UPDATE Mesa e " +
            "SET e.usuarioAsignadoLe =:usuario, e.fechaAsignadoLe =:fechaModificacion "
            + "  WHERE e.id =:idMesa ")
    void actualizarUsuarioAsignadoLE(@Param("fechaModificacion") Date fechaModificacion,
            @Param("usuario") String usuario,
            @Param("idMesa") Long idMesa);

    @Query(value = """
        SELECT n_mesa_pk
        FROM tab_mesa
        WHERE c_estado_digitalizacion_mm = :estado
        AND c_usuario_asignado_mm IS NULL
        ORDER BY n_mesa_pk
        FOR UPDATE SKIP LOCKED
        LIMIT 1
    """, nativeQuery = true)
    Long obtenerMesaForUpdate(@Param("estado") String estado);


}
