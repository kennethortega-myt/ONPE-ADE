package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPersonero;

public interface ImportPersoneroRepository extends JpaRepository<ImportPersonero, Long> {
	
	@Modifying
	@Query("DELETE FROM ImportPersonero")
	void deleteAllInBatch();

}
