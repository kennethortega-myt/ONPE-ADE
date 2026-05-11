package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaCola;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MiembroMesaColaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MiembroMesaColaService;
import java.util.List;

@Service
public class MiembroMesaColaServiceImpl implements MiembroMesaColaService {

    private final MiembroMesaColaRepository miembroMesaColaRepository;

    public MiembroMesaColaServiceImpl(MiembroMesaColaRepository miembroMesaColaRepository){
        this.miembroMesaColaRepository = miembroMesaColaRepository;
    }

    @Override
    public void save(MiembroMesaCola miembroMesaCola) {
        this.miembroMesaColaRepository.save(miembroMesaCola);
    }

    @Override
    public void saveAll(List<MiembroMesaCola> k) {
        this.miembroMesaColaRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.miembroMesaColaRepository.deleteAll();
    }

    @Override
    public List<MiembroMesaCola> findAll() {
        return this.miembroMesaColaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        this.miembroMesaColaRepository.deleteAllInBatch();
    }

    @Override
    public List<MiembroMesaCola> listMiembroMesaColaActivoByMesaId(Long idMesa) {
        return this.miembroMesaColaRepository.findByMesaIdActivo(idMesa);
    }

    @Override
    public int inhabilitarOmisoMiembroMesaCola(Long idMesa, String usuario) {
        try{
            return this.miembroMesaColaRepository.inhabilitarOmisoMiembroMesaCola(usuario, idMesa);
        }catch(Exception e){
            return 0;
        }
    }
}
