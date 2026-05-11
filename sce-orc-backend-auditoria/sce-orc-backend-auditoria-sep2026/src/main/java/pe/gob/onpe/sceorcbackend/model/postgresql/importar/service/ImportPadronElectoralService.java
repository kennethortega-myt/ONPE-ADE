package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPadronElectoral;

public interface ImportPadronElectoralService extends CrudService<ImportPadronElectoral> {

	List<ImportPadronElectoral> findPadronElectoralByCodigoMesaOrderByOrden(String codigoMesa);
	void truncateTable();
	
}
