package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcCatalogo;

import java.util.List;

public interface CatalogoRepository extends JpaRepository<OrcCatalogo, Integer> {
    List<OrcCatalogo> findByMaestro(String maestro);

}
