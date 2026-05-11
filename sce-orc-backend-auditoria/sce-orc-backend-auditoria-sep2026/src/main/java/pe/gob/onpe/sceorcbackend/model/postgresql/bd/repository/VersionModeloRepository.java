package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.VersionModelo;

public interface VersionModeloRepository extends JpaRepository<VersionModelo, Long> {

	@Modifying
    @Query("DELETE FROM VersionModelo ")
    void deleteAllInBatch();


    VersionModelo findTopByOrderByIdDesc();
	
}
