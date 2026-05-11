package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.OmisoMiembroMesa;

public interface OmisoMiembroMesaRepository extends JpaRepository<OmisoMiembroMesa, Long> {

	@Query("SELECT omm FROM OmisoMiembroMesa omm "
			+ "JOIN omm.mesa m "
			+ "JOIN omm.miembroMesaSorteado mms "
			+ "WHERE m.id = ?1 and mms.id= ?2")
	Optional<OmisoMiembroMesa> findByIdMesaAndIdMiembroMesaSorteado(Long idMesa, Long idMiembroMesaSorteado);
	
	
	@Query("SELECT omm FROM OmisoMiembroMesa omm "
			+ "WHERE omm.idCc= ?1 ")
	Optional<OmisoMiembroMesa> findByIdCc(String idCc);
	
}
