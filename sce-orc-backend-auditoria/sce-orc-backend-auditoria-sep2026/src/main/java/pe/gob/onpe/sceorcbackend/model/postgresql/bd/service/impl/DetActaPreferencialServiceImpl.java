package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaPreferencialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaPreferencialService;
import java.util.List;
import java.util.Optional;

@Service
public class DetActaPreferencialServiceImpl implements DetActaPreferencialService {

    private final DetActaPreferencialRepository detActaPreferencialRepository;

    public DetActaPreferencialServiceImpl(DetActaPreferencialRepository detActaPreferencialRepository) {
        this.detActaPreferencialRepository = detActaPreferencialRepository;
    }

    @Override
    public void save(DetActaPreferencial k) {
        this.detActaPreferencialRepository.save(k);
    }

    @Override
    public void saveAll(List<DetActaPreferencial> k) {
        this.detActaPreferencialRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detActaPreferencialRepository.deleteAll();
    }

    @Override
    public List<DetActaPreferencial> findAll() {
        return this.detActaPreferencialRepository.findAll();
    }

    @Override
    public void delete(DetActaPreferencial detActaPreferencial) {
        this.detActaPreferencialRepository.delete(detActaPreferencial);
    }

    @Override
    public void deleteAllInBatch() {
        this.detActaPreferencialRepository.deleteAllInBatch();
    }

    @Override
    public List<DetActaPreferencial> findByDetActa(DetActa detActa) {
        return this.detActaPreferencialRepository.findByDetActa(detActa);
    }

    @Override
    public Optional<DetActaPreferencial> getDetActaPreferencialByDetActaAndLista(DetActa detActa, Integer lista) {
        return this.detActaPreferencialRepository.findByDetActaAndLista(detActa, lista);
    }

    @Override
    public Optional<DetActaPreferencial> findById(Long id) {
        return this.detActaPreferencialRepository.findById(id);
    }


}
