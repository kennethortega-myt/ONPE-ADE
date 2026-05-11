package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.UbigeoEleccion;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface UbigeoEleccionRepository extends JpaRepository<UbigeoEleccion, Long>, MigracionRepository<UbigeoEleccion, String> {

	
	@Query("SELECT ue FROM UbigeoEleccion ue "
			+ "JOIN ue.ubigeo u "
			+ "JOIN u.centroComputo c "
			+ "WHERE c.codigo = ?1")
	public List<UbigeoEleccion> findByCc(String codigo);
	
	// para monitoreo
	List<UbigeoEleccion> findByEleccionId(Long idEleccion);
	
}
