package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleTipoEleccionDocumentoElectoral;

public interface OrcDetalleTipoEleccionDocumentoElectoralRepository extends JpaRepository<OrcDetalleTipoEleccionDocumentoElectoral, Integer> {

	@Query("SELECT dtede FROM OrcDetalleTipoEleccionDocumentoElectoral dtede " + 
			" WHERE dtede.activo=1 ")
	public List<OrcDetalleTipoEleccionDocumentoElectoral> importar();
	
	
	@Query("SELECT distinct dtede FROM OrcDetalleTipoEleccionDocumentoElectoral dtede "
			+ " JOIN dtede.procesoElectoral p "
			+ " JOIN p.elecciones e " 
			+ " JOIN e.ubigeosElecciones ue " 
			+ " JOIN ue.ubigeo u " 
			+ " JOIN u.centroComputo c WHERE c.codigo = ?1")
	public List<OrcDetalleTipoEleccionDocumentoElectoral> findByCc(String codigo);

	 @Query("""
            SELECT ad
            FROM OrcDetalleTipoEleccionDocumentoElectoral ad
            JOIN ad.documentoElectoral de
            LEFT JOIN FETCH ad.eleccion e
            WHERE de.abreviatura IN :abreviaturas
              AND :copia BETWEEN ad.rangoInicial AND ad.rangoFinal
        """)
    List<OrcDetalleTipoEleccionDocumentoElectoral> findByAbreviaturasAndCopia(
        @Param("abreviaturas") List<String> abreviaturas,
        @Param("copia") String copia
    );
	
}
