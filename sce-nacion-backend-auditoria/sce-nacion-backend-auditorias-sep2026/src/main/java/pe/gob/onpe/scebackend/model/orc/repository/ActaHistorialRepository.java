package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.dto.response.trazabilidad.ActaHistorialResumenProjection;
import pe.gob.onpe.scebackend.model.orc.entities.ActaHistorial;

import java.util.List;
import java.util.Optional;

public interface ActaHistorialRepository extends JpaRepository<ActaHistorial, Long> {

    @Query("""
    SELECT
    distinct
    a.id,
    a.estadoDigitalizacion,
    a.estadoActa,
    a.estadoCc,
    a.estadoActaResolucion,
    a.estadoErrorMaterial,
    a.verificador,
    a.verificadorv2,
    a.fechaModificacion
    FROM ActaHistorial a
    WHERE a.acta.id = :idActa
    ORDER BY a.id ASC
""")
    List<ActaHistorialResumenProjection> findHistorialByActa(@Param("idActa") Long idActa);
    
    boolean existsByIdTransmisionAndCentroCc(Long idTransmision, String cc);

}
