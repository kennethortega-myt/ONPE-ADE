package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.orc.entities.AgrupacionPolitica;
import pe.gob.onpe.scebackend.model.orc.entities.DetActa;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface DetActaRepository  extends JpaRepository<DetActa, Long>, MigracionRepository<DetActa, String> {

	@Query("SELECT da FROM DetActa da "
			+ "JOIN da.acta a "
			+ "JOIN a.mesa m "
			+ "JOIN m.localVotacion l "
			+ "JOIN l.ubigeo u "
			+ "JOIN u.centroComputo c WHERE c.codigo = ?1")
	List<DetActa> findByCc(String codigo);

	List<DetActa> findByActaAndAgrupacionPolitica(Acta acta, AgrupacionPolitica agrupacionPolitica);
	
	@Query("SELECT da FROM DetActa da WHERE da.acta.id = ?1 and da.agrupacionPolitica.id=?2")
	Optional<DetActa> findByIdActaAndIdAgrupacionPolitica(Long idActa, Long idAgrupacionPolitica);
	
}
