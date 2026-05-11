package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.personero;

import pe.gob.onpe.sceorcbackend.model.dto.BaseFilter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class PersoneroFilter  extends BaseFilter {

  private String numeroDocumento;

}
