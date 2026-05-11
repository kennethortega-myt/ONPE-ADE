package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.DetActaAccion;

public interface DetActaAccionRepository extends JpaRepository<DetActaAccion, Long> {

	Optional<DetActaAccion> findByIdCc(String idCc);
	
}
