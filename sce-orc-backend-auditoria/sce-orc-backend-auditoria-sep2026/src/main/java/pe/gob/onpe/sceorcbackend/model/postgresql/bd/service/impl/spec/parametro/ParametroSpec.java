package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.parametro;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabParametro;

import org.springframework.data.jpa.domain.Specification;

public interface ParametroSpec {
  Specification<CabParametro> filter(final ParametroFilter filter);
}
