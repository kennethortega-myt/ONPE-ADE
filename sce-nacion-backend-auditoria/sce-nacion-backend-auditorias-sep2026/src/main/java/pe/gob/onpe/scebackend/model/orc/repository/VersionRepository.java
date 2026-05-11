package pe.gob.onpe.scebackend.model.orc.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.scebackend.model.orc.entities.Version;

public interface VersionRepository extends JpaRepository<Version, Long> {

	
	
}
