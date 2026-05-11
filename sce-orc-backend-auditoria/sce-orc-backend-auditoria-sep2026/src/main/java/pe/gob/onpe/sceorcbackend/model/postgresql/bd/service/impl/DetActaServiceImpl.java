package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaService;
import java.util.List;
import java.util.Optional;

@Service
public class DetActaServiceImpl implements DetActaService {

    private final DetActaRepository detActaRepository;

    public DetActaServiceImpl(DetActaRepository detActaRepository) {
        this.detActaRepository = detActaRepository;
    }

    @Override
    public void save(DetActa detActa) {
        this.detActaRepository.save(detActa);
    }

    @Override
    public void saveAll(List<DetActa> k) {
        this.detActaRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detActaRepository.deleteAll();
    }

    @Override
    public List<DetActa> findAll() {
        return this.detActaRepository.findAll();
    }

    @Override
    public List<DetActa> findByCabActa(Acta acta) {
        return this.detActaRepository.findByActa(acta);
    }

    @Override
    public Optional<DetActa> getDetActa(Long actaAlearotia, long posicion) {
        return this.detActaRepository.findByActaIdAndPosicion(actaAlearotia, posicion);
    }

    @Override
    public Optional<DetActa> findById(Long id) {
        return this.detActaRepository.findById(id);
    }

    @Override
    public void deleteAllInBatch() {
        this.detActaRepository.deleteAllInBatch();
    }
    
    @Override
    public List<DetActa> findByIdActaOrderByPosicion(Long idActa) {
        return this.detActaRepository.findByActa_IdOrderByPosicion(idActa);
    }


}
