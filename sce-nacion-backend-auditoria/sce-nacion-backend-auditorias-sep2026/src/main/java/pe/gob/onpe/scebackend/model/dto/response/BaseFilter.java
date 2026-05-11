package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseFilter {

  private Integer size;

  private Integer page;

  private Integer status;

  private Integer id;
}
