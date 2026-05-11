package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DistritoElectoral;

public interface DistritoElectoralRepository extends JpaRepository<DistritoElectoral, Integer> {

	@Modifying
	@Query("DELETE FROM DistritoElectoral")
	void deleteAllInBatch();
	
}
