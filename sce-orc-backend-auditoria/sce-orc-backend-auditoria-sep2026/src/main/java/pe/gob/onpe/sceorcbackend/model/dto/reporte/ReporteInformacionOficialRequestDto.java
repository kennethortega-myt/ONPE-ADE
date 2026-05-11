package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;


@Getter
@Setter
public class ReporteInformacionOficialRequestDto extends ReporteRequestBaseDto {

  @Alphanumeric
  private String codigoUbigeoNivelUno;
  @Alphanumeric
  private String codigoUbigeoNivelDos;
  @Alphanumeric
  private String codigoUbigeoNivelTres;
  @Alphanumeric
  private String ubigeoNivelUno;
  @Alphanumeric
  private String ubigeoNivelDos;
  @Alphanumeric
  private String ubigeoNivelTres;
  private Integer idUbigeoNivelUno;
  private Integer idUbigeoNivelDos;
  private Integer idUbigeoNivelTres;
  private Integer idAmbitoElectoral;
  private String nombreUbigeoUno;
  private String nombreUbigeoDos;
  private String nombreUbigeoTres;

}
