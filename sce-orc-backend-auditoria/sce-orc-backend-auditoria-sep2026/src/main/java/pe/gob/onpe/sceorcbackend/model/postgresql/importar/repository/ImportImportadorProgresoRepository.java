package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportImportadorProgreso;

public interface ImportImportadorProgresoRepository extends JpaRepository<ImportImportadorProgreso, Long> {

	@Query("SELECT e FROM ImportImportadorProgreso e ORDER BY e.fechaCreacion DESC")
	Optional<ImportImportadorProgreso> findTopByOrderByFechaCreacionDesc();
	
}
