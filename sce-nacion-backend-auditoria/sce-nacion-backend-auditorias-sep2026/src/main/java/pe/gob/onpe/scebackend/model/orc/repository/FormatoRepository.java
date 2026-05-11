package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.Formato;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface FormatoRepository extends JpaRepository<Formato, Long>, MigracionRepository<Formato, String> {

	@Query("SELECT DISTINCT f FROM Formato f "
			+ "JOIN f.actasFormatos af "
			+ "JOIN af.detActasFormatos daf "
			+ "JOIN daf.acta a "
			+ "JOIN a.mesa m "
			+ "JOIN m.localVotacion l "
			+ "JOIN l.ubigeo u "
			+ "JOIN u.centroComputo c "
			+ "WHERE c.codigo = ?1")
	List<Formato> findByCc(String codigo);
	
	Optional<Formato> findByIdCc(String idCc);
	
}
