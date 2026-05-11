package pe.gob.onpe.scebackend.model.service.impl.spec.parametro;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.orc.entities.CabParametro;
import pe.gob.onpe.scebackend.utils.AbstractSpec;

import java.util.ArrayList;
import java.util.List;

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
