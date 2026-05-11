package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MesaDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaDocumentoService;

import java.util.List;
import java.util.Optional;

@Service
public class MesaDocumentoServiceImpl implements MesaDocumentoService {

    MesaDocumentoRepository mesaDocumentoRepository;

    public MesaDocumentoServiceImpl(MesaDocumentoRepository mesaDocumentoRepository){
        this.mesaDocumentoRepository = mesaDocumentoRepository;
    }

    @Override
    public void save(MesaDocumento mesaDocumento) {
        this.mesaDocumentoRepository.save(mesaDocumento);
    }

    @Override
    public void saveAll(List<MesaDocumento> k) {
        this.mesaDocumentoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.mesaDocumentoRepository.deleteAll();
    }

    @Override
    public List<MesaDocumento> findAll() {
        return this.mesaDocumentoRepository.findAll();
    }

    @Override
    public Optional<MesaDocumento> findByMesaAndAdmDocumentoElectoralAndTipoArchivoAndPagina(Long idMesa, Integer idDocumentoElectoral, String tipoArchivo, Integer pagina) {
        return mesaDocumentoRepository.findByMesa_IdAndAdmDocumentoElectoral_IdAndTipoArchivoAndPagina(idMesa,idDocumentoElectoral, tipoArchivo, pagina);
    }

    @Override
    public void deleteAllInBatch() {
        this.mesaDocumentoRepository.deleteAllInBatch();
    }

    @Override
    public void deleteListMesaDocumento(List<MesaDocumento> mesaDocumentoList) {
        this.mesaDocumentoRepository.deleteAll(mesaDocumentoList);
    }

    @Override
    public void deleteByAdmDocumentoElectoralIdAndMesaId(Integer admDocumentoElectoralId, Long mesaId) {
        this.mesaDocumentoRepository.deleteByAdmDocumentoElectoral_IdAndMesa_Id(admDocumentoElectoralId, mesaId);
    }

    @Override
    public long count() {
        return this.mesaDocumentoRepository.count();
    }

    @Override
    public List<MesaDocumento> buscarIdMesaAndIdDocumentoElectoral(Long idMesa, Integer idDocumentoElectoral) {
        return this.mesaDocumentoRepository.findByMesa_IdAndAdmDocumentoElectoral_Id( idMesa, idDocumentoElectoral);
    }
}
