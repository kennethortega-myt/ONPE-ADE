package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.LocalVotacion;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface LocalVotacionRepository extends JpaRepository<LocalVotacion, Long>, MigracionRepository<LocalVotacion, String> {

	@Query("SELECT l FROM LocalVotacion l "
			+ "JOIN l.ubigeo u "
			+ "JOIN u.centroComputo c "
			+ "WHERE c.codigo = ?1")
	public List<LocalVotacion> findByCc(String codigo);
	
	@Query("SELECT l FROM LocalVotacion l "
			+ "JOIN l.ubigeo u "
			+ "WHERE u.id = ?1")
	public List<LocalVotacion> findByUbigeo(Long id);
	
}
