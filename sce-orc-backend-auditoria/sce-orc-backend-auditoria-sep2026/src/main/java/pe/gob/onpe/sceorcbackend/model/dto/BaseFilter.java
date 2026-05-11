package pe.gob.onpe.sceorcbackend.model.dto;

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
