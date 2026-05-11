package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.JuradoElectoralEspecial;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface JuradoElectoralEspecialRepository extends JpaRepository<JuradoElectoralEspecial, Integer>, MigracionRepository<JuradoElectoralEspecial, String>  {

	@Query("SELECT j FROM JuradoElectoralEspecial j " + 
			"WHERE j.codigoCentroComputo = ?1")
	List<JuradoElectoralEspecial> findByCc(String codigo);
	
}
