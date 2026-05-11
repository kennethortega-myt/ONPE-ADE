package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.TransmisionControl;


public interface TransmisionControlRepository extends JpaRepository<TransmisionControl, Long>{

	Optional<TransmisionControl> findByCodigoCcAndIdActa(String codigoCc, Long idActa);
	
}
