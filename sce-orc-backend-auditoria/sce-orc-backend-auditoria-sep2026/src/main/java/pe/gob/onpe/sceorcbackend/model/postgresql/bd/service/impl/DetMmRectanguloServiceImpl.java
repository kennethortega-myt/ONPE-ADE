package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetMmRectangulo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetMmRectanguloRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetMmRectanguloService;

import java.util.List;

@Service
public class DetMmRectanguloServiceImpl implements DetMmRectanguloService {

    private final DetMmRectanguloRepository detMmRectanguloRepository;

    @Autowired
    public DetMmRectanguloServiceImpl(DetMmRectanguloRepository detMmRectanguloRepository) {
        this.detMmRectanguloRepository = detMmRectanguloRepository;
    }

    @Override
    public void save(DetMmRectangulo detMmRectangulo) {
        this.detMmRectanguloRepository.save(detMmRectangulo);
    }

    @Override
    public void saveAll(List<DetMmRectangulo> k) {
        this.detMmRectanguloRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detMmRectanguloRepository.deleteAll();
    }

    @Override
    public List<DetMmRectangulo> findAll() {
        return this.detMmRectanguloRepository.findAll();
    }

    @Override
    public List<DetMmRectangulo> findByMesaId(Long mesaId) {
        return this.detMmRectanguloRepository.findByMesaId(mesaId);
    }

    @Override
    public void deleteByMesaIdAndType(Long idMesa, String type) {
        this.detMmRectanguloRepository.deleteByMesaIdAndType(idMesa, type);
    }

    @Override
    public void deleteByMesaId(Long idMesa) {
        this.detMmRectanguloRepository.deleteByMesaId(idMesa);
    }

    @Override
    public void deleteAllInBatch() {
        this.detMmRectanguloRepository.deleteAllInBatch();
    }

}
