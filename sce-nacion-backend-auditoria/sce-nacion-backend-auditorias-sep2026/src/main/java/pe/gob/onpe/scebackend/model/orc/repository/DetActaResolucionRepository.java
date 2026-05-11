package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaResolucion;
import pe.gob.onpe.scebackend.model.orc.entities.TabResolucion;

import java.util.List;
import java.util.Optional;

public interface DetActaResolucionRepository extends JpaRepository<DetActaResolucion, Long> {
	
    List<DetActaResolucion> findByActaAndResolucion(Acta acta, TabResolucion resolucion);
    
    Long deleteDetActaResolucionByResolucion(TabResolucion tabResolucion);
    
    @Query("SELECT dar FROM DetActaResolucion dar WHERE dar.acta.id = ?1 and dar.resolucion.idCc=?2")
    Optional<DetActaResolucion> getByActaAndResolucion(
    		Long idActa, 
    		String idCc);
    
}
