package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.personero;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Personero;
import org.springframework.data.jpa.domain.Specification;

public interface PersoneroSpec {

  Specification<Personero> filter(final PersoneroFilter filter);

}
