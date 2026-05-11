package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;

public interface TabArchivoRepository extends JpaRepository<Archivo, Long>{

	Optional<Archivo> findByGuid(String guid);
	
}
