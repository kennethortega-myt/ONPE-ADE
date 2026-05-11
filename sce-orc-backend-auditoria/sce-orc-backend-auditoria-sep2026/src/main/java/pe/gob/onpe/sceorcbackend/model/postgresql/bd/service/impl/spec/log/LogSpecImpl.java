package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.log;

import java.util.ArrayList;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabLog;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.AbstractSpec;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@Component
public class LogSpecImpl extends AbstractSpec implements LogSpec {

  @Override
  public Specification<TabLog> filter(LogFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> conditions = new ArrayList<>();
      if (StringUtils.isNotBlank(filter.getError())) {
        conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("observacion")), "%" + filter.getError().toLowerCase() + "%"));
      }
      return and(criteriaBuilder, conditions);
    };
  }
}
