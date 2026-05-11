package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidato;

public interface ImportCandidatoRepository extends JpaRepository<ImportCandidato, Integer> {


}
