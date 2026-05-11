package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.DetOtroDocumento;

public interface DetOtroDocumentoRepository extends JpaRepository<DetOtroDocumento, Long> {

	Optional<DetOtroDocumento> findByIdCc(String idCc);
	
}
