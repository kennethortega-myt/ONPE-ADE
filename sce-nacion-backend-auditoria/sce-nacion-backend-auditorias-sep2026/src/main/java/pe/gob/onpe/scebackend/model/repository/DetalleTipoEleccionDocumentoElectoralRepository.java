package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.entities.DetalleTipoEleccionDocumentoElectoral;

import java.util.List;

public interface DetalleTipoEleccionDocumentoElectoralRepository extends JpaRepository<DetalleTipoEleccionDocumentoElectoral, Integer>, MigracionRepository<DetalleTipoEleccionDocumentoElectoral, String>  {

    List<DetalleTipoEleccionDocumentoElectoral> findByActivoAndTipoEleccionId(Integer activo, Integer id);
    List<DetalleTipoEleccionDocumentoElectoral> findByActivoAndTipoEleccionIsNull(Integer activo);
    List<DetalleTipoEleccionDocumentoElectoral> findByTipoEleccionId( Integer id);
    List<DetalleTipoEleccionDocumentoElectoral> findByTipoEleccionIsNull();
    List<DetalleTipoEleccionDocumentoElectoral> findByActivoAndRequeridoAndTipoEleccionId(Integer activo,Integer requerido, Integer id);

    List<DetalleTipoEleccionDocumentoElectoral> findByActivoAndRequeridoAndTipoEleccionIsNull(Integer activo,Integer requerido);


    @Modifying
    @Query("update DetalleTipoEleccionDocumentoElectoral det set det.archivo.id = :archivo where det.id = :id")
    void updateArchivo(@Param("archivo") Long idArchivo, @Param("id") Integer id);
    
    @Query("SELECT dtde FROM DetalleTipoEleccionDocumentoElectoral dtde")
    List<DetalleTipoEleccionDocumentoElectoral> findByCc(String acronimo);

    @Modifying
    @Query("update DetalleTipoEleccionDocumentoElectoral dtde set dtde.rangoInicial = :rangoInicial, dtde.rangoFinal = :rangoFinal, dtde.digitoChequeo = :digitoChequeo, dtde.digitoError = :digitoError where dtde.tipoEleccion.id = :idTipo and dtde.documentoElectoral.id = :idDocu")
    void updateDatos(@Param("idTipo") Integer idTipo,@Param("idDocu") Integer idDocu, @Param("rangoInicial") String rangoInicial,
                            @Param("rangoFinal") String rangoFinal, @Param("digitoChequeo") String digitoChequeo, @Param("digitoError") String digitoError);
}
