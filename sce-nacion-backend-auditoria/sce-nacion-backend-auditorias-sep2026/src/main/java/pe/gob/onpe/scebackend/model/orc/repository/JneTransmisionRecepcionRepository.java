package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.orc.entities.JneTransmisionRecepcion;

public interface JneTransmisionRecepcionRepository
        extends JpaRepository<JneTransmisionRecepcion, Long> {

    @Query("""
            SELECT r
            FROM JneTransmisionRecepcion r
            LEFT JOIN FETCH r.archivo
            WHERE r.estado IN :estados
            AND r.activo = :activo
            AND r.intentos < :maxIntentos
            ORDER BY r.audFechaCreacion ASC
            """)
    List<JneTransmisionRecepcion> findTop50Pendientes(@Param("estados") List<Short> estados,
            @Param("activo") Short activo, @Param("maxIntentos") Integer maxIntentos,
            Pageable pageable);

    @Query("""
            SELECT r
            FROM JneTransmisionRecepcion r
            LEFT JOIN FETCH r.archivo
            WHERE r.id = :id
            """)
    Optional<JneTransmisionRecepcion> findByIdWithArchivo(Long id);

    boolean existsByCodigoJneEnvio(String codigoJneEnvio);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
            UPDATE JneTransmisionRecepcion r
            SET r.estado = :estado,
                r.intentos = r.intentos + 1
            WHERE r.id = :id
              AND r.estado IN :estadosValidos
            """)
    int bloquearParaProceso(@Param("id") Long id, @Param("estado") Short estado,
            @Param("estadosValidos") List<Short> estadosValidos);
}
