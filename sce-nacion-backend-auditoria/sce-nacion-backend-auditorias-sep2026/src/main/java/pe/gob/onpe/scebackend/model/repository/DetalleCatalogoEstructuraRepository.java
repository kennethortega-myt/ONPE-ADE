package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.scebackend.model.entities.DetalleCatalogoEstructura;

import java.util.List;


public interface DetalleCatalogoEstructuraRepository  extends JpaRepository<DetalleCatalogoEstructura, Integer> {

    List<DetalleCatalogoEstructura> findByCatalogoIdIn(List<Integer> ids);
    
    
}
