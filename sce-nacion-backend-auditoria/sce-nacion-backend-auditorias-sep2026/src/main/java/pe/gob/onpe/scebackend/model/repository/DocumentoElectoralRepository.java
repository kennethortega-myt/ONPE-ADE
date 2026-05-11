package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.onpe.scebackend.model.entities.DocumentoElectoral;

import java.util.List;

public interface DocumentoElectoralRepository extends JpaRepository<DocumentoElectoral, Integer>, MigracionRepository<DocumentoElectoral, String> {
    @Modifying
    @Query("update DocumentoElectoral doc set doc.activo = :status where doc.id = :id")
    void updateEstado(@Param("status") Integer status, @Param("id") Integer id);
        
    List<DocumentoElectoral> findByActivo(Integer activo);
    
    @Query("SELECT de FROM DocumentoElectoral de "
    		+ "JOIN de.detallesTipoEleccionDocumentalHistorial dtedh "
    		+ "JOIN dtedh.configuracionProcesoElectoral cpe "
    		+ "WHERE cpe.acronimo = ?1")
	List<DocumentoElectoral> findByCc(String codigo);


    @Modifying
    @Query("update DocumentoElectoral doc set doc.nombre = :nombre, doc.abreviatura = :abreviatura, doc.tamanioHoja = :tamanio, doc.multipagina = :multipagina, doc.codigoBarraOrientacion = :orientacion where doc.id = :id")
    void updateDatos(@Param("id") Integer id, @Param("nombre") String nombre, @Param("abreviatura") String abreviatura,
                            @Param("tamanio") Integer tamanio, @Param("multipagina") Integer multipagina, @Param("orientacion") Integer orientacion);

    @Modifying
    @Query("update DocumentoElectoral doc set doc.abreviatura = :abreviatura, doc.tamanioHoja = :tamanio, doc.multipagina = :multipagina, doc.codigoBarraOrientacion = :orientacion where doc.id = :id")
    void updateDatos(@Param("id") Integer id, @Param("abreviatura") String abreviatura,
                            @Param("tamanio") Integer tamanio, @Param("multipagina") Integer multipagina, @Param("orientacion") Integer orientacion);


    List<DocumentoElectoral> findByActivoAndConfiguracionGeneral(Integer activo, Integer configuracionGeneral);
}
