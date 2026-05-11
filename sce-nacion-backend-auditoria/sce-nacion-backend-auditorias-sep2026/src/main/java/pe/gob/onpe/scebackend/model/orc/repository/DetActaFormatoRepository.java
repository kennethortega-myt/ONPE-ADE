package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.DetActaFormato;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface DetActaFormatoRepository extends JpaRepository<DetActaFormato, Long>, MigracionRepository<DetActaFormato, String> {
	
	@Query("SELECT DISTINCT daf FROM DetActaFormato daf " + 
			"JOIN daf.acta a "+
			"JOIN a.mesa m " + 
			"JOIN m.localVotacion l "+ 
			"JOIN l.ubigeo u "+
			"JOIN u.centroComputo c WHERE c.codigo = ?1")
	List<DetActaFormato> findByCc(String codigo);

	Optional<DetActaFormato> findByIdCc(String idCc);
}
