package pe.gob.onpe.sceorcbackend.model.dto.response;

import java.util.Date;
import java.util.Set;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcesoElectoralResponseDTO {
  private Long 	id;
  private String 	cNombre;
  private String 	cAcronimo;
  private Date dFechaConvocatoria;
  private Long 	nTipoAmbitoElectoral;
  private Integer nActivo;
  private Set<EleccionDto> elecciones;
}
