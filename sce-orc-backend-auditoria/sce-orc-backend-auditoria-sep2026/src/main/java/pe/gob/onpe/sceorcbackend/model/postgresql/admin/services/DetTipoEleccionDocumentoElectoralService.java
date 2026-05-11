package pe.gob.onpe.sceorcbackend.model.postgresql.admin.services;


import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import java.util.List;

public interface DetTipoEleccionDocumentoElectoralService extends CrudService<DetTipoEleccionDocumentoElectoral> {

    DetTipoEleccionDocumentoElectoral findByRangoInicialLessThanEqualAndRangoFinalGreaterThanEqualAndDocumentoElectoral(String rangoInicial, String rangoFinal, DocumentoElectoral documentoElectoral);

    DetTipoEleccionDocumentoElectoral findByEleccionAndDocumentoElectoral(Eleccion eleccion, DocumentoElectoral documentoElectoral);

    List<DetTipoEleccionDocumentoElectoral> findByDocumentoElectoral(DocumentoElectoral documentoElectoral);
    DetTipoEleccionDocumentoElectoral findAdminDetalleTipoEleccionByRangoAndDocumentoElectoral(Long idProcesoElectoral, String copia, String abrevDocElectoral);


    DetTipoEleccionDocumentoElectoral findByConfiguracionProcesoElectoralAndEleccionAndDocumentoElectoral(Long idProceso, Long idEleccion, String tipoDocumento);


    DetTipoEleccionDocumentoElectoral findByCopia(String copia);

    DetTipoEleccionDocumentoElectoral findAisByCopia(String copia);
}
