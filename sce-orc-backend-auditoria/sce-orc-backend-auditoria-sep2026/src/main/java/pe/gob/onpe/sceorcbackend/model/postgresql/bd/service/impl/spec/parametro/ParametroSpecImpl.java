package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.parametro;

import java.util.ArrayList;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.AbstractSpec;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ParametroSpecImpl extends AbstractSpec implements ParametroSpec {

  @Override
  public Specification<CabParametro> filter(ParametroFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> conditions = new ArrayList<>();
      if (StringUtils.isNotBlank(filter.getParametro())) {
        conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("parametro")), "%" + filter.getParametro().toLowerCase() + "%"));
      }
        if (StringUtils.isNotBlank(filter.getPerfil())) {
            conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("perfilesAutorizados")), "%" + filter.getPerfil().toLowerCase() + "%"));
        }
      return and(criteriaBuilder, conditions);
    };
  }

}
