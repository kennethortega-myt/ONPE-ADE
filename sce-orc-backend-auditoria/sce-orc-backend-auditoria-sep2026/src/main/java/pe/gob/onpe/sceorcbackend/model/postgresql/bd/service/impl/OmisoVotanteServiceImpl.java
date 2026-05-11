package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoVotante;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.OmisoVotanteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OmisoVotanteService;
import java.util.List;

@Service
public class OmisoVotanteServiceImpl implements OmisoVotanteService {

    private final OmisoVotanteRepository  omisoVotanteRepository;

    public OmisoVotanteServiceImpl(OmisoVotanteRepository  omisoVotanteRepository){
        this.omisoVotanteRepository = omisoVotanteRepository;
    }


    @Override
    public void save(OmisoVotante omisoVotante) {
        this.omisoVotanteRepository.save(omisoVotante);
    }

    @Override
    public void saveAll(List<OmisoVotante> k) {
        this.omisoVotanteRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.omisoVotanteRepository.deleteAll();
    }

    @Override
    public List<OmisoVotante> findAll() {
        return this.omisoVotanteRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        this.omisoVotanteRepository.deleteAllInBatch();
    }

    @Override
    public List<OmisoVotante> buscarPorIdMesa(Long idMesa) {
        return this.omisoVotanteRepository.findByMesaId(idMesa);
    }

    @Override
    public List<OmisoVotante> buscarPorIdMesaActivo(Long idMesa) {
        return this.omisoVotanteRepository.findByMesaIdActivo(idMesa);
    }

    @Override
    public int inhabilitarOmisosVotantes(Long idMesa, String usuario) {
        try{
            return this.omisoVotanteRepository.inhabilitarOmisoVotante(usuario, idMesa);
        }catch(Exception e){
            return 0;
        }
    }

    @Override
    public Long contarPorActivo(Integer activo) {
        return this.omisoVotanteRepository.contarPorActivo(activo);
    }


}
