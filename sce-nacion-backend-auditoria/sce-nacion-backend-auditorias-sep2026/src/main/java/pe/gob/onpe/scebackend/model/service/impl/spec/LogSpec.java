package pe.gob.onpe.scebackend.model.service.impl.spec;

import pe.gob.onpe.scebackend.model.entities.TabLog;

import org.springframework.data.jpa.domain.Specification;

public interface LogSpec {
  Specification<TabLog> filter(final LogFilter filter);
}


