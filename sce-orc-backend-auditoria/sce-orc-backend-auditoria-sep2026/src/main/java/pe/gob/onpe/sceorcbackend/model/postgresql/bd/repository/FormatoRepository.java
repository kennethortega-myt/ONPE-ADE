package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Formato;
import java.util.List;

public interface FormatoRepository extends JpaRepository<Formato, Long> {
    
	List<Formato> findByTipoFormato(Integer nTipoFormato);
	
	@Modifying
	@Query("DELETE FROM Formato")
	void deleteAllInBatch();


	@Modifying
	@Transactional
	@Query("UPDATE Formato e " +
			"SET e.correlativo = 0")
	int reseteaValores();
	
	@Query("SELECT f FROM Formato f " +
			"JOIN f.detCabActaFormato dcaf " +
			"WHERE dcaf.id = :id")
	Formato findByIdCabActaFormato(@Param("id") Long idCabActaFormato);
}
