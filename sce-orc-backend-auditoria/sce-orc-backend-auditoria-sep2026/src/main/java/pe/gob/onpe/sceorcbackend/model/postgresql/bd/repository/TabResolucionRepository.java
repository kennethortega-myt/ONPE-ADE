package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion.TabResolucionProjection;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ResolucionProjection;

import java.util.Date;
import java.util.List;

public interface TabResolucionRepository extends JpaRepository<TabResolucion, Long> {

    List<TabResolucion> findByNumeroResolucionAndActivo(String numeroResolucion, Integer activo);

    List<TabResolucion> findByNumeroResolucionAndActivoAndEstadoResolucionNot(String numeroResolucion, Integer activo, String estadoResolucion);

    List<TabResolucion> findByEstadoResolucionInAndTipoResolucionInAndAudUsuarioAsignadoAndActivo(List<String> estadosResolucion, List<Integer> tipoResolucion, String usuarioAsignado, Integer activo);

    /**
     * Obtener una resolución random para verificación con bloqueo pesimista
     * Usa FOR UPDATE SKIP LOCKED para evitar colisiones entre usuarios concurrentes
     */
    @Query(value = """
        SELECT r.*
        FROM tab_resolucion r
        WHERE r.c_estado_resolucion IN :estadosResolucion
          AND r.n_tipo_resolucion IN :tipoResolucion
          AND r.c_aud_usuario_asignado IS NULL
          AND r.n_activo = :activo
        ORDER BY RANDOM()
        LIMIT 1
        FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    TabResolucion findRandomResolucionParaVerificacion(
            @Param("estadosResolucion") List<String> estadosResolucion,
            @Param("tipoResolucion") List<Integer> tipoResolucion,
            @Param("activo") Integer activo
    );

    @Query("SELECT r.id AS id, r.estadoResolucion AS estadoResolucion " +
            "FROM TabResolucion r WHERE r.estadoResolucion = :estadoResolucion and r.activo = 1")
    List<TabResolucionProjection> findByEstadoResolucionV2(@Param("estadoResolucion") String estadoResolucion, Integer activo);

    List<TabResolucion> findByEstadoDigitalizacionAndUsuarioControlAndActivo(String estadosDigtal, String usuarioControl, Integer activo);


    List<TabResolucion> findByActivoOrderByAudFechaModificacionDesc(Integer activo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<TabResolucion> findByEstadoDigitalizacionAndUsuarioControlIsNullAndActivo(String estadosDigtal, Integer activo);


    @Query("""
        SELECT
            SUM(CASE WHEN r.estadoDigitalizacion IN :estadosDig AND r.activo = :activo THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.estadoResolucion = :registrada AND r.estadoDigitalizacion IN :estadosDig AND r.activo = :activo THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.estadoResolucion = :asociadas AND r.activo = :activo THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.estadoResolucion = :aplicada AND r.activo = :activo THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.estadoResolucion = :estadoAnulado AND r.activo = :activo THEN 1 ELSE 0 END)
        FROM TabResolucion r
    """)
    List<Object[]> resumenResoluciones (
            @Param("aplicada") String aplicada,
            @Param("registrada") String registrada,
            @Param("asociadas") String asociadas,
            @Param("estadosDig") List<String> estadosDigitalizacion,
            @Param("estadoAnulado") String estadoAnulado,
            @Param("activo") Integer activo
    );

    @Query("""
    SELECT r
    FROM TabResolucion r
    WHERE r.estadoDigitalizacion IN :estadosDigital
      AND r.activo = :activo
      AND (:numeroResolucion IS NULL OR r.numeroResolucion LIKE %:numeroResolucion%)
    """)
    List<TabResolucion> findByEstadosDigitalAndActivoAndNumeroResolucionLike(
            @Param("estadosDigital") List<String> estadosDigital,
            @Param("activo") Integer activo,
            @Param("numeroResolucion") String numeroResolucion
    );


    @Modifying
    @Query("DELETE FROM TabResolucion")
    void deleteAllInBatch();


    /**
     * metodos para liberar resoluciones en control de digitalización
     */
    @Query("""
        SELECT r.id AS id ,
               r.usuarioControl AS usuarioControl
        FROM TabResolucion r
        WHERE r.estadoDigitalizacion = :estadoDigitalizacion
          AND r.usuarioControl IS NOT NULL
    """)
    List<ResolucionProjection> buscarResolucionesControlDigitalizacionTomadas(@Param("estadoDigitalizacion") String estadoDigitalizacion);


    @Transactional
    @Modifying
    @Query("""
       UPDATE TabResolucion r
       SET
         r.usuarioControl  = null ,
         r.fechaUsuarioControl = null
       WHERE r.id= :idResolucion
       """)
    void liberarResolucionesControlDigitlaizacion(@Param("idResolucion") Long idResolucion);


    /**
     * metodos para liberar resoluciones en verificacion de resoluciones
     */
    @Query("""
        SELECT r.id AS id ,
               r.audUsuarioAsignado AS audUsuarioAsignado
        FROM TabResolucion r
        WHERE r.estadoResolucion in :estadosResolucionPermitidos
          AND r.tipoResolucion in :tipoPermitidos
          AND r.audUsuarioAsignado IS NOT NULL
          AND r.activo = 1
    """)
    List<ResolucionProjection> buscarResolucionesVerificacionTomadas(@Param("estadosResolucionPermitidos") List<String> estadosResolucionPermitidos,
                                                                     @Param("tipoPermitidos") List<Integer> tipoPermitidos);


    @Transactional
    @Modifying
    @Query("""
       UPDATE TabResolucion r
       SET
       r.audUsuarioAsignado  = null,
       r.audFechaAsignado = null,
       r.asignado = 0
       WHERE r.id= :idResolucion
       """)
    void liberarResolucionesVerificacion(@Param("idResolucion") Long idResolucion);


    /**
     * Bloquear resolución con FOR UPDATE SKIP LOCKED para evitar concurrencia
     * Retorna el ID si está disponible, null si está bloqueada por otro usuario
     */
    @Query(value = """
        SELECT r.n_resolucion_pk
        FROM tab_resolucion r
        WHERE r.n_resolucion_pk = :idResolucion
          AND r.c_usuario_asociacion IS NULL
          AND r.n_activo = 1
        FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    Long bloquearResolucionParaAsociacion(@Param("idResolucion") Long idResolucion);


    /**
     * Asignar usuario de asociación a la resolución ya bloqueada
     * Debe ejecutarse después de bloquearResolucionParaAsociacion en la misma transacción
     */
    @Transactional
    @Modifying
    @Query("""
       UPDATE TabResolucion r
       SET
       r.usuarioAsociacion = :usuarioAsociacion,
       r.fechaUsuarioAsociacion = :fechaAsociacion
       WHERE r.id = :idResolucion
       """)
    int asignarUsuarioAsociacion(
            @Param("idResolucion") Long idResolucion,
            @Param("usuarioAsociacion") String usuarioAsociacion,
            @Param("fechaAsociacion") Date fechaAsociacion
    );

    /**
     * Desbloquear resolución eliminando el usuario de asociación
     */
    @Transactional
    @Modifying
    @Query("""
       UPDATE TabResolucion r
       SET
       r.usuarioAsociacion = null,
       r.fechaUsuarioAsociacion = null
       WHERE r.id = :idResolucion
       """)
    int desbloquearResolucion(@Param("idResolucion") Long idResolucion);


}
