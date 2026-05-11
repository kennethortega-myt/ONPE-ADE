package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.orc.entities.TabResolucion;

import java.util.List;
import java.util.Optional;

public interface TabResolucionRepository extends JpaRepository<TabResolucion, Long>{

    List<TabResolucion> findByNumeroResolucion(String numeroResolucion);
    Optional<TabResolucion> findByIdCc(String idCc);

    @Query("""
    SELECT DISTINCT tr
    FROM DetActaResolucion dar
    JOIN dar.acta a
    JOIN dar.resolucion tr
    WHERE a.id = :idActa AND dar.activo = 1 AND tr.activo = 1
""")
    Optional<TabResolucion> findResolucionByActaId(@Param("idActa") Long actaId);

    @Override
    Optional<TabResolucion> findById(Long idResol);
}
