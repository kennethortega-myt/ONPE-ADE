package pe.gob.onpe.scebackend.model.service.impl.spec.parametro;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.onpe.scebackend.model.dto.response.BaseFilter;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ParametroFilter extends BaseFilter {

  private String parametro;

  private String perfil;

}
