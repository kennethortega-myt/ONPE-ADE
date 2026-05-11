package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.entities.DetalleConfiguracionDocumentoElectoral;


import java.util.List;

public interface DetalleConfiguracionDocumentoElectoralRepository extends JpaRepository<DetalleConfiguracionDocumentoElectoral, Integer>, MigracionRepository<DetalleConfiguracionDocumentoElectoral, String> {
    List<DetalleConfiguracionDocumentoElectoral> findByActivoAndDetalleTipoEleccionDocumentoElectoralId(Integer activo, Integer id);

    List<DetalleConfiguracionDocumentoElectoral> findByActivoAndHabilitadoAndDetalleTipoEleccionDocumentoElectoralId(Integer activo,Integer habilitado, Integer id);
    
    @Query("SELECT dce FROM DetalleConfiguracionDocumentoElectoral dce")
    List<DetalleConfiguracionDocumentoElectoral> findByCc(String acronimo);
    
}
