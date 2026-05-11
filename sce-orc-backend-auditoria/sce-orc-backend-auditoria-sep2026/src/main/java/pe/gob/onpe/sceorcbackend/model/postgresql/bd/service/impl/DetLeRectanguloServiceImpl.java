package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetLeRectangulo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetLeRectanguloRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetLeRectanguloService;

import java.util.List;

@Service
public class DetLeRectanguloServiceImpl implements DetLeRectanguloService {

    private final DetLeRectanguloRepository detLeRectanguloRepository;

    public DetLeRectanguloServiceImpl(DetLeRectanguloRepository detLeRectanguloRepository){
        this.detLeRectanguloRepository = detLeRectanguloRepository;
    }

    @Override
    public void save(DetLeRectangulo detLeRectangulo) {
        this.detLeRectanguloRepository.save(detLeRectangulo);
    }

    @Override
    public void saveAll(List<DetLeRectangulo> k) {
        this.detLeRectanguloRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detLeRectanguloRepository.deleteAll();
    }

    @Override
    public List<DetLeRectangulo> findAll() {
        return this.detLeRectanguloRepository.findAll();
    }

    @Override
    public List<DetLeRectangulo> findByMesaId(Long mesaId) {
        return this.detLeRectanguloRepository.findByMesaId(mesaId);
    }

    @Override
    @Transactional
    public void deleteByMesaIdAndType(Long idMesa, String type) {
        this.detLeRectanguloRepository.deleteByMesaIdAndType(idMesa, type);
    }


    @Override
    @Transactional
    public void deleteByMesaId(Long idMesa) {
        this.detLeRectanguloRepository.deleteByMesaId(idMesa);
    }

    @Override
    public void deleteAllInBatch() {
        this.detLeRectanguloRepository.deleteAllInBatch();
    }
}
