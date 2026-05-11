package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaCola;

public interface MiembroMesaColaRepository extends JpaRepository<MiembroMesaCola, Long> {
	
	@Query("SELECT mmc FROM MiembroMesaCola mmc "
			+ "JOIN mmc.mesa m "
			+ "JOIN mmc.padronElectoral p "
			+ "WHERE m.id = ?1 and p.id= ?2")
	Optional<MiembroMesaCola> findByIdMesaAndIdPadronElectoral(Long idMesa, Long idPadronElectoral);

}
