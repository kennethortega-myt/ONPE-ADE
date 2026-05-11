package pe.gob.onpe.sceorcbackend.model.postgresql.admin.services;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.Seccion;

import java.util.List;
import java.util.Optional;

public interface SeccionService extends CrudService<Seccion> {
    Optional<Seccion> findByAbreviatura(String abreviatura);

    Optional<Seccion> findByNombre(String nombre);

    List<Seccion> findIdsByAbreviaturas(List<String> abreviaturas);
}
