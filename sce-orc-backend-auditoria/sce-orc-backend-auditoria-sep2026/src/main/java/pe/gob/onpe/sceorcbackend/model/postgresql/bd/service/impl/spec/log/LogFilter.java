package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.log;

import pe.gob.onpe.sceorcbackend.model.dto.BaseFilter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LogFilter extends BaseFilter {
private String error;
}
