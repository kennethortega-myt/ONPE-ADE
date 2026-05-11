package pe.gob.onpe.scebackend.model.service.impl.spec.log;

import pe.gob.onpe.scebackend.model.orc.entities.TabLogTransaccional;
import pe.gob.onpe.scebackend.model.service.impl.spec.LogFilter;

import org.springframework.data.jpa.domain.Specification;

public interface LogTransacionalSpec {

  Specification<TabLogTransaccional> filter(final LogFilter filter);

}
