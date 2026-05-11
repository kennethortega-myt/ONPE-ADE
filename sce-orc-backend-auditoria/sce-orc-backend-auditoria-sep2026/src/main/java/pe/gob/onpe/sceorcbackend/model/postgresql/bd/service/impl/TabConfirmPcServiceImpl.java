package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabConfirmPc;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabConfirmPcRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.TabConfirmPcService;
import java.util.List;

@Service
public class TabConfirmPcServiceImpl implements TabConfirmPcService {

    private final TabConfirmPcRepository tabConfirmPcRepository;

    public TabConfirmPcServiceImpl(TabConfirmPcRepository tabConfirmPcRepository){
        this.tabConfirmPcRepository = tabConfirmPcRepository;
    }

    @Override
    public void save(TabConfirmPc tabConfirmPc) {
        this.tabConfirmPcRepository.save(tabConfirmPc);
    }

    @Override
    public void saveAll(List<TabConfirmPc> k) {
        this.tabConfirmPcRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.tabConfirmPcRepository.deleteAll();
    }

    @Override
    public List<TabConfirmPc> findAll() {
        return this.tabConfirmPcRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        this.tabConfirmPcRepository.deleteAllInBatch();
    }
}
