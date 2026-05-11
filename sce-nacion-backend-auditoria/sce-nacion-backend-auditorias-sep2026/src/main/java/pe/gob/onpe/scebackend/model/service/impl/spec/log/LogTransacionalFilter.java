package pe.gob.onpe.scebackend.model.service.impl.spec.log;

import pe.gob.onpe.scebackend.model.dto.response.BaseFilter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LogTransacionalFilter extends BaseFilter {
  private String error;
}
