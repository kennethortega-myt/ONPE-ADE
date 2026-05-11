package pe.gob.onpe.scebackend.model.service.impl.spec;

import pe.gob.onpe.scebackend.model.dto.response.BaseFilter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LogFilter extends BaseFilter {
private String error;
}
