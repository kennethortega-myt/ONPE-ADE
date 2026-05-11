package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.MiembroMesaSorteado;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

public interface MiembroMesaSorteadoRepository extends JpaRepository<MiembroMesaSorteado, Long>, MigracionRepository<MiembroMesaSorteado, String>{

	@Query("SELECT me FROM MiembroMesaSorteado me "
			+ "JOIN me.mesa m "
			+ "JOIN m.localVotacion l "
			+ "JOIN l.ubigeo u "
			+ "JOIN u.centroComputo c "
			+ "WHERE c.codigo = ?1")
	public List<MiembroMesaSorteado> findByCc(String codigo);


	Long countByEstado(Integer estado);
	
}
