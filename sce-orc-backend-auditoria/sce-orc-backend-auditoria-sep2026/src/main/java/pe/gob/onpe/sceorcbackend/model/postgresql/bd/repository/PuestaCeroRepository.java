package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PuestaCero;

public interface PuestaCeroRepository extends JpaRepository<PuestaCero, Long> {

	@Modifying
	@Query("DELETE FROM PuestaCero")
	void deleteAllInBatch();


	@Modifying
	@Transactional
	@Query("UPDATE PuestaCero p SET p.activo = 0 WHERE p.activo <> 0")
	int desactivarPuestaCeroMasivo();

	
}
