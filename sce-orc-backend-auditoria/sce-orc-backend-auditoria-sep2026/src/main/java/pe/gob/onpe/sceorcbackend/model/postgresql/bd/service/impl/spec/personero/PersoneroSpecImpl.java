package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.personero;

import java.util.ArrayList;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Personero;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.AbstractSpec;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PersoneroSpecImpl extends AbstractSpec implements PersoneroSpec {

  @Override
  public Specification<Personero> filter(PersoneroFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> conditions = new ArrayList<>();
      if (StringUtils.isNotBlank(filter.getNumeroDocumento())) {
        conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("documentoIdentidad")), "%" + filter.getNumeroDocumento().toLowerCase() + "%"));
      }
      return and(criteriaBuilder, conditions);
    };
  }

}
