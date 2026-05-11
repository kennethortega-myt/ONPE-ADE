package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.entities.TipoEleccion;

import java.util.List;

public interface TipoEleccionRepository extends JpaRepository<TipoEleccion, Integer>, MigracionRepository<TipoEleccion, String> {

    @Modifying
    @Query("update TipoEleccion tip set tip.activo = :status where tip.id = :id")
    void updateEstado(@Param("status") Integer status, @Param("id") Integer id);

    @Modifying
    @Query("update TipoEleccion tip set tip.nombre = :nombre, tip.usuarioModificacion = :usuario, tip.fechaModificacion = CURRENT_TIMESTAMP where tip.id = :id")
    void updateObject(@Param("nombre") String nombre, @Param("usuario") String usuario, @Param("id") Integer id);
    
    List<TipoEleccion> findByActivoOrderByOrdenAsc(Integer activo);
    
    @Query("SELECT te FROM TipoEleccion te "
    		+ "JOIN te.detallesTipoEleccionDocumentalHistorial dtedh "
    		+ "JOIN dtedh.configuracionProcesoElectoral cpe WHERE cpe.acronimo = ?1")
	List<TipoEleccion> findByCc(String codigo);

    List<TipoEleccion> findByIdPadreOrderByOrdenAsc(Integer idPadre);
}
