package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UbigeoEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UbigeoEleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UbigeoEleccionService;
import java.util.List;

@Service
public class UbigeoEleccionServiceImpl implements UbigeoEleccionService {

    UbigeoEleccionRepository ubigeoEleccionRepository;

    private UbigeoEleccionServiceImpl(UbigeoEleccionRepository ubigeoEleccionRepository){
        this.ubigeoEleccionRepository = ubigeoEleccionRepository;
    };


    @Override
    public void save(UbigeoEleccion ubigeoEleccion) {
        this.ubigeoEleccionRepository.save(ubigeoEleccion);
    }

    @Override
    public void saveAll(List<UbigeoEleccion> k) {
        this.ubigeoEleccionRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.ubigeoEleccionRepository.deleteAll();
    }

    @Override
    public List<UbigeoEleccion> findAll() {
        return this.ubigeoEleccionRepository.findAll();
    }


}
