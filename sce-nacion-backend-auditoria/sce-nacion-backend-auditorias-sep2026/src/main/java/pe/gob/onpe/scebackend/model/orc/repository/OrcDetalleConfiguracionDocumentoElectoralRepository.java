package pe.gob.onpe.scebackend.model.orc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleConfiguracionDocumentoElectoral;

public interface OrcDetalleConfiguracionDocumentoElectoralRepository 
		extends JpaRepository<OrcDetalleConfiguracionDocumentoElectoral, Integer> {

	@Query("SELECT dcde FROM OrcDetalleConfiguracionDocumentoElectoral dcde " + 
			" JOIN dcde.detalleTipoEleccionDocumentoElectoral dtede " +
			" WHERE dtede.activo=1 and dcde.activo=1 ")
	public List<OrcDetalleConfiguracionDocumentoElectoral> importar();
	
	
	@Query("SELECT distinct dcde FROM OrcDetalleConfiguracionDocumentoElectoral dcde "
			+ " JOIN dcde.detalleTipoEleccionDocumentoElectoral dtede "
			+ " JOIN dtede.procesoElectoral p "
			+ " JOIN p.elecciones e " 
			+ " JOIN e.ubigeosElecciones ue " 
			+ " JOIN ue.ubigeo u " 
			+ " JOIN u.centroComputo c WHERE c.codigo = ?1")
	public List<OrcDetalleConfiguracionDocumentoElectoral> findByCc(String codigo);
	
}
