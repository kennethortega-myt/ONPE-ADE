package pe.gob.onpe.scebackend.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.entities.DetalleTipoEleccionDocumentoElectoralHistorial;

public interface DetalleTipoEleccionDocumentoElectoralHistorialRepository extends JpaRepository<DetalleTipoEleccionDocumentoElectoralHistorial, Integer>, MigracionRepository<DetalleTipoEleccionDocumentoElectoralHistorial, String>{

	@Query("SELECT h FROM DetalleTipoEleccionDocumentoElectoralHistorial h "
			+ "JOIN h.configuracionProcesoElectoral c "
			+ "WHERE c.acronimo = ?1 and h.activo=1")
	List<DetalleTipoEleccionDocumentoElectoralHistorial> findByCc(String codigo);

	List<DetalleTipoEleccionDocumentoElectoralHistorial> findByConfiguracionProcesoElectoralId(Integer id);
	
}
