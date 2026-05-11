package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetOtroDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetOtroDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetOtroDocumentoService;
import java.util.List;

@Service
public class DetOtroDocumentoServiceImpl implements DetOtroDocumentoService {

    private final DetOtroDocumentoRepository detOtroDocumentoRepository;

    public DetOtroDocumentoServiceImpl(DetOtroDocumentoRepository detOtroDocumentoRepository) {
        this.detOtroDocumentoRepository = detOtroDocumentoRepository;
    }

    @Override
    public void deleteAllInBatch() {
        this.detOtroDocumentoRepository.deleteAllInBatch();
    }

    @Override
    public void save(DetOtroDocumento cabOtroDocumento) {
        this.detOtroDocumentoRepository.save(cabOtroDocumento);
    }

    @Override
    public void saveAll(List<DetOtroDocumento> k) {
        this.detOtroDocumentoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detOtroDocumentoRepository.deleteAll();
    }

    @Override
    public List<DetOtroDocumento> findAll() {
        return this.detOtroDocumentoRepository.findAll();
    }
}
