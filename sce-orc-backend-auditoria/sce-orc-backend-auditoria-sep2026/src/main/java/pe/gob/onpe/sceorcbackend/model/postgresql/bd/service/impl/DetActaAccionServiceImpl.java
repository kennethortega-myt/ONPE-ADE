package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaAccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaAccionService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.util.List;

@Service
public class DetActaAccionServiceImpl implements DetActaAccionService {

    private final DetActaAccionRepository detActaAccionRepository;

    public DetActaAccionServiceImpl(DetActaAccionRepository detActaAccionRepository){
        this.detActaAccionRepository = detActaAccionRepository;
    }

    @Override
    public void save(DetActaAccion detActaAccion) {

        if(detActaAccion.getTiempo().equals(ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN)) {
            List<DetActaAccion> detActaAccionList = this.detActaAccionRepository.findByActa_IdAndAccionAndTiempoOrderByIteracion(
                    detActaAccion.getActa().getId(),
                    detActaAccion.getAccion(),
                    ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI);

            int iteracionSet = 1;
            if (!detActaAccionList.isEmpty()) {//tomo la ultima iteracion
                DetActaAccion detActaAccion1 = detActaAccionList.getLast();
                iteracionSet = detActaAccion1.getIteracion();
            }
            detActaAccion.setIteracion(iteracionSet);
        }else{ //INICIO
            List<DetActaAccion> detActaAccionList = this.detActaAccionRepository.findByActa_IdAndAccionAndTiempoOrderByIteracion(
                    detActaAccion.getActa().getId(),
                    detActaAccion.getAccion(),
                    ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI);

            int iteracionSet = 1;
            if (!detActaAccionList.isEmpty()) {//tomo la ultima iteracion mas uno
                DetActaAccion detActaAccion1 = detActaAccionList.getLast();
                iteracionSet = detActaAccion1.getIteracion() + 1;
            }
            detActaAccion.setIteracion(iteracionSet);
        }

        this.detActaAccionRepository.save(detActaAccion);

    }

    @Override
    public void saveAll(List<DetActaAccion> k) {
        this.detActaAccionRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.detActaAccionRepository.deleteAll();
    }

    @Override
    public List<DetActaAccion> findAll() {
        return detActaAccionRepository.findAll();
    }

    @Override
    public List<DetActaAccion> findByActaAndAccionAndIteracion(Acta acta, String accion, Integer iteracion) {
        return this.detActaAccionRepository.findByActaAndAccionAndIteracion(acta, accion, iteracion);
    }

    @Override
    public List<DetActaAccion> findByActa_IdAndAccionAndIteracion(Long acta, String accion, Integer iteracion) {
        return this.detActaAccionRepository.findByActa_IdAndAccionAndIteracion(acta, accion, iteracion);
    }

    @Override
    public void deleteAllInBatch() {
        this.detActaAccionRepository.deleteAllInBatch();
    }

    @Override
    public int inactivarPorActaYAccion(Long actaId, String accion) {
        return this.detActaAccionRepository.inactivarPorActaYAccion(actaId, accion);
    }

    @Override
    public boolean existsByActaAccionTiempoAndUsuarioPrefix(Long actaId, String accion, String tiempo, String prefixUsuario) {
        String escapedPrefix = prefixUsuario.replace("_", "\\_") + "%";
        return this.detActaAccionRepository.existsByActaAccionTiempoAndUsuarioPrefix(actaId, accion, tiempo, escapedPrefix);
    }
}
