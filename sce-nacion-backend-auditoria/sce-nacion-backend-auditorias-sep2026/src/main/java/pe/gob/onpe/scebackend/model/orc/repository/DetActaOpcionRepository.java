package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.DetActaOpcion;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;


public interface DetActaOpcionRepository extends JpaRepository<DetActaOpcion, Integer>, MigracionRepository<DetActaOpcion, String> {

	@Query("SELECT dao FROM DetActaOpcion dao "
			+ "JOIN dao.detActa da "
			+ "JOIN da.acta a "
			+ "JOIN a.mesa m "
			+ "JOIN m.localVotacion l "
			+ "JOIN l.ubigeo u "
			+ "JOIN u.centroComputo c WHERE c.codigo = ?1")
	List<DetActaOpcion> findByCc(String codigo);
	
	Optional<DetActaOpcion> findByIdDetActaOpcionCc(String idDetActaOpcionCc);
}
