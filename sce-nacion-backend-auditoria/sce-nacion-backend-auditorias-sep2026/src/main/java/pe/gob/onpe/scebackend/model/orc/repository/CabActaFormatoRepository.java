package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.CabActaFormato;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface CabActaFormatoRepository extends JpaRepository<CabActaFormato, Long>, MigracionRepository<CabActaFormato, String> {
	
	@Query("SELECT DISTINCT caf FROM CabActaFormato caf " + 
			"JOIN caf.detActasFormatos daf "+
			"JOIN daf.acta a "+
			"JOIN a.mesa m " + 
			"JOIN m.localVotacion l "+ 
			"JOIN l.ubigeo u "+
			"JOIN u.centroComputo c WHERE c.codigo = ?1")
	List<CabActaFormato> findByCc(String codigo);
	
	Optional<CabActaFormato> findByIdCc(String idCc);

}
