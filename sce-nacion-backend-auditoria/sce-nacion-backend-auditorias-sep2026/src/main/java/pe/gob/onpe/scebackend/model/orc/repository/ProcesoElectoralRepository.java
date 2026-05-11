package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface ProcesoElectoralRepository extends JpaRepository<ProcesoElectoral, Long>, MigracionRepository<ProcesoElectoral, String>{

	@Query("SELECT distinct p FROM ProcesoElectoral p JOIN p.elecciones e JOIN e.ubigeosElecciones ue JOIN ue.ubigeo u JOIN u.centroComputo c WHERE c.codigo = ?1")
	List<ProcesoElectoral> findByCc(String codigo);

	ProcesoElectoral findByActivo(Integer activo);
	Optional<ProcesoElectoral> findByAcronimo(String acronimo);
	
}
