package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.OmisoVotante;

public interface OmisoVotanteRepository extends JpaRepository<OmisoVotante, Long> {

	
	@Query("SELECT o FROM OmisoVotante o "
			+ "JOIN o.mesa m "
			+ "JOIN o.padronElectoral u "
			+ "WHERE m.id = ?1 and u.id= ?2")
	Optional<OmisoVotante> findByIdMesaAndIdPadronElectoral(Long idMesa, Long idPadronElectoral);
	
}
