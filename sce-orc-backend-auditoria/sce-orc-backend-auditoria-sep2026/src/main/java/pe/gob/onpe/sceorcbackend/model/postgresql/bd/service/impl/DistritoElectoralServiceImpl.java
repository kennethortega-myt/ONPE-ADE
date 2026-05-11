package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DistritoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DistritoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DistritoElectoralService;

import java.util.List;
import java.util.Optional;


@Service
public class DistritoElectoralServiceImpl implements DistritoElectoralService {

    private final DistritoElectoralRepository maeDistritoElectoralRepository;

    public DistritoElectoralServiceImpl(DistritoElectoralRepository maeDistritoElectoralRepository) {
        this.maeDistritoElectoralRepository = maeDistritoElectoralRepository;
    }


    @Override
    public void save(DistritoElectoral distritoElectoral) {
        this.maeDistritoElectoralRepository.save(distritoElectoral);
    }

    @Override
    public void saveAll(List<DistritoElectoral> k) {
        this.maeDistritoElectoralRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.maeDistritoElectoralRepository.deleteAll();
    }

    @Override
    public List<DistritoElectoral> findAll() {
        return this.maeDistritoElectoralRepository.findAll();
    }

    @Override
    public Optional<DistritoElectoral> getById(Integer id) {
        return this.maeDistritoElectoralRepository.findById(id);
    }
}
