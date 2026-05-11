package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaRectangle;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ActaIdSeccionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO;

import java.util.List;

public interface DetActaRectangleRepository extends JpaRepository<DetActaRectangle, Long> {

    List<DetActaRectangle> findByActaIdAndSeccion(Long idActa, Long idSeccion);


    List<DetActaRectangle> findByActaId(Long idActa);


    List<DetActaRectangle> findByActaIdAndSeccionIn(Long idActa, List<Integer> idSeccions);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT new pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.ActaIdSeccionDTO(d.actaId, d.eleccionId, COUNT(*)) " +
            "FROM DetActaRectangle d " +
            "WHERE d.actaId IN :actaIds AND d.seccion in :idSecciones " +
            "GROUP BY d.actaId, d.eleccionId " +
            "HAVING (d.eleccionId IN :eleccionGroup1  AND COUNT(*) >= :cantidadSecciones) " +
            "   OR (d.eleccionId IN :idEleccionesPreferencial  AND COUNT(*) >= :cantidadSeccionesPreferencial)")
    List<ActaIdSeccionDTO> findActaIdAndEleccionIdWithRecordCount(@Param("actaIds") List<Long> actaIds,
                                                                  @Param("idSecciones") List<Integer> idSecciones,
                                                                  @Param("cantidadSecciones") Long cantidadSecciones,
                                                                  @Param("cantidadSeccionesPreferencial") Long cantidadSeccionesPreferencial,
                                                                  @Param("eleccionGroup1") List<Integer> eleccionGroup1,
                                                                  @Param("idEleccionesPreferencial") List<Integer> idEleccionesPreferencial);



    @Query("SELECT new pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO(" +
            "d.id, d.seccion, d.actaId, d.eleccionId, d.archivo, " +
            "d.type, d.valid, d.totalVotos, d.values, s.abreviatura) " +
            "FROM DetActaRectangle d " +
            "LEFT JOIN Seccion s ON d.seccion = s.id " +
            "WHERE d.actaId = :actaId"
    )
    List<DetActaRectangleDTO> findAllWithAbreviatura(Long actaId);



    void deleteDetActaRectangleByActaId(Long idActa);

    @Modifying
    @Query("DELETE FROM DetActaRectangle")
    void deleteAllInBatch();

    @Query("SELECT new pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO(" +
        "d.id, d.seccion, d.actaId, d.eleccionId, d.archivo, " +
        "d.type, d.valid, d.totalVotos, d.values, s.abreviatura) " +
        "FROM DetActaRectangle d " +
        "LEFT JOIN Seccion s ON d.seccion = s.id " +
        "WHERE d.actaId = :actaId and d.seccion = :seccionid"
    )
    List<DetActaRectangleDTO> findByActaAndSeccion(Long actaId, Integer seccionid);

}
