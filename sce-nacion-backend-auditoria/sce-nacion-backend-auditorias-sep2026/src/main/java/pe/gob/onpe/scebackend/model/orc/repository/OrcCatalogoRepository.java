package pe.gob.onpe.scebackend.model.orc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.scebackend.model.orc.entities.OrcCatalogo;

import java.util.List;


@Repository
public interface OrcCatalogoRepository extends JpaRepository<OrcCatalogo, Integer> {
    List<OrcCatalogo> findByMaestro(String maestro);
    
}
