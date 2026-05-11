package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportVersion;

public interface ImportVersionRepository extends JpaRepository<ImportVersion, Long> {

	
	
}
