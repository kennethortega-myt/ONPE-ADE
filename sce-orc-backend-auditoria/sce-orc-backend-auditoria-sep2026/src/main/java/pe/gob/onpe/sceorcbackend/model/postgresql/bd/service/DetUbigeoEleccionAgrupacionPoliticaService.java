package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UbigeoEleccion;
import java.util.List;
import java.util.Optional;

public interface DetUbigeoEleccionAgrupacionPoliticaService extends CrudService<DetUbigeoEleccionAgrupacionPolitica> {


    List<DetUbigeoEleccionAgrupacionPolitica> findByUbigeoEleccion(UbigeoEleccion ubigeoEleccion);

    List<DetUbigeoEleccionAgrupacionPolitica> findByUbigeoEleccionId(Long ubigeoEleccionId);

    Optional<DetUbigeoEleccionAgrupacionPolitica> findByPosicionAndIdUbigeoEleccion(Integer posicion, Long idUbigeoEleccion);

}
