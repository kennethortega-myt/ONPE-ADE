package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OpcionVoto;



public interface OpcionVotoRepository extends JpaRepository<OpcionVoto, Integer> {

	@Modifying
    @Query("DELETE FROM OpcionVoto")
    void deleteAllInBatch();
	
}
