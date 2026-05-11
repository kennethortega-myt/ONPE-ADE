package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service;

import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetImportadorProgreso;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportImportadorProgreso;

public interface ImportImportadorProgresoService {

	Optional<ImportImportadorProgreso> findTopByOrderByFechaCreacionDesc();
	ImportImportadorProgreso guardar(Integer estado, Double porcentaje, String usuario); 
	ImportImportadorProgreso actualizar(ImportImportadorProgreso importador, Integer estado, Double porcentaje, String usuario); 
	public ImportDetImportadorProgreso guardarDetalle(ImportImportadorProgreso importador, 
														Double porcentaje, 
														String mensaje, 
														String usuario);
}
