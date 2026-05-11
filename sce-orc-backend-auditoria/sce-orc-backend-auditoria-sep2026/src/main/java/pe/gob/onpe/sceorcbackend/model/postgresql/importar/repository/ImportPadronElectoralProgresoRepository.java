package pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPadronElectoralProgreso;

public interface ImportPadronElectoralProgresoRepository extends JpaRepository<ImportPadronElectoralProgreso, Integer> {
	
	Optional<ImportPadronElectoralProgreso> findFirstByOrderByIdDesc();

}
