package pe.gob.onpe.scebackend.model.service.impl.spec.parametro;

import org.springframework.data.jpa.domain.Specification;
import pe.gob.onpe.scebackend.model.orc.entities.CabParametro;

public interface ParametroSpec {
  Specification<CabParametro> filter(final ParametroFilter filter);
}
