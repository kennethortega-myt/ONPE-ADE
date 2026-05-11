package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.log;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabLog;

import org.springframework.data.jpa.domain.Specification;

public interface LogSpec {
  Specification<TabLog> filter(final LogFilter filter);
}


