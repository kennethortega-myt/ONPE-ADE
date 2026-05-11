package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service;

import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPadronElectoralProgreso;

public interface ImportPadronElectoralProgresoService extends CrudService<ImportPadronElectoralProgreso> {
	
	Optional<ImportPadronElectoralProgreso> findFirstByOrderByIdDesc();

}
