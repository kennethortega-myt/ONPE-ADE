package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabAutorizacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.TabAutorizacionService;
import java.util.List;
import java.util.Optional;

@Service
public class TabAutorizacionServiceImpl implements TabAutorizacionService {

   private final TabAutorizacionRepository tabAutorizacionRepository;

   public TabAutorizacionServiceImpl(TabAutorizacionRepository tabAutorizacionRepository){
       this.tabAutorizacionRepository = tabAutorizacionRepository;
   }

    @Override
    public void save(TabAutorizacion tabAutorizacion) {
        this.tabAutorizacionRepository.save(tabAutorizacion);
    }

    @Override
    public void saveAll(List<TabAutorizacion> k) {
        this.tabAutorizacionRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.tabAutorizacionRepository.deleteAll();
    }

    @Override
    public List<TabAutorizacion> findAll() {
        return this.tabAutorizacionRepository.findAll();
    }

    @Override
    public List<TabAutorizacion> findByAutorizacionAndUsuarioCreacion(int autorizacion, String usuarioCreacion) {
        return this.tabAutorizacionRepository.findByAutorizacionAndUsuarioCreacion(autorizacion, usuarioCreacion);
    }

    @Override
    public List<TabAutorizacion> findByAutorizacion(int autorizacion) {
        return this.tabAutorizacionRepository.findByAutorizacion(autorizacion);
    }

    @Override
    public List<TabAutorizacion> findByAutorizacionOrderByFechaModificacionDesc(int autorizacion) {
        return this.tabAutorizacionRepository.findByAutorizacionOrderByFechaModificacionDesc(autorizacion);
    }

    @Override
    public List<TabAutorizacion> findByAutorizacionAndTipoAutorizacionAndActivo(int autorizacion, String tipo, Integer activo) {
        return this.tabAutorizacionRepository.findByAutorizacionAndTipoAutorizacionAndActivo(autorizacion,tipo,activo);
    }

    @Override
    public Optional<TabAutorizacion> findById(Long id) {
        return this.tabAutorizacionRepository.findById(id);
    }

    @Override
    public void deleteAllInBatch() {
        this.tabAutorizacionRepository.deleteAllInBatch();
    }

    @Override
    public void deleteAllInBatchExceptLast() {
        this.tabAutorizacionRepository.deleteAllInBatchExceptLast();
    }

    @Override
    public Long count() {
        return this.tabAutorizacionRepository.count();
    }

    @Override
    public Long findMaxNumeroAutorizacion() {
        Long maxNumeroAutorizacion = tabAutorizacionRepository.findMaxNumeroAutorizacion();
        if (maxNumeroAutorizacion == null) {
            maxNumeroAutorizacion = 0L;
        }

        return maxNumeroAutorizacion;
    }
}
