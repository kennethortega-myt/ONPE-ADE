package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AmbitoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.AmbitoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AmbitoElectoralService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.util.List;
import java.util.Optional;

@Service
public class AmbitoElectoralServiceImpl implements AmbitoElectoralService {

    private final AmbitoElectoralRepository maeAmbitoElectoralRepository;

    public AmbitoElectoralServiceImpl(AmbitoElectoralRepository maeAmbitoElectoralRepository) {
        this.maeAmbitoElectoralRepository = maeAmbitoElectoralRepository;
    }

    @Override
    public void save(AmbitoElectoral k) {
        this.maeAmbitoElectoralRepository.save(k);
    }

    @Override
    public void saveAll(List<AmbitoElectoral> k) {
        this.maeAmbitoElectoralRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.maeAmbitoElectoralRepository.deleteAll();
    }


    public Optional<AmbitoElectoral> findOneAmbitoElectoral(){

        List<AmbitoElectoral> ambitoElectorals = this.maeAmbitoElectoralRepository.findAll();

      return ambitoElectorals.stream()
            .filter(a -> !ConstantesComunes.CC_NACION_DESCRIPCION.equalsIgnoreCase(a.getNombre()))
            .findFirst();

    }

    @Override
    public List<AmbitoElectoral> findAll() {
        return this.maeAmbitoElectoralRepository.findAll();
    }
}
