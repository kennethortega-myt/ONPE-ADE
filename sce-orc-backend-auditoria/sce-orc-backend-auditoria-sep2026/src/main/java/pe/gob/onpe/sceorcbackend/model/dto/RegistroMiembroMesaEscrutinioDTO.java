package pe.gob.onpe.sceorcbackend.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RegistroMiembroMesaEscrutinioDTO extends RegistroMesaBaseDTO{
  private MiembroMesaEscrutinioSeccionesDto secciones;
  private List<MiembroMesaEscrutinioDTO> data;

}
