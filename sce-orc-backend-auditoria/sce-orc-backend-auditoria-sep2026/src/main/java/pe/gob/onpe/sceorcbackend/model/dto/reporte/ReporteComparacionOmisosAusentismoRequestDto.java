package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
@ToString
public class ReporteComparacionOmisosAusentismoRequestDto extends ReporteRequestBaseDto {
  private Integer idAmbito;
  private Integer preferencial;
  @Alphanumeric
  private String ambitoElectoral;
  @Alphanumeric
  private String codigoAmbitoElectoral;
  //@NotNull(message = "Tipo de Reporte es requerido")
  private Integer tipoReporte;
}
