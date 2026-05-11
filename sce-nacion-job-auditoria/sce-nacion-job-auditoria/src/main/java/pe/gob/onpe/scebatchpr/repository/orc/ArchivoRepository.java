package pe.gob.onpe.scebatchpr.repository.orc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebatchpr.entities.orc.Archivo;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE Archivo a SET a.estadoTransmision = :estado, a.fechaModificacion = CURRENT_TIMESTAMP  WHERE a.guid = :guid")
	void updateTransmision(@Param("guid") String guid, @Param("estado") Integer estado);
	
}
