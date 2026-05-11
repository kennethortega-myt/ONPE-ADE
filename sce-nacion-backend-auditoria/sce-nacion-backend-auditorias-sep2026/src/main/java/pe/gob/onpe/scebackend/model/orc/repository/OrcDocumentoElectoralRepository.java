package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.OrcDocumentoElectoral;

public interface OrcDocumentoElectoralRepository extends JpaRepository<OrcDocumentoElectoral, Integer> {

	@Query("SELECT de FROM OrcDocumentoElectoral de " + 
			" WHERE de.activo=1 ")
	public List<OrcDocumentoElectoral> importar();
	
	
	@Query(value =  "SELECT de.* FROM tab_documento_electoral de " + 
			" ORDER BY de.n_documento_electoral_padre NULLS FIRST ", nativeQuery = true)
	public List<OrcDocumentoElectoral> listAllByFirstNull();
	
}
