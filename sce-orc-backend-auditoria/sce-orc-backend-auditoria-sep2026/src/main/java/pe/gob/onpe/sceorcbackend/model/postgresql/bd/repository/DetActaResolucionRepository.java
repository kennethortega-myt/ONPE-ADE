package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;

import java.util.List;
import java.util.Optional;

public interface DetActaResolucionRepository extends JpaRepository<DetActaResolucion, Long> {

    List<DetActaResolucion> findByResolucion(TabResolucion tabResolucion);
    void deleteDetActaResolucionByResolucion(TabResolucion tabResolucion);

    void deleteDetActaResolucionByActa(Acta acta);

    List<DetActaResolucion> findByResolucionAndActa(TabResolucion tabResolucion, Acta acta);
    List<DetActaResolucion> findByActaOrderByFechaCreacionDesc(Acta acta);

    List<DetActaResolucion> findByActa_Id(Long idActa);

    List<DetActaResolucion> findByActa_IdOrderByCorrelativoDesc(Long idActa);

    Optional<DetActaResolucion> findFirstByActa_IdAndActivoAndResolucion_TipoResolucionInOrderByIdDesc(
            Long idActa, Integer activo, List<Integer> tiposResolucion);
    
    @Modifying
    @Query("DELETE FROM DetActaResolucion")
    void deleteAllInBatch();
    
    
}
