package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.gob.onpe.scebackend.model.orc.entities.CabOtroDocumento;


public interface CabOtroDocumentoRepository extends JpaRepository<CabOtroDocumento, Long> {

	Optional<CabOtroDocumento> findByIdCc(String idCc);
	
}
