package pe.gob.onpe.sceorcbackend.model.postgresql.admin.services;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;

import java.util.List;

public interface DocumentoElectoralService extends CrudService<DocumentoElectoral> {

    DocumentoElectoral findByAbreviatura(String abreviatura);

    List<DocumentoElectoral> buscarDocumentosConfigurados(String abreviaturaProceso);

    List<DocumentoElectoral> findByDocumentoElectoralPadre(DocumentoElectoral documentoElectoralPadre);


    List<DocumentoElectoral> findByDocumentoElectoralPadreIsNull();
}
