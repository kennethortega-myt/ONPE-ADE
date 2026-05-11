package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaSorteado;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MiembroMesaSorteadoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MiembroMesaSorteadoService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.util.List;

@Service
public class MiembroMesaSorteadoServiceImpl implements MiembroMesaSorteadoService {

    private final MiembroMesaSorteadoRepository miembroMesaSorteadoRepository;

    public MiembroMesaSorteadoServiceImpl(MiembroMesaSorteadoRepository miembroMesaSorteadoRepository){
        this.miembroMesaSorteadoRepository = miembroMesaSorteadoRepository;
    }


    @Override
    public void save(MiembroMesaSorteado miembroMesaSorteado) {
        this.miembroMesaSorteadoRepository.save(miembroMesaSorteado);
    }

    @Override
    public void saveAll(List<MiembroMesaSorteado> k) {
        this.miembroMesaSorteadoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.miembroMesaSorteadoRepository.deleteAll();
    }

    @Override
    public List<MiembroMesaSorteado> findAll() {
        return this.miembroMesaSorteadoRepository.findAll();
    }

    @Override
    public List<MiembroMesaSorteado> findByMesa(Mesa tabMesa) {
        return this.miembroMesaSorteadoRepository.findByMesa(tabMesa);
    }

    @Override
    public Long count() {

        return this.miembroMesaSorteadoRepository.countByEstado(ConstantesComunes.ACTIVO);
    }
}
