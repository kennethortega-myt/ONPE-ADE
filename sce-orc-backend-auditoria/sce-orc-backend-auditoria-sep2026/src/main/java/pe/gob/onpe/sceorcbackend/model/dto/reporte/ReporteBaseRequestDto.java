package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
@ToString
public class ReporteBaseRequestDto {
  @Alphanumeric
  private String esquema;
  @Alphanumeric
  private String eleccion;
  private Integer idProceso;
  private Integer idEleccion;
  private Integer idAmbitoElectoral;
  @Alphanumeric
  private String codigoAmbitoElectoral;
  private Integer idCentroComputo;
  @Alphanumeric
  private String codigoCentroComputo;
  private Integer idUbigeoNivelUno;
  private Integer idUbigeoNivelDos;
  private Integer idUbigeoNivelTres;
  @Alphanumeric
  private String usuario;
  private String acronimo;
}
