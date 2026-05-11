package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MesaDocumento;
import java.util.List;
import java.util.Optional;

public interface MesaDocumentoService extends CrudService<MesaDocumento> {


    Optional<MesaDocumento> findByMesaAndAdmDocumentoElectoralAndTipoArchivoAndPagina(Long idMesa, Integer idDocumentoElectoral, String tipoArchivo, Integer pagina);
    void deleteAllInBatch();

    void deleteListMesaDocumento(List<MesaDocumento> mesaDocumentoList);

    void deleteByAdmDocumentoElectoralIdAndMesaId(Integer admDocumentoElectoralId, Long mesaId);

    long count();

    List<MesaDocumento> buscarIdMesaAndIdDocumentoElectoral(Long idMesa, Integer idDocumentoElectoral);
}
