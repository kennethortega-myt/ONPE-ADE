package pe.gob.onpe.scebackend.model.service.impl.spec.log;

import java.util.ArrayList;
import java.util.List;

import pe.gob.onpe.scebackend.model.orc.entities.TabLogTransaccional;
import pe.gob.onpe.scebackend.model.service.impl.spec.LogFilter;

import pe.gob.onpe.scebackend.utils.AbstractSpec;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LogTransacionalSpecImpl extends AbstractSpec implements LogTransacionalSpec {

  @Override
  public Specification<TabLogTransaccional> filter(LogFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> conditions = new ArrayList<>();
      if (StringUtils.isNotBlank(filter.getError())) {
        conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("observacion")), "%" + filter.getError().toLowerCase() + "%"));
      }
      return and(criteriaBuilder, conditions);
    };
  }
}
