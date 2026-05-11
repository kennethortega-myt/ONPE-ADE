package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CabActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaFormatoService;
import java.util.List;

@Service
public class CabActaFormatoServiceImpl implements CabActaFormatoService {

    private final CabActaFormatoRepository cabActaFormatoRepository;

    public CabActaFormatoServiceImpl(CabActaFormatoRepository cabActaFormatoRepository) {
        this.cabActaFormatoRepository = cabActaFormatoRepository;
    }

    @Override
    public void save(CabActaFormato cabActaFormato) {
        this.cabActaFormatoRepository.save(cabActaFormato);
    }

    @Override
    public void saveAll(List<CabActaFormato> k) {
        this.cabActaFormatoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.cabActaFormatoRepository.deleteAll();
    }

    @Override
    public List<CabActaFormato> findAll() {
        return this.cabActaFormatoRepository.findAll();
    }

    @Override
    public void deleteAllInBatch() {
        this.cabActaFormatoRepository.deleteAllInBatch();
    }
}
