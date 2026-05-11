package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.entities.DetalleConfiguracionDocumentoElectoralHistorial;

import java.util.List;

public interface DetalleConfiguracionDocumentoElectoralHistorialRepository extends JpaRepository<DetalleConfiguracionDocumentoElectoralHistorial, Integer>, MigracionRepository<DetalleConfiguracionDocumentoElectoralHistorial, String>{
    
    @Query("SELECT ch FROM DetalleConfiguracionDocumentoElectoralHistorial ch JOIN ch.detalleTipoEleccionDocumentoElectoralHistorial th JOIN th.configuracionProcesoElectoral c WHERE c.acronimo = ?1 and ch.activo=1")
	List<DetalleConfiguracionDocumentoElectoralHistorial> findByCc(String codigo);

}
