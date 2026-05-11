package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.parametro;

import pe.gob.onpe.sceorcbackend.model.dto.BaseFilter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ParametroFilter extends BaseFilter {

  private String parametro;

  private String perfil;

}
