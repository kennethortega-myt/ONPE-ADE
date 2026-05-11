package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.functionalinterface;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;

@FunctionalInterface
public interface ManejadorVotoIlegible<T> {
  void manejar(Acta acta, T votoOpcion, DetActaOpcion detActaOpcion);
}