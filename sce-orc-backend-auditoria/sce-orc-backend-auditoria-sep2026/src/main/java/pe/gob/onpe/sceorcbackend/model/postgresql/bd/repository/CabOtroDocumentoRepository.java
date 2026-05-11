package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabOtroDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DenunciaProjection;

import java.util.List;

public interface CabOtroDocumentoRepository extends JpaRepository<CabOtroDocumento, Long> {

    List<CabOtroDocumento> findAllByOrderByAudFechaModificacionDesc();  //

    List<CabOtroDocumento> findByNumeroDocumento(String numeroDocumento);

    @Modifying
    @Query("DELETE FROM CabOtroDocumento")
    void deleteAllInBatch();

    List<CabOtroDocumento> findByEstadoDigitalizacionAndCodTipoDocumentoAndUsuarioControlAndActivo(String estadoDigitalizacion, String abrevTipoDocumento, String usuario, Integer activo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<CabOtroDocumento> findByEstadoDigitalizacionAndCodTipoDocumentoAndUsuarioControlIsNullAndActivo(String digitalizada, String abrevTipoDocumento, Integer activo);

    List<CabOtroDocumento> findByEstadoDigitalizacionAndActivo(String estadoDigitalizacion, Integer activo);

    long countByEstadoDigitalizacionAndActivo(String estadoDigitalizacion, Integer activo);

    @Query("""
          SELECT c FROM CabOtroDocumento c
          WHERE (:numeroDocumento IS NULL OR c.numeroDocumento LIKE %:numeroDocumento%)
            AND c.estadoDigitalizacion = :estadoDigitalizacion
            AND (:estadoDocumento IS NULL OR c.estadoDocumento = :estadoDocumento)
            AND c.activo = :activo ORDER BY c.audFechaCreacion desc
        """)
    List<CabOtroDocumento> buscar(
            @Param("numeroDocumento") String numeroDocumento,
            @Param("estadoDocumento") String estadoDocumento,
            @Param("estadoDigitalizacion") String estadoDigitalizacion,
            @Param("activo") Integer activo);

    List<CabOtroDocumento> findByEstadoDocumentoAndEstadoDigitalizacionAndActivo(String registradaSinProcesar, String digtalPrimerCcAceptada, Integer activo);

    long countByEstadoDocumentoAndEstadoDigitalizacionAndActivo(
            String estadoDocumento,
            String estadoDigitalizacion,
            Integer activo
    );


    /**
     * metodos para liberar denuncias en control de digitalización
     */
    @Query("""
        SELECT r.id AS id ,
        r.numeroDocumento AS numeroDocumento,
        r.usuarioControl AS usuarioControl
        FROM CabOtroDocumento r
        WHERE r.estadoDigitalizacion = :estadoDigitalizacion
          AND r.codTipoDocumento = :codTipoDocumento
          AND r.usuarioControl IS NOT NULL
    """)
    List<DenunciaProjection> buscarDenunciasControlDigitalizacionTomadas(@Param("estadoDigitalizacion")String estadoDigitalizacion,
                                                                           @Param("codTipoDocumento")String codTipoDocumento);


    @Transactional
    @Modifying
    @Query("""
       UPDATE CabOtroDocumento r
       SET
         r.usuarioControl  = null ,
         r.fechaUsuarioControl = null
       WHERE r.id= :id
       """)
    void liberarDenunciasControlDigitlaizacion(@Param("id") Long id);

}
