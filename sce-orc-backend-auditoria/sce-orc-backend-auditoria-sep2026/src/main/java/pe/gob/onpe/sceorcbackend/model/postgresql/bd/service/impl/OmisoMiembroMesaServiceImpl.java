package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoMiembroMesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.OmisoMiembroMesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OmisoMiembroMesaService;
import java.util.List;

@Service
public class OmisoMiembroMesaServiceImpl implements OmisoMiembroMesaService {

    private final OmisoMiembroMesaRepository omisoMiembroMesaRepository;

    public OmisoMiembroMesaServiceImpl(OmisoMiembroMesaRepository omisoMiembroMesaRepository){
        this.omisoMiembroMesaRepository = omisoMiembroMesaRepository;
    }


    @Override
    public void save(OmisoMiembroMesa omisoMiembroMesa) {
        this.omisoMiembroMesaRepository.save(omisoMiembroMesa);
    }

    @Override
    public void saveAll(List<OmisoMiembroMesa> k) {
        this.omisoMiembroMesaRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.omisoMiembroMesaRepository.deleteAll();
    }

    @Override
    public List<OmisoMiembroMesa> findAll() {
        return this.omisoMiembroMesaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        this.omisoMiembroMesaRepository.deleteAllInBatch();
    }

    @Override
    public List<OmisoMiembroMesa> buscarPorMesa(Long idMesa) {
        return this.omisoMiembroMesaRepository.findByMesaId(idMesa);
    }

    @Override
    public List<OmisoMiembroMesa> buscarOmisoActivoPorMesa(Long idMesa) {
        return this.omisoMiembroMesaRepository.findByMesaIdActivo(idMesa);
    }

    @Override
    public int inhabilitarOmisoMiembroMesa(Long idMesa, String usuario) {
        try{
            return this.omisoMiembroMesaRepository.inhabilitarOmisoMiembroMesa(usuario, idMesa);
        }catch(Exception e){
            return 0;
        }
    }

    @Override
    public Long contarPorActivo(Integer activo) {
        return this.omisoMiembroMesaRepository.contarPorActivo(activo);
    }
}
