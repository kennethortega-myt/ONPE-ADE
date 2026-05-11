package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.LocalVotacion;

import java.util.List;

public interface LocalVotacionRepository extends JpaRepository<LocalVotacion, Long> {

	@Modifying
	@Query("DELETE FROM LocalVotacion")
	void deleteAllInBatch();

	List<LocalVotacion> findLocalVotacionByUbigeo_Id(Long idUbigeo);
	
}
