package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface DetUbigeoEleccionAgrupacionPoliticaRepository extends JpaRepository<DetUbigeoEleccionAgrupacionPolitica, Long>, MigracionRepository<DetUbigeoEleccionAgrupacionPolitica, String>  {

	@Query("SELECT distinct d FROM DetUbigeoEleccionAgrupacionPolitica d "
			+ "JOIN d.ubigeoEleccion ue "
			+ "JOIN ue.ubigeo u "
			+ "JOIN u.centroComputo c WHERE c.codigo = ?1")
	public List<DetUbigeoEleccionAgrupacionPolitica> findByCc(String codigo);
	
}
