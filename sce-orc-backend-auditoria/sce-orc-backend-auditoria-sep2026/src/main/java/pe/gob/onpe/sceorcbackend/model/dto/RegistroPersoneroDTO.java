package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RegistroPersoneroDTO extends RegistroMesaBaseDTO {
  private Long fileId;
  private List<PersoneroDTO> data;
}