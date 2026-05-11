package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.DetDistritoElectoralEleccion;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface DetDistritoElectoralEleccionRepository extends JpaRepository<DetDistritoElectoralEleccion, Long>, MigracionRepository<DetDistritoElectoralEleccion, String>{

	@Query("SELECT distinct dee FROM DetDistritoElectoralEleccion dee JOIN dee.eleccion e JOIN e.ubigeosElecciones ue JOIN ue.ubigeo u JOIN u.centroComputo c WHERE c.codigo = ?1")
	public List<DetDistritoElectoralEleccion> findByCc(String codigo);
	
	
}
