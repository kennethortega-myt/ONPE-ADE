package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.entities.Archivo;

import java.util.List;
import java.util.Optional;

public interface ArchivoRepository extends JpaRepository<Archivo, Long>, MigracionRepository<Archivo, String> {
    
    Archivo findByGuid(String guid);

    Optional<Archivo> findByGuidAndActivo(String guid, Integer activo);
    
    @Query("SELECT distinct ar FROM Archivo ar "
    		+ "JOIN ar.detallesTipoEleccionDocumentalHistorial dtedh "
    		+ "JOIN dtedh.configuracionProcesoElectoral cpe "
    		+ "WHERE cpe.acronimo = ?1")
	List<Archivo> findByCc(String acronimo);
    
    
}
